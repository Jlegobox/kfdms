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

    private long REGISTER_TIME = 3000000L;

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
            if(System.currentTimeMillis() - Long.parseLong(registerInfo.getTime()) > REGISTER_TIME){
                return "register_time_out";
            }
            String return_msg="error";
            // 搜集注册所需信息
            User user = new User();
            // todo 账号密码传输需要加密
            user.setEmail(registerInfo.email);
            user.setPassword(registerInfo.password);
            user.setVerification(registerInfo.verification_code);// 邀请码逻辑
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
}
