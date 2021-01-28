package com.kfgs.pretrialclassification.updateIpc.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kfgs.pretrialclassification.caseClassification.service.CaseClassificationService;
import com.kfgs.pretrialclassification.common.exception.UpdateIpcEnum;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.xpath.operations.Bool;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author mango
 */
@Slf4j
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

    public QueryResponseResult selectInitList(String state){
        List<FenleiBaohuUpdateIpc> list = fenleiBaohuUpdateipcMapper.selectFenleiBaohuUpdateIpcPage(state);
        QueryResult queryResult = new QueryResult();
        queryResult.setList(list);
        queryResult.setTotal(list.size());
        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);
    }


    /**
     * 管理员处理分类号更正，的同意或者驳回操作
     * @param id 案件id
     * @param state 案件状态
     * @return
     */
    @Transactional
    public QueryResponseResult updateIpcState(String id, String state,String worker) {
        QueryWrapper<FenleiBaohuResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",id);
        //<FenleiBaohuResult> fenleiBaohuResultsCount = fenleiBaohuResultMapper.selectList(queryWrapper);
        //queryWrapper.eq("state","2");
        queryWrapper.in("state",new ArrayList<>(Arrays.asList("0","1","7")));
        List<FenleiBaohuResult> fenleiBaohuResultsStateTwoCount = fenleiBaohuResultMapper.selectList(queryWrapper);
        //一个案件如果多次提出更正就会有bug
       /* QueryWrapper<FenleiBaohuUpdateIpc> queryWrapperUpdateIpc = new QueryWrapper<>();
        queryWrapperUpdateIpc.eq("id",id).eq("state","0").eq("worker",worker);*/
       // String worker = fenleiBaohuUpdateipcMapper.selectOne(queryWrapperUpdateIpc).getWorker();
        if(fenleiBaohuResultsStateTwoCount.size() == 0){//表述均已出案
            return AllProChuanCaoZuo(id,worker,state);
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
        UpdateIpcEnum.CANNOT_UPDATE_NOT_GET_CASE.assertNotNull(updateIpc);
        if("1".equals(state)){//管理员同意出案
            FenleiBaohuResult fenleiBaohuResult = new FenleiBaohuResult();
            fenleiBaohuResult.setId(id);
            fenleiBaohuResult.setState("2");
            fenleiBaohuResult.setIPCMI(updateIpc.getIPCMI());
            fenleiBaohuResult.setIPCOI(updateIpc.getIPCOI());
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
    private QueryResponseResult AllProChuanCaoZuo(String id, String worker,String state) {
        if("1".equals(state)){//管理员同意出案
            // 0.更新result表中的分类号
            // 0.1查询新的分类号
            QueryWrapper queryWrapperUpdate = new QueryWrapper<>();
            queryWrapperUpdate.eq("id",id);
            queryWrapperUpdate.eq("worker",worker);
            queryWrapperUpdate.eq("state","0");
            try {
                FenleiBaohuUpdateIpc fenleiBaohuUpdateIpc = fenleiBaohuUpdateipcMapper.selectOne(queryWrapperUpdate);
                FenleiBaohuResult result = new FenleiBaohuResult();
                /*result.setId(id);
                result.setIPCMI(fenleiBaohuUpdateIpc.getIpcmi());
                result.setIPCOI(fenleiBaohuUpdateIpc.getIpcoi());
                result.setIpca(fenleiBaohuUpdateIpc.getIpca());
                result.set*/
                BeanUtils.copyProperties(fenleiBaohuUpdateIpc,result);
                result.setState("2");
                QueryWrapper queryWrapper  = new QueryWrapper<>();
                queryWrapper.eq("id",id);
                queryWrapper.eq("worker",worker);
                queryWrapper.eq("state","9");
                // 0.2 更新result表的分类号信息和案子状态
                int update = fenleiBaohuResultMapper.update(result,queryWrapper);
                if(update == 1){
                    /**
                     * 不考虑是否出发裁决，直接通过
                     */
                    fenleiBaohuUpdateIpc.setState("1");
                    fenleiBaohuUpdateipcMapper.update(fenleiBaohuUpdateIpc,queryWrapperUpdate);
                    return caseClassificationService.lastFinish(id, worker,null);
                }else{
                    return new QueryResponseResult(UpdateIpcResponseEnum.CANNOT_PASS_AMEND_UPDATEIPC,null);
                }
            }catch (Exception e){
                log.error("在 FENLEI_BAOHU_UPDATEIPC 表中数据异常：同一个人提交分类号更正时，该案子第一个第一次提交更正未处理。重复提交");
                e.printStackTrace();
                return new QueryResponseResult(UpdateIpcResponseEnum.DATA_ERROE,null);
            }
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
            //修改 updateIpc 表状态
            FenleiBaohuUpdateIpc updateIpc = new FenleiBaohuUpdateIpc();
            updateIpc.setId(id);
            updateIpc.setState("2");
            fenleiBaohuUpdateipcMapper.updateById(updateIpc);
            if( j == 1){
                return new QueryResponseResult(CommonCode.SUCCESS,null);
            }else{
                return new QueryResponseResult(CommonCode.FAIL,null);
            }
        }else {
            return new QueryResponseResult(CommonCode.INVALID_PARAM,null);
        }
    }
}
