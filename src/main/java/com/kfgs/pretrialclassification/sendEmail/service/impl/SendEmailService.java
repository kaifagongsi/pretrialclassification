package com.kfgs.pretrialclassification.sendEmail.service.impl;


import com.kfgs.pretrialclassification.dao.FenleiBaohuResultMapper;
import com.kfgs.pretrialclassification.domain.EmailIntervalEntity;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuResultExt;
import com.kfgs.pretrialclassification.sendEmail.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SendEmailService {


    @Autowired
    FenleiBaohuResultMapper fenleiBaohuResultMapper;

    @Value("${pretrialclassification.email.toFenlei}")
    private String toFenlei;

    @Value("${pretrialclassification.email.toJiagong}")
    private String toJiagong;

    @Value("${pretrialclassification.email.toGuiHua}")
    private String toGuiHua;

    @Autowired
    MailService mailService;

    /**
     * 定时发送邮件使用
     */
    public void sendEmail(){
        //获取map
        HashMap<String,List<EmailIntervalEntity>> map = this.findAll();
        //发送邮件
        sendEmailToPerson(map);
    }

    /**
     * 由传入的ids 发送邮件提醒
     * @param ids
     * @return
     */
    public boolean sendEmail(String[] ids) {
        List<FenleiBaohuResultExt> fenleiBaohuResultExts = fenleiBaohuResultMapper.AfterDeploymentSendEmail(ids);
        List<String> to = new ArrayList<>();
        List<String> cc = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int state = -1;
        sb.append("&nbsp;&nbsp;&nbsp;<table  border='1px' cellpadding='5px' style='font-size:14px;border-collapse: collapse;margin: 20px; '><thead><tr><th>预审编号</th><th>部门</th><th>主分人</th><th>发明名称</th><th>粗分号</th><th>分配时间</th></tr></thead><tbody>");
        for (int i = 0; i < fenleiBaohuResultExts.size(); i++) {
            FenleiBaohuResultExt r = fenleiBaohuResultExts.get(i);
            String fenpeitime = r.getFenpeitime();
            to.add(r.getEmail());

            if("FL".equals(r.getOrgname()) && state != 2){//只有分类
                state = 1;
            }else if("JG".equals(r.getOrgname()) && state !=1){//只有加工
                state = 2;
            }else{
                state = 3;
            }
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
                sb.append("<tr><td>"+ r.getId() + "</td><td>" + r.getDep1() + "</td><td>" + r.getWorker() + "</td><td>" + r.getMingcheng() + "</td><td>" + r.getSimpleclasscode()+ "</td><td>" +  sdf.format(sdf1.parse(fenpeitime)) + "</td></tr>" );
            } catch (java.text.ParseException e1) {
                e1.printStackTrace();
            }
        }
        sb.append("</tbody></table>");

        String content = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;各位领导，以下是今天新分配的保护中心案件列表! &nbsp;&nbsp;&nbsp;请注意查收！" + sb.toString();
        // 首先添加规划部门
        cc.addAll(Arrays.asList(toGuiHua.split(",")));
        if(state == 1){
            cc.addAll(Arrays.asList(toFenlei.split(",")));
        }else if(state == 2){
            cc.addAll(Arrays.asList(toJiagong.split(",")));
        }else if(state == 3){
            cc.addAll(Arrays.asList(toFenlei.split(",")));
            cc.addAll(Arrays.asList(toJiagong.split(",")));
        }

        //list 去重
        to = to.stream().distinct().collect(Collectors.toList());
        cc = cc.stream().distinct().collect(Collectors.toList());
        //发送邮件
        return mailService.sendHtmlMail(to.toArray(new String[to.size()]),cc.toArray(new String[cc.size()]),"保护中心案件列表", content);
    }


    private void sendEmailToPerson(HashMap<String, List<EmailIntervalEntity>> map) {
        //收件人
        List<String> toEmailAddress = new ArrayList<String>();
        //抄送人
        List<String> ccEmailAddres = new ArrayList<String>();
        //修正list(添加部分list)
        StringBuffer context =  new StringBuffer("");
        boolean fist = true;
        List<String> orgList = new ArrayList<String>();
        int count = 0;
        for(Map.Entry<String, List<EmailIntervalEntity>> l_m : map.entrySet()){
            List<EmailIntervalEntity> list = l_m.getValue();
            for(EmailIntervalEntity b : list ){
                String fenpeitime = b.getFenpeitime();
                try {
                    Date date_sql = new SimpleDateFormat("yyyyMMddHHmmss").parse(fenpeitime);
                    Date date_cur = new Date();
                    Long diff = date_cur.getTime() - date_sql.getTime();
                    Long hour = diff % (1000 * 24 * 60 * 60) / (1000 * 60 * 60);
                    log.info("相差" + hour +"个小时");
                    if(hour % 4 == 0 && hour != 0){
                        if(fist){
                            //距案件分配已过6小时
                            context.append("<h2>距案件分配已过"+hour+"小时，下列人员还未出案</h2>" +
                                    "<h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;案件分配以及保护中心问题，请联系于惠（1839），李思瑶（1394）</h4>"+
                                    "<table style='font-family:verdana;font-size:22px;border-width:1px;border-color:#666666;border-collapse:collapse;'> " +
                                    //context.append("<h3>以下待完成案件"+hour+"小时之前已结束粗分</h3><br/><table style='font-family:verdana;font-size:22px;border-width:1px;border-color:#666666;border-collapse:collapse;'> " +
                                    "<tr> <th style='border-width: 1px;padding: 8px;border-style: solid;border-color: #666666;background-color: #dedede;'>预审编号</th>" +
                                    " 	  <th style='border-width: 1px;padding: 8px;border-style: solid;border-color: #666666;background-color: #dedede;'>部门</th>  "+
                                    " 	  <th style='border-width: 1px;padding: 8px;border-style: solid;border-color: #666666;background-color: #dedede;'>主分人</th>  "+
                                    " 	  <th style='border-width: 1px;padding: 8px;border-style: solid;border-color: #666666;background-color: #dedede;'>发明名称</th>  "+
                                    " 	  <th style='border-width: 1px;padding: 8px;border-style: solid;border-color: #666666;background-color: #dedede;'>粗分号</th>  "+
                                    " 	  <th style='border-width: 1px;padding: 8px;border-style: solid;border-color: #666666;background-color: #dedede;'>分配时间</th>  "+
                                    "</tr>");
                            fist  = false;
                        }
                        if(toEmailAddress.contains(b.getEmail())){//email已存在
                        }else{
                            count =  count + 1;
                            toEmailAddress.add(b.getEmail());
                            //2019年9月4日09:44:30  修改 bug：发现距离0小时的依然在列表中，且收件人没有此人，因此将列表调整
                            context.append("<tr>" +
                                    "<td style='border-width: 1px;padding: 8px;border-style: solid;border-color: #666666;background-color: #ffffff;'>" + b.getId() + "</td>" +
                                    "<td style='border-width: 1px;padding: 8px;border-style: solid;border-color: #666666;background-color: #ffffff;'>" + b.getOrgname() + "</td>" +
                                    "<td style='border-width: 1px;padding: 8px;border-style: solid;border-color: #666666;background-color: #ffffff;'>" + b.getWorker() + "</td>" +
                                    "<td style='border-width: 1px;padding: 8px;border-style: solid;border-color: #666666;background-color: #ffffff;'>" + b.getMingcheng() + "</td>" +
                                    "<td style='border-width: 1px;padding: 8px;border-style: solid;border-color: #666666;background-color: #ffffff;'>" + b.getSimpleclasscode() + "</td>" +
                                    "<td style='border-width: 1px;padding: 8px;border-style: solid;border-color: #666666;background-color: #ffffff;'>" + b.getFenpeitime().substring(0, 8) + " " + b.getFenpeitime().substring(8, 10) + ":" + b.getFenpeitime().substring(10,12) + ":" +  b.getFenpeitime().substring(12) + "</td>" +
                                    "</tr>");
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //用于判断分类加工部门收件人  此list应该最多只有两个
                if(orgList.contains(b.getOrgname())){
                }else{
                    orgList.add(b.getOrgname());
                }
            }
        }
        context.append("</table>");
        String title = "保护中心未完成案件间隔提醒";
        //配置分类加工部门的收件人
        for(int i = 0; i < orgList.size(); i++){
            String name = orgList.get(i);
            if("FL".equals(name)){
                ccEmailAddres.addAll(Arrays.asList(toFenlei.split(",")));
            }else if("JG".equals(name)) {
                ccEmailAddres.addAll(Arrays.asList(toJiagong.split(",")));
            }
        }
        //添加 规划发展部的人
        ccEmailAddres.addAll(Arrays.asList(toGuiHua.split(",")));
        if(count > 0){
            mailService.sendHtmlMail(toEmailAddress.toArray(new String[toEmailAddress.size()]),ccEmailAddres.toArray(new String[ccEmailAddres.size()]),title,context.toString());
        }
        log.info("经过一次邮件检测，时间：" + new SimpleDateFormat("yyyy 年 MM 月dd 日 HH时mm分ss秒").format(new Date()));
    }


    /**
     * 查询数据库中还未完成的案件（状态不为2）
     * @return
     */
    private HashMap<String, List<EmailIntervalEntity>> findAll() {
        List<EmailIntervalEntity> all = fenleiBaohuResultMapper.findAll();
        HashMap<String,List<EmailIntervalEntity>> map = new HashMap<String, List<EmailIntervalEntity>>();
        for(EmailIntervalEntity b : all){
            if(map.size() > 0){
                if(map.containsKey(b.getWorker_all())){
                    List<EmailIntervalEntity> list_have = map.get(b.getWorker_all());
                    list_have.add(b);
                    map.put(b.getWorker_all(), list_have);
                }else{
                    List<EmailIntervalEntity> list_new = new ArrayList<EmailIntervalEntity>();
                    list_new.add(b);
                    map.put(b.getWorker_all(), list_new);
                }
            }else {
                List<EmailIntervalEntity> list_M = new ArrayList<EmailIntervalEntity>();
                list_M.add(b);
                map.put(b.getWorker_all(), list_M);
            }
        }
        return map;
    }
}
