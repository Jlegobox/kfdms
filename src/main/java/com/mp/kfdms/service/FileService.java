package com.mp.kfdms.service;

import com.mp.kfdms.configuration.ConfigurationReader;
import com.mp.kfdms.domain.FileNode;
import com.mp.kfdms.domain.Folder;
import com.mp.kfdms.domain.User;
import com.mp.kfdms.mapper.FileNodeMapper;
import com.mp.kfdms.mapper.FolderMapper;
import com.mp.kfdms.mapper.UserMapper;
import com.mp.kfdms.pojo.FileInfo;
import com.mp.kfdms.util.*;
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


    public String doUploadFile(final MultipartHttpServletRequest request, int folderId) {
        // 获得上传的文件
        MultiValueMap<String, MultipartFile> multiFileMap = request.getMultiFileMap();
        MultipartFile upload_file = multiFileMap.getFirst("file");
        // 获得此次分片的fileInfo
        FileInfo fileInfo = RequestUtil.collectFileInfo(request);
        FileUtil.saveSlice(upload_file,fileInfo);

        return "success";
    }

    public String completeUploadFile(HttpServletRequest request, int folderId){
        // 验证登录状态和上传权限
        User currentUser = UserUtil.getUserFromToken(request.getHeader("lg_token"));
        currentUser = userMapper.findOneByEmail(currentUser);
        if(currentUser == null)
            return "authError";

        // 从request中获取MD5
        FileInfo fileInfo = RequestUtil.collectFileInfo(request);

        // 获取上传路径（当前文件夹）
        if (folderId == 0) {
            return "error";
        }
        Folder folder = new Folder();
        folder.setFolder_id(folderId);
        folder = folderMapper.getFolderById(folder.getFolder_id());
        if(folder == null)
            return "error";

        // 根据文件的MD5获取上传文件的FileNode对象
        FileInfo localFileInfo = null;
        try{
            localFileInfo = FileUtil.getFileInfo(fileInfo);
            if(localFileInfo == null)
                return "error";
        }catch (Exception e){
            e.printStackTrace();
        }

        // 判断是否需要合并
        File fileEntity = FileUtil.getFileEntity(localFileInfo);
        if(fileEntity == null){
            fileEntity = FileUtil.mergeSlice(fileInfo);
            if(fileEntity == null){
                return "error";
            }
        }

        FileNode fileNode = UploadFileLogUtil.createFileNode(localFileInfo, folder, currentUser);
        // 在数据库中储存新建文件信息并更新文件夹信息
        fileNodeMapper.addNewFile(fileNode);

        FileUtil.deleteSliceDir(localFileInfo);
        return "success";
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
    public String checkUploadFile(final HttpServletRequest request, final int folderId) {
        // 获取信息
        FileInfo fileInfo = RequestUtil.collectFileInfo(request);

        User user = userService.getUserFromToken(request.getHeader("lg_token"));
        user = userMapper.findOneByEmail(user);

        // 校验上传权限
        boolean uploadAuth = userService.checkUploadAuth(user);
        if(!uploadAuth)
            return "authError";

        //校验重名
        List<FileNode> files = fileNodeMapper.getFileByFileFolderId(folderId);
        String uploadFilename = fileInfo.getOriginFileName();
        boolean renameFlag=false;
        for (FileNode file : files) {
            if(file.getFile_name().equals(uploadFilename)){
                renameFlag=true;
                break;
            }
        }
        if(renameFlag){
            String renameFileStrategy = ConfigurationReader.instance().getConf("renameFileStrategy");
            if(renameFileStrategy != null && !"true".equals(renameFileStrategy)) { // 自动重命名
                String new_fileName="new upload file";
                try {
                    new_fileName = FileUtil.renameFileByStrategy(renameFileStrategy, files, fileInfo);
                }catch (Exception e){
                    return "rename error";
                }
                request.setAttribute("new_fileName", new_fileName);
            }else { //未开启自动重命名,返回提示
                return "duplicateFileName";
            }
        }

        // 校验MD5,秒传功能
        List<FileNode> fileByMD5 = fileNodeMapper.getFileByMD5(fileInfo.getMD5());
        if(fileByMD5 != null && fileByMD5.size()>0){
            // 保存文件
            boolean saved = quickSave(fileInfo, user, folderId);
            return saved?"exists":"permit";
        }
        return "permit";
    }

    public boolean quickSave(FileInfo fileInfo, User user, int folderId){
        // 秒传功能，直接进行数据库数据的更新
        // 更新失败，则按照正常流程重新上传
        // 创建文件夹
        File sliceDirByMD5 = FileUtil.getSliceDirByMD5(fileInfo.getMD5());
        try {
            FileUtil.refreshFileInfo(fileInfo);
            Folder folder = new Folder();
            folder.setFolder_id(folderId);
            FileNode fileNode = UploadFileLogUtil.createFileNode(fileInfo, folder, user);
            fileNodeMapper.addNewFile(fileNode);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean checkUploadFileSlice(HttpServletRequest request) {
        FileInfo fileInfo = RequestUtil.collectFileInfo(request);
        String fileName = fileInfo.getCurrentChunk() + ".slice";
        File folderByMD5 = FileUtil.getSliceDirByMD5(fileInfo.getMD5());
        if(folderByMD5!=null){
            File sliceFile = new File(folderByMD5.getPath() + File.separator + fileName);
            if(sliceFile.exists()){
                try{
                    String md5 = MD5Util.calMD5(sliceFile);
                    return fileInfo.getCurrentChunkMD5().equals(md5);
                }catch (IOException e){
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
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
