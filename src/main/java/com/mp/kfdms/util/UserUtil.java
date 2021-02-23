package com.mp.kfdms.util;

import com.mp.kfdms.domain.User;
import com.mp.kfdms.pojo.FileShareLinkInfo;
import com.mp.kfdms.pojo.LoginInfo;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/11
 * @Time 17:50
 */
public class UserUtil {
    public static long REGISTER_TIME = 3000000L;
    public static long LOGIN_TIME = 3000000L;
    public static long TOKEN_VALIDITY = 12*3600*1000L;
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

    public static User getUserFromToken(String token){
        if(token == null || token.length()<10){
            return null;
        }
        final String encryptedStr = RSAKeyUtil.dncryption(token, RSAKeyUtil.getPrivateKey());
        if(encryptedStr==null){
            return null;
        }
        LoginInfo loginInfo = GsonUtil.instance().fromJson(encryptedStr, LoginInfo.class);
        User user = new User();
        user.setEmail(loginInfo.getEmail());
        user.setPassword(loginInfo.getPassword());
        return user;
    }

    public static boolean checkFileShareAuth(User currentUser, FileShareLinkInfo fileShareLinkInfo) {
        return true;
    }
}
