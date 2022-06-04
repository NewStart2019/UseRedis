package com.example.demo.controller;

import com.example.demo.common.web.AjaxResult;
import com.example.demo.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/ImageUpload")
@Slf4j
public class ImageUploadController {

    @Autowired
    private FileService fileService;

    @PostMapping(value = "/ImageUp")
    public AjaxResult<String> fileUpload(@RequestParam("file") MultipartFile file) {
        AjaxResult<String> result = AjaxResult.success("文件已经上传成功");
        // 如果上传失败返回true
        result.setError(!fileService.fileUpload(file, true));
        return result;
    }
}

