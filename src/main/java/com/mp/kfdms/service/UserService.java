package com.mp.kfdms.service;

import com.mp.kfdms.configuration.ConfigurationReader;
import com.mp.kfdms.domain.Folder;
import com.mp.kfdms.domain.VerificationLog;
import com.mp.kfdms.mapper.FolderMapper;
import com.mp.kfdms.mapper.UserMapper;
import com.mp.kfdms.domain.User;
import com.mp.kfdms.mapper.VerificationLogMapper;
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
import java.util.List;
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
    private VerificationLogMapper verificationLogMapper;

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
        final String encrypted = request.getParameter("registerInfo");
        final String verificationCode = request.getParameter("verificationCode");
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
            user.setVerification(verificationCode == null ? "" : verificationCode);// 邀请码逻辑
            user.setUsername(UUID.randomUUID().toString());

            // 邀请码检验
            if (user.getVerification() != null && user.getVerification().length() > 1) { // 存在输入的邀请码就检验一下
                VerificationLog oneByVerification = verificationLogMapper.findOneByVerification(user.getVerification());
                if (oneByVerification == null) {
                    return "errorInviteCode";
                } else if (oneByVerification.getIsUsed() == 0) {
                    return "expiredInviteCode";
                }
            }
            if ("1".equals(ConfigurationReader.instance().getConf("sys.login.inviteMode"))) { // 邀请模式开启
                if (user.getVerification() == null || user.getVerification().length() < 1) { // 前面输入错误的情况已经检验，这里排除没有输入的情况
                    return "needInvite";
                }
            }
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
            Folder baerFolder = folderService.createBaseFolder(oneByEmail);
            if (baerFolder != null) {
                oneByEmail.setBase_folder_id(baerFolder.getFolder_id());
                userMapper.updateUser(oneByEmail);
                return_msg = "success";
            }
        } catch (Exception e){
            e.printStackTrace();
            return "activate_error";
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
                user = userMapper.findOneByEmail(user);
                if (user.getLogin_forbidden() == 1)
                    return "loginForbidden";
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

    public String createInviteCode(User currentUser) {
        try {
            VerificationLog one = verificationLogMapper.findOne(currentUser.getId());
            one.setIsUsed(0); // 废弃
            one.setDropTime(new Date());
            verificationLogMapper.updateLog(one);
        } catch (Exception e) {
            e.printStackTrace();
        }

        VerificationLog verificationLog = new VerificationLog();
        String uuid = UUID.randomUUID().toString();
        verificationLog.setVerificationCode(uuid);
        verificationLog.setVerificationOwner(currentUser.getId());
        verificationLog.setIsUsed(1); // 正在使用
        verificationLog.setUsedUserEmail("");
        verificationLogMapper.addLog(verificationLog);
        return verificationLog.getVerificationCode();
    }

    public VerificationLog showVerificationLog(User currentUser) {
        currentUser = userMapper.findOneByEmail(currentUser);
        if (currentUser != null) {
            VerificationLog one = verificationLogMapper.findOne(currentUser.getId());
            System.out.println("OK");
            return one;
        } else {
            return null;
        }
    }

    public String refreshInviteCode(User currentUser) {
        currentUser = userMapper.findOneByEmail(currentUser);
        if(currentUser != null){
            String inviteCode = createInviteCode(currentUser);
            return inviteCode;
        }
        return "error";
    }

    public JsonModel getAllAccount(User currentUser) {
        JsonModel jsonModel = new JsonModel();
        if (currentUser.getUser_type() != 0) {
            jsonModel.setMessage("authError");
            return jsonModel;
        }
        List<User> allUser = userMapper.findAll();
        for (User user : allUser) {
            try {
                VerificationLog oneByVerification = verificationLogMapper.findOneByVerification(user.getVerification());
                User oneById = userMapper.findOneById(oneByVerification.getVerificationOwner());
                if (oneById != null) {
                    user.setVerification(oneById.getUsername());
                }
            } catch (Exception e) {
                user.setVerification("");
            }
        }
        jsonModel.setMessage("success");
        jsonModel.setData(allUser);
        return jsonModel;
    }

    public String setLoginForbidden(User currentUser, int userId) {
        if (currentUser.getUser_type() != 0) {
            return "authError";
        }
        userMapper.setLoginForbidden(userId);
        return "success";
    }
}
