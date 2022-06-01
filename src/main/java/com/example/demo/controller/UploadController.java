package com.example.demo.controller;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

@RestController
public class UploadController {

    private final static String utf8 = "utf-8";

    @Value("${server.tomcat.basedir}")
    private String baseDir;


    @PostMapping("/up")
    @ApiOperation("断点续传实现")
    public void upload(HttpServletRequest request, HttpServletResponse response) {
        response.setCharacterEncoding(utf8);
        //长传时候会有多个分片，需要记录当前为那个分片
        Integer schunk = null;
        //总分片数
        Integer schunks = null;
        //名字
        String name = null;
        //文件目录
        String path = baseDir;
        BufferedOutputStream os = null;
        try {
            //设置缓冲区大小  先读到内存里在从内存写
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(1024);
            factory.setRepository(new File(path));
            //解析
            ServletFileUpload upload = new ServletFileUpload(factory);
            //设置单个大小与最大大小
            upload.setFileSizeMax(5L * 1024L * 1024L * 1024L);
            upload.setSizeMax(10L * 1024L * 1024L * 1024L);
            List<FileItem> items = upload.parseRequest(request);
            for (FileItem item : items) {
                if (item.isFormField()) {
                    //获取分片数赋值给遍量
                    if ("chunk".equals(item.getFieldName())) {
                        schunk = Integer.parseInt(item.getString(utf8));
                    }
                    if ("chunks".equals(item.getFieldName())) {
                        schunks = Integer.parseInt(item.getString(utf8));
                    }
                    if ("name".equals(item.getFieldName())) {
                        name = item.getString(utf8);
                    }
                }
            }
            //取出文件基本信息后
            for (FileItem item : items) {
                if (!item.isFormField()) {
                    //有分片需要临时目录
                    String temFileName = name;
                    if (name != null) {
                        if (schunk != null) {
                            temFileName = schunk + "_" + name;
                        }
                        //判断文件是否存在
                        File temfile = new File(path, temFileName);
                        //断点续传  判断文件是否存在，若存在则不传
                        if (!temfile.exists()) {
                            item.write(temfile);
                        }
                    }
                }
            }
            //文件合并  当前分片为最后一个就合并
            if (schunk != null && schunk.intValue() == schunks.intValue() - 1) {
                File tempFile = new File(path, name);
                os = new BufferedOutputStream(new FileOutputStream(tempFile));
                //根据之前命名规则找到所有分片
                for (int i = 0; i < schunks; i++) {
                    File file = new File(path, i + "_" + name);
                    //并发情况 需要判断所有  因为可能最后一个分片传完，之前有的还没传完
                    while (!file.exists()) {
                        //不存在休眠100毫秒后在从新判断
                        Thread.sleep(100);
                    }
                    //分片存在  读入数组中
                    byte[] bytes = FileUtils.readFileToByteArray(file);
                    os.write(bytes);
                    os.flush();
                    file.delete();
                }
                os.flush();
            }
            response.getWriter().write("上传成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @GetMapping("download")
    @ApiOperation("文件分片下载")
    public void downloadFile(HttpServletRequest request, HttpServletResponse response) {
        response.setCharacterEncoding(utf8);
        //定义文件路径
        File file = new File(baseDir);
        InputStream is = null;
        OutputStream os = null;
        try {
            //分片下载
            long fSize = file.length();//获取长度
            response.setContentType("application/x-download");
            String fileName = URLEncoder.encode(file.getName(), utf8);
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
            //根据前端传来的Range  判断支不支持分片下载
            response.setHeader("Accept-Range", "bytes");
            //获取文件大小
            response.setHeader("fSize", String.valueOf(fSize));
            response.setHeader("fName", fileName);
            //定义断点
            long pos = 0;
            long last = fSize - 1;
            long sum = pos;
            //判断前端需不需要分片下载
            if (null != request.getHeader("Range")) {
                response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                String numRange = request.getHeader("Range").replaceAll("bytes=", "");
                String[] strRange = numRange.split("-");
                if (strRange.length == 2) {
                    pos = Long.parseLong(strRange[0].trim());
                    last = Long.parseLong(strRange[1].trim());
                    //若结束字节超出文件大小 取文件大小
                    if (last > fSize - 1) {
                        last = fSize - 1;
                    }
                } else {
                    //若只给一个长度  开始位置一直到结束
                    pos = Long.parseLong(numRange.replaceAll("-", "").trim());
                }
            }
            long rangeLenght = last - pos + 1;
            String contentRange = new StringBuilder("bytes").append(pos).append("-").append(last).append("/").append(fSize).toString();
            response.setHeader("Content-Range", contentRange);
            response.setHeader("Content-Lenght", String.valueOf(rangeLenght));
            os = new BufferedOutputStream(response.getOutputStream());
            is = new BufferedInputStream(new FileInputStream(file));
            is.skip(pos);//跳过已读的文件
            byte[] buffer = new byte[1024];
            int lenght = 0;
            //相等证明读完
            while (sum < rangeLenght) {
                lenght = is.read(buffer, 0, (rangeLenght - sum) <= buffer.length ? (int) (rangeLenght - sum) : buffer.length);
                sum = sum + lenght;
                os.write(buffer, 0, lenght);

            }
            System.out.println("下载完成");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
