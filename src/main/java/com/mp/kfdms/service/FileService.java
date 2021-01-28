package com.mp.kfdms.service;

import com.mp.kfdms.domain.FileNode;
import com.mp.kfdms.domain.Folder;
import com.mp.kfdms.domain.User;
import com.mp.kfdms.mapper.FileNodeMapper;
import com.mp.kfdms.mapper.FolderMapper;
import com.mp.kfdms.mapper.UserMapper;
import com.mp.kfdms.util.FileNodeUtil;
import com.mp.kfdms.util.RequestUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Date;
import java.util.UUID;

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


    public String doUploadFile(final MultipartHttpServletRequest request) {
        // 获取上传路径（当前文件夹）
        int current_location = RequestUtil.getInt(request, "current_location");
        if (current_location == 0) {
            return "error";
        }
        Folder folder = new Folder();
        folder.setFolder_id(current_location);
        folder = folderMapper.getFolderById(folder);

        // 验证登录状态和上传权限
        User user = new User();
        user.setId(1);
        Boolean upload_permission = true;

        // 新建FileNode对象并从request中获取需要信息
        FileNode fileNode = new FileNode();
        MultiValueMap<String, MultipartFile> multiFileMap = request.getMultiFileMap();
        MultipartFile upload_file = multiFileMap.getFirst("file");
        if (upload_file != null) {
            fileNode.setFile_encode_name(UUID.randomUUID().toString());
            fileNode.setFile_name(upload_file.getOriginalFilename());
            fileNode.setFile_folder_id(folder.getFolder_id());
            fileNode.setFile_owner_id(user.getId());
            fileNode.setFile_type(1);// 默认私有
            fileNode.setData_type(FileNodeUtil.getDataType(upload_file.getOriginalFilename()));
            fileNode.setFile_description("");//默认为空
            fileNode.setFile_size(upload_file.getSize());
            String file_md5 = ""; // 应该来自前端
            fileNode.setFile_md5(file_md5);
            fileNode.setFile_permission(0);

            fileNode.setFile_create_time(new Date(System.currentTimeMillis()));
        } else {
            return "error";
        }

        // TODO: 2021/1/28 文件重复校验
        // 在数据库中储存新建信息并更新文件夹信息
        fileNodeMapper.addNewFile(fileNode);
        // 保存upload_file
        File saved_file = FileNodeUtil.saveFile(fileNode, upload_file);
        // 再次检验
        if (saved_file != null && saved_file.exists()) {
            return "success";
        } else {
            return "error";
        }
    }
}
