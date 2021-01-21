package com.mp.kfdms.controller;

import com.mp.kfdms.pojo.LoginInfo;
import com.mp.kfdms.pojo.PublicKeyInfo;
import com.mp.kfdms.service.UserService;
import com.mp.kfdms.util.GsonUtil;
import com.mp.kfdms.util.RSAKeyUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/16
 * @Time 21:42
 */
@RequestMapping("/Login")
@RestController
public class LoginController {
    @Resource
    private UserService userService;

    @Resource
    private RSAKeyUtil rsaKeyUtil;



    @RequestMapping("getPublicKey")
    @ResponseBody
    public String getPublicKey(final HttpServletRequest request){
        PublicKeyInfo publicKeyInfo = new PublicKeyInfo();
        String publicKey = rsaKeyUtil.getPublicKey();
        publicKeyInfo.setPublicKey(publicKey);
        publicKeyInfo.setTime(System.currentTimeMillis());
        return GsonUtil.instance().toJson(publicKeyInfo);
    }

    @RequestMapping("doRegister.ajax")
    public String doRegister(final HttpServletRequest request, final HttpServletResponse response){
        String return_msg = "error";
        return_msg = userService.doRegister(request, response);
        return return_msg;
    }

    @RequestMapping("/doLogin.ajax")
    public String doLogin(final HttpServletRequest request, final HttpServletResponse response){
        String return_msg = "error";
        return_msg = userService.doLogin(request, response);
        return return_msg;
    }
}
