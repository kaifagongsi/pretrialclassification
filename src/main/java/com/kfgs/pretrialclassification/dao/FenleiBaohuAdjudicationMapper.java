package com.kfgs.pretrialclassification.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.domain.FenleiBaohuAdjudication;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuAdjudicationExt;
import com.kfgs.pretrialclassification.domain.request.ArbiterParam;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
@Repository
public interface FenleiBaohuAdjudicationMapper extends BaseMapper<FenleiBaohuAdjudication> {
    IPage<FenleiBaohuAdjudicationExt> getArbiterInitList(Page<?> page, @Param("username") String username);

    int saveAribiterClassfication(@Param("ext") FenleiBaohuAdjudicationExt ext,@Param("username") String username);

    int updateAdjudicatorById(@Param("list") ArrayList<ArbiterParam> list, @Param("id") String id);

    String selectAdjudicatorWorker(String id);

    int updateCaseStateAndFinishTime(@Param("state") String state,@Param("finishTime")String finishTime, @Param("id") String id);

    IPage<FenleiBaohuAdjudicationExt> getArbiterPersonInitList(Page<FenleiBaohuAdjudicationExt> page, @Param("username")String loginname);

    //int updateAdjudicatorById(@Param("adjudicator") String adjudicator, @Param("id") String id);
}
