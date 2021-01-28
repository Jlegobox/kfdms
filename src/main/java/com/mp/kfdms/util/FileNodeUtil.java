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
    // 使用绝对路径，使用相对路径时，再multiFile.transfer时会出现文件路径找不到的错误
    // TODO: 2021/1/28 使用绝对路径提供自定义文件保存路径的功能，默认初始化为APPDATA
    private static String BASE_URL = "C:\\Users\\J\\GitHub\\kfdms\\APPDATA";

    public FileNode createFileNoe(MultipartFile multipartFile, String path) {
        File file = new File(path);
        return new FileNode();
    }

    public static String getFilePath(FileNode fileNode) {
        return BASE_URL + "\\" + fileNode.getFile_encode_name();
    }

    public static File saveFile(FileNode fileNode, MultipartFile upload_file) {
        // 按照策略保存文件
        File file = new File(getFilePath(fileNode));
        try {
            file.createNewFile();
            if (file.exists()) {
                upload_file.transferTo(file);
            } else {
                return null;
            }
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

    public static String getDataType(String fileName) {
        if (fileName != null) {
            String[] split = fileName.split("\\.");
            if (split.length > 1) {
                return split[split.length - 1];
            }
        }
        return "file";
    }
}
