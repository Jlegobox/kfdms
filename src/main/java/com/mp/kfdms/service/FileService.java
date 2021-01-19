package com.mp.kfdms.service;

import com.mp.kfdms.domain.FileNode;
import com.mp.kfdms.domain.Folder;
import com.mp.kfdms.domain.User;
import com.mp.kfdms.mapper.FileNodeMapper;
import com.mp.kfdms.mapper.FolderMapper;
import com.mp.kfdms.mapper.UserMapper;
import com.mp.kfdms.util.FileNodeUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Date;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/14
 * @Time 14:42
 */
@Service
public class FileService {
    @Resource
    private FileNodeMapper fileNodeMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private FolderMapper folderMapper;


    public String doUploadFile(final HttpServletRequest request, final HttpServletResponse response, final MultipartFile upload_file){
        // 获取上传路径（当前文件夹）
        String current_location = (String) request.getAttribute("current_location");
        Folder folder = new Folder();
        folder.setFolder_name(current_location);
        folderMapper.getFolderById(folder);

        // 验证登录状态和上传权限
        User user = new User();
        user.setId(1);
        Boolean upload_permission = true;

        // 新建FileNode对象并从request中获取需要信息
        FileNode fileNode = new FileNode();
        fileNode.setFile_owner_id(user.getId());
        fileNode.setFile_name(upload_file.getOriginalFilename());
        fileNode.setFile_folder_id(folder.getFolder_id());
        fileNode.setFile_create_time(new Date(System.currentTimeMillis()));

        // 在数据库中储存新建信息并更新文件夹信息
        fileNodeMapper.addNewFile(fileNode);
        // 保存upload_file
        File saved_file = FileNodeUtil.saveFile(fileNode, upload_file);
        if (saved_file.exists()){
            return "OK";
        }else {
            return "Error";
        }
    }
}
