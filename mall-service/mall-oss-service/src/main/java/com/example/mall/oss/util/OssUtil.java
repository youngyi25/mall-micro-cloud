package com.example.mall.oss.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.example.mall.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Component
public class OssUtil {
    @Autowired
    private OSS ossClient;

    @Value("${aliyun.cloud.bucket-name}")
    private String bucketName;

    @Value("${aliyun.cloud.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.prefix}")
    private String prefix;


    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

    /**
     * 通用文件上传
     * @param file 前端上传文件
     * @param dir 存储目录：
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String dir) throws Exception {
        // 1. 获取文件后缀
        String originalName = file.getOriginalFilename();
        String suffix = originalName.substring(originalName.lastIndexOf("."));
        // 2. 生成唯一key：目录/日期/UUID.后缀 防止重名覆盖
        String datePath = DATE_FORMAT.format(new Date());
        String objectKey = prefix + "/" +dir + "/" + datePath + "/" + UUID.randomUUID() + suffix;
        // 3. 文件流上传
        try (InputStream is = file.getInputStream()) {
            PutObjectRequest request = new PutObjectRequest(bucketName, objectKey, is);
            // 设置文件ContentType
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentType(file.getContentType());
// 设置inline：内联预览，不强制下载
            meta.setContentDisposition("inline");
            request.setMetadata(meta);
            ossClient.putObject(request);
        }catch (Exception e){
            throw new BusinessException(6001,"上传失败");
        }
        // 4. 拼接访问地址
        return String.format("https://%s.%s/%s", bucketName, endpoint, objectKey);
    }

    /**
     * 根据文件key删除OSS文件
     */
    public void deleteFile(String objectKey) {
        ossClient.deleteObject(bucketName, objectKey);
    }

    /**
     * 下载文件流
     */
    public InputStream downloadFileStream(String objectKey) {
        OSSObject ossObject = ossClient.getObject(bucketName, objectKey);
        return ossObject.getObjectContent();
    }

    /**
     * 私有文件生成临时签名URL（30分钟有效期）
     * @param objectKey 文件路径
     * @param expireMin 过期分钟
     */
    public String getPresignedUrl(String objectKey, long expireMin) {
        Date expireTime = new Date(System.currentTimeMillis() + expireMin * 60 * 1000);
        URL url = ossClient.generatePresignedUrl(bucketName, objectKey, expireTime);
        return url.toString();
    }
    @Value("${aliyun.oss.cdn-domain}")
    private String cdnDomain;

    public String uploadFile(MultipartFile file, String dir, ObjectMetadata meta) throws IOException {
        // 1. 获取文件后缀
        String originalName = file.getOriginalFilename();
        String suffix = originalName.substring(originalName.lastIndexOf("."));
        // 2. 生成唯一key：目录/日期/UUID.后缀 防止重名覆盖
        String datePath = DATE_FORMAT.format(new Date());
        String objectKey = prefix + "/" +dir + "/" + datePath + "/" + UUID.randomUUID() + suffix;
        // 3. 文件流上传
        try (InputStream is = file.getInputStream()) {
            PutObjectRequest request = new PutObjectRequest(bucketName, objectKey, is);
            // 设置文件ContentType
            meta.setContentType(file.getContentType());
            // 设置inline：内联预览，不强制下载
            meta.setContentDisposition("inline");
            request.setMetadata(meta);
            ossClient.putObject(request);
        }catch (Exception e){
            new BusinessException(6001,"上传失败");
        }
        // 4. 拼接访问地址,返回自定义域名地址（推荐，避免原生oss域名强制下载）
        return String.format("https://%s/%s", cdnDomain, objectKey);
    }
}
