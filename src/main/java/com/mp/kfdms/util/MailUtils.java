package com.mp.kfdms.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * 邮件发送相关工具类
 * 一个系统一般只会设置一个邮箱，所以直接放到Spring中管理
 * todo 运行期间修改邮箱账号密码
 *
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/3/13
 * @Time 21:19
 */
@Component
public class MailUtils {

    /**
     * 根据邮箱名称和授权码创建一个JavaMailSender
     * @param username
     * @param password
     * @return
     */
    public static JavaMailSender createMailSender(String username, String password) {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setDefaultEncoding("UTF-8");
        if (username.endsWith("@qq.com")) {
            javaMailSender.setHost("smtp.qq.com");
        } else {
            javaMailSender.setHost("smtp.163.com");
        }
        javaMailSender.setPort(25); // 25端口为非SSL 465/994为SSL协议端口号
        javaMailSender.setProtocol("smtp");
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);
        return javaMailSender;
    }

    public static String createVerificationContext(String userName) {
        String verificationCode = AESUtil.encodeBase64(userName);
        String message = "<h2 >KFDMS网盘</h2>" +
                "<h3>用户注册-邮箱验证<h3/>" +
                "您现在正在注册账号" + userName + "<br>" +
                "请点击下方链接完成验证<br>" + verificationCode +
                "<h5 style='color : red'>如果并非本人操作,请忽略本邮件</h5>";
        return message;
    }
}
