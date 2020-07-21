package com.kfgs.pretrialclassification.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuMainResultExt;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuResultExt;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Map;

/**
 * Date: 2020-01-19-10-27
 * Module:
 * Description:
 *
 * @author:
 */
public interface FenleiBaohuResultMapper extends BaseMapper<FenleiBaohuResult> {

    List<FenleiBaohuResultExt> AfterDeploymentSendEmail(String[] ids);

    IPage<FenleiBaohuResultExt> selectCaseOut(Page<FenleiBaohuResult> page, @Param("begintime") String begintime, @Param("endtime") String endtime, @Param("type") String type, @Param("dept") String dept,@Param("userName") String userName);

    List<FenleiBaohuResult> findClassInfoByID(String id,String sqh,String mingcheng);

    IPage<FenleiBaohuMainResultExt> selectCaseByState(IPage<FenleiBaohuMainResultExt> page, @Param("state") String state,@Param("classtype") String classtype, @Param("user") String user,@Param("begintime") String begintime, @Param("endtime") String endtime);

    Map selectCaseInfo (@Param("id")String id, @Param("worker") String worker);

    int selectCaseStateCount(@Param("id") String id);

    List<String> getTransWorkerList(@Param("id") String id);
}
