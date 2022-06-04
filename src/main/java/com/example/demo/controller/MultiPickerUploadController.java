package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 多个文件选择器上传文件，一个选择器对应一个文件
 */
@RestController
@RequestMapping("/MultiPickerUpload")
public class MultiPickerUploadController {

    @PostMapping("/")
    public ResponseEntity<Void> fileUpload(@RequestParam("type") String type,
                                           @RequestParam("name") String name,
                                           @RequestParam("file") MultipartFile file) {

        switch (type) {
            case "researchReport": //研究报告
                //save file
                break;
            case "researchReportStuff": //研究报告支撑材料(限PDF)
                //save file
                break;
            case "applyReport": //应用报告
                //save file
                break;
            case "applyReportStuff": //应用报告支撑材料(限PDF)
                //save file
                break;
            default:
                return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
