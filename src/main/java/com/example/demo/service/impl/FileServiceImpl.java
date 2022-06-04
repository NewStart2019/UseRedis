package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.mapper.FileMapper;
import com.example.demo.model.File;
import com.example.demo.service.FileService;
import com.example.demo.util.Md5Util;
import com.example.demo.util.SaveFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    @Override
    public boolean isMd5Exist(String md5) {
        Map<String, Object> parameter = new HashMap<>(1);
        parameter.put("md5", md5);
        List<File> files = baseMapper.selectByMap(parameter);
        return CollectionUtils.isNotEmpty(files);
    }

    @Override
    public boolean fileUpload(MultipartFile file, boolean isImage) {
        String fileName = file.getOriginalFilename();
        String md5;
        try {
            // 如果上传的是图片，判断是否是图片
            if (isImage) {
                ImageInputStream is = ImageIO.createImageInputStream(file);
                //is.close();
            }

            md5 = Md5Util.createMd5(file).toString();
            if (isMd5Exist(md5)) {
                log.error("文件已经上传过");
                return true;
            }

            assert fileName != null;
            String ext = fileName.substring(fileName.lastIndexOf("."));
            fileName = UUID.randomUUID().toString() + ext;
            SaveFile.saveFile(SaveFile.getRealPath(), fileName, file);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        // 保存文件信息，到数据库
        try {
            File temp = new File(fileName, md5, new Date(System.currentTimeMillis()));
            temp.setStatus(0);
            save(temp);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
