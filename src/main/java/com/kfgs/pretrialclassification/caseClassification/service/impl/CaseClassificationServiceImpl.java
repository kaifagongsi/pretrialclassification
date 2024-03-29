package com.kfgs.pretrialclassification.caseClassification.service.impl;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.caseArbiter.service.CaseArbiterService;
import com.kfgs.pretrialclassification.caseClassification.service.CaseClassificationService;
import com.kfgs.pretrialclassification.common.config.RedisLockUtils;
import com.kfgs.pretrialclassification.common.utils.*;
import com.kfgs.pretrialclassification.dao.*;
import com.kfgs.pretrialclassification.domain.*;
import com.kfgs.pretrialclassification.domain.ext.*;
import com.kfgs.pretrialclassification.domain.response.*;
import com.kfgs.pretrialclassification.caseArbiter.service.IFenleiBaohuAdjuInforBackupService;
import com.kfgs.pretrialclassification.sendEmail.service.impl.SendEmailService;
import com.kfgs.pretrialclassification.updateIpc.service.FenleiBaohuUpdateipcService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    FenleiBaohuAdjudicationMapper fenleiBaohuAdjudicationMapper;

    @Autowired
    FenleiBaohuUpdateipcMapper fenleiBaohuUpdateipcMapper;

    @Autowired
    SendEmailService sendEmailService;

    @Autowired
    FenleiBaohuCpctoipcMapper fenleiBaohuCpctoipcMapper;

    @Autowired
    FenleiBaohuAdjuInforBackupMapper fenleiBaohuAdjuInforBackupMapper;

    @Autowired
    IFenleiBaohuAdjuInforBackupService fenleiBaohuAdjuInforBackupService;

    @Autowired
    FenleiBaohuCitycodeMapper fenleiBaohuCitycodeMapper;

    @Autowired
    RedisLockUtils redisLockUtils;

    @Autowired
    @Lazy
    private RedisTemplate<String, Object> redisTemplate;

    @NacosValue("${spring.application.name}")
    private String serverName;

    private BoundHashOperations<String, String, Object> pretrialClassification() {
        return redisTemplate.boundHashOps(serverName);
    }
    @Override
    @Transactional
    //按状态查询分类员下案件
    public IPage findCaseByState(String pageNo,String limit, String state, String classtype, String user,String begintime,String endtime) {
        Page<FenleiBaohuMainResultExt> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(limit));
        IPage<FenleiBaohuMainResultExt> iPage = fenleiBaohuResultMapper.selectCaseByState(page, state, classtype, user, begintime, endtime);
        List<FenleiBaohuMainResultExt> list = iPage.getRecords();
        // 遍历查询结果确定已出案案件出案类型
        if (state == "2"){
            for(int i=0;i<list.size();i++){
                FenleiBaohuMainResultExt fenleiBaohuMainResultExt = list.get(i);
                String id = fenleiBaohuMainResultExt.getId();
                String ipcmi = fenleiBaohuMainResultExt.getIpcmi();
                String ipcoi = fenleiBaohuMainResultExt.getIpcoi();
                // 判断是否进裁决
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("id",id);
                List<FenleiBaohuAdjudication> adjList = fenleiBaohuAdjudicationMapper.selectList(queryWrapper);
                if (adjList != null && adjList.size()!=0){
                    fenleiBaohuMainResultExt.setChuantype("出案进裁决");
                    continue;
                }
                if (ipcmi != null) {
                    fenleiBaohuMainResultExt.setChuantype("主分出案");
                }else if (ipcmi == null && ipcoi == null){
                    fenleiBaohuMainResultExt.setChuantype("空号出案");
                }else if (ipcmi == null && ipcoi != null) {
                    fenleiBaohuMainResultExt.setChuantype("副分出案");
                }
            }
        }
        // 判断是否有相似件
        for(int i=0;i<list.size();i++){
            FenleiBaohuMainResultExt fenleiBaohuMainResultExt = list.get(i);
            FenleiBaohuMainFuzzyMatchABCD abcd = JSON.parseObject(fenleiBaohuMainResultExt.getFuzzyMatchResult(), FenleiBaohuMainFuzzyMatchABCD.class);
            //2022-08-12 16:36:27 王磊-2 确认将名称完全一致，申请人不一致进行提醒
            if(abcd != null){
                if(StringUtils.isNotBlank(abcd.getA()) || StringUtils.isNotBlank(abcd.getB()) || StringUtils.isNotBlank(abcd.getC())){
                    fenleiBaohuMainResultExt.setSimilarCases(true);
                }
            }
            //查找citycde
            BoundHashOperations<String, String, Object> operations = pretrialClassification();
            HashMap map = (HashMap)operations.get("cityCode");
            String oraginization = fenleiBaohuMainResultExt.getOraginization();
            Object cityCode = map.get(oraginization);
            if(null != cityCode){
                fenleiBaohuMainResultExt.setCityCode(cityCode.toString());
            }else {
//                这样写前端有bug
//                fenleiBaohuMainResultExt.setCityCode(null);
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("city",oraginization);
                FenleiBaohuCitycode selectOne = fenleiBaohuCitycodeMapper.selectOne(queryWrapper);
                if(null != selectOne){
                    map.put(oraginization,selectOne.getCode());
                    operations.put("cityCode",map);
                    fenleiBaohuMainResultExt.setCityCode(selectOne.getCode());
                }else{
                    fenleiBaohuMainResultExt.setCityCode("");
                }

            }

        }
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
    public FenleiBaohuMain searchByCondition(String id, String sqr, String mingcheng) {
        try{
            FenleiBaohuMain fenleiBaohuMain = new FenleiBaohuMain();
            fenleiBaohuMain = fenleiBaohuMainMapper.searchByCondition(id, sqr, mingcheng);
            return fenleiBaohuMain;
        }catch (Exception e){
            if( "nested exception is org.apache.ibatis.exceptions.TooManyResultsException: Expected one result (or null) to be returned by selectOne(), but found: 2".equals(e.getMessage())){
                log.error("查询异常。异常，参数为：ID="+id+",sqr="+sqr+",mingcheng="+mingcheng);
            }
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public IPage searchByVagueCondition(String pageNo,String limit,String id, String sqr, String mingcheng) {
        Map resultMap = new HashMap();
        Page<FenleiBaohuMainResultExt> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(limit));
        IPage<FenleiBaohuMainResultExt> iPage = fenleiBaohuMainMapper.searchByVagueCondition(page,id,sqr,mingcheng);
        return iPage;
    }

    @Override
    @Transactional
    //查找主副分详细信息
    public List<FenleiBaohuResult> getSingleResult(String id, String sqr, String mingcheng) {
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
        list.forEach(item -> {
            item.setEdit(true);
            item.setSetSet(false);
        });
        return list;
    }

    @Override
    @Transactional
    //获取转案人员列表
    public List<String> getTransWorkerList(String id) {
        List<String> list = new ArrayList<>();
        if(id == null || "".equals(id)){
            return null;
        } else {
            list = fenleiBaohuResultMapper.getTransWorkerList(id);
            return list;
        }
    }

    /**
     * 转案
     *  已出案、裁决、更正案件不可进行转案
     * @param list
     * @return
     */
    @Override
    @Transactional
    public QueryResponseResult caseTrans(List<FenleiBaohuResult> list) {
        if (list.size() == 0 || list == null){
            return new QueryResponseResult(CaseClassificationEnum.NO_TRANS_WORKER,null);
        }
        //案件id
        String id = list.get(0).getId();
        String worker = list.get(0).getFenpeiren();
        /**
         * 03.04修改 转案前对人员重复转案进行判断
         */
        List<String> transperson = getTransWorkerList(id);
        for(int i=0;i<list.size();i++){
            if (transperson.contains(list.get(i).getWorker())){
                QueryResult result = new QueryResult();
                Map<String,String> map = new HashMap<>();
                map.put("name",list.get(i).getWorker());
                result.setMap(map);
                return new QueryResponseResult(CaseClassificationEnum.INCALID_CASE_RETRANS,result);
            }
        }
        /**
         * 02.20修改 转案前对案件状态进行判断
         */
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id",id);
        queryWrapper.eq("worker",worker);
        FenleiBaohuResult fenleiBaohuResult = fenleiBaohuResultMapper.selectOne(queryWrapper);
        String state = fenleiBaohuResult.getState();
        if ("2".equals(state)){ //已完成
            return new QueryResponseResult(CaseClassificationEnum.INVALID_CASE_FINISH,null);
        }
        if ("7".equals(state)){ //更正中
            return new QueryResponseResult(CaseClassificationEnum.INVALID_CASE_RULED,null);
        }
        if ("9".equals(state)){ //裁决中
            return new QueryResponseResult(CaseClassificationEnum.INVALID_CASE_UPDATE,null);
        }
        else {
            List<String> tranworker=new ArrayList<>();
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
            /**
             * 0317修改 增加转案发送邮件功能
             * 2021-12-21 暂时放弃
             */
            //sendEmailService.sendTransEmail(id,worker,tranworker);

            return new QueryResponseResult(CommonCode.SUCCESS,null);
        }
    }

    //获取案件出案情况
    @Override
    @Transactional
    public List<String> getCaseUnFinish(String id) {
        List<String> list = new ArrayList<>();
        list = fenleiBaohuResultMapper.getCaseUnFinish(id);
        return list;
    }

    /*最后一人出案时判断是否需要进入裁决
     *  裁决：返回理由
     *  不裁决：返回分类号
     */
    @Override
    public QueryResponseResult caseRule(String id) {

        //获取result表中案件分类号情况
        //获取主分类号ipcmi 并按照ipcmi 和worker排序
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
        QueryResponseResult responseResult = AdjudicationBusinessUtils.judgeWhetherToEnterTheRuling(id,ipcmiList, ipcoiList,ipcaList, csetsList, csets, cci, type);
        if(!"20000".equals(responseResult.getCode()) && responseResult.getCode() != 20000){
            // 表示进裁决
            return  responseResult;
        }else{ // 表示不进裁决正常出案
            //拼接ipci:ipcmi+ipcoi+ipca
            //String ipci = "";
            //不用进裁决则保存合并后的分类号
            //合并ipci
            //拼接主分类号
            String ipci= AdjudicationBusinessUtils.mergeIpci(ipcmiList,ipcoiList,ipcaList);
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

        /**
         * 02.20修改 已出案案件及裁决案件不可再进行保存操作(仅state为0和1时可以进行保存操作)
         */
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id",id);
        queryWrapper.eq("worker",worker);
        FenleiBaohuResult myResult = fenleiBaohuResultMapper.selectOne(queryWrapper);
        String myState = myResult.getState();
        if ("7".equals(myState)){ //裁决中
            return new QueryResponseResult(CaseClassificationEnum.INVALID_CASE_RULED,null);
        }
        if ("9".equals(myState)){ //更正中
            return new QueryResponseResult(CaseClassificationEnum.INVALID_CASE_UPDATE,null);
        }
        if ("2".equals(myState)){ //已出案
            return new QueryResponseResult(CaseClassificationEnum.INVALID_CASE_FINISH,null);
        }
        /*if (!("0".equals(myState) || "1".equals(myState))){
            return new QueryResponseResult(CaseClassificationEnum.INVALID_RESAVE,null);
        }*/
        //已出案案件不允许修改分类号
        if ("2".equals(state)){
            return new QueryResponseResult(CaseClassificationEnum.INVALID_RESAVE,null);
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
        QueryResponseResult queryResponseResult = null;
        //2021.02.20修改  增加出案前案件状态验证(只有状态为1时才可进行出案)
        String myFinish = fenleiBaohuResultMapper.getMyFinish(id,user);
        if ("7".equals(myFinish)){
            return new QueryResponseResult(CaseClassificationEnum.INVALID_CASE_RULED,null);
        }
        if (!"1".equals(myFinish)){
            return new QueryResponseResult(CaseClassificationEnum.INVALID_CASE_STATE,null);
        }
        /**
         *  加锁 没有值，则为true 2022年10月27日 15:40:56 lxl
         *  SET lock_key unique_value NX PX 10000
         *  lock_key 就是 key 键；
         *  unique_value 是客户端生成的唯一的标识，区分来自不同客户端的锁操作；
         *  NX 代表只在 lock_key 不存在时，才对 lock_key 进行设置操作；
         *  PX 10000 表示设置 lock_key 的过期时间为 10s，这是为了避免客户端发生异常而无法释放锁。
         *  Boolean lock = redisTemplate.opsForValue().setIfAbsent(id, "lock", 150, TimeUnit.SECONDS);
         */
        Boolean lock = redisLockUtils.getLock(id, "lock");
        if(lock){
            log.info(user+ "获取到锁("+id+")成功");
            List<String> unFinish = new ArrayList<>();
            //待出案的案件
            /*1.判断是否最后一个出案
                查询除自己以外其他未出案
                即当除自己之外其余人的案件状态为(0,1,9)时自己是最后一个未出案
            */
            unFinish = fenleiBaohuResultMapper.getCaseUnFinish(id);
            if (unFinish.size() == 1){
                //最后一个未出案
                queryResponseResult = lastFinish(id,user,queryResponseResult,CaseClassificationServiceImpl.class);
            }else if (unFinish.size() > 1){
                /**
                 * 不是最后一个出案,可直接出案,不改变main表状态
                 */
                int res = finishMyResult(id,user);
                if (res == 1){
                    queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,null);
                }else{
                    queryResponseResult = new QueryResponseResult(CaseClassificationEnum.INVALID_ERROR,null);
                }
            } else { //02.20修改  unFinish.size==0 案件已进入裁决不可再进行出案
                queryResponseResult = new QueryResponseResult(CaseClassificationEnum.INVALID_CASE_RULED,null);
            }
            //删除锁
//            redisTemplate.delete(id);
            redisLockUtils.releaseLock(id,"lock");
            log.info(user+ "释放锁("+id+")");
        }else{
            log.info(user+ "获取到锁失败");
            //获得锁失败
            queryResponseResult =  new QueryResponseResult(CaseClassificationEnum.INVALID_CASE_MORE_FINISH,null);
        }
        return queryResponseResult;
    }

    @Override
    public QueryResponseResult caseOutInBulk(List<String> idlist,String worker,HttpServletResponse response) {
        if (idlist == null || idlist.size() == 0){
            return new QueryResponseResult(CommonCode.INVALID_PARAM,null);
        } else {
            int arbFlag = 0;
            QueryResponseResult queryResponseResult = new QueryResponseResult();
            for (int i=0;i<idlist.size();i++) {
                queryResponseResult = caseFinish(idlist.get(i),worker);
                if (!queryResponseResult.isSuccess()){
                    arbFlag ++;
                }
            }
            if (arbFlag != 0){ // 代表有案件进裁决
                queryResponseResult.setSuccess(false);
                queryResponseResult.setMessage("有案件进入裁决，请到裁决列表查看详情");
                //return new QueryResponseResult(CommonCode.SUCCESS,null);
                return queryResponseResult;
            }else {
                queryResponseResult.setMessage("批量出案完成");
                return queryResponseResult;
            }
        }
    }

    /**
     * 最后一个出案,判断是否进裁决,不用裁决则出案完成更改result表和main表案件状态，否则进入裁决
     * 校验：无主分、多个主分、超过两人给出组合码且总数大于99组,FM案件CPC为空
     *  该方法有多处应用，修改时要注意
     * @param id 案件id
     * @param queryResponseResult 响应结果
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public QueryResponseResult lastFinish( String id,String user,QueryResponseResult queryResponseResult,Class clzss ){
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
         if (code != 20000){
            /**
             * 进入裁决，更改案件为裁决状态
             */
            // 01.21更改 最后一个出案进裁决先写入出案时间
            fenleiBaohuResult.setChuantime(Long.parseLong(chuantime));
            int result = fenleiBaohuResultMapper.update(fenleiBaohuResult,queryWrapper);
            /** 备份当前 result表中案件信息*/
            backUpResult(id);
            if (result == 1){
                int rule = updateCaseRule(id,queryResponseResult,clzss);
                if (rule == 1) {
                    // 01.22 2021年2月25日 10:31:55  新增发送邮件功能
                    String arbiter = ((FenleiBaohuAdjudication)queryResponseResult.getQueryResult().getMap().get("item")).getProcessingPerson();
                    // bug记录：不应该由于邮件发送失败造成事务回滚 ，解决方法：修改事务 的传播 将邮件发送设置为新事务
                    if(sendEmailService.sendEmailCaseArbiter(id,arbiter)){
                        queryResponseResult.setMessage( "已成功发送邮件" + queryResponseResult.getMessage() );
                    }else{
                        queryResponseResult.setMessage("邮件发送失败，请自行告知裁决组长" +queryResponseResult.getMessage() );
                    }
                    // 如果是更正表引起的裁决，需要将当前案件其他更正信息 进行更改状态
                    if(clzss == FenleiBaohuUpdateipcService.class){
                        fenleiBaohuUpdateipcMapper.updateOtherState(id,user);
                    }
                    return queryResponseResult;
                }else{
                    // 数据处理失败   要准备回滚数据 ---
                    //手动回滚事务
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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

            /**
             * 02.04 lsy修改 增加main表的主副分类员
             */
            //获取相关案件的result信息
            String mainClassifiers = "";
            StringBuilder viceClassifiers = new StringBuilder();
            QueryWrapper mainWrapper = new QueryWrapper();
            QueryWrapper viceWrapper = new QueryWrapper();
            //主分类员,未进裁决的案子一定有且仅有唯一一个主分类员
            mainWrapper.eq("id",id);
            mainWrapper.isNotNull("ipcmi");
            FenleiBaohuResult mainClassResult = fenleiBaohuResultMapper.selectOne(mainWrapper);
            mainClassifiers = mainClassResult.getWorker();
            //副分类员,可以有多个给了副分类号的分类员
            viceWrapper.eq("id",id);
            viceWrapper.isNull("ipcmi");
            List<FenleiBaohuResult> resultList = fenleiBaohuResultMapper.selectList(viceWrapper);
            if (resultList != null && resultList.size()!=0) {
                resultList.forEach(item ->{
                    viceClassifiers.append(item.getWorker()).append(",");
                });
            }
            if (result == 1){
                //fenleiBaohuMain.setId(map.get("id").toString());
                fenleiBaohuMain.setIpci(map.get("ipci").toString());
                fenleiBaohuMain.setCci(map.get("cci").toString());
                fenleiBaohuMain.setCca(map.get("cca").toString());
                fenleiBaohuMain.setCsets(map.get("csets").toString());
                fenleiBaohuMain.setMainClassifiers(mainClassifiers);
                String viceClassifiersString = viceClassifiers.toString();
                // 副分有可能为空
                if(StringUtils.isNotEmpty(viceClassifiersString)){
                    fenleiBaohuMain.setViceClassifiers(viceClassifiersString.substring(0,viceClassifiersString.length() -1));
                }else{
                }
               
                /**  更正不更新出案时间 20220819  lxl */
                if(clzss != FenleiBaohuUpdateipcService.class){
                    fenleiBaohuMain.setChuantime(Long.parseLong(chuantime));
                }else{
                    // 如果是由更正走到这里，那就判断有没有出案时间，没有就加上，有就不修改
                    if(null == fenleiBaohuMain.getChuantime() || 0 == fenleiBaohuMain.getChuantime()){
                        fenleiBaohuMain.setChuantime(Long.parseLong(chuantime));
                    }
                }
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

    /**
     * 将触发裁决的案件信息，保存到处决备份表中
     * @param id  触发裁决的案件编号
     */
    private void backUpResult(String id) {
        List<FenleiBaohuResult> fenleiBaohuResults = fenleiBaohuResultMapper.selectListByID(id);
        List<FenleiBaohuAdjuInforBackup> saveList = new ArrayList<>();
        for(FenleiBaohuResult result : fenleiBaohuResults){
            FenleiBaohuAdjuInforBackup backup = new FenleiBaohuAdjuInforBackup();
            BeanUtils.copyProperties(result,backup);
            backup.setFenpeitime(LocalDateTimeUtils.getTime(Long.parseLong(result.getFenpeitime())));
            backup.setChuantime(LocalDateTimeUtils.getTime(result.getChuantime()));
            saveList.add(backup);
        }
        fenleiBaohuAdjuInforBackupService.saveBatch(saveList);
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
        //02.20修改  进入裁决的案子不可再提更正
        String myState = result.getState();
        if ("7".equals(myState)){
            return new QueryResponseResult(CaseFinishResponseEnum.CASE_RULED,null);
        }
        if ("1".equals(export)){
            //已导出案件不可再提出更改
            return new QueryResponseResult(CaseFinishResponseEnum.EXPORT_FINISH,null);
        } else if ("0".equals(export)){

            // 李思瑶修改 fenleiBaohuResult为提交的修改，只需要拿到六个号
            log.info("fenleiBaohuResult："+fenleiBaohuResult.toString());
            /*String classtype = fenleiBaohuResult.getClasstype();
            String fenpeiren = fenleiBaohuResult.getFenpeiren();
            String fenpeitime = fenleiBaohuResult.getFenpeitime();*/
            //之前的出案时间
            Long chuantime = fenleiBaohuResult.getChuantime();
            //时间戳转字符串
            String time = timeStampToDate(chuantime,"yyyyMMddHHmmss");
            fenleiBaohuResult.setChuantime(Long.parseLong(time));
            // 更改后的分类号
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
                fenleiBaohuUpdateipc.setIPCMI(ipcmi);
            }
            if (ipcoi != null){
                fenleiBaohuUpdateipc.setIPCOI(ipcoi);
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
            log.info("分类号更正，开始保存至分类号更正表。。。");
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
                log.error("分类员{}，提出案件{}更正成功。",user,id);
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
     *  1.将result表 id案件的所有列，修改为裁决状态：7
     *  2.如果是由于更正引起的裁决，则不修改出案时间
     * @param id
     * @return
     */
    @Transactional
    public int updateCaseRule(String id,QueryResponseResult responseResult,Class clzss){
        int res = 0;
        String chuantime = DateUtil.formatFullTime(LocalDateTime.now());
        //result表
        res = fenleiBaohuResultMapper.updateResultRule(id,"7");
        if(clzss  != FenleiBaohuUpdateipcService.class){
            res = fenleiBaohuMainMapper.updateMainRule(id,chuantime,"7");
        }else{
            // 表示由更正引起的裁决
            FenleiBaohuMain baohuMain = fenleiBaohuMainMapper.selectById(id);
            if( null == baohuMain.getChuantime() || 0 == baohuMain.getChuantime()){
                res = fenleiBaohuMainMapper.updateMainRule(id,chuantime,"7");
            }else{
                res = fenleiBaohuMainMapper.updateMainRule(id,null,"7");
            }

        }
        caseArbiterService.insertIntoAdjudication(id,responseResult,clzss);
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
        /*
         2021年1月22日 17:23:36 李晓亮  修改在result 表中不在合并 ipci
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
        }*/
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
        if (timestamp == null || "null".equals(timestamp)){
            return "";
        }
        if (format == null || format.isEmpty()){
            format = "yyyyMMddHHmmss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(timestamp));
    }

    @Override
    public QueryResponseResult judgeIfLastFinish(String id ) {
        List<String> unFinish = fenleiBaohuResultMapper.getCaseUnFinish(id);
        if (unFinish.size() == 1){
            // 表示最后一个出案
            // 判断是否是否有多个主分,除去当前操作人外
            FenleiBaohuUserinfoExt loginUser = SecurityUtil.getLoginUser();
            int mapperIPCMINotNull = fenleiBaohuResultMapper.getIPCMINotNullAndWorker(id,loginUser.getWorkername());
            if(mapperIPCMINotNull == 0){
                return  new QueryResponseResult(CommonCode.SUCCESS,null);
            }else{
                //已存在主分号
                return new QueryResponseResult(CommonCode.FAIL,null);
            }
        }else{
            // 表示可以出案
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        }

    }

    @Override
    public QueryResponseResult cpcToIpc(String cci, String cca) {
        System.out.println(cci + "-------"+ cca);
        FenleiBaohuAdjudicationExt ext = new FenleiBaohuAdjudicationExt();
        QueryResponseResult result = null;
        String  cciAll = null;
        String ccaAll = null;
        String ipcmi = null;
        String ipcoi = null;
        String ipca = null;
        if(StringUtils.isNotEmpty(cci)){
            // 1.获取完整cci，并校验
            ext.setCci(cci);
            result = caseArbiterService.checkIPC_CCI_CCA(ext, "cci");
            if( result.getCode()!= 20000){
                return result;
            }else{
                cciAll = result.getQueryResult().getMap().get("newClassification").toString();
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("cpc",cciAll.split(",")[0]);
                // 1.1获取主分
                ipcmi = fenleiBaohuCpctoipcMapper.selectOne(queryWrapper).getIpc();
                // 1.2 获取副分
                // 1.2.1去掉主分号
                final String ipcmi_f = ipcmi;
                if(cciAll.contains(",")){
                    List<String> strings = Arrays.asList(cciAll.substring(cciAll.indexOf(",") + 1).split(","));
                    ipcoi = fenleiBaohuCpctoipcMapper.getIpcByCpcList(strings);
                    //副分自行去重
                    List<String> ipcoiList = ListUtils.delRepeatReturnList(Arrays.asList(ipcoi.split(",")));
                    // 副分去掉主分号
                    ipcoiList = ipcoiList.stream().filter(t ->
                            !t.equals(ipcmi_f)
                    ).collect(Collectors.toList());
                    /*ipcoi = ListUtils.delRepeatReturnString(Arrays.asList(ipcoi.split(",")));
                    //副分中，直接含有重复的主分号，去掉
                    ipcoi = ipcoi.replaceAll(ipcmi,"").replaceAll(",,",",");
                    if(ipcoi.length() == 1){
                        ipcoi = "";
                    }*/
                    if(ipcoiList.size() == 0){
                        ipcoi = "";
                    }else{
                        ipcoi = ipcoiList.stream().map(String::valueOf).collect(Collectors.joining(","));
                    }
                }else{
                    ipcoi = "";
                }
            }
        }
        if(StringUtils.isNotEmpty(cca)){
            // 2.获取完整cci，并校验
            ext.setCca(cca);
            result = caseArbiterService.checkIPC_CCI_CCA(ext, "cca");
            ccaAll = result.getQueryResult().getMap().get("newClassification").toString();
            ipca = fenleiBaohuCpctoipcMapper.getIpcByCpcList(Arrays.asList(ccaAll.split(",")));
            ipca = ListUtils.delRepeatReturnString(Arrays.asList(ipca.split(",")));
        }
        Map map = new HashMap();
        if(StringUtils.isNotEmpty(ipcmi)){
            map.put("ipcmi",ipcmi.replaceAll("CPCONLY","").replaceAll(",,",","));
        }else{
            map.put("ipcmi","");
        }

        if(StringUtils.isNotEmpty(ipcoi)){
            map.put("ipcoi",ipcoi.replaceAll("CPCONLY","").replaceAll(",,",","));
        }else{
            map.put("ipcoi","");
        }
        if(StringUtils.isNotEmpty(ipca)){
            ipca = ipca.replaceAll("CPCONLY","").replaceAll(",,",",");
            if(",".equalsIgnoreCase(ipca)){
                map.put("ipca","");
            }else{
                map.put("ipca",ipca);
            }
        }else{
            map.put("ipca","");
        }
        QueryResult queryResult = new QueryResult();
        queryResult.setMap(map);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

    @Override
    public QueryResponseResult searchFuzzyMatchResult(String id, FenleiBaohuMainFuzzyMatchABCD abcd){
        FenleiBaohuMain main = fenleiBaohuMainMapper.selectById(id);
        String fuzzyMatchResult = main.getFuzzyMatchResult();
        if(StringUtils.equals(fuzzyMatchResult,JSONObject.toJSONString(abcd))){
            /** 20220812 16:51:54  王磊-2 提出需要将 名称完成一直，申请人不一致进行提示显示 */
            List<String> list = Arrays.asList( (abcd.getA() + "," + abcd.getB() + "," + abcd.getC()).split(","));
            List<FenleiBaohuMain> mainList = fenleiBaohuMainMapper.selectByList(list);
            QueryResult queryResult = new QueryResult();
            queryResult.setList(mainList);
            return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        }else{
            return new QueryResponseResult(CaseClassificationEnum.MAIN_FUZZY_MATCH_REULT,null);
        }
    }
}
