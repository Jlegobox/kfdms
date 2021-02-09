package com.mp.kfdms.controller;

import com.mp.kfdms.domain.Folder;
import com.mp.kfdms.service.FileService;
import com.mp.kfdms.service.FolderService;
import com.mp.kfdms.service.UserService;
import com.mp.kfdms.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/10
 * @Time 14:08
 */
//@Controller
@Controller()
public class WelcomeController {
    private static final String CHARSET_BY_AJAX = "text/html; charset=utf-8";
    @Autowired
    public UserService userservice;
    @Autowired
    public FolderService folderService;
    @Autowired
    public FileService fileService;

    @RequestMapping(value = {"/"},produces = {CHARSET_BY_AJAX})
    public void index(final HttpServletResponse response) throws IOException {
        response.sendRedirect("login.html");
//        return "login.html"; 直接返回字符串不能跳转，因为之前加了RestfulController，返回字符串而不是视图
    }

    @RequestMapping(value = {"/welcome"},produces = {CHARSET_BY_AJAX})
    @ResponseBody
    public String welcome(final HttpServletRequest request,final HttpServletResponse response) throws IOException {
        String lg_token = request.getHeader("lg_token");
        if(lg_token!=null && userservice.checkToken(lg_token)){
            response.setHeader("lg_token", UserUtil.updateToken(lg_token));
            return "success";
        }else {
            return "error";
        }
    }

}
