package com.mp.kfdms.service;

import com.mp.kfdms.mapper.FolderMapper;
import com.mp.kfdms.mapper.UserMapper;
import com.mp.kfdms.domain.User;
import com.mp.kfdms.pojo.JsonModel;
import com.mp.kfdms.pojo.LoginInfo;
import com.mp.kfdms.pojo.PublicKeyInfo;
import com.mp.kfdms.pojo.RegisterInfo;
import com.mp.kfdms.util.GsonUtil;
import com.mp.kfdms.util.RSAKeyUtil;
import com.mp.kfdms.util.UserUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
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
    private FolderMapper folderMapper;

    @Resource
    private FolderService folderService;

    @Resource
    private RSAKeyUtil rsaKeyUtil;


    public String getPublicKey() {
        PublicKeyInfo publicKeyInfo = new PublicKeyInfo();
        publicKeyInfo.setPublicKey(rsaKeyUtil.getPublicKey());
        publicKeyInfo.setTime(System.currentTimeMillis());
        return GsonUtil.instance().toJson(publicKeyInfo);
    }

    public String doRegister(final HttpServletRequest request, final HttpServletResponse response) {
        // todo 注册许可检验
        final String encrypted = request.getParameter("RegisterInfo");
        try {
            final String encryptedStr = RSAKeyUtil.dncryption(encrypted, RSAKeyUtil.getPrivateKey());
            RegisterInfo registerInfo = GsonUtil.instance().fromJson(encryptedStr, RegisterInfo.class);
            String return_msg = "error";
            // 注册信息验证
            if (System.currentTimeMillis() - Long.parseLong(registerInfo.getTime()) > UserUtil.REGISTER_TIME) {
                return "register_time_out";
            }
            // email不能为空 todo 交给前端
            if (registerInfo.getEmail().length() < 1) {
                return "error";
            }

            // 搜集注册所需信息 todo 用户密码加密
            User user = new User();
            user.setEmail(registerInfo.getEmail());
            user.setPassword(registerInfo.getPassword());
            user.setVerification(registerInfo.getVerification_code());// 邀请码逻辑
            user.setUsername(UUID.randomUUID().toString());
            User exist_user = userMapper.findOneByEmail(user);
            if (exist_user != null) {
                return_msg = "exist";
                System.out.println("用户已存在");
            } else {
                int i = userMapper.registerUser(user);
                if (i > 0) {
                    String s = doActivate(request, response, user); // TODO: 2021/1/21 邮件激活
                    return_msg = "success";
                } else {
                    return_msg = "error";
                }
            }
            return return_msg;
        } catch (Exception e) {
            System.out.println(e);
            return "error";
        }
    }

    public String doActivate(final HttpServletRequest request, final HttpServletResponse response, User user) {
        String return_msg = "error";
        if (user == null) {
            user = UserUtil.getUserFromToken(request.getParameter("lg_token"));
            if (user == null) {
                return return_msg;
            }
        }
        try {
            User oneByEmail = userMapper.findOneByEmail(user);
            String baerFolder = folderService.createBaseFolder(oneByEmail);
            if ("error".equals(baerFolder)) {
                return_msg = "activate_error";// 激活策略
            } else {
                return_msg = "success";
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return return_msg;
    }

    public String doLogin(final HttpServletRequest request, final HttpServletResponse response) {
        // todo 登录许可检验
        final String encrypted = request.getParameter("LoginInfo");
        try {
            final String encryptedStr = RSAKeyUtil.dncryption(encrypted, RSAKeyUtil.getPrivateKey());
            LoginInfo loginInfo = GsonUtil.instance().fromJson(encryptedStr, LoginInfo.class);
            String return_msg = "error";
            // 登录信息验证
            if (System.currentTimeMillis() - Long.parseLong(loginInfo.getTime()) > UserUtil.REGISTER_TIME) {
                return "register_time_out";
            }

            // 搜集登录所需信息
            User user = new User();
            user.setEmail(loginInfo.getEmail());
            user.setPassword(loginInfo.getPassword());
            // todo 黑名单逻辑

            int count = userMapper.login(user);
            if (count > 0) { //找到user
                String token = UserUtil.updateToken(loginInfo);
                response.setHeader("lg_token", token);
                return_msg = "success";
            } else {
                return return_msg;
            }
            return return_msg;
        } catch (Exception e) {
            System.out.println(e);
            return "error";
        }
    }

    public boolean checkToken(String token) {
        User userFromToken = UserUtil.getUserFromToken(token);
        int login = userMapper.login(userFromToken);
        return login > 0;
    }

    public User getUserFromToken(String token) {
        User userFromToken = UserUtil.getUserFromToken(token);
        User detailUser = null;
        if (userFromToken != null) {
            detailUser = userMapper.findOneByEmail(userFromToken);
        }
        return detailUser;

    }

    public boolean checkUploadAuth(User user) {
        return true;
    }

    public boolean checkDeleteAuth() {
        return true;
    }

    public boolean checkModifyAuth(User user) {
        return true;
    }

    public JsonModel getDetailedUserFromToken(String token) {
        JsonModel jsonModel = new JsonModel();
        User userFromToken = getUserFromToken(token);
        if (userFromToken == null) {
            jsonModel.setMessage("errorToken");
            return jsonModel;
        }
        jsonModel.setMessage("success");
        jsonModel.setData(userFromToken);
        return jsonModel;
    }

    public String modifyAccountInfo(User currentUser, HttpServletRequest request) {
        currentUser.setUsername(request.getParameter("username"));
        currentUser.setSex(Integer.parseInt(request.getParameter("sex")));
        currentUser.setStudent_id(request.getParameter("studentId"));
        Date date = new Date();
        try {
            String birthday = request.getParameter("birthday");
            SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
            date = sdf.parse(birthday);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        currentUser.setBirthday(date);
        currentUser.setStart_year(Integer.parseInt(request.getParameter("startYear")));
        currentUser.setTelephone(request.getParameter("telephone"));
        currentUser.setUser_type(Integer.parseInt(request.getParameter("userType")));
        currentUser.setVerification(request.getParameter("verification"));
        userMapper.updateUser(currentUser);
        return "success";
    }

    public String changePassword(User currentUser, String oldPass, String newPass) {
        if (currentUser != null) {
            if (!currentUser.getPassword().equals(oldPass)) {
                return "原始密码错误";
            }
            currentUser.setPassword(newPass);
            userMapper.changePassword(currentUser);
            return "密码修改成功";
        }
        return "服务器错误";
    }

    public String eliminateAccount(User currentUser, String password) {
        if (currentUser == null || !currentUser.getPassword().equals(password)) {
            return "密码错误";
        }
        // TODO: 2021/3/2 设置标志位的方式来删除用户，并将用户的数据进行清除
        userMapper.deleteUser(currentUser);
        clearUser();
        return "success";
    }

    public String clearUser() {
        // TODO: 2021/3/2 用户表设置用户删除后，根据用户id，删除上传记录，文件分享记录等 考虑异步方式进行
        return null;
    }
}
