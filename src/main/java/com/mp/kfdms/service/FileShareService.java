package com.mp.kfdms.service;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mp.kfdms.domain.FileNode;
import com.mp.kfdms.domain.FileShareShareLog;
import com.mp.kfdms.domain.Folder;
import com.mp.kfdms.domain.User;
import com.mp.kfdms.enumeration.FileShareShareLogStatusEnum;
import com.mp.kfdms.mapper.FileNodeMapper;
import com.mp.kfdms.mapper.FileShareMapper;
import com.mp.kfdms.mapper.FolderMapper;
import com.mp.kfdms.mapper.UserMapper;
import com.mp.kfdms.pojo.FileShareLinkInfo;
import com.mp.kfdms.pojo.FolderView;
import com.mp.kfdms.pojo.JsonModel;
import com.mp.kfdms.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/2/23
 * @Time 11:30
 */
@Service
public class FileShareService {
    // authCode的有效持续时间
    static long ACCESS_TIME = 60000000;

    @Autowired
    private FileService fileService;

    @Resource
    private FileShareMapper fileShareMapper;

    @Resource
    private FileNodeMapper fileNodeMapper;

    @Resource
    private FolderMapper folderMapper;

    @Resource
    private UserMapper userMapper;

    public JsonModel createLink(User currentUser, FileShareLinkInfo fileShareLinkInfo) {
        JsonModel jsonModel = new JsonModel();
        jsonModel.setMessage("error");
        currentUser = userMapper.findOneByEmail(currentUser);
        boolean shareAuth = UserUtil.checkFileShareAuth(currentUser, fileShareLinkInfo);
        if (shareAuth) {
            FileShareShareLog shareLog = FileShareUtil.getShareLog(currentUser, fileShareLinkInfo);
            if (shareLog != null) {
                // 查找分享文件是否存在
                if (shareLog.getFileId() != -1) {
                    FileNode fileById = fileNodeMapper.getFileById(shareLog.getFileId());
                    if (fileById == null)
                        return jsonModel;
                    shareLog.setFileName(fileById.getFile_name());
                } else if (shareLog.getFolderId() != -1) { // 查找分享文件夹是否存在
                    Folder folderById = folderMapper.getFolderById(shareLog.getFolderId());
                    if (folderById == null)
                        return jsonModel;
                    shareLog.setFileName(folderById.getFolder_name());
                }
                // 文件存在，则先创建记录
                if (fileShareMapper.addShareLog(shareLog) > 0) {
                    // 创建加密网址
                    String shareLink = FileShareUtil.createShareLink(shareLog);
                    if (shareLink != null) {
                        // TODO: 2021/2/24 服务器地址自动获取
                        shareLink = "localhost:8080/DataCloud/s/" + shareLink;
                        shareLog.setStatus(FileShareShareLogStatusEnum.SHARING.ordinal());
                        shareLog.setShareLink(shareLink);
                        if (fileShareMapper.updateShareLog(shareLog) > 0) {
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

    public JsonModel checkShareLink(User currentUser, String shareLinkData) {
        JsonModel jsonModel = new JsonModel();
        if (currentUser == null) { // 未登录，没有权限进入
            jsonModel.setMessage("userAuthError");
            return jsonModel;
        }
        String shareLink = null;
        String accessCode = null;
        try {
            String dncryption = RSAKeyUtil.dncryption(shareLinkData, RSAKeyUtil.getPrivateKey());
            Map map = GsonUtil.instance().fromJson(dncryption, Map.class);
            shareLink = (String) map.get("shareLink");
            accessCode = (String) map.get("accessCode");
        } catch (Exception e) {
            e.printStackTrace();
            jsonModel.setMessage("userAuthError");
            return jsonModel;
        }

        int logId = FileShareUtil.decodeShareLink(shareLink, accessCode);
        if (logId != -1) { // 解密获得了logId
            int updateFlag = 0;
            while (updateFlag < 10) { // 乐观锁，尝试十次。基于版本号的CAS锁保证线程安全
                updateFlag++;
                FileShareShareLog shareLog = fileShareMapper.getShareLogById(logId);
                if (shareLog != null && shareLog.getStatus() == FileShareShareLogStatusEnum.SHARING.ordinal()) { // 链接为分享中
                    Date now = new Date();
                    if (shareLog.getExpiredTime() == null || now.compareTo(shareLog.getExpiredTime()) < 0) { // 未失效
                        // 更新访问次数
                        int visitNum = shareLog.getVisitNum() + 1;
                        if (shareLog.getVisitLimit() != -1 && visitNum > shareLog.getVisitLimit()) { // 是否超限，前端做验证。后端处理多线程情况
                            jsonModel.setMessage("exceedLimit");
                            return jsonModel;
                        }
                        shareLog.setVisitNum(visitNum);
                        // 使用版本号实现乐观锁
                        if (fileShareMapper.updateShareLog(shareLog) > 0) { // 更新成功
                            jsonModel.setData(getEncodeShareCode(currentUser, shareLog));
                            return jsonModel;
                        }
                    } else {
                        // 失效更新状态
                        shareLog.setStatus(FileShareShareLogStatusEnum.EXPIRED.ordinal());
                        fileShareMapper.updateShareLog(shareLog);
                        jsonModel.setMessage("expired");
                        return jsonModel;
                    }
                }
            }

        }
        return jsonModel;
    }

    /**
     * 根据shareLink,accessCode以及用户email确定确实获得了权限
     *
     * @param shareLog
     * @return
     */
    public String getEncodeShareCode(User currentUser, FileShareShareLog shareLog) {
        String code = null;
        try {
            HashMap<String, String> codeMap = new HashMap<>();
            codeMap.put("shareLogId", String.valueOf(shareLog.getShareLogId()));
            codeMap.put("accessCode", shareLog.getAccessCode());
            codeMap.put("userEmail", currentUser.getEmail());
            codeMap.put("checkTime", String.valueOf(Calendar.getInstance().getTimeInMillis()));
            // RSA加密不能超过117bytes，需要分选
//            code = RSAKeyUtil.encryption(codeMap.toString(), RSAKeyUtil.getPublicKey());
            code = AESUtil.encodeBase64(GsonUtil.instance().toJson(codeMap));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    public JsonModel getShareLinkList(User currentUser) {
        JsonModel jsonModel = new JsonModel();
        currentUser = userMapper.findOneByEmail(currentUser);
        if (currentUser != null) {
            List<FileShareShareLog> shareLinkList = fileShareMapper.getShareLogByUserId(currentUser.getId());
            jsonModel.setData(shareLinkList);
            return jsonModel;
        }
        jsonModel.setMessage("error");
        return jsonModel;
    }

    public JsonModel cancelShareLink(User currentUser, int shareLogId) {
        JsonModel jsonModel = new JsonModel();
        if (currentUser == null) { // 用户权限检验
            jsonModel.setMessage("用户无权限");
        }
        FileShareShareLog shareLogById = fileShareMapper.getShareLogById(shareLogId);
        if (shareLogById != null) {
            shareLogById.setStatus(FileShareShareLogStatusEnum.DELETE.ordinal());
            shareLogById.setDeleteTime(new Date());
            if (fileShareMapper.updateShareLog(shareLogById) > 0) {
                jsonModel.setMessage("链接已删除");
            }
        }
        return jsonModel;
    }

    public JsonModel cancelAllShareLink(User currentUser, List<Integer> shareLogIdList) {
        JsonModel jsonModel = new JsonModel();
        for (int shareLogId : shareLogIdList) {
            cancelShareLink(currentUser, shareLogId);
        }
        jsonModel.setMessage("已取消全部链接");
        return jsonModel;
    }

    public JsonModel getSharedFile(String authCode) {
        JsonModel jsonModel = new JsonModel();
        // 从authCode获取必要信息
        try{
            String decodedShareCode = AESUtil.decodeBase64(authCode);
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> EncodeShareCodeMap = GsonUtil.instance().fromJson(decodedShareCode, type);
            int shareLogId = Integer.parseInt(EncodeShareCodeMap.get("shareLogId"));
            String accessCode = EncodeShareCodeMap.get("accessCode");
            String userEmail = EncodeShareCodeMap.get("userEmail");
            String checkTime = EncodeShareCodeMap.get("checkTime");
            // 检验验证时间
            if (Calendar.getInstance().getTimeInMillis() - Long.parseLong(checkTime) > ACCESS_TIME){
                jsonModel.setMessage("code_expired");
                return jsonModel;
            }

            // 获得信息
            FileShareShareLog shareLogById = fileShareMapper.getShareLogById(shareLogId);
            // TODO: 2021/3/1 返回文件信息用于更丰富的展示
//            FolderView shareView = getShareView(shareLogById);
            // 目前只返回shareLog
            jsonModel.setData(shareLogById);
            return jsonModel;
        }catch (Exception e){
            e.printStackTrace();
            jsonModel.setMessage("error");
            return jsonModel;
        }
    }

    public FolderView getShareView(FileShareShareLog shareLog) {
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
        return folderView;
    }

    public String downloadSharedFile(User currentUser, HttpServletRequest request, HttpServletResponse response, String authCode, int shareLogId) {
        JsonModel jsonModel = new JsonModel();
        // 从authCode获取必要信息
        try {
            String decodedShareCode = AESUtil.decodeBase64(authCode);
            Type type = new TypeToken<Map<String, String>>() {
            }.getType();
            Map<String, String> EncodeShareCodeMap = GsonUtil.instance().fromJson(decodedShareCode, type);
            String userEmail = EncodeShareCodeMap.get("userEmail");
            String checkTime = EncodeShareCodeMap.get("checkTime");
            // 检验验证时间
            if (Calendar.getInstance().getTimeInMillis() - Long.parseLong(checkTime) > ACCESS_TIME) {
                return "code_expired";
            }
            // 检查权限
            if(currentUser!=null && currentUser.getEmail().equals(userEmail)){
                FileShareShareLog shareLog = fileShareMapper.getShareLogById(shareLogId);
                if(shareLog.getFolderId()!=-1){
                    return "folder";
                }
                // 下载需要从前端重新起一个请求
                return String.valueOf(shareLog.getFileId());
            }
        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }
        return "error";
    }
}
