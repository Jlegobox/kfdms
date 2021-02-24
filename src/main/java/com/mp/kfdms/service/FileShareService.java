package com.mp.kfdms.service;

import com.mp.kfdms.domain.FileNode;
import com.mp.kfdms.domain.FileShareShareLog;
import com.mp.kfdms.domain.Folder;
import com.mp.kfdms.domain.User;
import com.mp.kfdms.enumeration.FileShareShareLogStatusEnum;
import com.mp.kfdms.mapper.FileNodeMapper;
import com.mp.kfdms.mapper.FileShareMapper;
import com.mp.kfdms.mapper.FolderMapper;
import com.mp.kfdms.pojo.FileShareLinkInfo;
import com.mp.kfdms.pojo.FolderView;
import com.mp.kfdms.pojo.JsonModel;
import com.mp.kfdms.util.FileShareUtil;
import com.mp.kfdms.util.GsonUtil;
import com.mp.kfdms.util.UserUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/2/23
 * @Time 11:30
 */
@Service
public class FileShareService {
    @Resource
    private FileShareMapper fileShareMapper;

    @Resource
    private FileNodeMapper fileNodeMapper;

    @Resource
    private FolderMapper folderMapper;

    public JsonModel createLink(User currentUser, FileShareLinkInfo fileShareLinkInfo) {
        JsonModel jsonModel = new JsonModel();
        jsonModel.setMessage("error");
        boolean shareAuth = UserUtil.checkFileShareAuth(currentUser, fileShareLinkInfo);
        if (shareAuth) {
            FileShareShareLog shareLog = FileShareUtil.getShareLog(currentUser, fileShareLinkInfo);
            if (shareLog != null) {
                // 先创建记录
                if (fileShareMapper.addShareLog(shareLog) > 0){
                    // 查找分享文件是否存在
                    if (shareLog.getFileId() != -1) {
                        FileNode fileById = fileNodeMapper.getFileById(shareLog.getFileId());
                        if (fileById == null)
                            return jsonModel;
                    } else if (shareLog.getFolderId() != -1) { // 查找分享文件夹是否存在
                        Folder folderById = folderMapper.getFolderById(shareLog.getFolderId());
                        if (folderById == null)
                            return jsonModel;
                    }
                    // 创建加密网址
                    String shareLink = FileShareUtil.createShareLink(shareLog);
                    if (shareLink != null) {
                        // TODO: 2021/2/24 服务器地址自动获取
                        shareLink = "localhost:8080/DataCloud/s/"+shareLink;
                        shareLog.setStatus(FileShareShareLogStatusEnum.SHARING.ordinal());
                        shareLog.setShareLink(shareLink);
                        if (fileShareMapper.updateShareLog(shareLog) > 0){
                            jsonModel.setMessage("success");
                            jsonModel.put("shareLink", shareLink);
                            jsonModel.put("accessCode", shareLog.getAccessCode());
                        }
                        return jsonModel;
                    }
                }
            }
        }
        return jsonModel;
    }

    public String checkShareLink(User currentUser, String shareLink, String accessCode) {
        int logId = FileShareUtil.decodeShareLink(shareLink, accessCode);
        if (logId != -1) { // 解密获得了logId
            int updateFlag = 0;
            while (updateFlag < 10) { // 乐观锁，尝试十次。基于版本号的CAS锁保证线程安全
                updateFlag++;
                FileShareShareLog shareLog = fileShareMapper.getShareLogById(logId);
                if (shareLog != null && shareLog.getStatus() == FileShareShareLogStatusEnum.SHARING.ordinal()) { // 链接为分享中
                    Date now = new Date();
                    if (now.compareTo(shareLog.getExpiredTime()) < 0) { // 未失效
                        // 更新访问次数
                        int visitNum = shareLog.getVisitNum() + 1;
                        if (shareLog.getVisitLimit() != -1 && visitNum > shareLog.getVisitLimit()) { // 是否超限，前端做验证。后端处理多线程情况
                            return "exceed_limit";
                        }
                        shareLog.setVisitNum(visitNum);
                        // 使用版本号实现乐观锁
                        if (fileShareMapper.updateShareLog(shareLog) > 0) { // 更新成功
                            return getShareView(shareLog);
                        }
                    } else {
                        // 失效更新状态
                        shareLog.setStatus(FileShareShareLogStatusEnum.EXPIRED.ordinal());
                        fileShareMapper.updateShareLog(shareLog);
                        return "expired";
                    }
                }
            }

        }
        return "error";
    }

    public String getShareView(FileShareShareLog shareLog) {
        FolderView folderView = new FolderView();
        if (shareLog.getFolderId() != -1) {
            Folder folderById = folderMapper.getFolderById(shareLog.getFolderId());
            ArrayList<Folder> folders = new ArrayList<>();
            ArrayList<String> foldersPermission = new ArrayList<>();
            foldersPermission.add("a");
            folders.add(folderById);
            folderView.setFolders(folders);
            folderView.setFoldersPermission(foldersPermission);
        }
        if (shareLog.getFileId() != -1) {
            FileNode fileById = fileNodeMapper.getFileById(shareLog.getFileId());
            ArrayList<FileNode> fileNodes = new ArrayList<>();
            ArrayList<String> fileNodesPermission = new ArrayList<>();
            fileNodes.add(fileById);
            folderView.setFiles(fileNodes);
            folderView.setFilesPermission(fileNodesPermission);
        }
        return GsonUtil.instance().toJson(folderView);
    }
}
