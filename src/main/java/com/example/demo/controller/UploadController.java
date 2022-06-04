package com.example.demo.controller;

import com.example.demo.common.web.AjaxResult;
import com.example.demo.service.FileService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/FileUpload")
@Slf4j
public class UploadController {

    private final static String utf8 = "utf-8";

    @Value("${server.self-path.basedir}")
    private String baseDir;

    @Resource
    private FileService fileService;


    @PostMapping("/fileUp")
    @ApiOperation("文件上传：第一次上传数据库连接需要10s左右")
    public AjaxResult<String> upload(@ApiParam("file") @RequestPart("file") MultipartFile file) {
        AjaxResult<String> result = AjaxResult.success("文件已经上传成功");
        // 如果上传失败返回true
        result.setError(!fileService.fileUpload(file, false));
        return result;
    }

    @GetMapping("download")
    @ApiOperation("文件分片下载")
    public void downloadFile(HttpServletRequest request, HttpServletResponse response) {

    }


}
