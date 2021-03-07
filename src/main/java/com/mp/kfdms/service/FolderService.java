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
    private static String SERVER_ERROR = "error"; //
    private static String SUCESS = "sucess";
    @Resource
    private UserMapper userMapper;

    @Resource
    private FolderMapper folderMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    public Folder wrapFolder(final HttpServletRequest request) {
        // 获得用户信息
        User login_info = UserUtil.getUserFromToken(request.getHeader("lg_token"));
        User oneByEmail = userMapper.findOneByEmail(login_info);

        Folder folder = new Folder();
        folder.setFolder_id(RequestUtil.getInt(request, "folderId"));
        folder.setFolder_name(RequestUtil.getStr(request, "folderName"));
        int folder_parent_id = RequestUtil.getInt(request, "parentFolderId");
        folder.setFolder_parent_id(folder_parent_id);


        folder.setFolder_owner_id(oneByEmail.getId());
        folder.setFolder_owner_name(oneByEmail.getUsername());

        folder.setFolder_type(RequestUtil.getInt(request, "folderType"));
        folder.setIs_private(RequestUtil.getInt(request, "isPrivate"));
        folder.setFolder_size(RequestUtil.getInt(request, "folderSize"));
        folder.setFolder_max_size(RequestUtil.getInt(request, "folderMaxSize"));
        folder.setFolder_description(RequestUtil.getStr(request, "folderDescription"));
        return folder;
    }

    public Folder createBaseFolder(User user) {
        Folder folder = new Folder();
        folder.setFolder_name(user.getUsername());
        folder.setFolder_parent_id(0); // 0为底文件
        folder.setFolder_owner_id(user.getId());
        folder.setFolder_owner_name(user.getUsername());
        folder.setFolder_type(0); // 0为私有
        folder.setFolder_size(0);
        folder.setFolder_description("");
        folder.setFolder_max_size(FolderUtil.DEFAULT_MAX_SIZE);
        int count = folderMapper.createFolder(folder);
        if (count > 0) {
            return folder;
        } else {
            return null;
        }
    }

    // 根据传入的user id，返回root文件夹
    public Folder getBaseFolderByUser(User user) {
        Folder baseFolderByUser = folderMapper.getBaseFolderByUser(user);
        return baseFolderByUser;
    }

    public String createFolder(final HttpServletRequest request) {
        Folder folder = wrapFolder(request);
        int count = folderMapper.createFolder(folder);
        if (count > 0) {
            return "success";
        } else {
            return "error";
        }
    }

    public List<Folder> listFolders(final HttpServletRequest request, Folder currentFolder) {
        return folderMapper.getFolderByFolderParentId(currentFolder.getFolder_id());
    }

    public String listFolders(final HttpServletRequest request, int folder_id) {
        List<Folder> folders = folderMapper.getFolderByFolderParentId(folder_id);
        return GsonUtil.instance().toJson(folders);
    }

    public Folder getCurrentFolder(User currentUser, int folderId) {
        Folder currentFolder = null; // 出错则直接返回null
        try {
            currentFolder = folderMapper.getFolderById(folderId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentFolder;
    }

    public String checkFolderPermission(User currentUser, Folder folder) {
        return "success";
    }

    // 返回父文件夹组（包括自己）
    public List<Folder> getParentFolder(int folderId) {
        ArrayList<Folder> folders = new ArrayList<Folder>();
        Folder currentFolder = new Folder();
        currentFolder.setFolder_id(folderId);
        if (folderId != 0) {
            currentFolder = folderMapper.getFolderById(currentFolder.getFolder_id());
            if (currentFolder == null)
                return null;
            folders.add(currentFolder);
        }
        while (currentFolder.getFolder_parent_id() != 0) {
            currentFolder = folderMapper.getParentFolderByFolderParentId(currentFolder.getFolder_parent_id());
            if (currentFolder == null)
                break;
            folders.add(currentFolder);
        }
        Folder rootFolder = new Folder();
        rootFolder.setFolder_id(0);
        folders.add(rootFolder);
        return folders;
    }

    public int getTotalCount(Folder currentFolder) {
        int i = folderMapper.countFolder(currentFolder);
        int j = fileService.getTotalCount(currentFolder);
        return i + j;
    }

    public String deleteFolder(HttpServletRequest request, int fileId) {
        User user = UserUtil.getUserFromToken(request.getHeader("lg_token"));
        boolean auth = userService.checkDeleteAuth();
        if (auth) {
            Folder currentFolder = new Folder();
            currentFolder.setFolder_id(fileId);
            int totalCountFolder = getTotalCount(currentFolder);
            int totalCountFile = fileService.getTotalCount(currentFolder);
            if (totalCountFile + totalCountFolder > 0)
                return "not empty";
            int count = folderMapper.deleteFolderById(fileId);
            if (count >= 1)
                return "success";
        }
        return "error";
    }

    public String modifyFolder(HttpServletRequest request, int folderId) {
        Folder folder = wrapFolder(request);
        try {
            Folder folderById = folderMapper.getFolderById(folderId);
            folderById.setFolder_name(folder.getFolder_name());
            folderById.setFolder_type(folder.getFolder_type());
            folderById.setFolder_description(folder.getFolder_description());
            int i = folderMapper.updateFolder(folderById);
            if (i < 1)
                return "error";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
        return "success";
    }

    public boolean updateFolder(Folder baseFolderByUser) {
        return folderMapper.updateFolder(baseFolderByUser) > 0;
    }
}
