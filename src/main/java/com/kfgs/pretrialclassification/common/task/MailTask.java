package com.kfgs.pretrialclassification.common.task;

import com.kfgs.pretrialclassification.sendEmail.service.impl.SendEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@ConditionalOnProperty(name = "pretrialclassification.task.enable", havingValue = "true")
public class MailTask {

    @Autowired
    SendEmailService sendEmailTask;

    @Scheduled(cron = "0 30 7-21 ? * MON-FRI")
    public  void test(){
        sendEmailTask.sendEmail();
        //new SendEmailTask().SendEmail();
    }
}
