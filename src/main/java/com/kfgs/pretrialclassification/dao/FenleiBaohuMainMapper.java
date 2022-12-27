package com.kfgs.pretrialclassification.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuMainResultExt;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mango
 */
@Repository
public interface FenleiBaohuMainMapper extends BaseMapper<FenleiBaohuMain> {

    IPage<FenleiBaohuMain> findAll();

    IPage<FenleiBaohuMain> findMainByState(Page<FenleiBaohuMain> page, @Param("state") String state, @Param("dep1") String dep1,@Param("dep2") String dep2);

    IPage<FenleiBaohuMain> selectCaseIn(Page<FenleiBaohuMain> page, @Param("begintime") String begintime, @Param("endtime") String endtime);

    IPage<FenleiBaohuMain> countCaseOutWithOrg(Page<FenleiBaohuMain> page, @Param("begintime") String begintime, @Param("endtime") String endtime);

    IPage<FenleiBaohuMainResultExt> searchByVagueCondition(IPage<FenleiBaohuMainResultExt> page, @Param("id") String id,@Param("sqr") String sqr,@Param("mingcheng") String mingcheng);
    //IPage<FenleiBaohuMainResultExt> selectByCondition(IPage<FenleiBaohuMainResultExt> page, @Param("ew") Wrapper<FenleiBaohuMainResultExt> queryWrapper);

    IPage<FenleiBaohuMainResultExt> selectByCondition(IPage<FenleiBaohuMainResultExt> page, @Param("id") String id,@Param("name") String name,@Param("oraginization") String oraginization,@Param("sqr") String sqr,@Param("sqh") String sqh,@Param("worker") String worker,@Param("state") String state,@Param("begintime") String begintime,@Param("endtime") String endtime, @Param("enterBeginTime")String enterBeginTime, @Param("enterEndTime")String enterEndTime);

    FenleiBaohuMain searchByCondition(@Param("id") String id,@Param("sqr") String sqr,@Param("mingcheng") String mingcheng);

    String getCaseID(@Param("sqh") String sqh,@Param("mingcheng") String mingcheng);

    int insertEntity(FenleiBaohuMain fenleiBaohuMain);

    int findDoubleByID(@Param("id") String id, @Param("oraginization") String oraginization);

    IPage<FenleiBaohuMain> findByState(Page<FenleiBaohuMain> page, @Param("state") String state);

    String getType(@Param("id") String id);

    int updateMainRule(@Param("id") String id,@Param("chuantime") String chuantime,@Param("state") String state);

    int updateIpciCciCcaCsetsById(@Param("finishTime")String finishTime ,@Param("ipci") String ipci, @Param("cci")String cci, @Param("cca")String cca, @Param("csets") String csets, @Param("id") String id, @Param("state")String state,@Param("mainClassifiers") String mainClassifiers,@Param("viceClassifiersString") String viceClassifiersString);

    int updateByIdAndWithOutNotExport(@Param("id") String id, @Param("ipci")String ipci,@Param("cci") String cci, @Param("cca")String cca, @Param("csets")String csets);

    int updateStateById(@Param("id") String id, @Param("state") String state);

    List<String> getExcelInfo(List<String> ids);

    /**
     * 将FUZZY_MATCH_NAME、FUZZY_MATCH_RESUKT清空
     */
    void updateFuzzyColumnNull();

    /**
     * 将FUZZY_MATCH_NAME字段跟新为去掉一种的前八个字符
     */
    void updateFuzzyNameColumn();

    /**
     * 返回所有数据map
     * @return
     */
    @MapKey("id")
    HashMap<String,FenleiBaohuMain> findDataToMapComma();

    /** 将中文逗号替换为英文逗号  */
    void updateSqrComma();

    /** 将中文分号替换为英文逗号 */
    void updateSqrSemicolonCN();

    /** 将英文分号替换为英文逗号 */
    void updateSqrSemicolonEN();

    /** 精确匹配名称相同，申请人相同 */
    List<String> selectIdByExactMatchMingChengAndExactMatchSqr(@Param("mingcheng") String mingcheng,@Param("sqr")String sqr, @Param("exclude") String exclude);
    /** 精确匹配名称相同，申请人相同 */
    List<String> selectIdByFuzzyMatchMingChengAndExactMatchSqr(@Param("mingcheng") String mingcheng,@Param("sqr")String sqr , @Param("exclude") String exclude);

    /** 名称相同 */
    List<String> selectIdByExactMatchMingCheng(@Param("mingcheng") String mingcheng , @Param("exclude") String exclude);

    /** 名称模糊相同 */
    List<String> selectIdByFuzzyMatchMingCheng(String mingcheng , @Param("exclude") String exclude);

    /** 根据ID跟新表中的FUZZY_MATCH_RESULT字段*/
    void updateFuzzyResultById(@Param("result")String result, @Param("id")String id);

    /** 查找名称相同的案件 */
    List<FenleiBaohuMain> selectByExactMatchMingCheng(@Param("mingcheng")String mingcheng, @Param("id")String id);

    /** 查找名称模糊相同的案件 */
    List<FenleiBaohuMain> selectByFuzzyMatchMingCheng(@Param("mingcheng")String mingcheng, @Param("id")String id);

    /** 查找名称前 length 位相同的案件*/
    List<FenleiBaohuMain> selectByFuzzyMatchMingChengLengLt(@Param("mingcheng")String mingcheng, @Param("id")String id, @Param("length")int length);

    /** 按照list 查找相关案件信息 */
    List<FenleiBaohuMain> selectByList(List<String> list);

    /** 返回main表中，当前时间 FUZZY_MATCH_RESULT 为空的  */
    @MapKey("id")
    HashMap<String, FenleiBaohuMain> findDataToMapCommaWhereFuzzyResultIsNull();

    void updateFuzzyColumnNullWhereFuzzyColumnisNull();
}
