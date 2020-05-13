package com.kfgs.pretrialclassification.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    List<FenleiBaohuResultExt> AfterDeploymentSendEmail(String[] ids);

    IPage<FenleiBaohuResultExt> selectCaseOut(Page<FenleiBaohuResult> page);


}
