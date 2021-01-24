package com.kfgs.pretrialclassification.caseArbiter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.common.exception.ArbiterEnum;
import com.kfgs.pretrialclassification.common.exception.CommonResponseEnum;
import com.kfgs.pretrialclassification.common.utils.AdjudicationBusinessUtils;
import com.kfgs.pretrialclassification.dao.*;
import com.kfgs.pretrialclassification.domain.FenleiBaohuAdjudication;
import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import com.kfgs.pretrialclassification.domain.FenleiBaohuUserinfo;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuAdjudicationExt;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuResultExt;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;
import com.kfgs.pretrialclassification.domain.request.ArbiterParam;
import com.kfgs.pretrialclassification.domain.response.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.applet.AppletContext;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Date: 2020-07-08-15-26
 * Module:
 * Description:
 *
 * @author:
 */
@Slf4j
@Service
public class CaseArbiterService   {


    @Autowired
    FenleiBaohuAdjudicationMapper fenleiBaohuAdjudicationMapper;
    @Autowired
    FenleiBaohuCPCMapper fenleiBaohuCPCMapper;
    @Autowired
    FenleiBaohuIPCMapper fenleiBaohuIPCMapper;
    @Autowired
    FenleiBaohuResultMapper fenleiBaohuResultMapper;
    @Autowired
    FenleiBaohuUserinfoMapper fenleiBaohuUserinfoMapper;
    @Autowired
    FenleiBaohuMainMapper fenleiBaohuMainMapper;

    /**
     * 获取裁决组长案件列表
     * @param pageNum 起始页
     * @param pageSize 每页条数
     * @return
     */
    public QueryResponseResult getArbiterInitList( int pageNum,int pageSize){

        Page<FenleiBaohuAdjudicationExt> page = new Page<>(pageNum,pageSize);
        FenleiBaohuUserinfoExt authentication = (FenleiBaohuUserinfoExt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ArbiterEnum.GET_USERNAME_FAILE.assertNotNull(authentication);
        IPage<FenleiBaohuAdjudicationExt> iPage = fenleiBaohuAdjudicationMapper.getArbiterInitList(page,authentication.getLoginname());
        //IPage<FenleiBaohuAdjudicationExt> iPage = fenleiBaohuAdjudicationMapper.getArbiterInitList(page,"000000");
        QueryResult quertResult =new QueryResult();
        quertResult.setList(iPage.getRecords());
        quertResult.setTotal(iPage.getTotal());
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, quertResult);
        return queryResponseResult;
    }

    /**
     * 获取待裁决员案件列表
     * @param pageNum 起始页
     * @param pageSize 每页条数
     * @return
     */
    public QueryResponseResult getArbiterPersonInitList( int pageNum,int pageSize){
        Page<FenleiBaohuAdjudicationExt> page = new Page<>(pageNum,pageSize);
        FenleiBaohuUserinfoExt authentication = (FenleiBaohuUserinfoExt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ArbiterEnum.GET_USERNAME_FAILE.assertNotNull(authentication);
        IPage<FenleiBaohuAdjudicationExt> iPage = fenleiBaohuAdjudicationMapper.getArbiterPersonInitList(page,authentication.getLoginname());
        //IPage<FenleiBaohuAdjudicationExt> iPage = fenleiBaohuAdjudicationMapper.getArbiterInitList(page,"000000");
        QueryResult quertResult =new QueryResult();
        quertResult.setList(iPage.getRecords());
        quertResult.setTotal(iPage.getTotal());
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS, quertResult);
        return queryResponseResult;
    }


    /**
     * 根据传入的实体更新分类号
     * @param ext 实体
     * @return
     */
    @Transactional
    public QueryResponseResult saveAribiterClassfication(FenleiBaohuAdjudicationExt ext){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id",ext.getId());
        queryWrapper.eq("state","7");
        FenleiBaohuAdjudication selectById = fenleiBaohuAdjudicationMapper.selectOne(queryWrapper);
        ArbiterEnum.GET_Arbiter_CASE_FAILE.assertNotNull(selectById);
       /**
        * FenleiBaohuUserinfoExt authentication = (FenleiBaohuUserinfoExt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        * ArbiterResponseEnum.GET_USERNAME_FAILE.assertNotNull(authentication);
        */
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        CommonResponseEnum.CANNOT_GET_USERINFO.assertNotNull(username);
        int i = fenleiBaohuAdjudicationMapper.saveAribiterClassfication(ext,username,"7");
        if(i == 1){
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        }else{
            int  A  =1 / 0;
            return new QueryResponseResult(CommonCode.FAIL,null);
        }

    }

    // 校验分类号规则
    public QueryResponseResult checkIPC_CCI_CCA(FenleiBaohuAdjudicationExt ext,String codeName){
        if( ext == null){
            return null;
        }
        String ipc = null;
        String codeType = "";
        if("ipcoi".equals(codeName)){
            ipc = ext.getIpcoi();
            codeType = "IPC";
        }else if ("ipca".equals(codeName)){
            ipc = ext.getIpca();
            codeType = "IPC";
        }else if ( "ipcmi".equals(codeName)){
            ipc = ext.getIpcmi();
            codeType = "IPC";
        }else if ("cci".equals(codeName)){
            ipc = ext.getCci();
            codeType = "CPC";
        }else if ("cca".equals(codeName)){
            ipc = ext.getCca();
            codeType = "CPC";
        }
        ipc = ipc.toUpperCase();
        String[] strs = ipc.split("[,;，；]");
        String subClass= "";
        String dazuClass = "";
        /**
         * 要求去重，2021年1月20日 09:02:51 修改为使用linkedhashset
         */
        LinkedHashSet<String> linkset = new LinkedHashSet<>();
        for(int i = 0; i < strs.length; i++){
            String ipc_ = strs[i];
            if(!StringUtils.isNotEmpty(ipc_)){
                continue;
            }
            if(ipc_.matches("[A-H][0-9][0-9][A-Z][0-9]+/[0-9]+")){
                subClass = ipc_.substring(0,4);
                dazuClass = ipc_.substring(0,ipc_.indexOf("/"));
                linkset.add(ipc_);
            }else if(ipc_.matches("[0-9]+/[0-9]+")){
                if(!subClass.equals("")){
                    ipc_ = subClass + ipc_;
                    linkset.add(ipc_);
                }else{
                    return new QueryResponseResult(ArbiterResponseEnum.CANNOT_RESOLVE_ABBREVIATION,null);
                }
            }else if(ipc_.matches("[0-9]+")){
                if(StringUtils.isNotBlank(dazuClass)){
                    ipc_ = dazuClass + ipc_;
                    linkset.add(ipc_);
                }else{
                    return new QueryResponseResult(ArbiterResponseEnum.CANNOT_RESOLVE_ABBREVIATION,null);
                }
            }else{
                return new QueryResponseResult(ArbiterResponseEnum.CANNOT_RESOLVE_ABBREVIATION_error,null);
            }
        }
        //校验当前分类号的版本
        /**
         * 20190523 接收徐勇需求，添加ipc，cpc分类号版本校验
         */
        if(!checkClassCodeVersion(linkset,codeType)){
            return new QueryResponseResult(ArbiterResponseEnum.CANNOT_RESOLVE_CLASSVERSION,null);
        }
        String newIpc= "";
        for(String str : linkset){
            newIpc =  newIpc + str + ",";
        }
        if(newIpc.length() > 1 && newIpc.endsWith(",")){
            newIpc = newIpc.substring(0,newIpc.length()-1);
        }

        if("ipcoi".equals(codeName)){
           ext.setIpcoi(newIpc);
        }else if ("ipca".equals(codeName)){
            ext.setIpca(newIpc);
        }else if ( "ipcmi".equals(codeName)){
            ext.setIpcmi(newIpc);
        }else if ("cci".equals(codeName)){
            ext.setCci(newIpc);
        }else if ("cca".equals(codeName)){
            ext.setCca(newIpc);
        }
        //判断彼此之间的重复 ipcoi,ipcmi,ipca
        /*if( ! (checkRepeatIpc(ext) && checkRepeatCpc(ext)) ){
            return new QueryResponseResult(ArbiterResponseEnum.IPCMI_IPCOI_IPCA_REPEAT,null);
        }*/
        if("IPC".equalsIgnoreCase(codeType)){
            if( !checkRepeatIpc(ext)){
                return new QueryResponseResult(ArbiterResponseEnum.IPCMI_IPCOI_IPCA_REPEAT,null);
            }
        }
        if("CPC".equalsIgnoreCase(codeType)){
            if(!checkRepeatCpc(ext)){
                return new QueryResponseResult(ArbiterResponseEnum.CCI_CCA_REPEAT,null);
            }
        }
        Map map = new HashMap<>();
        map.put("newClassification",newIpc);
        QueryResult queryResult = new QueryResult();
        queryResult.setMap(map);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

    //校验csets
    public QueryResponseResult checkClassCodeCsets(FenleiBaohuAdjudicationExt ext) {
        System.out.println(ext);
        String csets = ext.getCsets().replaceAll("\\s","")
                .replaceAll("，",",").replaceAll("；",";");
        String[] strs = csets.split(";");
        //不能超过99组
        if(strs.length > 99){
            return new QueryResponseResult(ArbiterResponseEnum.CSETS_TOO_MORE_ZU,null);
        }
        // 存放每个大组
        ArrayList<ArrayList<String> > list_zu = new ArrayList<ArrayList<String>>();
        /**
         * 存放所有的分类号 2021年1月20日 09:12:04 lxl  csets 不用去除
         */
        ArrayList<String> list = new ArrayList();
        //循环每组
        for(String str : strs){
            // 存放每组中的分类号
            ArrayList<String> temp = new ArrayList();
            String[] s = str.split(",");
            //list = getAllClassification(s);
            //往下10行写
            String subClass = "";
            String dazuClass = "";
            for(String s_csets : s){
                if(s_csets.matches("[A-H][0-9][0-9][A-Z][0-9]+/[0-9]+")){
                    subClass = s_csets.substring(0,4);
                    dazuClass = s_csets.substring(0,s_csets.indexOf("/"));
                    list.add(s_csets);
                    temp.add(s_csets);
                }else if(s_csets.matches("[0-9]+/[0-9]+")){
                    if(!subClass.equals("")){
                        s_csets = subClass + s_csets;
                        list.add(s_csets);
                        temp.add(s_csets);
                    }else{
                        return new QueryResponseResult(ArbiterResponseEnum.CANNOT_RESOLVE_ABBREVIATION,null);
                    }
                }else if(s_csets.matches("[0-9]+")){
                    if(StringUtils.isNotBlank(dazuClass)){
                        s_csets = dazuClass + s_csets;
                        list.add(s_csets);
                        temp.add(s_csets);
                    }else{
                        return new QueryResponseResult(ArbiterResponseEnum.CANNOT_RESOLVE_ABBREVIATION,null);
                    }
                }else{
                    return new QueryResponseResult(ArbiterResponseEnum.CANNOT_RESOLVE_ABBREVIATION_error,null);
                }
            }
            //每组不超过99个
            if(s.length > 99  || s.length < 2){
                return new QueryResponseResult(ArbiterResponseEnum.CSETS_TOO_MORE_GE,null);
            }
            //每组中的第一个
            String first = s[0];
            if(first.indexOf("/") == 8){
                //判断为2000系列 8位 A01F2015/0891
                if( (ext.getCca() == null)||  (ext.getCca().indexOf(first) == -1) ){
                    return new QueryResponseResult(ArbiterResponseEnum.CSETS_2000_CANNOT_BE_HERE,null);
                }
            }else{
                //判断为非2000系列 6位的 A24C5/396 或者 A44B13/02
               /* if( ext.getCca() == null || ext.getCci() == null  || ((ext.getCca().indexOf(first) ==-1 ) && (ext.getCci().indexOf(first) == -1))){
                    return new QueryResponseResult(ArbiterResponseEnum.CSETS_2000_MUST_IN_CCA_OR_CCI,null);
                }*/
                if(ext.getCca() == null && ext.getCci() == null ){
                    return new QueryResponseResult(ArbiterResponseEnum.CSETS_2000_MUST_IN_CCA_OR_CCI,null);
                }else{
                    String cci_cca = ext.getCca() + ext.getCci();
                    if(cci_cca.indexOf(first) == -1){
                        return new QueryResponseResult(ArbiterResponseEnum.CSETS_2000_MUST_IN_CCA_OR_CCI,null);
                    }
                }

            }
            list_zu.add(temp);
        }
        if(!checkClassCodeVersion(list,"CPC")){
            return new QueryResponseResult(ArbiterResponseEnum.CANNOT_RESOLVE_CLASSVERSION,null);
        }
        // 将处理好的分类号返回
        String newCsets= "";
        for(ArrayList<String> zu : list_zu){
            for(String str : zu){
                newCsets =  newCsets + str + ",";
            }
            newCsets = newCsets.substring(0,newCsets.length()-1) + ";";
        }
        Map map = new HashMap<>();
        map.put("newClassification",newCsets.substring(0,newCsets.length()-1));
        QueryResult queryResult = new QueryResult();
        queryResult.setMap(map);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

    private boolean checkClassCodeVersion(ArrayList<String> list, String codeName) {
        HashSet<String> set = new HashSet<>();
        if(codeName.equalsIgnoreCase("IPC")){
            set = fenleiBaohuIPCMapper.getHashSetFromIPCList(list );
        }else if(codeName.equalsIgnoreCase("CPC")){
            set = fenleiBaohuCPCMapper.getHashSetFromCPCList(list);
        }
        //将数据库对比出来的和传入的进行对比
        ArrayList clone =(ArrayList) list.clone();
        clone.removeAll(set);
        if(!clone.isEmpty() && clone.size() !=0){
            return false;
        }else{
            return true;
        }
    }

    private boolean checkClassCodeVersion(LinkedHashSet<String> list, String codeName) {
        HashSet<String> set = new HashSet<>();
        ArrayList<String> list_db = new ArrayList<String>(list);
        if(codeName.equalsIgnoreCase("IPC")){
            set = fenleiBaohuIPCMapper.getHashSetFromIPCList(list_db );
        }else if(codeName.equalsIgnoreCase("CPC")){
            set = fenleiBaohuCPCMapper.getHashSetFromCPCList(list_db);
        }
        //将数据库对比出来的和传入的进行对比
        ArrayList clone =(ArrayList) list_db.clone();
        clone.removeAll(set);
        if(!clone.isEmpty() && clone.size() !=0){
            return false;
        }else{
            return true;
        }
    }

    //校验ipca,ipcmi,ipcoi之间的重复
    private boolean checkRepeatIpc(FenleiBaohuAdjudicationExt ext) {
        ArrayList list = new ArrayList();
        if(ext.getIpcmi() != null){
            //加入主分
            list.add(ext.getIpcmi().toUpperCase());
        }
        if(ext.getIpcoi() != null){
            //加入副分
            String[] ipcoi = ext.getIpcoi().split(",");
            for(String s : ipcoi){
                list.add(s);
            }
        }
        if(ext.getIpca() != null){
            //加入附加信息
            String[] ipca = ext.getIpca().split(",");
            for(String s : ipca){
                list.add(s);
            }
        }
        HashSet set = new HashSet(list);
        if(set.size() == list.size()){
            return true;
        }else{
            return false;
        }
    }

    //校验cci,cca之间的重复
    private boolean checkRepeatCpc(FenleiBaohuAdjudicationExt ext) {
        ArrayList list = new ArrayList();

        if(ext.getCci() != null){
            //加入CCI
            String[] CCI = ext.getCci().split(",");
            for(String s : CCI){
                list.add(s);
            }
        }
        if(ext.getCca() != null){
            //加入附加信息
            String[] cca = ext.getCca().split(",");
            for(String s : cca){
                list.add(s);
            }
        }
        HashSet set = new HashSet(list);
        if(set.size() == list.size()){
            return true;
        }else{
            return false;
        }
    }
    //根据id查询裁决员给出的分类号
    public QueryResponseResult findClassInfoByID(String id,String state,String processingPerson) {
        //查找list
        QueryWrapper queryWrapper = new QueryWrapper();
        //模糊查询
        queryWrapper.eq("id",id);

        Map resultMap = new HashMap();
        List<FenleiBaohuResult> list = fenleiBaohuResultMapper.selectList(queryWrapper);
        resultMap.put("data",list);
        //查找裁决员给出的分类号
        queryWrapper.eq("state",state);
        processingPerson = processingPerson.split("-")[0];
        queryWrapper.eq("PROCESSINGPERSON",processingPerson);
        FenleiBaohuAdjudication adjudication = null;
        List<FenleiBaohuAdjudication> adjudicationList = fenleiBaohuAdjudicationMapper.selectList(queryWrapper);
        adjudication = adjudicationList.stream().max(Comparator.comparing(FenleiBaohuAdjudication :: getRukuTime)).get();
        resultMap.put("ipcmi",adjudication.getIpcmi());
        resultMap.put("ipcoi",adjudication.getIpcoi());
        resultMap.put("ipca",adjudication.getIpca());
        resultMap.put("cci",adjudication.getCci());
        resultMap.put("cca",adjudication.getCca());
        resultMap.put("csets",adjudication.getCsets());
        QueryResult queryResult = new QueryResult();
        queryResult.setMap(resultMap);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }
    /**
     *  根据部门去查询具有裁决权限的人员
     */
    public QueryResponseResult findAribiterPersonList(ArbiterParam arbiterParam) {
        List<String> name_list =  fenleiBaohuUserinfoMapper.selectListByDep1AndDep2(arbiterParam.getDep1(),arbiterParam.getDep2());
        QueryResult queryResult = new QueryResult();
        queryResult.setList(name_list);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }
    // 更新裁决员
    public QueryResponseResult updateAribiterPerson(ArrayList<ArbiterParam> list, String id) {
        int i = fenleiBaohuAdjudicationMapper.updateAdjudicatorById(list,id);
        if(1 == i){
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        }else{
            return new QueryResponseResult(CommonCode.FAIL,null);
        }
    }
    // 查询某个裁决案件的裁决员信息
    public QueryResponseResult findAdjudicatorWorker(String id) {
        String workerName = fenleiBaohuAdjudicationMapper.selectAdjudicatorWorker(id);
        List list =  Arrays.asList(workerName.split(","));
        List<ArbiterParam> arbiterParamList = fenleiBaohuUserinfoMapper.selectListByWorkerName(list);
        QueryResult queryResult = new QueryResult();
        queryResult.setList(arbiterParamList);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }
    // 裁决组长出案
    @Transactional
    public QueryResponseResult arbiterChuAn(String id) {
        //1.修改案件状态以及出案时间
        String finishTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        // 没卡人员不知道是否存在bug
        // 获取当前登录人员
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //未登录
        if("anonymousUser".equalsIgnoreCase(principal.toString())){ //anonymousUser
            log.error("当前用户未登录");
            return null;
        }else {
            FenleiBaohuUserinfoExt userDetails = (FenleiBaohuUserinfoExt) principal;

            QueryWrapper queryWrapperAD = new QueryWrapper();
            queryWrapperAD.eq("id",id);
            queryWrapperAD.eq("state","7");
            queryWrapperAD.eq("PROCESSINGPERSON",userDetails.getLoginname());
            FenleiBaohuAdjudication adjudication = fenleiBaohuAdjudicationMapper.selectOne(queryWrapperAD);

            // 1.更新了裁决表
            int i = fenleiBaohuAdjudicationMapper.updateCaseStateAndFinishTime("8",finishTime,id,userDetails.getLoginname());
            //2 将分类号插入main表中
            //2.1 拼装分类号 主分，副分*附加信息
            //主分必定不为空
            String ipci =  adjudication.getIpcmi();
            if(adjudication.getIpcoi() != null){
                ipci = ipci + ","+adjudication.getIpcoi();
            }
            if(adjudication.getIpca() != null){
                ipci = ipci + "*" + adjudication.getIpca();
            }
            //3. 更新main表状态 以及分类号
            int main_j = fenleiBaohuMainMapper.updateIpciCciCcaCsetsById(finishTime,ipci,adjudication.getCci(),adjudication.getCca(),adjudication.getCsets(),id,"2");
            //4. 更新result表中的状态 以及 6个分类号
            QueryWrapper<FenleiBaohuResult> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id",id);
            List<FenleiBaohuResult> fenleiBaohuResults = fenleiBaohuResultMapper.selectList(queryWrapper);
            queryWrapper.eq("state","7");
            FenleiBaohuResult result = new FenleiBaohuResult();
            result.setIPCMI(adjudication.getIpcmi());
            result.setIPCOI(adjudication.getIpcoi());
            result.setIpca(adjudication.getIpca());
            result.setCca(adjudication.getCca());
            result.setCci(adjudication.getCci());
            result.setCsets(adjudication.getCsets());
            result.setState("2");
            //int update_int = fenleiBaohuResultMapper.updateStateById(id,"2");
            int update_int = fenleiBaohuResultMapper.update(result,queryWrapper);
            if(main_j == 1 && i == 1 && (update_int == fenleiBaohuResults.size())){
                return new QueryResponseResult(CommonCode.SUCCESS,null);
            }else{
                int c = 1/0;
                return new QueryResponseResult(CommonCode.FAIL,null);
            }
        }
    }

    public QueryResponseResult insertIntoAdjudication(String id, QueryResponseResult responseResult){

        //获取相应参数根据id获取实体 此sql有排序
        //List<FenleiBaohuResultExt> fenleiBaohuResultExtList =  fenleiBaohuResultMapper.selectSimpleClassCodeAndClassificationById(id);
        //获取主分类号ipcmi
        /*List<String> ipcmiList = new ArrayList<>();
        ipcmiList = fenleiBaohuResultMapper.getIPCMI(id);
        //获取副分类号ipcoi
        List<String> ipcoiList = new ArrayList<>();
        ipcoiList = fenleiBaohuResultMapper.getIPCOI(id);
        //获取附加信息ipca
        List<String> ipcaList = new ArrayList<>();
        ipcaList = fenleiBaohuResultMapper.getIPCA(id);
        //获取CCI
        List<String> cciList = new ArrayList<>();
        cciList = fenleiBaohuResultMapper.getCCI(id);
        //获取组合码csets
        List<String> csetsList = new ArrayList<>();
        csetsList = fenleiBaohuResultMapper.getCSETS(id);
        //获取案件类型
        String type = fenleiBaohuMainMapper.getType(id);
        String cci = AdjudicationBusinessUtils.margeCci(cciList);
        //拼接组合码,不用去重
        String csets = AdjudicationBusinessUtils.margeCsets(csetsList);
        QueryResponseResult responseResult = AdjudicationBusinessUtils.JudgeWhetherToEnterTheRuling(id,ipcmiList, ipcoiList,ipcaList, csetsList, csets, cci, type);*/
        FenleiBaohuAdjudication item = (FenleiBaohuAdjudication)responseResult.getQueryResult().getMap().get("item");
        item.setProcessingreasons(responseResult.getMessage());
        if(responseResult.getCode() == 20000){
            log.error("出现逻辑问题：案件id:" + id + ",当前案件无法判断出触发裁决的原因。");
            return new QueryResponseResult(CommonCode.FAIL,null);
        }else if(responseResult.getCode() == 24004){
            //1. 无分类号
            //item.setProcessingreasons(responseResult.getMessage());
            int insert = fenleiBaohuAdjudicationMapper.insert(item);
            if( insert == 1){
                return new QueryResponseResult(CaseFinishResponseEnum.NO_CLASSIFICATION,null);
            }else{
                return new QueryResponseResult(CommonCode.FAIL,null);
            }
        }else if(responseResult.getCode() == 24001){
            //2.多个主分
            int insert = fenleiBaohuAdjudicationMapper.insert(item);
            if( insert == 1){
                return new QueryResponseResult(CaseFinishResponseEnum.ONE_MORE_IPCMI,null);
            }else{
                return new QueryResponseResult(CommonCode.FAIL,null);
            }
        } else if(responseResult.getCode() == 24005){
            //3.1无主分有一个分类员给副分：案件发给副分分类员对应的裁决组长
            int insert = fenleiBaohuAdjudicationMapper.insert(item);
            if( insert == 1){
                return new QueryResponseResult(CaseFinishResponseEnum.NO_IPCMI_ONE_IPCOI,null);
            }else{
                return new QueryResponseResult(CommonCode.FAIL,null);
            }
        }else if(responseResult.getCode() == 24006){
            //3.2 无主分有多个个分类元给副分：案件发给随机裁决组长
            int insert = fenleiBaohuAdjudicationMapper.insert(item);
            if( insert == 1){
                return new QueryResponseResult(CaseFinishResponseEnum.NO_IPCMI_MORE_IPCOI,null);
            }else{
                return new QueryResponseResult(CommonCode.FAIL,null);
            }
        }else if(responseResult.getCode() == 24007){
            //3.3无主分，无副分，仅有一个人有附加信息:给附加信息的分类员对应的裁决组长
            int insert = fenleiBaohuAdjudicationMapper.insert(item);
            if( insert == 1){
                return new QueryResponseResult(CaseFinishResponseEnum.NO_IPCMI_NO_IPCOI_ONE_IPCA,null);
            }else{
                return new QueryResponseResult(CommonCode.FAIL,null);
            }
        }else if(responseResult.getCode() == 24008){
            //3.4无主分，无副分，有多个人有附加信息:给附加信息的随机分类员的裁决组长
            int insert = fenleiBaohuAdjudicationMapper.insert(item);
            if( insert == 1){
                return new QueryResponseResult(CaseFinishResponseEnum.NO_IPCMI_NO_IPCOI_MORE_IPCA,null);
            }else{
                return new QueryResponseResult(CommonCode.FAIL,null);
            }
        }else if(responseResult.getCode() == 11111){
            //3.5 无主分无副分无附加信息（当前情况不应该出现，逻辑出现bug）
            log.error("出现逻辑问题：案件id:" + id + ",当前案件无法判断出触发裁决的原因。");
            return new QueryResponseResult(CommonCode.FAIL,null);
        }else if(responseResult.getCode() ==24002){
            //4校验组合码
            int insert = fenleiBaohuAdjudicationMapper.insert(item);
            if( insert == 1){
                return new QueryResponseResult(CaseFinishResponseEnum.NO_IPCMI_NO_IPCOI_MORE_IPCA,null);
            }else{
                return new QueryResponseResult(CommonCode.FAIL,null);
            }
        } else if(responseResult.getCode() ==24003 ) {
            //5.若是发明案件，判断CPC是否为空，为空进裁决
            int insert = fenleiBaohuAdjudicationMapper.insert(item);
            if( insert == 1){
                return new QueryResponseResult(CaseFinishResponseEnum.FM_NO_CCI,null);
            }else{
                return new QueryResponseResult(CommonCode.FAIL,null);
            }
        }else{
            log.error("出现逻辑问题：案件id:" + id + ",当前案件无法判断出触发裁决的原因。");
            return new QueryResponseResult(CommonCode.FAIL,null);
        }

        /*//1.无分类号
        if( ipcmiList.size() == 0 && ipcoiList.size() ==0 && ipcaList.size() == 0){
            fenleiBaohuAdjudication.setProcessingreasons("无分类号");
            //设置裁决组长
            //获取案件处理人员
            String worker = fenleiBaohuResultExtList.get(0).getWorker();
            String Adjudicator = fenleiBaohuUserinfoMapper.selectAdjudicatorByWorkerName(worker);
            fenleiBaohuAdjudication.setProcessingPerson(Adjudicator);
            int insert = fenleiBaohuAdjudicationMapper.insert(fenleiBaohuAdjudication);
            if( insert == 1){
                return new QueryResponseResult(CaseFinishResponseEnum.NO_CLASSIFICATION,null);
            }else{
                return new QueryResponseResult(CommonCode.FAIL,null);
            }
        } else if(ipcmiList.size() > 1){//2.多个主分
            //设置随机裁决组长
            int xiabiao = getArrayXiaoBiao(fenleiBaohuResultExtList,"ipcmi");
            //int xiabiao =(int) ( fenleiBaohuResultExtList.size() * Math.random());
            // 获取到随机到给出主分号的某个人
            String workerName = fenleiBaohuResultExtList.get(xiabiao).getWorker();
            //获取对应人的裁决组长
            String Adjudicator = fenleiBaohuUserinfoMapper.selectAdjudicatorByWorkerName(workerName);
            fenleiBaohuAdjudication.setProcessingPerson(Adjudicator);
            int insert = fenleiBaohuAdjudicationMapper.insert(fenleiBaohuAdjudication);
            if( insert == 1){
                return new QueryResponseResult(CaseFinishResponseEnum.ONE_MORE_IPCMI,null);
            }else{
                return new QueryResponseResult(CommonCode.FAIL,null);
            }
        } else if(ipcmiList.size() == 0){ //3.无主分
            if(ipcoiList.size() == 1){
                //3.1无主分有一个分类员给副分：案件发给副分分类员对应的裁决组长
                String workerName = fenleiBaohuResultExtList.get(0).getWorker();
                //获取对应人的裁决组长
                String Adjudicator = fenleiBaohuUserinfoMapper.selectAdjudicatorByWorkerName(workerName);
                fenleiBaohuAdjudication.setProcessingPerson(Adjudicator);
                int insert = fenleiBaohuAdjudicationMapper.insert(fenleiBaohuAdjudication);
                if( insert == 1){
                    return new QueryResponseResult(CaseFinishResponseEnum.NO_IPCMI_ONE_IPCOI,null);
                }else{
                    return new QueryResponseResult(CommonCode.FAIL,null);
                }

            }else if(ipcoiList.size() > 1){
                //3.2无主分有多个个分类元给副分：案件发给随机裁决组长
                // 获取到随机到给出副分号的某个人
                int xiabiao = getArrayXiaoBiao(fenleiBaohuResultExtList,"ipcoi");
                String workerName = fenleiBaohuResultExtList.get(xiabiao).getWorker();
                //获取对应人的裁决组长
                String Adjudicator = fenleiBaohuUserinfoMapper.selectAdjudicatorByWorkerName(workerName);
                fenleiBaohuAdjudication.setProcessingPerson(Adjudicator);
                int insert = fenleiBaohuAdjudicationMapper.insert(fenleiBaohuAdjudication);
                if( insert == 1){
                    return new QueryResponseResult(CaseFinishResponseEnum.NO_IPCMI_MORE_IPCOI,null);
                }else{
                    return new QueryResponseResult(CommonCode.FAIL,null);
                }

            }else if(ipcoiList.size() == 0 && ipcaList.size() == 1){
                //3.3无主分，无副分，仅有一个人有附加信息:给附加信息的分类员对应的裁决组长
                String workerName = fenleiBaohuResultExtList.get(0).getWorker();
                String Adjudicator = fenleiBaohuUserinfoMapper.selectAdjudicatorByWorkerName(workerName);
                fenleiBaohuAdjudication.setProcessingPerson(Adjudicator);
                int insert = fenleiBaohuAdjudicationMapper.insert(fenleiBaohuAdjudication);
                if( insert == 1){
                    return new QueryResponseResult(CaseFinishResponseEnum.NO_IPCMI_NO_IPCOI_ONE_IPCA,null);
                }else{
                    return new QueryResponseResult(CommonCode.FAIL,null);
                }
            }else if(ipcoiList.size() == 0 && ipcaList.size() > 1){
                //3.4无主分，无副分，有多个人有附加信息:给附加信息的随机分类员的裁决组长
                //获取到随机下标
                int xiabiao = getArrayXiaoBiao(fenleiBaohuResultExtList,"ipcai");
                String worker = fenleiBaohuResultExtList.get(xiabiao).getWorker();
                String adjudicator = fenleiBaohuUserinfoMapper.selectAdjudicatorByWorkerName(worker);
                fenleiBaohuAdjudication.setProcessingPerson(adjudicator);
                int insert = fenleiBaohuAdjudicationMapper.insert(fenleiBaohuAdjudication);
                if( insert == 1){
                    return new QueryResponseResult(CaseFinishResponseEnum.NO_IPCMI_NO_IPCOI_MORE_IPCA,null);
                }else{
                    return new QueryResponseResult(CommonCode.FAIL,null);
                }
            }else {
                //3.5 无主分无副分无附加信息（当前情况不应该出现，逻辑出现bug）
                log.error("出现逻辑问题：案件id:" + id + ",当前案件无法判断出触发裁决的原因。");
                return new QueryResponseResult(CommonCode.FAIL,null);
            }
        } else if("FM".equals(type) ){
            //拼接CCI,需要去重
            String cci = "";
            //先去重
            if (cciList.size() > 1){
                List<String> list_cci = new ArrayList<>();
                StringBuffer sb_cci = new StringBuffer();
                for (int i=0;i<cciList.size();i++){
                    if(!list_cci.contains(cciList.get(i))){
                        list_cci.add(cciList.get(i));
                        sb_cci.append(cciList.get(i) + ",");
                    }
                }
                cci = sb_cci.toString().substring(0,sb_cci.toString().length()-1);
            }else if (cciList.size() == 1) {
                cci = cciList.get(0);
            }
            if("FM".equals(type) && "".equals(cci)) {
                //若是发明案件，判断CPC是否为空，为空进裁决
                fenleiBaohuAdjudication.setProcessingreasons("发明案件的的CPC分类号为空");
                String worker = fenleiBaohuResultExtList.get(0).getWorker();
                String adjudicator = fenleiBaohuUserinfoMapper.selectAdjudicatorByWorkerName(worker);
                fenleiBaohuAdjudication.setProcessingPerson(adjudicator);
                int insert = fenleiBaohuAdjudicationMapper.insert(fenleiBaohuAdjudication);
                if( insert == 1){
                    return new QueryResponseResult(CaseFinishResponseEnum.NO_IPCMI_NO_IPCOI_MORE_IPCA,null);
                }else{
                    return new QueryResponseResult(CommonCode.FAIL,null);
                }
            }else{
                log.error("出现逻辑问题：案件id:" + id + ",当前案件无法判断出触发裁决的原因。");
                return new QueryResponseResult(CommonCode.FAIL,null);
            }
        } else {
            log.error("出现逻辑问题：案件id:" + id + ",当前案件无法判断出触发裁决的原因。");
            return new QueryResponseResult(CommonCode.FAIL,null);
        }*/
    }

    /**
     * 获取符合要求的下标
     * @param fenleiBaohuResultExtList  需要获取下标的数组
     * @param type 类型 ipcmi，ipcoi，ipca
     * @return
     */
    private int getArrayXiaoBiao(List<FenleiBaohuResultExt> fenleiBaohuResultExtList, String type) {
        int random = 0 ;
        int size = 0;
        if("ipcmi".equalsIgnoreCase(type)){
            for(FenleiBaohuResultExt fenleiBaohuResultExt : fenleiBaohuResultExtList){
                String ipcmi = fenleiBaohuResultExt.getIPCMI();
                if("" != ipcmi && null != ipcmi){
                    size ++;
                }
            }
            random = (int) (Math.random() * size);
        }else if("ipcoi".equalsIgnoreCase(type)) {
            for(FenleiBaohuResultExt fenleiBaohuResultExt : fenleiBaohuResultExtList){
                String ipcoi = fenleiBaohuResultExt.getIPCOI();
                if("" != ipcoi && null != ipcoi){
                    size ++;
                }
            }
            random = (int) (Math.random() * size);
        }
        else if("ipca".equalsIgnoreCase(type)) {

            for(FenleiBaohuResultExt fenleiBaohuResultExt : fenleiBaohuResultExtList){
                String ipca = fenleiBaohuResultExt.getIpca();
                if("" != ipca && null != ipca){
                    size ++;
                }
            }
            random = (int) (Math.random() * size);
        }
        return random;
    }

    //裁决组长出案之前进行，该案件的校验
    public boolean beforeTheCaseOfTheChiefJudge(String id,String processingPerson) {
        QueryResponseResult classInfoByID = this.findClassInfoByID(id,"7",processingPerson);
        Object ipcmi = classInfoByID.getQueryResult().getMap().get("ipcmi");
        if( null == ipcmi   ){
            return false;
        }else {
            if( 0 == ipcmi.toString().length()){
                return  false;
            }else{
                return true;
            }
        }
    }

    /*private ArrayList getAllClassification(String[] classifications ){
        ArrayList list = new ArrayList();
        //往下10行写
        String subClass = "";
        String dazuClass = "";
        for(String s_csets : classifications){
            if(s_csets.matches("[A-H][0-9][0-9][A-Z][0-9]+/[0-9]+")){
                subClass = s_csets.substring(0,4);
                dazuClass = s_csets.substring(0,s_csets.indexOf("/"));
                list.add(s_csets);
            }else if(s_csets.matches("[0-9]+/[0-9]+")){
                if(!subClass.equals("")){
                    s_csets = subClass + s_csets;
                    list.add(s_csets);
                }else{
                    return new QueryResponseResult(ArbiterResponseEnum.CANNOT_RESOLVE_ABBREVIATION,null);
                }
            }else if(s_csets.matches("[0-9]+")){
                if(StringUtils.isNotBlank(dazuClass)){
                    s_csets = dazuClass + s_csets;
                    list.add(s_csets);
                }else{
                    return new QueryResponseResult(ArbiterResponseEnum.CANNOT_RESOLVE_ABBREVIATION,null);
                }
            }else{
                return new QueryResponseResult(ArbiterResponseEnum.CANNOT_RESOLVE_ABBREVIATION,null);
            }
            list.add(s_csets);
        }
        return list;
    }*/

}
