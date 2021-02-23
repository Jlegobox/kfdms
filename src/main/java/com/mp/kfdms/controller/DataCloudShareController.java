package com.mp.kfdms.controller;

import com.mp.kfdms.annotation.CurrentUser;
import com.mp.kfdms.domain.User;
import com.mp.kfdms.pojo.FileShareLinkInfo;
import com.mp.kfdms.util.UserUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/2/22
 * @Time 20:14
 */
@Controller
@RequestMapping("/DataCloud/Share")
public class DataCloudShareController {
    @RequestMapping("initTest")
    public String test(HttpServletResponse response){
        System.out.println("进入");
        return "forward:/login.html";
    }

    @RequestMapping("createLink.ajax")
    @ResponseBody
    public String createLink(final HttpServletRequest request, @CurrentUser final User currentUser, final FileShareLinkInfo fileShareLinkInfo){
        System.out.println(fileShareLinkInfo);
        return "success";
    }
}
