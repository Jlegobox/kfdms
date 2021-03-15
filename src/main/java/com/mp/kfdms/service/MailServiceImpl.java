package com.mp.kfdms.service;

import com.mp.kfdms.service.serviceInterface.IMailService;
import com.mp.kfdms.util.MailUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * 邮件服务
 *
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/3/15
 * @Time 17:38
 */
@Service
public class MailServiceImpl implements IMailService {
    private Logger logger = LoggerFactory.getLogger(MailUtils.class);

    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String sender;

    /**
     * 默认的构造函数，从配置项中获得
     *
     * @param mailSender
     */
    @Autowired
    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        logger.info("开始发送简单邮件...");
        logger.info("mailSender对象为:"+this.mailSender);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(subject);
        message.setText(content);
        message.setFrom(this.sender);
        message.setTo(to);
        System.out.println(this.mailSender);
        logger.info("message对象为:"+message);
        mailSender.send(message);
    }

    @Override
    public void sendHtmlMail(String to, String subject, String content) {
        logger.info("开始发送Html邮件...");
        logger.info("mailSender对象为:"+this.mailSender);
        MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        try {
            helper.setSubject(subject);
            helper.setText(content,true);
            helper.setFrom(this.sender);
            helper.setTo(to);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        logger.info("mimeMessage对象为:"+mimeMessage);
        mailSender.send(mimeMessage);
    }

    @Override
    public void sendAttachmentsMail(String to, String subject, String content, String filePath) {
        logger.info("开始发送带附件邮件");
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(this.sender);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            FileSystemResource file = new FileSystemResource(new File(filePath));
            String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
            helper.addAttachment(fileName, file);
            this.mailSender.send(message);
            //日志信息
            logger.info("邮件已经发送。");
        } catch (MessagingException e) {
            logger.error("发送邮件时发生异常！", e);
        }
    }
}
