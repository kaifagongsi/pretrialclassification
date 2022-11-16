package com.kfgs.pretrialclassification.caseDisposition.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kfgs.pretrialclassification.caseDisposition.service.CaseChangeAllIpcCpcService;
import com.kfgs.pretrialclassification.common.utils.AdjudicationBusinessUtils;
import com.kfgs.pretrialclassification.common.utils.DateUtil;
import com.kfgs.pretrialclassification.common.utils.SecurityUtil;
import com.kfgs.pretrialclassification.dao.FenleiBaohuLogMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuMainMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuResultChangBackMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuResultMapper;
import com.kfgs.pretrialclassification.domain.FenleiBaohuLog;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import com.kfgs.pretrialclassification.domain.FenleiBaohuResultChangBack;
import com.kfgs.pretrialclassification.domain.response.CaseChangeIpcEnum;
import com.kfgs.pretrialclassification.domain.response.CaseClassificationEnum;
import com.kfgs.pretrialclassification.domain.response.CommonCode;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class CaseChangeAllIpcCpcServiceImpl implements CaseChangeAllIpcCpcService {

    @Autowired
    FenleiBaohuResultMapper fenleiBaohuResultMapper;

    @Autowired
    FenleiBaohuLogMapper fenleiBaohuLogMapper;

    @Autowired
    FenleiBaohuResultChangBackMapper fenleiBaohuResultChangBackMapper;

    @Autowired
    FenleiBaohuMainMapper fenleiBaohuMainMapper;

    @Override
    public QueryResponseResult changOneRow(FenleiBaohuResult result) {
        //1.记录
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id",result.getId());
        queryWrapper.eq("worker",result.getWorker());
        FenleiBaohuResult resultDB = fenleiBaohuResultMapper.selectOne(queryWrapper);
        FenleiBaohuLog baohuLog = new FenleiBaohuLog();
        baohuLog.setId(result.getId());
        String workername = SecurityUtil.getLoginUser().getWorkername();
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date());
        baohuLog.setMessage("当前登录人："+workername + "采用直接修改方式，修改分类员分类号，原始："+resultDB.toString()+",修改为："+result.toString()+"." +
                "访问接口为：/caseDisposition/caseChange/changeOne");
        baohuLog.setTime(date);
        baohuLog.setResult("直接修改分类号");
        int logInt = fenleiBaohuLogMapper.insert(baohuLog);
        //2.备份
        FenleiBaohuResultChangBack back = new FenleiBaohuResultChangBack();
        BeanUtils.copyProperties(resultDB,back);
        back.setOperatortime(date);
        back.setOperatoruser(workername);
        int backInt = fenleiBaohuResultChangBackMapper.insert(back);
        //3.更新
        resultDB.setIPCMI(result.getIPCMI());
        resultDB.setIPCOI(result.getIPCOI());
        resultDB.setIpca(result.getIpca());
        resultDB.setIpci(result.getIpci());
        resultDB.setCci(result.getCci());
        resultDB.setCca(result.getCca());
        resultDB.setCsets(result.getCsets());
        int updateInt = fenleiBaohuResultMapper.updateByModel(resultDB,resultDB.getId(),resultDB.getWorker(),resultDB.getState());
        if(logInt == 1 && backInt == 1 && updateInt == 1){
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        }else{
            return new QueryResponseResult(CommonCode.FAIL,null);
        }
    }

    @Override
    public QueryResponseResult deleteOneRow(FenleiBaohuResult result) {
        //1.记录等前操作
        FenleiBaohuLog baohuLog = new FenleiBaohuLog();
        baohuLog.setId(result.getId());
        String workername = SecurityUtil.getLoginUser().getWorkername();
        String date = DateUtil.formatFullTimeSplitPattern(LocalDateTime.now());
        baohuLog.setMessage("当前登录人："+workername + "采用直接删除方式，删除数据为："+ result.toString() +
                ",访问接口为：/caseDisposition/caseChange/deleteOne");
        baohuLog.setTime(date);
        baohuLog.setResult("直接修改分类号");
        int logInt = fenleiBaohuLogMapper.insert(baohuLog);
        //2.备份当前操作
        FenleiBaohuResultChangBack back = new FenleiBaohuResultChangBack();
        BeanUtils.copyProperties(result,back);
        back.setOperatortime(date);
        back.setOperatoruser(workername);
        int backInt = fenleiBaohuResultChangBackMapper.insert(back);
        //3.删除
        QueryWrapper delete = new QueryWrapper();
        delete.eq("id",result.getId());
        delete.eq("worker",result.getWorker());
        int deleteNum = fenleiBaohuResultMapper.delete(delete);
        if(logInt == 1 && backInt == 1 &&  deleteNum == 1){
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        }else{
            return new QueryResponseResult(CommonCode.FAIL,null);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QueryResponseResult caseFinishAll(List<FenleiBaohuResult> list) {
        //0.首先添加主分人的分类号
        //1.初始值
        List<String> ipcmiList = new ArrayList<>(list.size() * 2 );
        List<String> ipcoiList = new ArrayList<>(list.size() * 2);
        List<String> ipcaList = new ArrayList<>(list.size() * 2);
        List<String> csetsList = new ArrayList<>(list.size() * 2);
        List<String> cciList = new ArrayList<>(list.size() * 2);
        List<String> ccaList = new ArrayList<>(list.size() * 2);
        for(FenleiBaohuResult result : list){
            if(StringUtils.isNotEmpty(result.getIPCMI())){
                ipcmiList.add(result.getIPCMI());
                if(StringUtils.isNotEmpty(result.getIPCOI())){
                    ipcoiList.add(result.getIPCOI());
                }
                if(StringUtils.isNotEmpty(result.getIpca())){
                    ipcaList.add(result.getIpca());
                }
                if(StringUtils.isNotEmpty(result.getCca())){
                    ccaList.add(result.getCca());
                }
                if(StringUtils.isNotEmpty(result.getCci())){
                    cciList.add(result.getCci());
                }
                if(StringUtils.isNotEmpty(result.getCsets())){
                    csetsList.add(result.getCsets());
                }
                list.remove(result);
                break;
            }
        }
        for(FenleiBaohuResult result : list){
            if(StringUtils.isNotEmpty(result.getIPCMI())){
                ipcmiList.add(result.getIPCMI());
            }
            if(StringUtils.isNotEmpty(result.getIPCOI())){
                ipcoiList.add(result.getIPCOI());
            }
            if(StringUtils.isNotEmpty(result.getIpca())){
                ipcaList.add(result.getIpca());
            }
            if(StringUtils.isNotEmpty(result.getCca())){
                ccaList.add(result.getCca());
            }
            if(StringUtils.isNotEmpty(result.getCci())){
                cciList.add(result.getCci());
            }
            if(StringUtils.isNotEmpty(result.getCsets())){
                csetsList.add(result.getCsets());
            }
        }
        //拼接组合码,不用去重
        String csets = AdjudicationBusinessUtils.margeCsets(csetsList);
        //拼接CCI,需要去重
        String cci = AdjudicationBusinessUtils.margeCci(cciList);
        //拼接CCA，需要去重
        String cca = AdjudicationBusinessUtils.margeCca(ccaList);
        String ipci= AdjudicationBusinessUtils.mergeIPCI(ipcmiList,ipcoiList,ipcaList);
        FenleiBaohuMain main = new FenleiBaohuMain();
        main.setId(list.get(0).getId());
        main.setIpci(ipci);
        main.setCca(cca);
        main.setCci(cci);
        main.setCsets(csets);
        int updateById = fenleiBaohuMainMapper.updateById(main);
        if(updateById == 1){
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        }else{
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new QueryResponseResult(CaseChangeIpcEnum.CASE_MAIN_INFO_ERROR,null);
        }
    }
}
