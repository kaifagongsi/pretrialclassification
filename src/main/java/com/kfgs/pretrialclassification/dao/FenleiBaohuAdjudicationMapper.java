package com.kfgs.pretrialclassification.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.domain.FenleiBaohuAdjudication;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuAdjudicationExt;
import org.apache.ibatis.annotations.Param;

public interface FenleiBaohuAdjudicationMapper extends BaseMapper<FenleiBaohuAdjudication> {
    IPage<FenleiBaohuAdjudicationExt> getArbiterInitList(Page<?> page, @Param("username") String username);

    int saveAribiterClassfication(@Param("ext") FenleiBaohuAdjudicationExt ext,@Param("username") String username);
}
