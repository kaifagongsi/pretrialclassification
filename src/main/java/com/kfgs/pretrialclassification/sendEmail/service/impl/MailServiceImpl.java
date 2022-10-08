package com.kfgs.pretrialclassification.sendEmail.service.impl;

import com.kfgs.pretrialclassification.sendEmail.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;

/**
 * Date: 2020-05-13-09-02
 * Module:
 * Description:
 *
 * @author:
 */
@Service
@Slf4j
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;



    /**
     * 发送html格式的邮件
     * @param to 接受者
     * @param subject 主题
     * @param content 内容
     */
    @Override
//    @Async("pretroalclassificationAsyncExecutor")
    public boolean sendHtmlMail(String[] to, String subject, String content) {
        MimeMessage message=mailSender.createMimeMessage();
        try {
            //true表示需要创建一个multipart message
            MimeMessageHelper helper=new MimeMessageHelper(message,true);
            //发送者
            helper.setFrom(username);
            //接受者
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content,true);
            mailSender.send(message);
            System.out.println("html格式邮件发送成功");
            return  true;
        }catch (Exception e){
            log.error("html格式邮件发送失败,接受者：{}，主题：{}",to,subject);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 发送html格式的邮件
     * @param to 接受者
     * @param cc 抄送
     * @param content 内容
     */
    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED )
//    @Async("pretroalclassificationAsyncExecutor")
    public boolean sendHtmlMail(String[] to,String[] cc,String subject,String content) {
        MimeMessage message=mailSender.createMimeMessage();
        try {
            //true表示需要创建一个multipart message
            MimeMessageHelper helper=new MimeMessageHelper(message,true);
            //发送者
            helper.setFrom(username);
            //接受者
            helper.setTo(to);
            helper.setCc(cc);
            helper.setSubject(subject);
            helper.setText(content,true);
            mailSender.send(message);
            System.out.println("html格式邮件抄送成功");
            return  true;
        }catch (Exception e){
            log.error("html格式邮件发送失败,接受者：{}，抄送：{},主题：{}",to,cc,subject);
            e.printStackTrace();
            return false;
        }
    }
}
