package com.kfgs.pretrialclassification.caseArbiter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.common.exception.ArbiterEnum;
import com.kfgs.pretrialclassification.dao.*;
import com.kfgs.pretrialclassification.domain.FenleiBaohuAdjudication;
import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import com.kfgs.pretrialclassification.domain.FenleiBaohuUserinfo;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuAdjudicationExt;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;
import com.kfgs.pretrialclassification.domain.request.ArbiterParam;
import com.kfgs.pretrialclassification.domain.response.ArbiterResponseEnum;
import com.kfgs.pretrialclassification.domain.response.CommonCode;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;
import com.kfgs.pretrialclassification.domain.response.QueryResult;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Date: 2020-07-08-15-26
 * Module:
 * Description:
 *
 * @author:
 */
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
     * 获取待裁决案件列表
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
     * 根据传入的实体更新分类号
     * @param ext 实体
     * @return
     */
    public QueryResponseResult saveAribiterClassfication(FenleiBaohuAdjudicationExt ext){
        FenleiBaohuAdjudication selectById = fenleiBaohuAdjudicationMapper.selectById(ext.getId());
        ArbiterEnum.GET_Arbiter_CASE_FAILE.assertNotNull(selectById);
       /**
        * FenleiBaohuUserinfoExt authentication = (FenleiBaohuUserinfoExt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        * ArbiterResponseEnum.GET_USERNAME_FAILE.assertNotNull(authentication);
        */
        int i = fenleiBaohuAdjudicationMapper.saveAribiterClassfication(ext,"000000");
        if(i == 1){
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        }else{
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
        ArrayList<String> linkset = new ArrayList<>();
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
        // 存放所有的分类号
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
            set = fenleiBaohuIPCMapper.getHashSetFromIPCList(list);
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

    public QueryResponseResult findClassInfoByID(String id) {
        //查找list
        QueryWrapper queryWrapper = new QueryWrapper();
        //模糊查询
        queryWrapper.eq("id",id);
        Map resultMap = new HashMap();
        List<FenleiBaohuResult> list = fenleiBaohuResultMapper.selectList(queryWrapper);
        resultMap.put("data",list);
        //查找裁决员给出的分类号
        FenleiBaohuAdjudication adjudication = fenleiBaohuAdjudicationMapper.selectById(id);
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

    public QueryResponseResult findAribiterPersonList(ArbiterParam arbiterParam) {
        List<String> name_list =  fenleiBaohuUserinfoMapper.selectListByDep1AndDep2(arbiterParam.getDep1(),arbiterParam.getDep2());
        QueryResult queryResult = new QueryResult();
        queryResult.setList(name_list);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

    public QueryResponseResult updateAribiterPerson(ArrayList<ArbiterParam> list, String id) {
        int i = fenleiBaohuAdjudicationMapper.updateAdjudicatorById(list,id);
        if(1 == i){
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        }else{
            return new QueryResponseResult(CommonCode.FAIL,null);
        }
    }

    public QueryResponseResult findAdjudicatorWorker(String id) {
        String workerName = fenleiBaohuAdjudicationMapper.selectAdjudicatorWorker(id);
        List list =  Arrays.asList(workerName.split(","));
        List<ArbiterParam> arbiterParamList = fenleiBaohuUserinfoMapper.selectListByWorkerName(list);
        QueryResult queryResult = new QueryResult();
        queryResult.setList(arbiterParamList);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

    public QueryResponseResult arbiterChuAn(String id) {
        //1.修改案件状态
        // 没卡人员不知道是否存在bug
        int i = fenleiBaohuAdjudicationMapper.updateCaseState("8",id);
        //2.将分类号插入main表中
        FenleiBaohuAdjudication adjudication = fenleiBaohuAdjudicationMapper.selectById(id);
        //2.1 拼装分类号 主分，副分*附加信息
        String ipci =  adjudication.getIpcmi() + ","+adjudication.getIpcoi() + "*" + adjudication.getIpca();
        int main_j = fenleiBaohuMainMapper.updateIpciCciCcaCsetsById(ipci,adjudication.getCci(),adjudication.getCca(),adjudication.getCsets(),id);
        if(main_j == 1 && i == 1){
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        }else{
            int c = 1/0;
            return new QueryResponseResult(CommonCode.FAIL,null);
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
