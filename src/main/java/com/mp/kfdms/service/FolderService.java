package com.mp.kfdms.service;

import com.mp.kfdms.domain.Folder;
import com.mp.kfdms.domain.User;
import com.mp.kfdms.mapper.FolderMapper;
import com.mp.kfdms.mapper.UserMapper;
import com.mp.kfdms.util.FolderUtil;
import com.mp.kfdms.util.RequestUtil;
import com.mp.kfdms.util.UserUtil;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    public static void main(String[] args) {
        System.out.println();
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

    public String createFolder(final HttpServletRequest request, final HttpServletResponse response){
        Folder folder = wrapFolder(request);
        int count = folderMapper.createFolder(folder);
        if(count>0){
            return "success";
        }else {
            return "error";
        }
    }

}
