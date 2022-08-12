package com.kfgs.pretrialclassification.caseDisposition.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.caseDisposition.service.CaseAllocationService;
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
import com.kfgs.pretrialclassification.sendEmail.service.impl.SendEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CaseAllocationServiceImpl implements CaseAllocationService {

    @Autowired
    FenleiBaohuUserinfoMapper fenleiBaohuUserinfoMapper;
    @Autowired
    FenleiBaohuResultMapper fenleiBaohuResultMapper;

    @Autowired
    FenleiBaohuLogMapper fenleiBaohuLogMapper;

    @Autowired
    FenleiBaohuMainMapper fenleiBaohuMainMapper;

    @Autowired
    SendEmailService sendEmailService;








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
        String worker_old = fenleiBaohuMain.getWorker();
        String worker_new = fenleiBaohuMain.getPdfPath().substring(0,fenleiBaohuMain.getPdfPath().indexOf("["));
        databases.setWorker(worker_new);
        int i = fenleiBaohuResultMapper.updateById(databases);
        if(i == 1){
            //记录log日志
            String ipAddr = IPUtil.getIpAddr(request);
            FenleiBaohuLog log = new FenleiBaohuLog();
            log.setId(databases.getId());
            log.setTime(DateUtil.formatFullTime(LocalDateTime.now()));
            log.setMessage("案件调配：主机ip为：" + ipAddr + "， 当前用户："  + UserUtil.getLoginUser().getWorkername() + " 将 "+worker_old+" 的案件" + fenleiBaohuMain.getId() + " 调配到：" + worker_new  );
            log.setResult("调配成功");
            fenleiBaohuLogMapper.insert(log);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public IPage findMainByState(String pageNo, String limit,String dep1,String dep2) {
        Page<FenleiBaohuMain> page = new Page<FenleiBaohuMain>(Long.parseLong(pageNo),Long.parseLong(limit));
        return fenleiBaohuMainMapper.findMainByState(page,"1",dep1,dep2);
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
    public boolean sendEmail(String[] ids)  {
        try {
            return sendEmailService.sendEmail(ids);
        }catch (Exception e){
            return false;
        }
    }

}
