package com.kfgs.pretrialclassification.sendEmail.service.impl;


import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kfgs.pretrialclassification.common.utils.FreeMarkerUtils;
import com.kfgs.pretrialclassification.common.utils.SimpleDateFormateUtils;
import com.kfgs.pretrialclassification.dao.FenleiBaohuMainMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuResultMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuUserinfoMapper;
import com.kfgs.pretrialclassification.domain.EmailIntervalEntity;
import com.kfgs.pretrialclassification.domain.FenleiBaohuAdjudication;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuResultExt;
import com.kfgs.pretrialclassification.sendEmail.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SendEmailService {


    @Autowired
    FenleiBaohuMainMapper fenleiBaohuMainMapper;

    @Autowired
    FenleiBaohuUserinfoMapper fenleiBaohuUserinfoMapper;

    @Autowired
    FenleiBaohuResultMapper fenleiBaohuResultMapper;


    @NacosValue(value = "${pretrialclassification.email.toGuiHua:30}",autoRefreshed = true)
    private String toGuiHua;

    @NacosValue(value = "${pretrialclassification.email.toYiBu:30}",autoRefreshed = true)
    private String toYiBu;

    @NacosValue(value = "${pretrialclassification.email.toErBu:30}",autoRefreshed = true)
    private String toErBu;

    @NacosValue(value = "${pretrialclassification.email.toSanBu:30}",autoRefreshed = true)
    private String toSanBu;

    @NacosValue(value = "${pretrialclassification.email.toSiBu:30}",autoRefreshed = true)
    private String toSiBu;

    @NacosValue(value = "${pretrialclassification.email.toYiBu_overtime:30}",autoRefreshed = true)
    private String toYiBu_overtime;

    @NacosValue(value = "${pretrialclassification.email.toErBu_overtime:30}",autoRefreshed = true)
    private String toErBu_overtime;

    @NacosValue(value = "${pretrialclassification.email.toSanBu_overtime:30}",autoRefreshed = true)
    private String toSanBu_overtime;

    @NacosValue(value = "${pretrialclassification.email.toSiBu_overtime:30}",autoRefreshed = true)
    private String toSiBu_overtime;

    @NacosValue(value = "${pretrialclassification.email.toSanBu_Trans:30}",autoRefreshed = true)
    private String toSanBu_Trans;

    @NacosValue(value = "${pretrialclassification.email.toSiBu_Trans:30}",autoRefreshed = true)
    private String toSiBu_Trans;

    @NacosValue(value = "${pretrialclassification.arbiter.toYiBu_arbiter:30}",autoRefreshed = true)
    private String toYiBu_arbiter;

    @NacosValue(value = "${pretrialclassification.arbiter.toErBu_arbiter:30}",autoRefreshed = true)
    private String toErBu_arbiter;

    @NacosValue(value = "${pretrialclassification.arbiter.toSanBu_arbiter:30}",autoRefreshed = true)
    private String toSanBu_arbiter;

    @NacosValue(value = "${pretrialclassification.arbiter.toSiBu_arbiter:30}",autoRefreshed = true)
    private String toSiBu_arbiter;

    @NacosValue(value = "${pretrialclassification.arbiter.toGuiHua_arbiter:30}",autoRefreshed = true)
    private String toGuiHua_arbiter;

    @NacosValue(value = "${pretrialclassification.arbiter.toFenlei:30}",autoRefreshed = true)
    private String toFenlei_arbiter;

    @NacosValue(value = "${pretrialclassification.arbiter.toJiagong:30}",autoRefreshed = true)
    private String toJiagong_arbiter;


    @Autowired
    FreeMarkerConfigurer freeMarkerConfigurer;

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
     * 0317 新增转案邮件提醒
     */
    /*public boolean sendTransEmail(String id,String fenpeiren,List<String> transworker){
        List<String> to = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("&nbsp;&nbsp;&nbsp;<table  border='1px' cellpadding='5px' style='font-size:14px;border-collapse: collapse;margin: 20px; '><thead><tr><th>预审编号</th><th>部门</th><th>分类员</th><th>转案来源</th><th>转案时间</th></tr></thead><tbody>");
        for (int i=0;i<transworker.size();i++){
            FenleiBaohuResultExt r = fenleiBaohuResultMapper.AfterTransSendEmail(id,transworker.get(i));
            String fenpeitime = r.getFenpeitime();
            String worker = r.getFldmworker();
            to.add(r.getEmail());
            if("三部".equals(r.getDep1())){
                to.addAll(Arrays.asList(toSanBu_Trans.split(",")));
            }else if ("一部".equals(r.getDep1())){ //一部同时发裁决人员
                //获取裁决人员邮箱
                String cjworker = fenleiBaohuUserinfoMapper.getArbiterByworker(worker);
                String cjemail = fenleiBaohuUserinfoMapper.getEamilById(cjworker);
                to.add(cjemail);
            }
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
                sb.append("<tr><td>"+ r.getId() + "</td><td>" + r.getDep1() + "</td><td>" + r.getWorker() + "</td><td>" + fenpeiren + "</td><td>" +  sdf.format(sdf1.parse(fenpeitime)) + "</td></tr>" );
            } catch (java.text.ParseException e1) {
                e1.printStackTrace();
            }
        }
        sb.append("</tbody></table>");
        String content = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;各位同事，以下是新转案的保护中心案件列表! &nbsp;&nbsp;&nbsp;请注意查收！" + sb.toString();
        // 首先添加规划部门
        //to.addAll(Arrays.asList(toGuiHua.split(",")));
        //list 去重
        to = to.stream().distinct().collect(Collectors.toList());
        //发送邮件
        return mailService.sendHtmlMail(to.toArray(new String[to.size()]),"保护中心案件列表", content);
    }*/
    /**
     * 由传入的ids 发送邮件提醒
     * @param ids
     * @return
     */
    public boolean sendEmail(String[] ids) throws Exception {
        List<FenleiBaohuResultExt> fenleiBaohuResultExts = fenleiBaohuResultMapper.AfterDeploymentSendEmail(ids);
        List<String> to = new ArrayList<>(), cc = new ArrayList<>(),adjudicatorList = new ArrayList<>();
        for (int i = 0; i < fenleiBaohuResultExts.size(); i++) {
            FenleiBaohuResultExt r = fenleiBaohuResultExts.get(i);
            /** 添加收件人 */
            to.add(r.getEmail());
            if(StringUtils.isNotEmpty(r.getAdjudicator())){
                adjudicatorList.add(r.getAdjudicator());
            }else{
                log.error("当前人员的裁决组长为空，"+ r.toString());
            }
            /** 添加抄送人 20210317修改  */
            if("三部".equals(r.getDep1())){
                cc.addAll(Arrays.asList(toSanBu.split(",")));
            }else if ("四部".equals(r.getDep1())){
                cc.addAll(Arrays.asList(toSiBu.split(",")));
            }else if ("一部".equals(r.getDep1())){
                cc.addAll(Arrays.asList(toYiBu.split(",")));
            }else if ("二部".equals(r.getDep1())){
                cc.addAll(Arrays.asList(toErBu.split(",")));
            }
            r.setFenpeitime(SimpleDateFormateUtils.getFormat(r.getFenpeitime()));
        }
        /** 首先添加规划部门 */
        cc.addAll(Arrays.asList(toGuiHua.split(",")));
        // 20210520 新增案件对应人员的裁决组长为收件人
        List<String> adjudicatorEmailList = fenleiBaohuUserinfoMapper.selectEmailByList(adjudicatorList);
        to.addAll(adjudicatorEmailList);
        //list 去重
        to = to.stream().distinct().collect(Collectors.toList());
        cc = cc.stream().distinct().collect(Collectors.toList());
        String content = FreeMarkerUtils.newlyAssignedCases(fenleiBaohuResultExts,freeMarkerConfigurer);
        //发送邮件
        return mailService.sendHtmlMail(to.toArray(new String[to.size()]),cc.toArray(new String[cc.size()]),"保护中心案件列表", content);
    }

    /**
     * 由于触发裁决，根据传入的 id发送邮件
     * @param id  案件编号
     * @param arbiter 裁决组长id
     * @return
     */
    public boolean sendEmailCaseArbiter(String id, String arbiter){
        List<String> to = new ArrayList<>();
        QueryWrapper qwMain = new QueryWrapper();
        qwMain.eq("id",id);
        FenleiBaohuMain fenleiBaohuMain = fenleiBaohuMainMapper.selectOne(qwMain);
        // 有一个为加工或者分类  有两个表示为都有
        ArrayList<String> arrayList = new ArrayList();
        // 获取案件相关人员代码
        List<FenleiBaohuResultExt> fenleiBaohuResults = fenleiBaohuResultMapper.selectListWithOrgNameByID(id);
        for(FenleiBaohuResultExt result : fenleiBaohuResults){
            to.add(result.getWorker().split("-")[0]);
            /**
             * 0317修改
             */
            if (!arrayList.contains(result.getDep1())){
                arrayList.add(result.getDep1());
            }
            /*if(!arrayList.contains(result.getOrgname())){
                arrayList.add(result.getOrgname());
            }*/
        }
        to.add(arbiter);
        //获取人员邮箱  相关人员 +  裁决组长
        to = fenleiBaohuUserinfoMapper.selectEmailByList(to);
        // 抄送人
        ArrayList<String> cc = new ArrayList<>();
        cc.addAll(Arrays.asList(toGuiHua_arbiter.split(",")));
        if (arrayList.size() != 0){
            for (int i=0;i<arrayList.size();i++){
                if ("三部".equalsIgnoreCase(arrayList.get(i))){
                    cc.addAll(Arrays.asList(toSanBu_arbiter.split(",")));
                }else if ("四部".equalsIgnoreCase(arrayList.get(i))){
                    cc.addAll(Arrays.asList(toSiBu_arbiter.split(",")));
                }else if ("一部".equalsIgnoreCase(arrayList.get(i))){
                    cc.addAll(Arrays.asList(toYiBu_arbiter.split(",")));
                }else if ("二部".equalsIgnoreCase(arrayList.get(i))) {
                    cc.addAll(Arrays.asList(toErBu_arbiter.split(",")));
                }
            }
        }
        /*if(arrayList.size() == 1){
            if("JG".equalsIgnoreCase(arrayList.get(0))){
                cc.addAll(Arrays.asList(toJiagong_arbiter.split(",")));
            }else{
                cc.addAll(Arrays.asList(toFenlei_arbiter.split(",")));
            }
        } else{
            cc.addAll(Arrays.asList(toJiagong_arbiter.split(",")));
            cc.addAll(Arrays.asList(toFenlei_arbiter.split(",")));
        }*/
        //拼接内容
        StringBuffer sb = new StringBuffer();
        sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;各位领导，案件编号为："+fenleiBaohuMain.getId()+"，案件名称为："+fenleiBaohuMain.getMingcheng() + ",已触发裁决，请及时处理。");
        return mailService.sendHtmlMail(to.toArray(new String[to.size()]),cc.toArray(new String[cc.size()]),"保护中心裁决案件提醒",sb.toString());
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
                                    "<h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;案件分配以及保护中心问题，请联系于惠（1839），孙泽文（1389）</h4>"+
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
                                    "<td style='border-width: 1px;padding: 8px;border-style: solid;border-color: #666666;background-color: #ffffff;'>" + b.getDep1()+" "+b.getDep2() + "</td>" +
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
                /*if(orgList.contains(b.getOrgname())){
                }else{
                    orgList.add(b.getOrgname());
                }*/
                // 0317修改
                if (orgList.contains(b.getDep1())){

                }else {
                    orgList.add(b.getDep1());
                }
            }
        }
        context.append("</table>");
        String title = "保护中心未完成案件间隔提醒";
        //配置分类加工部门的收件人
        for(int i = 0; i < orgList.size(); i++){
            String name = orgList.get(i);
            if("三部".equals(name)){
                ccEmailAddres.addAll(Arrays.asList(toSanBu_overtime.split(",")));
            }else if("四部".equals(name)) {
                ccEmailAddres.addAll(Arrays.asList(toSiBu_overtime.split(",")));
            }else if ("一部".equals(name)){
                ccEmailAddres.addAll(Arrays.asList(toYiBu_overtime.split(",")));
            }else if ("二部".equals(name)){
                ccEmailAddres.addAll(Arrays.asList(toErBu_overtime.split(",")));
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
    public HashMap<String, List<EmailIntervalEntity>> findAll() {
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
