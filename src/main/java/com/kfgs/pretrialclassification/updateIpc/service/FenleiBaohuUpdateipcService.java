package com.kfgs.pretrialclassification.updateIpc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kfgs.pretrialclassification.dao.FenleiBaohuMainMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuResultMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuUpdateipcMapper;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import com.kfgs.pretrialclassification.domain.FenleiBaohuUpdateipc;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUpdateipcExt;
import com.kfgs.pretrialclassification.domain.response.CommonCode;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;
import com.kfgs.pretrialclassification.domain.response.QueryResult;
import com.kfgs.pretrialclassification.domain.response.UpdateIpcResponseEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author mango
 */
@Service
public class FenleiBaohuUpdateipcService extends ServiceImpl<FenleiBaohuUpdateipcMapper, FenleiBaohuUpdateipc>  {

    @Autowired
    FenleiBaohuUpdateipcMapper fenleiBaohuUpdateipcMapper;

    @Autowired
    FenleiBaohuResultMapper fenleiBaohuResultMapper;

    @Autowired
    FenleiBaohuMainMapper fenleiBaohuMainMapper;

    public QueryResponseResult selectInitList(int pageNum,int size,String state){
        Page<FenleiBaohuUpdateipc> page = new Page<>(pageNum,size);
        IPage<FenleiBaohuUpdateipcExt> iPage = fenleiBaohuUpdateipcMapper.selectFenleiBaohuUpdateIpcPage(page, state);
        List<FenleiBaohuUpdateipcExt> records = iPage.getRecords();
        QueryResult queryResult = new QueryResult();
        queryResult.setList(records);
        queryResult.setTotal(iPage.getTotal());
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }


    public QueryResponseResult updateIpcState(String id, String state) {
        if("1".equalsIgnoreCase(state)){
            return  updateIpcStateWithOne(id);
        }else if("2".equalsIgnoreCase(state)){
            return updateIpcStateWithTwo(id);
        }else {
            return new QueryResponseResult(CommonCode.INVALID_PARAM,null);
        }
    }
    //分类号更正通过方法
    private QueryResponseResult updateIpcStateWithOne(String id){
        //获取案件分类号
        FenleiBaohuUpdateipc fenleiBaohuUpdateipc = fenleiBaohuUpdateipcMapper.selectById(id);
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
        FenleiBaohuUpdateipc fenleiBaohuUpdateipc = fenleiBaohuUpdateipcMapper.selectById(id);
        int i = fenleiBaohuUpdateipcMapper.updateByIdAndWorker(id,fenleiBaohuUpdateipc.getWorker(),"2");
        if( i == 1){
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        }else{
            return new QueryResponseResult(CommonCode.FAIL,null);
        }
    }

}
