package com.kfgs.pretrialclassification.updateIpc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kfgs.pretrialclassification.caseClassification.service.CaseClassificationService;
import com.kfgs.pretrialclassification.dao.FenleiBaohuMainMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuResultMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuUpdateipcMapper;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import com.kfgs.pretrialclassification.domain.FenleiBaohuUpdateIpc;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUpdateipcExt;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;
import com.kfgs.pretrialclassification.domain.response.CommonCode;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;
import com.kfgs.pretrialclassification.domain.response.QueryResult;
import com.kfgs.pretrialclassification.domain.response.UpdateIpcResponseEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.xpath.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author mango
 */
@Service
@Transactional
public class FenleiBaohuUpdateipcService extends ServiceImpl<FenleiBaohuUpdateipcMapper, FenleiBaohuUpdateIpc>  {

    @Autowired
    private FenleiBaohuUpdateipcMapper fenleiBaohuUpdateipcMapper;

    @Autowired
    private FenleiBaohuResultMapper fenleiBaohuResultMapper;

    @Autowired
    private FenleiBaohuMainMapper fenleiBaohuMainMapper;



    @Autowired
    CaseClassificationService caseClassificationService;

    public QueryResponseResult selectInitList(int pageNum,int size,String state){
        Page<FenleiBaohuUpdateIpc> page = new Page<>(pageNum,size);
        IPage<FenleiBaohuUpdateIpc> iPage = fenleiBaohuUpdateipcMapper.selectFenleiBaohuUpdateIpcPage(page, state);
        List<FenleiBaohuUpdateIpc> records = iPage.getRecords();
        QueryResult queryResult = new QueryResult();
        queryResult.setList(records);
        queryResult.setTotal(iPage.getTotal());
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }


    /**
     * 管理员处理分类号更正，的同意或者驳回操作
     * @param id 案件id
     * @param state 案件状态
     * @return
     */
    @Transactional
    public QueryResponseResult updateIpcState(String id, String state) {
        QueryWrapper<FenleiBaohuResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        List<FenleiBaohuResult> fenleiBaohuResultsCount = fenleiBaohuResultMapper.selectList(queryWrapper);
        queryWrapper.eq("state","2");
        List<FenleiBaohuResult> fenleiBaohuResultsStateTwoCount = fenleiBaohuResultMapper.selectList(queryWrapper);
        //一个案件如果多次提出更正就会有bug
        QueryWrapper<FenleiBaohuUpdateIpc> queryWrapperUpdateIpc = new QueryWrapper<>();
        queryWrapperUpdateIpc.eq("id",id).eq("state","0");
        String worker = fenleiBaohuUpdateipcMapper.selectOne(queryWrapperUpdateIpc).getWorker();
        if(fenleiBaohuResultsCount.size() == fenleiBaohuResultsStateTwoCount.size()){//表述均已出案
            return AllProChuanCaoZuo(id,state);
        }else{ // 表示有一人未出案
            return OneOrMoreNotChuAn(id,worker,state);
        }
    }

    /**
     * 管理在确定前，仍然有大于一个人没有出案
     * @param id 案件id
     * @param worker  案件提交人
     * @param state 状态
     * @return
     */
    private QueryResponseResult OneOrMoreNotChuAn(String id, String worker,String state) {
        QueryWrapper<FenleiBaohuUpdateIpc> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id).eq("worker",worker).eq("state","0");
        FenleiBaohuUpdateIpc updateIpc = fenleiBaohuUpdateipcMapper.selectOne(queryWrapper);
        if("1".equals(state)){//管理员同意出案
            FenleiBaohuResult fenleiBaohuResult = new FenleiBaohuResult();
            fenleiBaohuResult.setId(id);
            fenleiBaohuResult.setState("2");
            fenleiBaohuResult.setIPCMI(updateIpc.getIpcmi());
            fenleiBaohuResult.setIPCOI(updateIpc.getIpcoi());
            fenleiBaohuResult.setIpca(updateIpc.getIpca());
            fenleiBaohuResult.setCca(updateIpc.getCca());
            fenleiBaohuResult.setCci(updateIpc.getCci());
            fenleiBaohuResult.setCsets(updateIpc.getCsets());
            // 将新的分类号更新到result表中 并更新状态
            int i = fenleiBaohuResultMapper.saveClassificationInfo(fenleiBaohuResult,worker);
            // 更新main表状态
            int j = fenleiBaohuMainMapper.updateStateById(id,"1");
            //更新裁决表的状态
            updateIpc.setState("1");
            // queryWrapper 实体对象封装操作类（可以为 null,里面的 entity 用于生成 where 语句）
            int updateById = fenleiBaohuUpdateipcMapper.update(updateIpc,queryWrapper);
            if( i == j){
                return new QueryResponseResult(CommonCode.SUCCESS,null);
            }else{
                return new QueryResponseResult(CommonCode.FAIL,null);
            }
        }else if("2".equals(state)){//管理员不同意出案
            // 偷懒不写sql文件了
            updateIpc.setState("2");
            int updateById = fenleiBaohuUpdateipcMapper.update(updateIpc,queryWrapper);
            int i = fenleiBaohuResultMapper.updateStateByIdAndWorker(id,worker,"2");
            int j = fenleiBaohuMainMapper.updateStateById(id,"1");
            if( i == j){
                return new QueryResponseResult(CommonCode.SUCCESS,null);
            }else{
                return new QueryResponseResult(CommonCode.FAIL,null);
            }
        }else {
            return new QueryResponseResult(CommonCode.INVALID_PARAM,null);
        }
    }
    /**
     * 管理员点击后，该案件所有案件均正常出案
     */
    private QueryResponseResult AllProChuanCaoZuo(String id, String state) {
        if("1".equals(state)){//管理员同意出案
            QueryResponseResult queryResponseResult = caseClassificationService.lastFinish(id, null);
            return queryResponseResult;
        }else if("2".equals(state)){//管理员不同意出案
            //仅修改main/result表的状态
            FenleiBaohuMain main = new FenleiBaohuMain();
            main.setId(id);
            main.setState("2");
            int j = fenleiBaohuMainMapper.updateById(main);
            FenleiBaohuResult result = new FenleiBaohuResult();
            result.setId(id);
            result.setState("2");
            fenleiBaohuResultMapper.updateById(result);
            if( j == 1){
                return new QueryResponseResult(CommonCode.SUCCESS,null);
            }else{
                return new QueryResponseResult(CommonCode.FAIL,null);
            }
        }else {
            return new QueryResponseResult(CommonCode.INVALID_PARAM,null);
        }
    }

    //一下方法放弃使用-------------------------------------------------------------------------------------------------------------
    //分类号更正通过方法
    private QueryResponseResult updateIpcStateWithOne(String id){
        //获取案件分类号
        FenleiBaohuUpdateIpc fenleiBaohuUpdateipc = fenleiBaohuUpdateipcMapper.selectById(id);
        QueryWrapper<FenleiBaohuResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        List<FenleiBaohuResult> fenleiBaohuResults = fenleiBaohuResultMapper.selectList(queryWrapper);
        for(FenleiBaohuResult  result : fenleiBaohuResults){
            if(result.getWorker().equalsIgnoreCase(fenleiBaohuUpdateipc.getWorker())){
                result.setIPCMI(fenleiBaohuUpdateipc.getIpcmi());
                result.setIPCOI(fenleiBaohuUpdateipc.getIpcoi());
                result.setIpca(fenleiBaohuUpdateipc.getIpca());
                result.setCci(fenleiBaohuUpdateipc.getCci());
                result.setCca(fenleiBaohuUpdateipc.getCca());
                result.setCsets(fenleiBaohuUpdateipc.getCsets());
            }
        }
        // 拼接分类号
        String ipcmi = "", ipcoi="", ipca = "", cci = "", cca = "", csets= "";
        for(FenleiBaohuResult  result : fenleiBaohuResults){
            if(StringUtils.isNotBlank(result.getIPCMI())){
                ipcmi = result.getIPCMI();
            }
            if(StringUtils.isNotBlank(result.getIPCOI())){
                ipcoi = result.getIPCOI()+ "," + ipcoi ;
            }
            if(StringUtils.isNotBlank(result.getIpca())){
                ipca =  result.getIpca() + "," +ipca;
            }
            if(StringUtils.isNotBlank(result.getCci())){
                cci = result.getCci() + "," +cci;
            }
            if(StringUtils.isNotBlank(result.getCca())){
                cca =  result.getCca() + "," +cca;
            }
            if(StringUtils.isNotBlank(result.getCsets())){
                csets = result.getCsets() + "," +csets;
            }
        }
        ipcoi = ipcoi.substring(0,ipcoi.lastIndexOf(","));
        ipca = ipca.substring(0,ipca.lastIndexOf(","));
        cci = cci.substring(0,cci.lastIndexOf(","));
        cca = cca.substring(0,cca.lastIndexOf(","));
        csets = csets.substring(0,csets.lastIndexOf(","));

        String ipci = ipcmi;
        if(StringUtils.isNotBlank(ipcoi)){
            ipci = ipci + "," + ipcoi;
        }
        if(StringUtils.isNotBlank(ipca)){
            ipci = ipci + "*" + ipca;
        }
        //更新状态
        int i = fenleiBaohuUpdateipcMapper.updateByIdAndWorker(id,fenleiBaohuUpdateipc.getWorker(),"1");
        //将分类号添加到main表中
        int j = fenleiBaohuMainMapper.updateByIdAndWithOutNotExport(id,ipci,cci,cca,csets);
        if( i == 1 && j ==1){
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        }else{
            if(j == 0){
                return new QueryResponseResult(UpdateIpcResponseEnum.CANNOT_PASS_AMEND_MAIN,null);
            }else{
                return new QueryResponseResult(UpdateIpcResponseEnum.CANNOT_PASS_AMEND_UPDATEIPC,null);
            }
        }
    }

    //分类号更正驳回功能
    private QueryResponseResult updateIpcStateWithTwo(String id){
        FenleiBaohuUpdateIpc fenleiBaohuUpdateipc = fenleiBaohuUpdateipcMapper.selectById(id);
        int i = fenleiBaohuUpdateipcMapper.updateByIdAndWorker(id,fenleiBaohuUpdateipc.getWorker(),"2");
        if( i == 1){
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        }else{
            return new QueryResponseResult(CommonCode.FAIL,null);
        }
    }

}
