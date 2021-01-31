package com.mp.kfdms.interceptor;

import com.mp.kfdms.controller.LoginController;
import com.mp.kfdms.domain.User;
import com.mp.kfdms.mapper.UserMapper;
import com.mp.kfdms.pojo.LoginInfo;
import com.mp.kfdms.service.UserService;
import com.mp.kfdms.util.GsonUtil;
import com.mp.kfdms.util.RSAKeyUtil;
import com.mp.kfdms.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Priority;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/20
 * @Time 11:02
 */
@Priority(1)
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Resource
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("执行interceptor！");
        // 暂时
        if(request != null){
            return true;
        }
        String lg_token = request.getHeader("lg_token");
        if(lg_token == null || lg_token.length()<10){
            response.sendRedirect("login.html");
            return false;
        }
        try{
            String dncryption = RSAKeyUtil.dncryption(lg_token, RSAKeyUtil.getPrivateKey());
            LoginInfo loginInfo = GsonUtil.instance().fromJson(dncryption, LoginInfo.class);
            if(System.currentTimeMillis() - Long.parseLong(loginInfo.getTime())> UserUtil.TOKEN_VALIDITY){
                response.sendRedirect("login.html");
                return false;
            }
            User user = new User();
            user.setEmail(loginInfo.getEmail());
            user.setPassword(loginInfo.getPassword());
            int account = userMapper.login(user);
            if(account<0){
                response.sendRedirect("login.html");
                return false;
            }
            return true;
        }catch (Exception e){
            System.out.println(e);
            response.sendRedirect("login.html");
            return false;
        }
    }
}
