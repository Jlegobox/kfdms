package com.mp.kfdms.service;

import com.mp.kfdms.mapper.UserMapper;
import com.mp.kfdms.domain.User;
import com.mp.kfdms.pojo.LoginInfo;
import com.mp.kfdms.pojo.PublicKeyInfo;
import com.mp.kfdms.pojo.RegisterInfo;
import com.mp.kfdms.util.GsonUtil;
import com.mp.kfdms.util.RSAKeyUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/10
 * @Time 14:20
 */
@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private RSAKeyUtil rsaKeyUtil;

    public static long REGISTER_TIME = 3000000L;
    public static long LOGIN_TIME = 3000000L;
    public static long TOKEN_VALIDITY = 12*3600*1000L;

    public String getPublicKey(){
        PublicKeyInfo publicKeyInfo = new PublicKeyInfo();
        publicKeyInfo.setPublicKey(rsaKeyUtil.getPublicKey());
        publicKeyInfo.setTime(System.currentTimeMillis());
        return GsonUtil.instance().toJson(publicKeyInfo);
    }

    public String doRegister(final HttpServletRequest request, final HttpSession session){
        // todo 注册许可检验
        final String encrypted = request.getParameter("RegisterInfo");
        try{
            final String encryptedStr = RSAKeyUtil.dncryption(encrypted, RSAKeyUtil.getPrivateKey());
            RegisterInfo registerInfo = GsonUtil.instance().fromJson(encryptedStr, RegisterInfo.class);
            String return_msg="error";
            // 注册信息验证
            if(System.currentTimeMillis() - Long.parseLong(registerInfo.getTime()) > REGISTER_TIME){
                return "register_time_out";
            }
            // email不能为空 todo 交给前端
            if(registerInfo.getEmail().length()<1){
                return "error";
            }

            // 搜集注册所需信息
            User user = new User();
            user.setEmail(registerInfo.getEmail());
            user.setPassword(registerInfo.getPassword());
            user.setVerification(registerInfo.getVerification_code());// 邀请码逻辑
            user.setUsername(UUID.randomUUID().toString());
            User exist_user = userMapper.findOneByEmail(user);
            if(exist_user != null){
                return_msg = "exist";
                System.out.println("用户已存在");
            }else {
                int i = userMapper.registerUser(user);
                if(i>0){
                    return_msg = "success";
                }else {
                    return_msg = "error";
                }
            }
            return return_msg;
        }catch (Exception e){
            System.out.println(e);
            return "error";
        }
    }

    public String doLogin(final HttpServletRequest request, final HttpServletResponse response){
        // todo 登录许可检验
        final String encrypted = request.getParameter("LoginInfo");
        try{
            final String encryptedStr = RSAKeyUtil.dncryption(encrypted, RSAKeyUtil.getPrivateKey());
            LoginInfo loginInfo = GsonUtil.instance().fromJson(encryptedStr, LoginInfo.class);
            String return_msg="error";
            // 登录信息验证
            if(System.currentTimeMillis() - Long.parseLong(loginInfo.getTime()) > REGISTER_TIME){
                return "register_time_out";
            }

            // 搜集登录所需信息
            User user = new User();
            user.setEmail(loginInfo.getEmail());
            user.setPassword(loginInfo.getPassword());
            // todo 黑名单逻辑

            int count = userMapper.login(user);
            if(count>0){ //找到user
                String token = updateToken(loginInfo);
                response.setHeader("lg_token", token);
                return_msg = "success";
            }else {
                return return_msg;
            }
            return return_msg;
        }catch (Exception e){
            System.out.println(e);
            return "error";
        }
    }

    public static String updateToken(LoginInfo loginInfo){
        loginInfo.setTime(System.currentTimeMillis() + TOKEN_VALIDITY + "");
        String json_str = GsonUtil.instance().toJson(loginInfo);
        return RSAKeyUtil.encryption(json_str, RSAKeyUtil.getPublicKey());
    }

    public static String updateToken(String token){
        final String encryptedStr = RSAKeyUtil.dncryption(token, RSAKeyUtil.getPrivateKey());
        LoginInfo loginInfo = GsonUtil.instance().fromJson(encryptedStr, LoginInfo.class);
        return updateToken(loginInfo);
    }
}
