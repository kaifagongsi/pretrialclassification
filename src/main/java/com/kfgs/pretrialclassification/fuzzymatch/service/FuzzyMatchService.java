package com.kfgs.pretrialclassification.fuzzymatch.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kfgs.pretrialclassification.common.utils.ListUtils;
import com.kfgs.pretrialclassification.dao.FenleiBaohuMainMapper;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuMainFuzzyMatchABCD;
import lombok.experimental.var;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FuzzyMatchService {

    //p

    @Autowired
    FenleiBaohuMainMapper fenleiBaohuMainMapper;


    @Async
    public void matchAll(){
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
        Map<String, FenleiBaohuMain> withOutComma = fenleiBaohuMainMapper.findDataToMapComma();
        ConcurrentHashMap<String, FenleiBaohuMain> concurrentHashMap = new ConcurrentHashMap(withOutComma);
        System.out.println("待处理的集合："+ concurrentHashMap.size());
        //2.处理map
        for(Map.Entry<String,FenleiBaohuMain> map : concurrentHashMap.entrySet()){
            //所有相似案件的list
            List<String> moHuList = new ArrayList<>();
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
            List<FenleiBaohuMain> nameEqualsList = fenleiBaohuMainMapper.selectByExactMatchMingCheng(entity.getMingcheng(),entity.getId());
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
            String mingcheng = entity.getMingcheng().replace("一种","");
            if(mingcheng.length() >=8){
                mingcheng = mingcheng.substring(0,8);
            }
            List<FenleiBaohuMain> nameLikeList = fenleiBaohuMainMapper.selectByFuzzyMatchMingCheng(mingcheng,entity.getId());
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
            /*if(entity.getSqr().contains(",")){
            }else{
                //A 申请人相同 + 名称相同
                List<String> firstList = fenleiBaohuMainMapper.selectIdByExactMatchMingChengAndExactMatchSqr(entity.getMingcheng(), entity.getSqr(),entity.getId());
                abcd.setA(StringUtils.join(firstList, ","));
                moHuList.addAll(firstList);
                // B 申请人相同 + 名称去掉一种，前八个字符相同
                List<String> secondList = fenleiBaohuMainMapper.selectIdByFuzzyMatchMingChengAndExactMatchSqr(entity.getMingcheng().replace("一种","").substring(0,8),entity.getSqr(),entity.getId());
                secondList.removeAll(firstList);
                abcd.setB(StringUtils.join(secondList, ","));
                moHuList.addAll(secondList);
                // C 名称相同 申请人不同
                firstList = fenleiBaohuMainMapper.selectIdByExactMatchMingCheng(entity.getMingcheng(),entity.getId());
                abcd.setC(StringUtils.join(firstList, ","));
                moHuList.addAll(firstList);
                // D 名称模糊相同 申请人不同
                secondList = fenleiBaohuMainMapper.selectIdByFuzzyMatchMingCheng(entity.getMingcheng().replace("一种","").substring(0,8),entity.getId());
                secondList.removeAll(firstList);
                abcd.setD(StringUtils.join(secondList,","));
                moHuList.addAll(secondList);
            }*/
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
                withOutComma.remove(str);
            }
        }
    }
}
