package com.kfgs.pretrialclassification.caseClassification.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.caseClassification.service.CaseClassificationService;
import com.kfgs.pretrialclassification.common.utils.AdjudicationBusinessUtils;
import com.kfgs.pretrialclassification.common.utils.DateUtil;
import com.kfgs.pretrialclassification.dao.FenleiBaohuLogMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuMainMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuResultMapper;
import com.kfgs.pretrialclassification.domain.FenleiBaohuLog;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuMainResultExt;
import com.kfgs.pretrialclassification.domain.response.CaseFinishResponseEnum;
import com.kfgs.pretrialclassification.domain.response.CommonCode;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;
import com.kfgs.pretrialclassification.domain.response.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CaseClassificationServiceImpl implements CaseClassificationService {

    @Autowired
    FenleiBaohuMainMapper fenleiBaohuMainMapper;

    @Autowired
    FenleiBaohuResultMapper fenleiBaohuResultMapper;

    @Autowired
    FenleiBaohuLogMapper fenleiBaohuLogMapper;

    @Override
    @Transactional
    //按状态查询分类员下案件
    public IPage findCaseByState(String pageNo, String limit, String state, String classtype, String user,String begintime,String endtime) {
        Map resultMap = new HashMap();
        Page<FenleiBaohuMainResultExt> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(limit));
        IPage<FenleiBaohuMainResultExt> iPage = fenleiBaohuResultMapper.selectCaseByState(page,state,classtype,user,begintime,endtime);
        return iPage;
    }

    @Override
    @Transactional
    //获取案件信息
    public Map<String, String> getCaseInfo(String id, String worker) {
        Map<String,String> map = new HashMap<>();
        map = fenleiBaohuResultMapper.selectCaseInfo(id, worker);
        return map;
    }

    @Override
    @Transactional
    public int updateResult(String id,String worker,String state){
        int res = 0;
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id",id);
        queryWrapper.like("worker",worker);
        //queryWrapper.eq("worker",worker);
        FenleiBaohuResult fenleiBaohuResult = fenleiBaohuResultMapper.selectOne(queryWrapper);
        String chuantime = DateUtil.formatFullTime(LocalDateTime.now());
        //获取分类号做拼接
        //获取主分类号、副分类号、附加信息
        String ipcmi = fenleiBaohuResult.getIPCMI();
        String ipcoi = fenleiBaohuResult.getIPCOI();
        String ipca = fenleiBaohuResult.getIpca();
        String ipci = "";
        if (ipcmi != "" && ipcmi != null){
            ipci += ipcmi;
        }
        if (ipcoi != "" && ipcoi != null){
            if (ipci != "" && ipci != null){
                ipci += ",";
                ipci += ipcoi;
            }else {
                ipci += ipcoi;
            }
        }
        if (ipca != "" && ipca != null){
            ipci += "*";
            ipci += ipca;
        }
        if (ipci != "" && ipci != null){
            fenleiBaohuResult.setIpci(ipci);
        }
        if (Integer.parseInt(state)<3){
            fenleiBaohuResult.setState(state);
            fenleiBaohuResult.setChuantime(Long.parseLong(chuantime));
            //个人单一出案
            res = fenleiBaohuResultMapper.update(fenleiBaohuResult,queryWrapper);
        }
        return  res;
    }

    @Override
    @Transactional
    public int updateMain(FenleiBaohuMain fenleiBaohuMain){
        String id = fenleiBaohuMain.getId();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id",id);
        String chuantime = DateUtil.formatFullTime(LocalDateTime.now());
        fenleiBaohuMain.setChuantime(Long.parseLong(chuantime));
        //更新main表
        int res = fenleiBaohuMainMapper.update(fenleiBaohuMain,queryWrapper);
        /*QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id",id);
        FenleiBaohuMain fenleiBaohuMain = fenleiBaohuMainMapper.selectOne(queryWrapper);
        String chuantime = DateUtil.formatFullTime(LocalDateTime.now());
        fenleiBaohuMain.setChuantime(Long.parseLong(chuantime));
        fenleiBaohuMain.setState(state);
        //更新main表
        fenleiBaohuMainMapper.update(fenleiBaohuMain,queryWrapper);
        return 1;*/
        return res;
    }

    @Override
    @Transactional
    public FenleiBaohuMain searchByCondition(String id, String sqh, String mingcheng) {
        FenleiBaohuMain fenleiBaohuMain = new FenleiBaohuMain();
        fenleiBaohuMain = fenleiBaohuMainMapper.searchByCondition(id, sqh, mingcheng);
        return fenleiBaohuMain;
    }

    @Override
    @Transactional
    //查找主副分详细信息
    public List<FenleiBaohuResult> getSingleResult(String id, String sqh, String mingcheng) {
        QueryWrapper queryWrapper = new QueryWrapper();
        //查询
        /*if (id == null || id == ""){
            if (sqh == null){
                sqh = "";
            }
            if (mingcheng == null){
                mingcheng = "";
            }
            //当根据sqh或mingcheng查询时获取案件id
            id = fenleiBaohuMainMapper.getCaseID(sqh,mingcheng);
        }*/
        queryWrapper.eq("id",id);
        List<FenleiBaohuResult> list = fenleiBaohuResultMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    @Transactional
    //获取转案人员列表
    public List<String> getTransWorkerList(String id) {
        List<String> list = new ArrayList<>();
        if(id == null || "".equals(id)){
            return null;
        }else {
            list = fenleiBaohuResultMapper.getTransWorkerList(id);
            return list;
        }
    }

    @Override
    @Transactional
    //转案
    public boolean caseTrans(List<FenleiBaohuResult> list) {
        if (list.size() == 0 || list == null){
            return false;
        }else {
            for (int i=0;i<list.size();i++){
                int res = fenleiBaohuResultMapper.insert(list.get(i));
                if (res == 1){
                    //log
                    FenleiBaohuLog log = new FenleiBaohuLog();
                    log.setId(list.get(i).getId());
                    log.setMessage("转案:由用户"+list.get(i).getFenpeiren()+"转案给:"+list.get(i).getWorker());
                    log.setTime(list.get(i).getFenpeitime());
                    log.setResult("转案成功");
                    fenleiBaohuLogMapper.insert(log);
                }
            }
        }
        return true;
    }

    @Override
    @Transactional
    //获取案件出案情况
    public List<String> getCaseUnFinish(String id) {
        List<String> list = new ArrayList<>();
        list = fenleiBaohuResultMapper.getCaseUnFinish(id);
        return list;
    }

    @Override
    /*最后一人出案时判断是否需要进入裁决
      裁决：返回理由
      不裁决：返回分类号
    */
    public QueryResponseResult caseRule(String id) {

        QueryResponseResult queryResponseResult = new QueryResponseResult();
        //获取result表中案件分类号情况
        //获取主分类号ipcmi
        List<String> ipcmiList = new ArrayList<>();
        ipcmiList = fenleiBaohuResultMapper.getIPCMI(id);
        //获取副分类号ipcoi
        List<String> ipcoiList = new ArrayList<>();
        ipcoiList = fenleiBaohuResultMapper.getIPCOI(id);
        //获取附加信息ipca
        List<String> ipcaList = new ArrayList<>();
        ipcaList = fenleiBaohuResultMapper.getIPCA(id);
        //获取组合码csets
        List<String> csetsList = new ArrayList<>();
        csetsList = fenleiBaohuResultMapper.getCSETS(id);
        //获取CCI
        List<String> cciList = new ArrayList<>();
        cciList = fenleiBaohuResultMapper.getCCI(id);
        //获取CCA
        List<String> ccaList = new ArrayList<>();
        ccaList = fenleiBaohuResultMapper.getCCA(id);

        //获取案件类型
        String type = fenleiBaohuMainMapper.getType(id);

        //拼接组合码,不用去重
        String csets = AdjudicationBusinessUtils.margeCsets(csetsList);

        //拼接CCI,需要去重
        String cci = AdjudicationBusinessUtils.margeCci(cciList);

        //拼接CCA，需要去重
        String cca = AdjudicationBusinessUtils.margeCca(ccaList);
        QueryResponseResult responseResult = AdjudicationBusinessUtils.JudgeWhetherToEnterTheRuling(id,ipcmiList, ipcoiList,ipcaList, csetsList, csets, cci, type);
        if(!"20000".equals(responseResult.getCode())){
            return  responseResult;
        }
        //拼接ipci:ipcmi+ipcoi+ipca
        //String ipci = "";
        //不用进裁决则保存合并后的分类号
        //合并ipci
        //拼接主分类号
        String ipci= AdjudicationBusinessUtils.mergeIPCI(ipcmiList,ipcoiList,ipcoiList);
        //分类号存入main表
        /*QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id",id);*/
        FenleiBaohuMain fenleiBaohuMain = new FenleiBaohuMain();
        fenleiBaohuMain.setId(id);
        fenleiBaohuMain.setCci(cci);
        fenleiBaohuMain.setCca(cca);
        fenleiBaohuMain.setCsets(csets);
        fenleiBaohuMain.setIpci(ipci);
        fenleiBaohuMain.setState("2");
        //fenleiBaohuMainMapper.update(fenleiBaohuMain,queryWrapper);
        Map map = new HashMap();
        map.put("mainUpdateInfo",fenleiBaohuMain);
        QueryResult queryResult = new QueryResult();
        queryResult.setMap(map);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

    @Override
    @Transactional
    public int updateCaseRule(String id,String ruleState) {
        int res = -1;
        res += fenleiBaohuResultMapper.updateCaseRule(id,ruleState);
        res += fenleiBaohuMainMapper.updateCaseRule(id, ruleState);
        return res;
    }





}
