package com.mp.kfdms.service;

import com.mp.kfdms.domain.Folder;
import com.mp.kfdms.domain.User;
import com.mp.kfdms.mapper.UserMapper;
import com.mp.kfdms.util.UserUtil;
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
    public UserMapper userMapper;


    public String createFolder(final HttpServletRequest request, final HttpServletResponse response){
        Folder folder = new Folder();
        folder.setFolder_name(request.getParameter("folder_name"));
        folder.setFolder_parent_id(Integer.parseInt(request.getParameter("folder_id")));
        User login_info = UserUtil.getUserFromToken(request.getHeader("lg_token"));
        User oneByEmail = userMapper.findOneByEmail(login_info);
        folder.setFolder_owner_id(oneByEmail.getId());
        folder.setFolder_type(Integer.parseInt(request.getParameter("folder_type")));
        folder.setFolder_description(request.getParameter("description"));
        return "OK";

    }
}
