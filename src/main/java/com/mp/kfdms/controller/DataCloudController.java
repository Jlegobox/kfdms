package com.mp.kfdms.controller;

import com.mp.kfdms.domain.FileNode;
import com.mp.kfdms.domain.Folder;
import com.mp.kfdms.domain.User;
import com.mp.kfdms.pojo.FolderView;
import com.mp.kfdms.pojo.PageParam;
import com.mp.kfdms.service.FileService;
import com.mp.kfdms.service.FolderService;
import com.mp.kfdms.service.UserService;
import com.mp.kfdms.util.FolderUtil;
import com.mp.kfdms.util.GsonUtil;
import com.mp.kfdms.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
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
    public UserService userservice;
    @Autowired
    public FolderService folderService;
    @Autowired
    public FileService fileService;

    // 新建文件夹
    @RequestMapping(value = {"/createFolder.ajax"})
    public String createFolder(final HttpServletRequest request, final HttpServletResponse response){
        String returnMsg = folderService.createFolder(request, response);
        return returnMsg;
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
        Folder currentFolder = folderService.getCurrentFolder(currentUser, folderId);
        if(currentFolder == null){
            return returnMsg;
        }else {
            //权限校验
            String permission = folderService.checkFolderPermission(currentUser, currentFolder);
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



    // 上传文件
    @RequestMapping("/doUploadFile.ajax") //使用filter来做编码设定,所以不需要 produces = { CHARSET_BY_AJAX }
    public String doUploadFile(final MultipartHttpServletRequest request){
        fileService.doUploadFile(request);
        return "OK";
    }
    // 下载文件
    @RequestMapping("/downloadFile.do")
    public String downloadFile(final HttpServletRequest request, final HttpServletResponse response, @RequestParam("fileId") final String fileId){
        return fileService.downloadFile(request,response,fileId);
    }

}
