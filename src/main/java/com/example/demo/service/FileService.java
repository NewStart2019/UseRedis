package com.example.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.demo.model.File;
import org.springframework.web.multipart.MultipartFile;

public interface FileService extends IService<File> {
    /**
     * md5 是否存在
     *
     * @param md5 摘要
     * @return 结果
     */
    boolean isMd5Exist(String md5);

    /**
     * 上传文件，返回文件是否上传成功
     * true：代表上传成功
     *
     * @param file    文件
     * @param isImage 是否是图片上传
     * @return 上传成功与否
     */
    boolean fileUpload(MultipartFile file, boolean isImage);
}
