package com.kfgs.pretrialclassification.caseArbiter.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.common.exception.ArbiterEnum;
import com.kfgs.pretrialclassification.dao.FenleiBaohuAdjudicationMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuCPCMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuIPCMapper;
import com.kfgs.pretrialclassification.domain.FenleiBaohuAdjudication;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuAdjudicationExt;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;
import com.kfgs.pretrialclassification.domain.response.ArbiterResponseEnum;
import com.kfgs.pretrialclassification.domain.response.CommonCode;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;
import com.kfgs.pretrialclassification.domain.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

    // 校验主分
    public QueryResponseResult checkIPC_CCI_CCA(String ipc,String codeName){
        if(codeName.equalsIgnoreCase("CSETS")){
            return checkClassCodeCsets(ipc,codeName);
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
                return new QueryResponseResult(ArbiterResponseEnum.CANNOT_RESOLVE_ABBREVIATION,null);
            }
        }

        //校验当前分类号的版本
        /**
         * 20190523 接收徐勇需求，添加ipc，cpc分类号版本校验
         */
        if(!checkClassCodeVersion(linkset,codeName)){
            return new QueryResponseResult(ArbiterResponseEnum.CANNOT_RESOLVE_CLASSVERSION,null);
        }
        String newIpc= "";
        for(String str : linkset){
            newIpc =  newIpc + str + ",";
        }
        if(newIpc.length() > 1 && newIpc.endsWith(",")){
            newIpc = newIpc.substring(0,newIpc.length()-1);
        }
        Map map = new HashMap<>();
        map.put("newClassification",newIpc);
        QueryResult queryResult = new QueryResult();
        queryResult.setMap(map);
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }

    //校验csets
    private QueryResponseResult checkClassCodeCsets(String ipc, String codeName) {
        return null;
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

}
