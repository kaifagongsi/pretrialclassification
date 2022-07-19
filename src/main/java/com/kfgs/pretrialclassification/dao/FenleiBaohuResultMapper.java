package com.kfgs.pretrialclassification.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.domain.EmailIntervalEntity;
import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuMainResultExt;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuResultExt;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Date: 2020-01-19-10-27
 * Module:
 * Description:
 *
 * @author:
 */
@Repository
public interface FenleiBaohuResultMapper extends BaseMapper<FenleiBaohuResult> {

    int updateByModel(@Param("entity")FenleiBaohuResult result,@Param("id") String id,@Param("worker")String worker,@Param("state")String state);

    List<FenleiBaohuResultExt> AfterDeploymentSendEmail(String[] ids);

    IPage<FenleiBaohuResultExt> selectCaseOut(Page<FenleiBaohuResult> page, @Param("begintime") String begintime, @Param("endtime") String endtime, @Param("type") String type, @Param("dept1") String dept1, @Param("dept2") String dept2,@Param("userName") String userName);

    List<FenleiBaohuResult> findClassInfoByID(String id,String sqh,String mingcheng);

    List<FenleiBaohuResult> selectListByID(@Param("id")String id);

    //IPage<FenleiBaohuMainResultExt> selectCaseByState(IPage<FenleiBaohuMainResultExt> page, @Param("state") String state,@Param("classtype") String classtype, @Param("user") String user,@Param("begintime") String begintime, @Param("endtime") String endtime);

    List<FenleiBaohuMainResultExt> selectCaseByState(@Param("state") String state,@Param("classtype") String classtype, @Param("user") String user,@Param("begintime") String begintime, @Param("endtime") String endtime);

    Map selectCaseInfo (@Param("id")String id, @Param("worker") String worker);

    int selectCaseStateCount(@Param("id") String id);

    List<String> getTransWorkerList(@Param("id") String id);

    List<String> getCaseUnFinish(@Param("id") String id);

    String getMyFinish(@Param("id") String id,@Param("worker") String worker);

    List<String> getIPCMI(@Param("id") String id);

    List<String> getIPCOI(@Param("id") String id);

    List<String> getIPCA(@Param("id") String id);

    List<String> getCSETS(@Param("id") String id);

    List<String> getCCI(@Param("id") String id);

    List<String> getCCA(@Param("id") String id);

    int updateResultRule(@Param("id") String id,@Param("state") String state);

    List<FenleiBaohuResultExt> selectSimpleClassCodeAndClassificationById(@Param("id") String id);

    int saveClassificationInfo(@Param("ext") FenleiBaohuResult ext,@Param("worker") String worker);

    int updateStateByIdAndWorker(@Param("id") String id, @Param("worker")String worker, @Param("state")String state);

    int updateStateById(@Param("id")String id,@Param("state")String state);


    List<EmailIntervalEntity> findAll();

    List<String> selectWorkerEmailById(String id);

    List<FenleiBaohuResultExt> selectListWithOrgNameByID(String id);

    int getIPCMINotNullAndWorker(@Param("id")String id,@Param("worker")String worker );

    // 根据案件id获取主分类员
    String findMainWorkerByID(@Param("id") String id);

    //根据案件id获取副分类员
    List<String> findAssWorkerByID(@Param("id") String id);

    List<FenleiBaohuResult> selectUnFinishListByLoginName(@Param("loginname")String loginname);
}
