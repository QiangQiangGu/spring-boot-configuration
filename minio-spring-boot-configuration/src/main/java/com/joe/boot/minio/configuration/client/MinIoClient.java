package com.joe.boot.minio.configuration.client;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.joe.boot.minio.configuration.exception.MinIoClientException;
import com.joe.boot.minio.configuration.properties.MinIoProperties;
import io.minio.*;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * TODO 根据shardingsphere分片特性去做对应的数据存储
 *
 * @author Administrator
 */
@Slf4j
public class MinIoClient {

    private MinioClient minioClient;

    private List<String> bucketNames = new ArrayList<>();

    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(1L, 1L);

    /**
     * Policy List
     * none, download, upload, public
     * <p>
     * <p>
     * HttpUrl.Builder builder = new HttpUrl.Builder();
     * builder.scheme("http");
     * builder.host(minIoProperties.getEndpoint());
     * builder.port(minIoProperties.getPort());
     * builder.username(minIoProperties.getAccessKey());
     * builder.password(minIoProperties.getSecretKey());
     * HttpUrl url = builder.build();
     *
     * @param minIoProperties minIoProperties
     */
    public MinIoClient(MinIoProperties minIoProperties) {
        String bucketName = minIoProperties.getBucketName();
        String nodes = minIoProperties.getBucketNodes();
        Assert.notEmpty(bucketName,"minIo bucketName not config!!!");
        Assert.notEmpty(nodes,"minIo nodes not config!!!");
        List<String> nodeList = StrUtil.split(nodes, '-');
        this.minioClient = MinioClient.builder()
                .endpoint(minIoProperties.getEndpoint(), minIoProperties.getPort(), false)
                .credentials(minIoProperties.getAccessKey(), minIoProperties.getSecretKey())
                .build();
        Integer startNode = Convert.toInt(nodeList.get(0));
        Integer endNode = Convert.toInt(nodeList.get(1));
        try {
            for (; startNode <= endNode; startNode++) {
                String targetBucket = bucketName + "-" + startNode;
                if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(targetBucket).build())) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(targetBucket).build());
                }
                minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(targetBucket).config("{\n" +
                        "  \"Version\": \"2012-10-17\",\n" +
                        "  \"Statement\": [\n" +
                        "    {\n" +
                        "      \"Effect\": \"Allow\",\n" +
                        "      \"Principal\": {\n" +
                        "        \"AWS\": [\n" +
                        "          \"*\"\n" +
                        "        ]\n" +
                        "      },\n" +
                        "      \"Action\": [\n" +
                        "        \"s3:ListBucket\",\n" +
                        "        \"s3:ListBucketMultipartUploads\",\n" +
                        "        \"s3:GetBucketLocation\"\n" +
                        "      ],\n" +
                        "      \"Resource\": [\n" +
                        "        \"arn:aws:s3:::" + targetBucket + "\"\n" +
                        "      ]\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"Effect\": \"Allow\",\n" +
                        "      \"Principal\": {\n" +
                        "        \"AWS\": [\n" +
                        "          \"*\"\n" +
                        "        ]\n" +
                        "      },\n" +
                        "      \"Action\": [\n" +
                        "        \"s3:GetObject\",\n" +
                        "        \"s3:ListMultipartUploadParts\",\n" +
                        "        \"s3:PutObject\",\n" +
                        "        \"s3:AbortMultipartUpload\",\n" +
                        "        \"s3:DeleteObject\"\n" +
                        "      ],\n" +
                        "      \"Resource\": [\n" +
                        "        \"arn:aws:s3:::" + targetBucket + "/*\"\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}").build());
                bucketNames.add(targetBucket);
            }
        } catch (InvalidKeyException | NoSuchAlgorithmException | ErrorResponseException | InvalidResponseException | InvalidBucketNameException | ServerException | RegionConflictException | InsufficientDataException | XmlParserException | InternalException | IOException e) {
            e.printStackTrace();
            throw new MinIoClientException(e);
        }
    }

    private String putObjectReturnObjectName(String bucketName, InputStream inputStream, String contentType) throws NoSuchAlgorithmException, InsufficientDataException, InternalException, InvalidResponseException, InvalidKeyException, InvalidBucketNameException, ErrorResponseException, IOException, ServerException, XmlParserException {
        if (!(inputStream instanceof BufferedInputStream)) {
            inputStream = new BufferedInputStream(inputStream);
        }
        inputStream.mark(0);
        String type = FileTypeUtil.getType(inputStream);
        inputStream.reset();
        String objectName = SNOWFLAKE.nextIdStr() + '.' + type;
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(inputStream, inputStream.available(), 5242880)
                .contentType(contentType)
                .build();
        minioClient.putObject(putObjectArgs);
        return getObjectUrl(bucketName, objectName);
    }

    private String getObjectUrl(String bucketName, String objectName) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException {
        return minioClient.getObjectUrl(bucketName, objectName);
    }

    public String getObjectFileUrl(String bucketName, String objectName) {
        try {
            return getObjectUrl(bucketName, objectName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * upload file to MinIO Server
     * upload success return perfectly url
     * upload fail return null
     *
     * @param stream      stream
     * @param contentType contentType
     * @return perfectly url
     */
    public String upload(InputStream stream, String contentType) {
        Random random = new Random();
        int nextInt = random.nextInt(this.bucketNames.size());
        String bucketName = this.bucketNames.get(nextInt);
        try {
            return this.putObjectReturnObjectName(bucketName, stream, contentType);
        } catch (NoSuchAlgorithmException | InsufficientDataException | InternalException | InvalidResponseException | InvalidKeyException | InvalidBucketNameException | ErrorResponseException | IOException | XmlParserException | ServerException e) {
            e.printStackTrace();
        }
        return null;
    }

}
