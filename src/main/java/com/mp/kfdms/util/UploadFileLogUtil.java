package com.mp.kfdms.util;

import com.mp.kfdms.domain.FileNode;
import com.mp.kfdms.domain.Folder;
import com.mp.kfdms.domain.User;
import com.mp.kfdms.pojo.FileInfo;

import java.util.Date;
import java.util.UUID;

/**
 * 为后面拆分展示用文件和服务器文件实体做准备
 * 这里为展示用文件的工具类。主要负责处理各关于展示用文件对应的数据表对应的domain的各种操作
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/2/19
 * @Time 20:30
 */
public class UploadFileLogUtil {
    public static FileNode createFileNode(FileInfo fileInfo, Folder folder, User user){
        //创建数据库对象
        FileNode fileNode = new FileNode();
        // UUID作为名称
        fileNode.setFile_encode_name(UUID.randomUUID().toString());
        fileNode.setFile_name(fileInfo.getOriginFileName());
        fileNode.setData_type(fileInfo.getFileType());
        fileNode.setFile_folder_id(folder.getFolder_id());
        fileNode.setFile_owner_id(user.getId());
        fileNode.setFile_owner_name(user.getUsername());
        fileNode.setFile_type(1);// 默认私有
        fileNode.setData_type(fileInfo.getFileType());
        fileNode.setFile_description("");//默认为空
        fileNode.setFile_size(fileInfo.getFileSize());
        fileNode.setFile_md5(fileInfo.getMD5());
        fileNode.setFile_permission(0);

        fileNode.setFile_create_time(new Date(System.currentTimeMillis()));
        return fileNode;
    }
}
