package com.kfgs.pretrialclassification.sendEmail.service;

public interface MailService {
    /**
     * 发送html格式的邮件
     * @param to
     * @param subject
     * @param content
     */
    boolean sendHtmlMail(String[] to,String subject,String content);

    /**
     * 发送html格式的邮件
     * @param to 接受者
     * @param cc  抄送者
     * @param content  内容
     */
    boolean sendHtmlMail(String[] to,String[] cc,String subject,String content);
}
