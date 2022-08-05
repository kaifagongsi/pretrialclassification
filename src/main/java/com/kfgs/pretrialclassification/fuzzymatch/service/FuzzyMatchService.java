package com.kfgs.pretrialclassification.fuzzymatch.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kfgs.pretrialclassification.common.utils.ListUtils;
import com.kfgs.pretrialclassification.dao.FenleiBaohuMainMapper;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuMainFuzzyMatchABCD;
import com.kfgs.pretrialclassification.domain.response.CommonCode;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;
import lombok.experimental.var;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class FuzzyMatchService {

    //p

    @Autowired
    FenleiBaohuMainMapper fenleiBaohuMainMapper;

    /** 查看当前任务的执行百分比 */
    private String progressState = "0";
    /**
     * 查看当前任务的执行状态 0/1
     * 0没有执行
     * 1表示执行中。。。
     * */
    private int state = 0;

    public int getState(){
        return this.state;
    }
    public String getProgressState(){
        return this.progressState;
    }

    /**
     * 补充相似案件
     * @return
     */
    public QueryResponseResult matchNeed(){
        try {
            //0.更新fuzzy_match_name 字段
            fenleiBaohuMainMapper.updateFuzzyColumnNullWhereFuzzyColumnisNull();
            //1.查询不包含逗号的所有，放入map 单个申请人
            HashMap<String, FenleiBaohuMain> withOutComma = fenleiBaohuMainMapper.findDataToMapCommaWhereFuzzyResultIsNull();
            dealWithMap(withOutComma,true);
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        }catch (Exception e){
            return new QueryResponseResult(CommonCode.FAIL,null);
        }
    }


    @Async
    public void matchAll(){
        // 判断没有任务执行过
        if(state == 0){
            state = 1;
            //0.初始化列表
            //0.1制空
            fenleiBaohuMainMapper.updateFuzzyColumnNull();
            //0.2将FUZZY_MATCH_NAME更新
            fenleiBaohuMainMapper.updateFuzzyNameColumn();
            //0.3将中文逗号代替为英文逗号，将中、英文封号替换为因为逗号
            fenleiBaohuMainMapper.updateSqrComma();
            fenleiBaohuMainMapper.updateSqrSemicolonCN();
            fenleiBaohuMainMapper.updateSqrSemicolonEN();
            //1.查询不包含逗号的所有，放入map 单个申请人
            HashMap<String, FenleiBaohuMain> withOutComma = fenleiBaohuMainMapper.findDataToMapComma();
            log.info("待处理的集合{}:",withOutComma.size());
            dealWithMap(withOutComma,true);
            progressState = "100";
            //状态重置
            state = 0;
        }
    }

    /**
     *
     * @param withOutComma 待处理的map
     * @param isCalculation 是否进行计算
     */
    private void dealWithMap(HashMap<String, FenleiBaohuMain> withOutComma,   boolean isCalculation){
        //0.当前执行的下标
        float progressIndex = 0;
        ConcurrentHashMap<String, FenleiBaohuMain> concurrentHashMap = new ConcurrentHashMap(withOutComma);
        //1.2格式处理
        NumberFormat numberFormat =  NumberFormat.getNumberInstance();
        float sumSize = (withOutComma.size() );
        for(Iterator<Map.Entry<String, FenleiBaohuMain>> iterator = concurrentHashMap.entrySet().iterator(); iterator.hasNext();  ){
            Map.Entry<String, FenleiBaohuMain> map = iterator.next();
            if(isCalculation){
                this.progressState = numberFormat.format(progressIndex / sumSize * 100);
            }
            //所有相似案件的list
            List<String> moHuList = new ArrayList<>();
            //把自己加进去
            moHuList.add(map.getKey());
            FenleiBaohuMainFuzzyMatchABCD abcd = new FenleiBaohuMainFuzzyMatchABCD();
            FenleiBaohuMain entity   = map.getValue();
            // A类 申请人相同 名称相同
            StringBuilder a = new StringBuilder();
            // B类 申请人相同 名称模糊相同
            StringBuilder b = new StringBuilder();
            // C类 申请人不同 名称相同
            StringBuilder c = new StringBuilder();
            // C类 申请人不同 名称模糊相同
            StringBuilder d = new StringBuilder();
            //找到名称相同的
            String mingCheng = entity.getMingcheng();
            List<FenleiBaohuMain> nameEqualsList = fenleiBaohuMainMapper.selectByExactMatchMingCheng(mingCheng,entity.getId());
            List<String> firstList = new ArrayList<>();
            for(FenleiBaohuMain main : nameEqualsList){
                boolean equals = Arrays.equals(entity.getSqr().split(","), main.getSqr().split(","));
                //相同表示，名称相同+申请人相同
                if(equals){
                    a.append(main.getId()).append(",");
                }else{
                    c.append(main.getId()).append(",");
                }
                firstList.add(main.getId());
                moHuList.add(main.getId());
            }
            //找到名称模糊相同的
            mingCheng = entity.getMingcheng().replace("一种","");
            List<FenleiBaohuMain> nameLikeList = null;
            if(mingCheng.length() >=8){
                mingCheng = mingCheng.substring(0,8);
                nameLikeList = fenleiBaohuMainMapper.selectByFuzzyMatchMingCheng(mingCheng,entity.getId());
            }else{
                nameLikeList = fenleiBaohuMainMapper.selectByFuzzyMatchMingChengLengLt(mingCheng,entity.getId(),mingCheng.length());
            }
            nameLikeList.removeAll(nameEqualsList);
            for(FenleiBaohuMain main : nameLikeList){
                System.out.println(entity.getSqr() + "---" + entity.getId()  + "=============="+ main.getSqr() + "---"+ main.getId());
                boolean equals = Arrays.equals(entity.getSqr().split(","), main.getSqr().split(","));
                //相同表示，名称模糊相同+申请人相同
                if(equals){
                    b.append(main.getId()).append(",");
                }else{
                    d.append(main.getId()).append(",");
                }
                moHuList.add(main.getId());
            }
            if(StringUtils.isNotEmpty(a.toString())){
                abcd.setA(a.toString().substring(0,a.toString().length() - 1));
            }else{
                abcd.setA("");
            }
            if(StringUtils.isNotEmpty(b.toString())){
                abcd.setB(b.toString().substring(0,b.toString().length() - 1));
            }else{
                abcd.setB("");
            }
            if(StringUtils.isNotEmpty(c.toString())){
                abcd.setC(c.toString().substring(0,c.toString().length() - 1));
            }else{
                abcd.setC("");
            }
            if(StringUtils.isNotEmpty(d.toString())){
                abcd.setD(d.toString().substring(0,d.toString().length() - 1));
            }else{
                abcd.setD("");
            }
            //相似list
            moHuList.add(entity.getId());
            //list去重
            moHuList = ListUtils.delRepeatReturnList(moHuList);
            entity.setFuzzyMatchResult(JSONObject.toJSONString(abcd));
            for(String str : moHuList){
                FenleiBaohuMainFuzzyMatchABCD temp = new FenleiBaohuMainFuzzyMatchABCD();
                BeanUtils.copyProperties(abcd,temp);
                // 将外部的ACBD结果对比是否包含本身，如果包含则替换为外部的
                if(temp.getA().contains(str)){
                    temp.setA(temp.getA().replace(str,entity.getId()));
                }else if(temp.getB().contains(str)){
                    temp.setB(temp.getB().replace(str,entity.getId()));
                }else if(temp.getC().contains(str)){
                    temp.setC(temp.getC().replace(str,entity.getId()));
                }else if(abcd.getD().contains(str)){
                    temp.setD(temp.getD().replace(str,entity.getId()));
                }
                //更新
                fenleiBaohuMainMapper.updateFuzzyResultById(JSONObject.toJSONString(temp),str);
                //从map中移除
                FenleiBaohuMain remove = concurrentHashMap.remove(str);
                if(remove!=null && isCalculation){
                    progressIndex++;
                }
            }
        }
    }
}
