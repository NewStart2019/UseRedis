package com.example.demo.controller;

import com.example.demo.common.web.AjaxResult;
import com.example.demo.service.FileService;
import com.example.demo.util.FileUtil;
import com.example.demo.util.SaveFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/BigFileUpload")
public class BigFileUploadController {

    @Resource
    private FileService fileService;

    @PostMapping(value = "/IsMD5Exist")
    public AjaxResult<String> bigFileUpload(String fileMd5) {
        AjaxResult<String> isExist = AjaxResult.error("this file is exist");
        isExist.setError(false);
        try {
            boolean md5Exist = fileService.isMd5Exist(fileMd5);
            isExist.setError(md5Exist);
        } catch (Exception e) {
            e.printStackTrace();
            isExist.setError(true);
        }
        return isExist;
    }

    /**
     * 上传大文件
     *
     * @param guid             临时文件名
     * @param md5value         客户端生成md5值
     * @param chunks           分块数
     * @param chunk            分块序号
     * @param id               文件id便于区分
     * @param name             上传文件名
     * @param type             文件类型
     * @param lastModifiedDate 上次修改时间
     * @param size             文件大小
     * @param file             文件本身
     * @return
     */
    @PostMapping(value = "/BigFileUp")
    public String fileUpload(String guid, String md5value, String chunks,
                             String chunk, String id, String name,
                             String type, String lastModifiedDate, long size,
                             MultipartFile file) {
        String fileName;
        try {
            int index;
            String uploadFolderPath = SaveFile.getRealPath() + "/chunk";

            String mergePath = uploadFolderPath + guid + "/";
            String ext = name.substring(name.lastIndexOf("."));

            //判断文件是否分块
            if (chunks != null && chunk != null) {
                index = Integer.parseInt(chunk);
                fileName = index + ext;
                // 将文件分块保存到临时文件夹里，便于之后的合并文件
                SaveFile.saveFile(mergePath, fileName, file);
                // 验证所有分块是否上传成功，成功的话进行合并
                FileUtil.uploaded(md5value, guid, chunk, chunks, uploadFolderPath, fileName, ext, fileService);
            } else {
                fileName = guid + ext;
                //上传文件没有分块的话就直接保存
                SaveFile.saveFile(uploadFolderPath, fileName, file);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "{\"error\":true}";
        }

        return "{jsonrpc = \"2.0\",id = id,filePath = \"/Upload/\" + fileFullName}";
    }


}
