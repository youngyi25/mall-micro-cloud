package com.example.mall.oss.controller;

import com.aliyun.oss.model.ObjectMetadata;
import com.example.mall.oss.util.ByteMultipartFile;
import com.example.mall.oss.util.OssUtil;
import com.example.mall.vo.Result;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

@RestController
@RequestMapping("/oss")
public class OssController {
    @Autowired
    private OssUtil ossUtil;

    /**
     * 文件上传接口
     */
    @PostMapping("/upload")
    public String upload(
            @RequestParam MultipartFile file,
            @RequestParam(defaultValue = "file") String dir
    ) throws Exception {
        return ossUtil.uploadFile(file, dir);
    }
    /**
     * 下载文件
     */
    @GetMapping("/download")
    public void download(@RequestParam String objectKey, HttpServletResponse response) throws IOException {
        // 提取文件名
        String fileName = objectKey.substring(objectKey.lastIndexOf("/") + 1);

        // 设置响应头
        response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\"");
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        // 从OSS获取文件流，直接写到响应输出流
        try (InputStream inputStream = ossUtil.downloadFileStream(objectKey);
             OutputStream outputStream = response.getOutputStream()) {
            // 使用 commons-io 或手工拷贝
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/delete")
    public String delete(@RequestParam String objectKey) {
        ossUtil.deleteFile(objectKey);
        return "文件删除成功";
    }

    /**
     * 获取私有文件临时访问链接（30分钟）
     */
    @GetMapping("/signUrl")
    public String signUrl(@RequestParam String objectKey) {
        String url = ossUtil.getPresignedUrl(objectKey, 30);
        return url;
    }
    @PostMapping("/uploadByte")
    public Result<String> uploadByte(
            @RequestBody byte[] fileBytes,
            @RequestParam String fileName,
            @RequestParam String contentType,
            @RequestParam(defaultValue = "file") String dir
    ) throws Exception {
        MultipartFile file = new ByteMultipartFile(fileName, fileName, contentType, fileBytes);
        // 关键：构建元数据
        ObjectMetadata meta = new ObjectMetadata();
        // 传入前端/Feign传的contentType，html固定text/html
        meta.setContentType(file.getContentType());
        // 设置inline：内联预览，不强制下载
        meta.setContentType("text/html;charset=UTF-8");
        meta.setContentDisposition("inline");
        String url = ossUtil.uploadFile(file, dir,meta);
        return Result.success(url);
    }
}
