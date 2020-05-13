package com.kfgs.pretrialclassification.caseDisposition.service;

public interface MailService {
    /**
     * 发送html格式的邮件
     * @param to
     * @param subject
     * @param content
     */
    void sendHtmlMail(String[] to,String subject,String content);
}
