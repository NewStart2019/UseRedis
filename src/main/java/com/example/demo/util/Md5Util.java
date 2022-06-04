package com.example.demo.util;

import com.sun.istack.internal.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {

    public static StringBuilder createMd5(@NotNull final MultipartFile file) throws NoSuchAlgorithmException, IOException {
        StringBuilder sb = new StringBuilder();
        //生成MD5实例
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        InputStream inputStream = file.getInputStream();
        int available = inputStream.available();
        byte[] bytes = new byte[available];
        md5.update(bytes);
        for (byte by : md5.digest()) {
            //将生成的字节MD5值转换成字符串
            sb.append(String.format("%02X", by));
        }
        return sb;
    }

}
