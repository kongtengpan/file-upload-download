package com.itktp.service;

import com.itktp.exception.FileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author: ktp
 * @date: 2022/3/30
 */

@Service
public class FileService {

    private Path fileStorageLocation; //文件在本地存储的地址

    //@Value("${file.upload.path}获取yml文件中下载地址
    public FileService(@Value("${file.upload.path}") String path) {
        //toAbsolutePath()返回绝对路径path对象
        //normalize()方法用于从当前路径返回路径，在该路径中消除了所有冗余名称元素
        this.fileStorageLocation = Paths.get(path).toAbsolutePath().normalize();
        try {
            //创建一个新的目录
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            throw new FileException("could not create the directory", e);
        }
    }

    /**
     * @param file 文件名
     * @return 存储文件到系统
     */
    public String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            //检查文件名是否包含无效字符
            if (fileName.contains("..")) {
                throw new FileException("sorry! Filename contains invalid path sequence" + fileName);
            }
            //将文件复制到目标位置(用相同的名称替换现有的文件)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new FileException("could not store file" + fileName + ". please try again!", e);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileException("file not found " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new FileException("file not found " + fileName, e);
        }
    }
}
