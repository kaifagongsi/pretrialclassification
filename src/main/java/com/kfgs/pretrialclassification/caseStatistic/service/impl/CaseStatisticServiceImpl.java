package com.kfgs.pretrialclassification.caseStatistic.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.caseDisposition.service.MailService;
import com.kfgs.pretrialclassification.caseStatistic.service.CaseStatisticService;
import com.kfgs.pretrialclassification.common.utils.DateUtil;
import com.kfgs.pretrialclassification.common.utils.IPUtil;
import com.kfgs.pretrialclassification.common.utils.UserUtil;
import com.kfgs.pretrialclassification.dao.FenleiBaohuLogMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuMainMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuResultMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuUserinfoMapper;
import com.kfgs.pretrialclassification.domain.FenleiBaohuLog;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import com.kfgs.pretrialclassification.domain.FenleiBaohuUserinfo;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuResultExt;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CaseStatisticServiceImpl implements CaseStatisticService {

    @Autowired
    FenleiBaohuUserinfoMapper fenleiBaohuUserinfoMapper;
    @Autowired
    FenleiBaohuResultMapper fenleiBaohuResultMapper;

    @Autowired
    FenleiBaohuLogMapper fenleiBaohuLogMapper;

    @Autowired
    FenleiBaohuMainMapper fenleiBaohuMainMapper;


    @Autowired
    private MailService mailService;

    @Value("spring.mail.username")
    private String username;

    @Value("pretrialclassification.email.toFenlei")
    private String toFenlei;

    @Value("pretrialclassification.email.toJiagong")
    private String toJiagong;

    @Value("pretrialclassification.email.toAll")
    private String toAll;

    @Override
    public List findAreaName() {
        List<FenleiBaohuUserinfoExt> infoExtList = new ArrayList<>();
        List<FenleiBaohuUserinfo> areaList = fenleiBaohuUserinfoMapper.findAreaName();
        Map<String,String> map = new HashMap();
        for(int i = 0; i < areaList.size(); i++){
            FenleiBaohuUserinfo userinfo = areaList.get(i);
            //1.设置父类
            FenleiBaohuUserinfoExt parentExt = new FenleiBaohuUserinfoExt();
            parentExt.setName(userinfo.getAreaname());
            parentExt.setId(i);
            //2设置子类
            List<FenleiBaohuUserinfoExt> childrenList = new ArrayList<FenleiBaohuUserinfoExt>();
            String[] split = userinfo.getWorkername().split(",");
            for(int j = 0 ; j < split.length; j++){
                String s = split[j];
                FenleiBaohuUserinfoExt info = new FenleiBaohuUserinfoExt();
                info.setName(s);
                info.setId(j);
                info.setPId(i);
                childrenList.add(info);
            }
            parentExt.setChildren(childrenList);
            infoExtList.add(parentExt);
        }
        return infoExtList;
    }

    @Override
    public boolean updateWorker(FenleiBaohuMain fenleiBaohuMain, HttpServletRequest request) {
        FenleiBaohuResult databases = fenleiBaohuResultMapper.selectById(fenleiBaohuMain.getId());
        String worker_new = fenleiBaohuMain.getPdfPath();
        databases.setWorker(worker_new.substring(0,worker_new.indexOf("[")));
        int i = fenleiBaohuResultMapper.updateById(databases);
        if(i == 1){
            //记录log日志
            String ipAddr = IPUtil.getIpAddr(request);
            FenleiBaohuLog log = new FenleiBaohuLog();
            log.setId(databases.getId());
            log.setTime(DateUtil.formatFullTime(LocalDateTime.now()));
            //log.setMessage("案件调配：主机ip为：" + ipAddr + "， 当前用户："  + request.getSession().getAttribute("USER_IN_SESSION"));
            log.setMessage("案件调配：主机ip为：" + ipAddr + "， 当前用户："  + UserUtil.getLoginUser().getWorkername());
            log.setResult("调配成功");
            fenleiBaohuLogMapper.insert(log);
            return true;
        }else{
            return false;
        }
    }

    //进案量统计
    @Override
    public IPage countCaseIn(String pageNo, String limit,String begintime,String endtime) {
        Page<FenleiBaohuMain> page = new Page<FenleiBaohuMain>(Long.parseLong(pageNo),Long.parseLong(limit));
        return fenleiBaohuMainMapper.selectCaseIn(page,begintime,endtime);
    }

    //出案量统计
    @Override
    public IPage countCaseOut(String pageNo, String limit,String begintime,String endtime) {
        Page<FenleiBaohuResult> page = new Page<FenleiBaohuResult>(Long.parseLong(pageNo),Long.parseLong(limit));
        return fenleiBaohuResultMapper.selectCaseOut(page,begintime,endtime);
    }

    @Override
    public IPage selectByExample(String pageNo, String limit) {
        Page<FenleiBaohuMain> page = new Page<FenleiBaohuMain>(Long.parseLong(pageNo),Long.parseLong(limit));
        return fenleiBaohuMainMapper.selectCaseIn(page,null,null);
    }

    @Override
    public String findWorkerById(String id) {
        return  fenleiBaohuResultMapper.selectById(id).getWorker();
    }

    @Override
    @Transactional
    public IPage findAll(String pageNo,String limit) {
        Map resultMap = new HashMap();
        Page<FenleiBaohuMain> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(limit));
        IPage<FenleiBaohuMain> iPage = fenleiBaohuMainMapper.selectPage(page, null);
        return iPage;
    }

    @Override
    public boolean update(FenleiBaohuMain fenleiBaohuMain) {
        int i = fenleiBaohuMainMapper.updateById(fenleiBaohuMain);
        if(i == 1){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean postFenleiBaohuMain(FenleiBaohuMain fenleiBaohuMain) {
        int i = fenleiBaohuMainMapper.insert(fenleiBaohuMain);
        if(i == 1){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean deleteFenleiBaohuMain(FenleiBaohuMain fenleiBaohuMain) {
        int i = fenleiBaohuMainMapper.deleteById(fenleiBaohuMain);
        if(i == 1){
            return true;
        }else{
            return false;
        }
    }


    @Override
    public boolean sendEmail(String[] ids) {
        List<FenleiBaohuResultExt> fenleiBaohuResultExts = fenleiBaohuResultMapper.AfterDeploymentSendEmail(ids);
        List<String> recipients = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        int state = -1;
        sb.append("&nbsp;&nbsp;&nbsp;<table  border='1px' cellpadding='5px' style='font-size:14px;border-collapse: collapse;margin: 20px; '><thead><tr><th>预审编号</th><th>部门</th><th>主分人</th><th>发明名称</th><th>粗分号</th><th>分配时间</th></tr></thead><tbody>");
        for (int i = 0; i < fenleiBaohuResultExts.size(); i++) {
            FenleiBaohuResultExt r = fenleiBaohuResultExts.get(i);
            String fenpeitime = r.getFenpeitime();
            recipients.add(r.getEmail());

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
        if(state == 1){
            recipients.addAll(Arrays.asList(toFenlei.split(",")));
        }else if(state == 2){
            recipients.addAll(Arrays.asList(toJiagong.split(",")));
        }else if(state == 3){
            recipients.addAll(Arrays.asList(toAll.split(",")));
        }

        //list 去重
        recipients = recipients.stream().distinct().collect(Collectors.toList());
        //转为String[] 数组
        String[] to = recipients.toArray(new String[recipients.size()]);

        return mailService.sendHtmlMail(to, "保护中心案件列表", content);
    }

    private List<String> distinct(List<String> recipients) {
        HashSet set = new HashSet();
        set.add(recipients);
        return new ArrayList<>(set);
    }
}
