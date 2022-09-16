package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import io.minio.*;
import io.minio.errors.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * author:atGuiGu-mqx
 * date:2022/8/28 11:40
 * 描述：
 **/
@RestController
@RequestMapping("/admin/product")
@RefreshScope// 热部署读取到 nacos 配置文件的数据
public class FileUploadController {

    //  表示从配置文件中获取地址：这个方式叫 软编码！当服务器地址发生改变的时候，只需要修改配置文件就可以了！
    //  如果说将ip地址写入java 代码中，服务器一换地址，则需要重写代码！ 重新编译，重新发布部署！ 硬编码！
    @Value("${minio.endpointUrl}")
    private String endpointUrl;

    @Value("${minio.accessKey}")
    public String accessKey;

    @Value("${minio.secreKey}")
    public String secreKey;

    @Value("${minio.bucketName}")
    public String bucketName;

    //  /admin/product/fileUpload
    @PostMapping("fileUpload")
    public Result fileUpload(MultipartFile file){
        //  声明一个变量接收url
        String url = "";
        try {
            // Create a minioClient with the MinIO server playground, its access key and secret key.
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint(endpointUrl)
                            .credentials(accessKey, secreKey)
                            .build();

            // Make 'asiatrip' bucket if not exist.
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                // Make a new bucket called 'asiatrip'.
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            } else {
                System.out.println("Bucket "+bucketName+" already exists.");
            }

            //  定义一个文件名称：
            String fileName = System.currentTimeMillis()+ UUID.randomUUID().toString();

            //  后缀名：minio 可以不用 xxx.png;
            //  file.getOriginalFilename() 截取：
            System.out.println("后缀名"+FilenameUtils.getExtension(file.getOriginalFilename()));

            //  新的上传方法：
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(
                            file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());

            //  上传之后的url  http://192.168.200.129:9000/gmall/164213218078225ee4ff7-871b-428f-8da5-18bd0b806bc9
            //  http://192.168.200.129:9000
            //  为什么要做拼接：因为 url后面有很多权限设置：http://172.17.0.4:9000/gmall/1642140181273624d619a-27e0-41be-b4c6-6169a45ee729?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=OHLTSDYXEIT7HYTD6B9T%2F20220828%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20220828T061310Z&X-Amz-Expires=604800&X-Amz-Security-Token=eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhY2Nlc3NLZXkiOiJPSExUU0RZWEVJVDdIWVRENkI5VCIsImV4cCI6MTY2MTY3MDQzNSwicGFyZW50IjoiYWRtaW4ifQ.eaid24GO2P15UWUWw-P2R5sU09c8uBsaS3xGKuYuOIoXjlyY9pBNv1uOeuzh1lA0cLZgXCo5xm1ktcr2i9YIXg&X-Amz-SignedHeaders=host&versionId=null&X-Amz-Signature=beed8ad3f67af1760e1b25d725bb2e1452e5b01bfd7637c2344e843b97a7c04d
            //  包含天数的设置：默认情况下 7 天有效期，超过了7天，则这个图片访问不了！
            //  改成拼接了！
            url= endpointUrl+"/"+bucketName+"/"+fileName;
            System.out.println("url:"+url);
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        }
        //  返回数据:
        return Result.ok(url);
    }
}