package com.mp.kfdms.controller;

import com.mp.kfdms.annotation.CurrentUser;
import com.mp.kfdms.domain.User;
import com.mp.kfdms.domain.VerificationLog;
import com.mp.kfdms.pojo.JsonModel;
import com.mp.kfdms.service.UserService;
import com.mp.kfdms.util.GsonUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/3/2
 * @Time 16:19
 */
@RestController
@RequestMapping("/Account")
public class AccountController {

    @Resource
    UserService userService;

    @RequestMapping("/getAccountInfo.ajax")
    public String getAccountInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonModel jsonModel = userService.getDetailedUserFromToken(request.getHeader("lg_token"));
        if("errorToken".equals(jsonModel.getMessage())){
            response.sendRedirect("/login.html");
        }
        return GsonUtil.instance().toJson(jsonModel);
    }

    @RequestMapping("/modifyAccountInfo.ajax")
    public String modifyAccountInfo(@CurrentUser User currentUser, HttpServletRequest request){
        userService.modifyAccountInfo(currentUser,request);
        return "success";
    }

    @RequestMapping("/changePassword.ajax")
    public String changePassword(@CurrentUser User currentUser,String oldPass, String newPass){
        return userService.changePassword(currentUser,oldPass,newPass);
    }

    @RequestMapping("/eliminateAccount.ajax")
    public String eliminateAccount(@CurrentUser User currentUser,String password){
        return userService.eliminateAccount(currentUser,password);
    }

    @RequestMapping("/createInviteCode.ajax")
    public String createInviteCode(@CurrentUser User currentUser){
        return userService.createInviteCode(currentUser);
    }

    @RequestMapping("/getInviteCode.ajax")
    public String getInviteCode(@CurrentUser User currentUser){
        VerificationLog verificationLog = userService.showVerificationLog(currentUser);
        return verificationLog.getVerificationCode();
    }

    @RequestMapping("/refreshInviteCode.ajax")
    public String refreshInviteCode(@CurrentUser User currentUser){
        return userService.refreshInviteCode(currentUser);
    }
}
