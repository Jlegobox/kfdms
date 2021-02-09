package com.mp.kfdms.service;

import com.mp.kfdms.configuration.ConfigurationReader;
import com.mp.kfdms.domain.FileNode;
import com.mp.kfdms.domain.Folder;
import com.mp.kfdms.domain.User;
import com.mp.kfdms.mapper.FileNodeMapper;
import com.mp.kfdms.mapper.FolderMapper;
import com.mp.kfdms.mapper.UserMapper;
import com.mp.kfdms.util.FileNodeUtil;
import com.mp.kfdms.util.RequestUtil;
import com.mp.kfdms.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.List;
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

    @Autowired
    private UserService userService;

    public int getTotalCount(Folder currentFolder) {
        return fileNodeMapper.countFile(currentFolder);
    }


    public String doUploadFile(final MultipartHttpServletRequest request) {
        // 验证登录状态和上传权限
        User currentUser = UserUtil.getUserFromToken(request.getHeader("lg_token"));
        currentUser = userMapper.findOneByEmail(currentUser);
        Boolean upload_permission = true;

        // 获取上传路径（当前文件夹）
        int current_location = RequestUtil.getInt(request, "current_location");
        if (current_location == 0) {
            return "error";
        }
        Folder folder = new Folder();
        folder.setFolder_id(current_location);
        folder = folderMapper.getFolderById(folder.getFolder_id());



        // 新建FileNode对象并从request中获取需要信息
        FileNode fileNode = new FileNode();
        MultiValueMap<String, MultipartFile> multiFileMap = request.getMultiFileMap();
        MultipartFile upload_file = multiFileMap.getFirst("file");
        if (upload_file != null) {
            fileNode.setFile_encode_name(UUID.randomUUID().toString());
            fileNode.setFile_name(upload_file.getOriginalFilename());
            fileNode.setFile_folder_id(folder.getFolder_id());
            fileNode.setFile_owner_id(currentUser.getId());
            fileNode.setFile_owner_name(currentUser.getUsername());
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

    public String downloadFile(HttpServletRequest request, final HttpServletResponse response, String fileId) {
        String fileName = "test.txt";
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", "attachment;fileName="
                + fileName);
        try {
            String path = "C:\\Users\\J\\GitHub\\kfdms\\APPDATA" + "\\" + fileName;
            InputStream inputStream = new FileInputStream(new File(path));

            OutputStream os = response.getOutputStream();
            byte[] b = new byte[2048];
            int length;
            while ((length = inputStream.read(b)) > 0) {
                os.write(b, 0, length);
            }

            // 这里主要关闭。
            os.close();

            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";
    }

    public List<FileNode> listFiles(final HttpServletRequest request, final Folder currentFolder){
        FileNode fileNode = new FileNode();
        fileNode.setFile_folder_id(currentFolder.getFolder_id());
        return fileNodeMapper.getFileByFileFolderId(fileNode.getFile_folder_id());
    }
    public String listFiles(final HttpServletRequest request, final int folderId){
        String returnMsg = "error";
        FileNode fileNode = new FileNode();
        fileNode.setFile_folder_id(folderId);

        fileNodeMapper.getFileByFileFolderId(fileNode.getFile_folder_id());
        return returnMsg;
    }

    public String checkFilePermission(User currentUser,FileNode fileNode){
        return "success";
    }

    /**
     * 文件上传前校验，包括上传权限,文件名和文件是否已上传
     * @param request
     * @param folderId
     * @return
     */
    public String checkUploadFile(final MultipartHttpServletRequest request, final int folderId) {
        String returnMSG = "error";
        // 获取信息
        MultiValueMap<String, MultipartFile> multiFileMap = request.getMultiFileMap();
        MultipartFile uploadFile = multiFileMap.getFirst("file");
        if(uploadFile == null){
            return returnMSG;
        }
        User user = userService.getUserFromToken(request.getHeader("lg_token"));
        // 校验上传权限
        boolean uploadAuth = userService.checkUploadAuth(user);
        if(!uploadAuth)
            return "authError";

        //校验重名
        List<FileNode> files = fileNodeMapper.getFileByFileFolderId(folderId);
        String uploadFilename = uploadFile.getOriginalFilename();
        boolean renameFlag=false;
        for (FileNode file : files) {
            if(file.getFile_name().equals(uploadFilename)){
                renameFlag=true;
                break;
            }
        }
        if(renameFlag){
            String renameFileStrategy = ConfigurationReader.instance().getConf("renameFileStrategy");
            if(!"false".equals(renameFileStrategy)) { // 自动重命名
                String new_fileName="new upload file";
                try {
                    new_fileName = FileNodeUtil.renameFileByStrategy(renameFileStrategy, files, uploadFile);
                }catch (Exception e){
                    return "rename error";
                }
                request.setAttribute("new_fileName", new_fileName);
            }else { //未开启自动重命名,返回提示
                return "duplicateFileName";
            }
        }

        // 校验MD5,加速上传



        return null;
    }


    public String deleteFile(HttpServletRequest request, int fileId) {
        User user = UserUtil.getUserFromToken(request.getHeader("lg_token"));
        boolean auth = userService.checkDeleteAuth();
        if(auth){
            int count = fileNodeMapper.deleteFileById(fileId);
            if(count>1)
                return "success";
            else { // 已经被删除
                FileNode fileById = fileNodeMapper.getFileById(fileId);
                return fileById==null?"success":"error";
            }
        }
        return "error";
    }
}
