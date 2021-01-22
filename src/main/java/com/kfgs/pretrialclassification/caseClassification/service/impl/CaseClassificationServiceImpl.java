package com.kfgs.pretrialclassification.caseClassification.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.caseArbiter.service.CaseArbiterService;
import com.kfgs.pretrialclassification.caseClassification.service.CaseClassificationService;
import com.kfgs.pretrialclassification.common.utils.AdjudicationBusinessUtils;
import com.kfgs.pretrialclassification.common.utils.DateUtil;
import com.kfgs.pretrialclassification.dao.*;
import com.kfgs.pretrialclassification.domain.FenleiBaohuLog;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import com.kfgs.pretrialclassification.domain.FenleiBaohuUpdateIpc;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuMainResultExt;
import com.kfgs.pretrialclassification.domain.response.CaseFinishResponseEnum;
import com.kfgs.pretrialclassification.domain.response.CommonCode;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;
import com.kfgs.pretrialclassification.domain.response.QueryResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class CaseClassificationServiceImpl implements CaseClassificationService {

    @Autowired
    FenleiBaohuMainMapper fenleiBaohuMainMapper;

    @Autowired
    FenleiBaohuResultMapper fenleiBaohuResultMapper;

    @Autowired
    FenleiBaohuLogMapper fenleiBaohuLogMapper;

    @Autowired
    CaseArbiterService caseArbiterService;

    @Autowired
    FenleiBaohuUpdateipcMapper fenleiBaohuUpdateipcMapper;

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
        if(!"20000".equals(responseResult.getCode()) && responseResult.getCode() != 20000){
            // 表示进裁决
            return  responseResult;
        }else{ // 表示不进裁决正常出案
            //拼接ipci:ipcmi+ipcoi+ipca
            //String ipci = "";
            //不用进裁决则保存合并后的分类号
            //合并ipci
            //拼接主分类号
            String ipci= AdjudicationBusinessUtils.mergeIPCI(ipcmiList,ipcoiList,ipcoiList);
            //分类号存入main表
            /*QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("id",id);
            */
            /*FenleiBaohuMain fenleiBaohuMain = new FenleiBaohuMain();
            fenleiBaohuMain.setId(id);
            fenleiBaohuMain.setCci(cci);
            fenleiBaohuMain.setCca(cca);
            fenleiBaohuMain.setCsets(csets);
            fenleiBaohuMain.setIpci(ipci);
            fenleiBaohuMain.setState("2");*/
            //fenleiBaohuMainMapper.update(fenleiBaohuMain,queryWrapper);
            Map map = new HashMap();
            map.put("cci",cci);
            map.put("cca",cca);
            map.put("csets",csets);
            map.put("ipci",ipci);
            QueryResult queryResult = new QueryResult();
            queryResult.setMap(map);
            return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        }
    }

    /***
     * 保存分类号
     * @param fenleiBaohuResult
     * @return
     */
    @Override
    public QueryResponseResult saveClassificationInfo(FenleiBaohuResult fenleiBaohuResult) {
        String id = fenleiBaohuResult.getId();
        String worker = fenleiBaohuResult.getWorker();
        String cci = fenleiBaohuResult.getCci();
        String cca = fenleiBaohuResult.getCca();
        String ipca = fenleiBaohuResult.getIpca();
        String ipcmi = fenleiBaohuResult.getIPCMI();
        String ipcoi = fenleiBaohuResult.getIPCOI();
        String csets = fenleiBaohuResult.getCsets();
        String state = fenleiBaohuResult.getState();
        //已出案案件不允许修改分类号
        if ("2".equals(state)){
            return new QueryResponseResult(CommonCode.INVALID_PARAM,null);
        }else {
            FenleiBaohuResult result = new FenleiBaohuResult();
            result.setId(id);
            result.setCci(cci);
            result.setCca(cca);
            result.setCsets(csets);
            result.setIpca(ipca);
            result.setIPCMI(ipcmi);
            result.setIPCOI(ipcoi);
            result.setState("1");
            //FenleiBaohuResult result = fenleiBaohuResultMapper.selectById(id);
            int res = fenleiBaohuResultMapper.saveClassificationInfo(result, worker);
            if (res == 1) {
                return new QueryResponseResult(CommonCode.SUCCESS, null);
            } else {
                return new QueryResponseResult(CommonCode.FAIL, null);
            }
        }
    }

    @Override
    @Transactional
    public QueryResponseResult caseFinish(String id, String user) {
        QueryResponseResult queryResponseResult = new QueryResponseResult();
        List<String> unFinish = new ArrayList<>();
        //待出案的案件
        /*1.判断是否最后一个出案
            查询除自己以外其他未出案
        */
        boolean isLast = false;
        unFinish = fenleiBaohuResultMapper.getCaseUnFinish(id);
        if (unFinish.size() == 1){
            //最后一个未出案
            return lastFinish(id,user,queryResponseResult);
        }else if (unFinish.size() > 1){
            /**
             * 不是最后一个出案,可直接出案,不改变main表状态
             */
            int res = finishMyResult(id,user);
            if (res == 1){
                return new QueryResponseResult(CommonCode.SUCCESS,null);
            }
        }
        return null;
    }

    /**
     * 最后一个出案,判断是否进裁决,不用裁决则出案完成更改result表和main表案件状态，否则进入裁决
     * 校验：无主分、多个主分、超过两人给出组合码且总数大于99组,FM案件CPC为空
     *  该方法有多出应用，修改时要注意
     * @param id 案件id
     * @param queryResponseResult 响应结果
     * @return
     */
    @Override
    @Transactional
    public QueryResponseResult lastFinish( String id,String user,QueryResponseResult queryResponseResult ){
        if(queryResponseResult == null){
            queryResponseResult = new QueryResponseResult();
        }
        String chuantime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        QueryWrapper queryWrapper = new QueryWrapper();
        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper.eq("id",id);
        queryWrapper.eq("worker",user);
        queryWrapper1.eq("id",id);

        FenleiBaohuResult fenleiBaohuResult = fenleiBaohuResultMapper.selectOne(queryWrapper);

        FenleiBaohuMain fenleiBaohuMain = fenleiBaohuMainMapper.selectOne(queryWrapper1);
        //判断是否进入裁决
        queryResponseResult = caseRule(id);
        int code = queryResponseResult.getCode();
        String message = queryResponseResult.getMessage();
        if (code != 20000){
            /**
             * 进入裁决，更改案件为裁决状态
             */
            // 01.21更改 最后一个出案进裁决先写入出案时间
            fenleiBaohuResult.setChuantime(Long.parseLong(chuantime));
            int result = fenleiBaohuResultMapper.update(fenleiBaohuResult,queryWrapper);

            if (result == 1){
                int rule = updateCaseRule(id,queryResponseResult);
                if (rule == 1) {
                    return queryResponseResult;
                }else{
                    // 数据处理失败   要准备回滚数据 ---
                    int c = 1 / 0;
                    return new QueryResponseResult(CommonCode.FAIL,null);
                }
            } else {
                return new QueryResponseResult(CommonCode.FAIL,null);
            }

        }else {
            //不用裁决
            //更改result表和main表状态
            QueryResult mainResult = queryResponseResult.getQueryResult();
            Map map = mainResult.getMap();
            fenleiBaohuResult.setChuantime(Long.parseLong(chuantime));
            fenleiBaohuResult.setState("2");
            int result = fenleiBaohuResultMapper.update(fenleiBaohuResult,queryWrapper);
            if (result == 1){
                //fenleiBaohuMain.setId(map.get("id").toString());
                fenleiBaohuMain.setIpci(map.get("ipci").toString());
                fenleiBaohuMain.setCci(map.get("cci").toString());
                fenleiBaohuMain.setCca(map.get("cca").toString());
                fenleiBaohuMain.setCsets(map.get("csets").toString());
                fenleiBaohuMain.setChuantime(Long.parseLong(chuantime));
                fenleiBaohuMain.setState("2");
                int main = fenleiBaohuMainMapper.update(fenleiBaohuMain,queryWrapper1);
                if (main == 1){
                    return new QueryResponseResult(CommonCode.SUCCESS,null);
                }else {
                    return new QueryResponseResult(CommonCode.FAIL,null);
                }
            }
            return queryResponseResult;
        }
    }

    @Override
    @Transactional
    public QueryResponseResult caseCorrect(FenleiBaohuResult fenleiBaohuResult) {
        log.info("进入分类号更正");
        int res = 0;
        String id = fenleiBaohuResult.getId();
        String user = fenleiBaohuResult.getWorker();
        //查询案件是否已导出，已导出的案件不可再提出更正
        // 预测有bug 案件多个人做   已修改
        QueryWrapper<FenleiBaohuResult> queryWrapperResult =new QueryWrapper();
        queryWrapperResult.eq("id",id).eq("worker",user);
       // FenleiBaohuResult result = fenleiBaohuResultMapper.selectById(id);
        FenleiBaohuResult result = fenleiBaohuResultMapper.selectOne(queryWrapperResult);
        FenleiBaohuMain fenleiBaohuMain = fenleiBaohuMainMapper.selectById(id);
        String export = fenleiBaohuMain.getIsExport();
        if ("1".equals(export)){
            //已导出案件不可再提出更改
            return new QueryResponseResult(CaseFinishResponseEnum.EXPORT_FINISH,null);
        } else if ("0".equals(export)){
            log.info("fenleiBaohuResult："+fenleiBaohuResult.toString());
            String classtype = fenleiBaohuResult.getClasstype();
            String fenpeiren = fenleiBaohuResult.getFenpeiren();
            String fenpeitime = fenleiBaohuResult.getFenpeitime();
            Long chuantime = fenleiBaohuResult.getChuantime();
            //时间戳转字符串
            String time = timeStampToDate(chuantime,"yyyyMMddHHmmss");
            fenleiBaohuResult.setChuantime(Long.parseLong(time));
            String ipcmi = fenleiBaohuResult.getIPCMI();
            String ipcoi = fenleiBaohuResult.getIPCOI();
            String ipca = fenleiBaohuResult.getIpca();
            String cci = fenleiBaohuResult.getCci();
            String cca = fenleiBaohuResult.getCca();
            String csets = fenleiBaohuResult.getCsets();
            // 李晓亮修改获取旧的分类号的值
            String oldIpcmi = result.getIPCMI();
            String oldIpcoi = result.getIPCOI();
            String oldIpca = result.getIpca();
            String oldCci = result.getCci();
            String oldCca = result.getCca();
            String oldCsets = result.getCsets();

            FenleiBaohuUpdateIpc fenleiBaohuUpdateipc = new FenleiBaohuUpdateIpc();
            if (ipcmi != null){
                fenleiBaohuUpdateipc.setIpcmi(ipcmi);
            }
            if (ipcoi != null){
                fenleiBaohuUpdateipc.setIpcoi(ipcoi);
            }
            if (ipca != null){
                fenleiBaohuUpdateipc.setIpca(ipca);
            }
            if (cci != null){
                fenleiBaohuUpdateipc.setCci(cci);
            }
            if (cca != null){
                fenleiBaohuUpdateipc.setCca(cca);
            }
            if (csets != null){
                fenleiBaohuUpdateipc.setCsets(csets);
            }
            if (oldIpcmi != null){
                fenleiBaohuUpdateipc.setOldIpcmi(oldIpcmi);
            }
            if (oldIpcoi != null){
                fenleiBaohuUpdateipc.setOldIpcoi(oldIpcoi);
            }
            if (oldIpca != null){
                fenleiBaohuUpdateipc.setOldIpca(oldIpca);
            }
            if (oldCci != null){
                fenleiBaohuUpdateipc.setOldCci(oldCci);
            }
            if (oldCca != null){
                fenleiBaohuUpdateipc.setOldCca(oldCca);
            }
            if (oldCsets != null){
                fenleiBaohuUpdateipc.setOldCsets(oldCsets);
            }
            //设置id
            fenleiBaohuUpdateipc.setId(id);
            fenleiBaohuUpdateipc.setWorker(user);
            //设置时间
            String updateDate = DateUtil.formatFullTime(LocalDateTime.now());
            fenleiBaohuUpdateipc.setUploadtime(updateDate);
            fenleiBaohuUpdateipc.setState("0");
            log.info("开始包存");
            res = fenleiBaohuUpdateipcMapper.insert(fenleiBaohuUpdateipc);
            //更改result表状态
            // 发现bug  在点击更正以后，result表中的内容直接被修改了 应该只该表state 当前案件的当前人员的state
            res = fenleiBaohuResultMapper.updateStateByIdAndWorker(id,user,"9");
           /* fenleiBaohuResult.setState("9");
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("id",id);
            queryWrapper.eq("worker",user);
            res = fenleiBaohuResultMapper.update(fenleiBaohuResult,queryWrapper);*/
            //更改main表状态
            fenleiBaohuMain.setState("9");
            res = fenleiBaohuMainMapper.updateById(fenleiBaohuMain);
            if (res == 1){
                return new QueryResponseResult(CommonCode.SUCCESS,null);
            }else {
                log.error("分类员"+user+"提出案件"+id+"更正失败，无法判断原因。");
                return new QueryResponseResult(CommonCode.FAIL,null);
            }
        }else {
            return new QueryResponseResult(CommonCode.FAIL,null);
        }
    }

    /**
     * 更改案件为裁决状态
     * @param id
     * @return
     */
    @Transactional
    public int updateCaseRule(String id,QueryResponseResult responseResult){
        int res = 0;
        String chuantime = DateUtil.formatFullTime(LocalDateTime.now());
        //result表
        res = fenleiBaohuResultMapper.updateResultRule(id,"7");
        res = fenleiBaohuMainMapper.updateMainRule(id,chuantime,"7");
        QueryResponseResult queryResponseResult = caseArbiterService.insertIntoAdjudication(id,responseResult);
        return res;
    }

    public int finishMyResult(String id,String user){
        int res = 0;
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id",id);
        queryWrapper.like("worker",user);
        FenleiBaohuResult fenleiBaohuResult = fenleiBaohuResultMapper.selectOne(queryWrapper);
        String state = fenleiBaohuResult.getState();
        String chuantime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
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
        if (Integer.parseInt(state) == 1){
            state = "2";
            fenleiBaohuResult.setState(state);
            fenleiBaohuResult.setChuantime(Long.parseLong(chuantime));
            //个人单一出案
            res = fenleiBaohuResultMapper.update(fenleiBaohuResult,queryWrapper);
        }
        return  res;
    }

    //时间戳转字符串
    public static String timeStampToDate(Long timestamp,String format){
        if (timestamp == null || timestamp.equals("null")){
            return "";
        }
        if (format == null || format.isEmpty()){
            format = "yyyyMMddHHmmss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(timestamp));
    }

}
