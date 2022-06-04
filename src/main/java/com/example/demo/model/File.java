package com.example.demo.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Date;

@Data
@TableName("file")
public class File {
    //这个用于主键上的注解，mybatis-plus默认注解是id
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String fileName;

    private String md5;

    private Date uploadTime;

    private Integer status;

    public File(String fileName, String md5, Date uploadTime) {
        this.fileName = fileName;
        this.md5 = md5;
        this.uploadTime = uploadTime;
    }

    public File() {
    }
}
