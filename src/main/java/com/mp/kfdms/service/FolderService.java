package com.mp.kfdms.service;

import com.mp.kfdms.domain.Folder;
import com.mp.kfdms.domain.User;
import com.mp.kfdms.mapper.FolderMapper;
import com.mp.kfdms.mapper.UserMapper;
import com.mp.kfdms.util.FolderUtil;
import com.mp.kfdms.util.GsonUtil;
import com.mp.kfdms.util.RequestUtil;
import com.mp.kfdms.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/11
 * @Time 17:50
 */
@Service
public class FolderService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private FolderMapper folderMapper;

    @Autowired
    private FileService fileService;

    public Folder wrapFolder(final HttpServletRequest request){
        Folder folder = new Folder();
        folder.setFolder_id(RequestUtil.getInt(request, "folder_id"));
        folder.setFolder_name(request.getParameter("folder_name"));
        folder.setFolder_parent_id(RequestUtil.getInt(request,"folder_parent_id"));

        User login_info = UserUtil.getUserFromToken(request.getHeader("lg_token"));
        User oneByEmail = userMapper.findOneByEmail(login_info);
        folder.setFolder_owner_id(oneByEmail.getId());

        folder.setFolder_type(RequestUtil.getInt(request,"folder_type"));
        folder.setIs_private(RequestUtil.getInt(request,"is_private"));
        folder.setFolder_size(RequestUtil.getInt(request,"folder_size"));
        folder.setFolder_max_size(RequestUtil.getInt(request,"folder_max_size"));
        folder.setFolder_description(request.getParameter("description"));
        return folder;
    }

    public String createBaerFolder(User user){
        Folder folder = new Folder();
        folder.setFolder_name(user.getUsername());
        folder.setFolder_parent_id(0); // 0为底文件
        folder.setFolder_owner_id(user.getId());
        folder.setFolder_type(0); // 0为私有
        folder.setFolder_size(0);
        folder.setFolder_max_size(FolderUtil.DEFAULT_MAX_SIZE);
        int count = folderMapper.createFolder(folder);
        if(count>0){
            return "success";
        }else {
            return "error";
        }
    }

    public Folder getBaseFolderByUser(User user){
        if(user.getId() == 0){
            user = userMapper.findOneByEmail(user);
        }
        Folder baseFolderByUser = folderMapper.getBaseFolderByUser(user);
        return baseFolderByUser;
    }

    public String createFolder(final HttpServletRequest request, final HttpServletResponse response){
        Folder folder = wrapFolder(request);
        int count = folderMapper.createFolder(folder);
        if(count>0){
            return "success";
        }else {
            return "error";
        }
    }

    public List<Folder> listFolders(final HttpServletRequest request, Folder currentFolder){
        return folderMapper.getFolderByFolderParentId(currentFolder.getFolder_id());
    }
    public String listFolders(final HttpServletRequest request,int folder_id){
        List<Folder> folders = folderMapper.getFolderByFolderParentId(folder_id);
        return GsonUtil.instance().toJson(folders);
    }

    public Folder getCurrentFolder(User currentUser, int folderId) {
        Folder currentFolder = new Folder();
        currentFolder.setFolder_id(folderId);
        currentFolder = folderMapper.getFolderById(currentFolder);
        if(currentFolder == null){ // 找不到文件
            currentFolder = getBaseFolderByUser(currentUser);
        }
        return currentFolder;
    }
    public String checkFolderPermission(User currentUser, Folder folder){
        return "success";
    }

    public List<Folder> getParentFolder(int folderId) {
        Folder currentFolder = new Folder();
        currentFolder.setFolder_id(folderId);
        currentFolder = folderMapper.getFolderById(currentFolder);
        if(currentFolder == null)
            return null;
        ArrayList<Folder> folders = new ArrayList<Folder>();
        folders.add(currentFolder);
        while(currentFolder.getFolder_parent_id() != 0){
            currentFolder = folderMapper.getParentFolderByFolderParentId(currentFolder.getFolder_parent_id());
            if(currentFolder == null)
                break;
            folders.add(currentFolder);
        }
        return folders;
    }

    public int getTotalCount(Folder currentFolder) {
        int i = folderMapper.countFolder(currentFolder);
        int j = fileService.getTotalCount(currentFolder);
        return i+j;
    }
}
