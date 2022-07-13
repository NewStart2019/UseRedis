package com.example.demo.util;

import com.example.demo.model.UploadInfo;
import com.example.demo.service.FileService;

import java.io.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileUtil {

    private final static List<UploadInfo> uploadInfoList = new ArrayList<>();

    /**
     * 深拷贝
     *
     * @return 深拷贝得到的新实例
     */
    public static Object deepClone(final Object object) throws IOException, ClassNotFoundException {
        // 序列化
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(object);
        // 反序列化
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        return ois.readObject();
    }


    /**
     * 检查是否所有块都已经上传
     *
     * @param md5    md5
     * @param chunks 总的块数
     * @return boolean值
     */
    public static boolean isAllUploaded(final String md5,
                                        final String chunks) {
        int size = (int) uploadInfoList.stream().filter(item -> md5 == null || md5.equals(item.getMd5())).distinct().count();
        boolean bool = (size == Integer.parseInt(chunks));
        if (size == Integer.parseInt(chunks)) {
            synchronized (uploadInfoList) {
                uploadInfoList.removeIf(item -> Objects.equals(item.getMd5(), md5));
            }
        }
        return bool;
    }

    /**
     * 从stream中保存文件
     *
     * @param inputStream inputStream
     * @param filePath    保存路径
     * @throws IOException 异常 抛异常代表失败了
     */
    public static void saveStreamToFile(final InputStream inputStream,
                                        final String filePath) throws IOException {
        /*创建输出流，写入数据，合并分块*/
        OutputStream outputStream = new FileOutputStream(filePath);
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            outputStream.close();
            inputStream.close();
        }
    }

    /**
     * 删除指定文件夹
     *
     * @param folderPath 文件夹路径
     * @return 是否删除成功
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static boolean deleteFolder(final String folderPath) {
        File dir = new File(folderPath);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                try {
                    file.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return dir.delete();
    }

    /**
     * @param chunksNumber     块号
     * @param ext              扩张名
     * @param guid             随机生成的文件名
     * @param uploadFolderPath 上传文件路径
     * @throws IOException 归并失败
     */
    public static void mergeFile(final int chunksNumber,
                                 final String ext,
                                 final String guid,
                                 final String uploadFolderPath)
            throws IOException {
        /*合并输入流*/
        String mergePath = uploadFolderPath + guid + "/";
        SequenceInputStream s;
        InputStream s1 = new FileInputStream(mergePath + 0 + ext);
        InputStream s2 = new FileInputStream(mergePath + 1 + ext);
        s = new SequenceInputStream(s1, s2);
        for (int i = 2; i < chunksNumber; i++) {
            InputStream s3 = new FileInputStream(mergePath + i + ext);
            s = new SequenceInputStream(s, s3);
        }

        //通过输出流向文件写入数据
        saveStreamToFile(s, uploadFolderPath + guid + ext);

        //删除保存分块文件的文件夹
        deleteFolder(mergePath);
    }

    /**
     * 保存上传的块如果上传成功则和合并块
     *
     * @param md5         MD5
     * @param guid        随机生成的文件名
     * @param chunk       文件分块序号
     * @param chunks      文件分块数
     * @param fileName    文件名
     * @param ext         文件后缀名
     * @param fileService fileService
     */
    public static void uploaded(final String md5, final String guid,
                                final String chunk, final String chunks,
                                final String uploadFolderPath, final String fileName,
                                final String ext, final FileService fileService)
            throws IOException {
        synchronized (uploadInfoList) {
            uploadInfoList.add(new UploadInfo(md5, chunks, chunk, uploadFolderPath, fileName, ext));
        }
        boolean allUploaded = isAllUploaded(md5, chunks);
        int chunksNumber = Integer.parseInt(chunks);

        // 所有的已经上传则归并
        if (allUploaded) {
            mergeFile(chunksNumber, ext, guid, uploadFolderPath);
            fileService.save(new com.example.demo.model.File(guid + ext, md5, new Date(System.currentTimeMillis())));
        }
    }
}
