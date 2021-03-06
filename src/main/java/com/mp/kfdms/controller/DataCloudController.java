package com.mp.kfdms.controller;

import com.mp.kfdms.annotation.CurrentUser;
import com.mp.kfdms.domain.FileNode;
import com.mp.kfdms.domain.Folder;
import com.mp.kfdms.domain.User;
import com.mp.kfdms.pojo.FileInfo;
import com.mp.kfdms.pojo.FolderView;
import com.mp.kfdms.pojo.PageParam;
import com.mp.kfdms.service.FileService;
import com.mp.kfdms.service.FolderService;
import com.mp.kfdms.service.UserService;
import com.mp.kfdms.util.FolderUtil;
import com.mp.kfdms.util.GsonUtil;
import com.mp.kfdms.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/21
 * @Time 10:40
 */
@RestController()
@RequestMapping("/DataCloud")
public class DataCloudController {
    @Autowired
    public UserService userService;
    @Autowired
    public FolderService folderService;
    @Autowired
    public FileService fileService;

    //返回root文件夹id
    @RequestMapping("getDeskFolderId.ajax")
    public String getDeskFolderId(final HttpServletRequest request){
        User currentUser = null;
        try{
            currentUser = UserUtil.getUserFromToken(request.getHeader("lg_token"));
            currentUser = userService.getUserByEmail(currentUser.email);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(currentUser == null)
        {
            return "error";
        }
        Folder baseFolderByUser = folderService.getBaseFolderByUser(currentUser);
        return GsonUtil.instance().toJson(baseFolderByUser.getFolder_id());
    }

    // 新建文件夹
    @RequestMapping(value = {"/createFolder.ajax"})
    public String createFolder(final HttpServletRequest request){
        String returnMsg = "error";
        returnMsg = folderService.createFolder(request);
        return returnMsg;
    }

    @RequestMapping("getFolderInfo.ajax")
    public String getFolderInfo(final HttpServletRequest request,final int folderId){
        User currentUser = UserUtil.getUserFromToken(request.getHeader("lg_token"));
        Folder currentFolder = folderService.getCurrentFolder(currentUser, folderId);
        return GsonUtil.instance().toJson(currentFolder);
    }

    @RequestMapping("modifyFolder.ajax")
    public String modifyFolder(final HttpServletRequest request, final int folderId){
        folderService.modifyFolder(request,folderId);
        return "success";
    }

    @RequestMapping("getFileInfo.ajax")
    public String getFileInfo(final HttpServletRequest request,final int fileId){

        FileNode fileNode = fileService.getFileInfo(request,fileId);
        return GsonUtil.instance().toJson(fileNode);
    }

    @RequestMapping("modifyFile.ajax")
    public String modifyFile(final HttpServletRequest request, final int fileId){
        fileService.modifyFile(request, fileId);
        return "success";
    }

    /**
     * 删除文件or文件夹
     * @param request
     * @param fileId
     * @param fileType
     * @return
     */
    @RequestMapping("deleteFile.ajax")
    public String deleteFile(@CurrentUser User currentUser, final HttpServletRequest request, final int fileId, final int fileType){
        String returnMSG = "error";
        if(fileType == 1){ // 文件夹
            returnMSG = folderService.deleteFolder(currentUser,request, fileId);
        }else{
            returnMSG = fileService.deleteFile(currentUser,request, fileId);
        }
        return returnMSG;
    }

    // 获得导航栏数据
    @RequestMapping("/getNavigation.ajax")
    public String getNavigation(final HttpServletRequest request, @RequestParam("folderId") final int folderId){
        List<Folder> parentFolders = folderService.getParentFolder(folderId);
        return GsonUtil.instance().toJson(parentFolders);
    }

    // 加载文件夹内容
    @RequestMapping("/loadFolder.ajax")
    public String loadFolder(final HttpServletRequest request, @RequestParam("folderId") int folderId, PageParam pageParam){
        // TODO: 2021/1/29 添加pageParam
        String returnMsg="error";
        // 文件夹权限判断
        User currentUser = UserUtil.getUserFromToken(request.getHeader("lg_token"));
        Folder currentFolder = null;
        if(folderId!=0){
            currentFolder = folderService.getCurrentFolder(currentUser, folderId);
            if(currentFolder == null){
                return returnMsg;
            }else {
                //权限校验
                String permission = folderService.checkFolderPermission(currentUser, currentFolder);
            }
        }else {
            currentFolder = new Folder();
            currentFolder.setFolder_id(0);
            String permission = "success";
        }

        // 新建文件夹视图
        FolderView folderView = new FolderView();
        // 找到文件夹内所有数据的数量
        int totalCount = folderService.getTotalCount(currentFolder);
        pageParam.setTotalCount(totalCount);
        pageParam.setTotalPage((int) Math.ceil(totalCount/pageParam.getPageSize()));
        folderView.setPageParam(pageParam);

        // 文件夹内文件夹逻辑
        List<Folder> folders = folderService.listFolders(request, currentFolder);
        ArrayList<Folder> checkedFolders = new ArrayList<>();
        ArrayList<String> foldersPermission = new ArrayList<>();
        for (Folder folder : folders) { //文件夹权限校验
            String permission = folderService.checkFolderPermission(currentUser, folder);
            // TODO: 2021/1/29 对于不需要展示的，不应当传输至前端，节约带宽
            checkedFolders.add(folder);
            foldersPermission.add(permission);
        }
        folderView.setFolders(checkedFolders);
        folderView.setFoldersPermission(foldersPermission);
        // 文件夹逻辑类似
        List<FileNode> fileNodes = fileService.listFiles(request, currentFolder);
        ArrayList<FileNode> checkedFiles = new ArrayList<>();
        ArrayList<String> filesPermission = new ArrayList<>();
        for (FileNode fileNode : fileNodes) {
            String permission = fileService.checkFilePermission(currentUser, fileNode);
            checkedFiles.add(fileNode);
            filesPermission.add(permission);
        }
        folderView.setFiles(checkedFiles);
        folderView.setFilesPermission(filesPermission);

        return GsonUtil.instance().toJson(folderView);
    }

    // 加载文件夹公开内容用于FileSquare
    @RequestMapping("/loadFolderPublic.ajax")
    public String loadFolderPublic(final HttpServletRequest request, @RequestParam("folderId") int folderId, PageParam pageParam){
        String returnMsg="error";
        // 文件夹权限判断
        User currentUser = UserUtil.getUserFromToken(request.getHeader("lg_token"));
        Folder currentFolder = null;
        if(folderId!=0){
            currentFolder = folderService.getCurrentFolder(currentUser, folderId);
            if(currentFolder == null){
                return returnMsg;
            }else {
                //权限校验
                String permission = folderService.checkFolderPermission(currentUser, currentFolder);
            }
        }else {
            currentFolder = new Folder();
            currentFolder.setFolder_id(0);
            String permission = "success";
        }

        // 新建文件夹视图
        FolderView folderView = new FolderView();
        // 找到文件夹内所有数据的数量
        int totalCount = folderService.getTotalCount(currentFolder);
        pageParam.setTotalCount(totalCount);
        pageParam.setTotalPage((int) Math.ceil(totalCount/pageParam.getPageSize()));
        folderView.setPageParam(pageParam);

        // 文件夹内文件夹逻辑
        List<Folder> folders = folderService.listFolders(request, currentFolder);
        ArrayList<Folder> checkedFolders = new ArrayList<>();
        ArrayList<String> foldersPermission = new ArrayList<>();
        for (Folder folder : folders) { //文件夹权限校验
            String permission = folderService.checkFolderPermission(currentUser, folder);
            if(folder.getFolder_type() == 0){
                continue;
            }
            checkedFolders.add(folder);
            foldersPermission.add(permission);
        }
        folderView.setFolders(checkedFolders);
        folderView.setFoldersPermission(foldersPermission);
        // 文件夹逻辑类似
        List<FileNode> fileNodes = fileService.listFiles(request, currentFolder);
        ArrayList<FileNode> checkedFiles = new ArrayList<>();
        ArrayList<String> filesPermission = new ArrayList<>();
        for (FileNode fileNode : fileNodes) {
            String permission = fileService.checkFilePermission(currentUser, fileNode);
            if(fileNode.getFile_type()==0){
                continue;
            }
            checkedFiles.add(fileNode);
            filesPermission.add(permission);
        }
        folderView.setFiles(checkedFiles);
        folderView.setFilesPermission(filesPermission);

        return GsonUtil.instance().toJson(folderView);
    }


    // 上传文件
    @RequestMapping("/doUploadFile.ajax") //使用filter来做编码设定,所以不需要 produces = { CHARSET_BY_AJAX }
    public String doUploadFile(final MultipartHttpServletRequest request, int folderId){
        fileService.doUploadFile(request,folderId);
        return "OK";
    }
    // 下载文件
    @RequestMapping("/downloadFile.do")
    public String downloadFile(final HttpServletRequest request, final HttpServletResponse response, @RequestParam("fileId") final int fileId){
        String returnMSG = "error";
        try {
            returnMSG = fileService.downloadFile(request, response, fileId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnMSG;
    }

    // 上传前校验
    @RequestMapping("/checkUploadFile.ajax")
    public String checkUploadFile(final HttpServletRequest request, final int folderId){
        String returnMSG="error";
        // todo 未来完成FileInfo的自定义注入，基于SpringMVC自定义参数解析器

        returnMSG = fileService.checkUploadFile(request, folderId);
        return returnMSG;
    }
    // 分片校验
    @RequestMapping("/checkUploadFileSlice.ajax")
    public String checkUploadFileSlice(final HttpServletRequest request){
        boolean existFlag = fileService.checkUploadFileSlice(request);
        if(existFlag){
            return "exists";
        }
        return "permit";
    }

    @RequestMapping("/completeUploadFile.ajax")
    public String completeUploadFile(final HttpServletRequest request, final int folderId){
        return fileService.completeUploadFile(request, folderId);
    }

    @RequestMapping("/s/{shareLink}")
    public String turnToCheckShareLink(HttpServletResponse response, @PathVariable String shareLink) throws IOException {
        // TODO: 2021/2/25 了解多种重定向带参方式
        response.sendRedirect("/DataCloud/Share/CheckShareLink.html?shareLink="+shareLink);
        return "success";
    }

}
