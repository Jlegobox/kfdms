package com.mp.kfdms.util;

import com.mp.kfdms.domain.FileNode;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/14
 * @Time 14:40
 */
public class FileNodeUtil {
    public FileNode createFileNoe(MultipartFile multipartFile, String path){
        File file = new File(path);
        return new FileNode();
    }

    public static File saveFile(FileNode fileNode, MultipartFile upload_file){
        // 按照策略保存文件
        File file = new File(fileNode.getFile_path());
        try {
            upload_file.transferTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static String getMD5(File file) throws IOException {
        // 计算文件MD5值
        String md5 = DigestUtils.md5DigestAsHex(new FileInputStream(file));
        return md5;
    }
}
