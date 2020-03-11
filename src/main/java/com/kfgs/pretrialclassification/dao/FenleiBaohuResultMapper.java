package com.kfgs.pretrialclassification.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuResultExt;

import java.util.List;

/**
 * Date: 2020-01-19-10-27
 * Module:
 * Description:
 *
 * @author:
 */
public interface FenleiBaohuResultMapper extends BaseMapper<FenleiBaohuResult> {

    List<FenleiBaohuResultExt> AfterDeploymentSendEmail(String date);

}
