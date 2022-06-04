package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("upload_info")
public class UploadInfo {
    //这个用于主键上的注解，mybatis-plus默认注解是id
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String md5;
    private String chunks;
    private String chunk;
    private String path;
    private String fileName;
    private String ext;

    public UploadInfo() {
    }

    public UploadInfo(String md5, String chunks, String chunk, String path, String fileName, String ext) {
        this.md5 = md5;
        this.chunks = chunks;
        this.chunk = chunk;
        this.path = path;
        this.fileName = fileName;
        this.ext = ext;
    }
}
