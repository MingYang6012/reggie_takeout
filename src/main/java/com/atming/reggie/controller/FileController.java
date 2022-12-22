package com.atming.reggie.controller;

import com.atming.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * @CreateTime: 2022-11-12-15:09
 * @Author: Hello77
 * @toUser:
 * @note: 用于接收文件上传以及下载
 */
@RestController
@Slf4j
@RequestMapping("/common")
public class FileController {

    //使用@Value注解,将配置文件设置的值动态的添加到属性中
    @Value("${reggie.basePath}")
    private String basePath;


    /**
     *  处理文件上传请求的方法
     * @param file MultipartFile,用于接受上传的文件
     * @return
     */
    @PostMapping("/upload")
    public R<String> update(MultipartFile file){

        log.info("文件的原始名称为：{}",file.getOriginalFilename());

        //获取上传文件的原始名称
        String originalFilename = file.getOriginalFilename();

        //获取原始上传的文件的后缀,通过lastIndexOf来截取后缀的索引
        int lastIndexOf = originalFilename.lastIndexOf(".");
        //通过substring来截取后缀
        String substring = originalFilename.substring(lastIndexOf);

        //使用UUID重新生成文件名,防止文件名重复
        String uuidName = UUID.randomUUID().toString();

        //创建一个目录对象
        File dir = new File(basePath);

        //判断当前目录结构是否存在
        if (!dir.exists()){
            //若当前目录结构不存在,则在相应的盘中创建这个目录结构
            //mkdir():创建对应的盘
            dir.mkdir();
        }


        try {
            file.transferTo(new File(basePath + uuidName + substring));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return R.success(uuidName + substring);
    }

    /**
     * 文件下载的控制器方法
     * @param name 需要下载的文件的文件名
     * @param response 需要将文件传给浏览器的响应输出流
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){


        //输入流,通过输入流来读取文件内容
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            //输出流,通过输出流将读取到的文件内容响应给浏览器
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            fileInputStream.close();
            outputStream.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
