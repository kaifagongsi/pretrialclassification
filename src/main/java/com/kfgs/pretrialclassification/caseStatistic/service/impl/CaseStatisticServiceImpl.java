package com.kfgs.pretrialclassification.caseStatistic.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.caseStatistic.service.CaseStatisticService;
import com.kfgs.pretrialclassification.dao.FenleiBaohuAdjudicationMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuLogMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuMainMapper;
import com.kfgs.pretrialclassification.dao.FenleiBaohuResultMapper;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuAdjudicationExt;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuResultExt;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CaseStatisticServiceImpl implements CaseStatisticService {

    @Autowired
    FenleiBaohuResultMapper fenleiBaohuResultMapper;

    @Autowired
    FenleiBaohuLogMapper fenleiBaohuLogMapper;

    @Autowired
    FenleiBaohuMainMapper fenleiBaohuMainMapper;

    @Autowired
    FenleiBaohuAdjudicationMapper fenleiBaohuAdjudicationMapper;

    //进案量统计
    @Override
    public IPage countCaseIn(String pageNo, String limit,String begintime,String endtime) {
        Page<FenleiBaohuMain> page = new Page<FenleiBaohuMain>(Long.parseLong(pageNo),Long.parseLong(limit));
        return fenleiBaohuMainMapper.selectCaseIn(page,begintime,endtime);
    }

    //出案量统计
    @Override
    public IPage countCaseOut(String pageNo, String limit, String begintime, String endtime, String type, String dept1, String dept2, String userName) {
        Page<FenleiBaohuResult> page = new Page<FenleiBaohuResult>(Long.parseLong(pageNo),Long.parseLong(limit));
        return fenleiBaohuResultMapper.selectCaseOut(page,begintime,endtime,type, dept1, dept2,userName);
    }


    @Override
    public IPage countCaseOutWithOrg(String pageNo, String limit, String beginTime, String endTime) {
        Page<FenleiBaohuMain> page = new Page<FenleiBaohuMain>(Long.parseLong(pageNo),Long.parseLong(limit));
        return fenleiBaohuMainMapper.countCaseOutWithOrg(page,beginTime,endTime);
    }

    //出案量统计
    @Override
    public IPage accountWork(String pageNo, String limit, String begintime, String endtime, String type, String dept1, String dept2, String userName) {
        Page<FenleiBaohuResult> page = new Page<FenleiBaohuResult>(Long.parseLong(pageNo),Long.parseLong(limit));
        Page<FenleiBaohuAdjudicationExt> adjudicationPage = new Page<FenleiBaohuAdjudicationExt>(Long.parseLong(pageNo),Long.parseLong(limit));

        //获取出案时间内案件统计
        IPage<FenleiBaohuResultExt> resultList = fenleiBaohuResultMapper.accountWork(page,begintime,endtime,type, dept1, dept2,userName);
        //获取出案时间内所有裁决案件
        IPage<FenleiBaohuAdjudicationExt> adjudicationList = fenleiBaohuAdjudicationMapper.selectAdjudicationOut(adjudicationPage,begintime,endtime);
        //查询出案时间内所有案件清单
        List<FenleiBaohuResultExt> allList = fenleiBaohuResultMapper.selectAllCase(begintime,endtime,type, dept1, dept2,userName);
       //存放最终出案各类型点数的集合
        HashMap<String,FenleiBaohuResultExt> accountWorkMap = new HashMap<String,FenleiBaohuResultExt>();

        //查询已出案数据按照人员、id、实际数据统计出来
        HashMap<String,HashMap<String,ArrayList<FenleiBaohuResultExt>>> resultMap = new HashMap<String,HashMap<String,ArrayList<FenleiBaohuResultExt>>>();
//        for(int i = 0; i<allList.size(); i++){
//            FenleiBaohuResultExt bean = allList.get(i);
//            if(resultMap.containsKey(bean.getFldm())){
//                HashMap<String,ArrayList<FenleiBaohuResultExt>>  temp = resultMap.get(bean.getFldm());
//                if(temp.containsKey(bean.getId())){
//                    ArrayList<FenleiBaohuResultExt> templist = temp.get(bean.getId());
//                    templist.add(bean);
//                    temp.put(bean.getId(),templist);
//                }else {
//                    ArrayList<FenleiBaohuResultExt> templist = new ArrayList<FenleiBaohuResultExt>();
//                    templist.add(bean);
//                    temp.put(bean.getId(),templist);
//                }
//                resultMap.put(bean.getFldm(),temp);
//            }else{
//                HashMap<String,ArrayList<FenleiBaohuResultExt>>  temp = new HashMap<String,ArrayList<FenleiBaohuResultExt>>();
//                if(temp.containsKey(bean.getId())){
//                    ArrayList<FenleiBaohuResultExt> templist = temp.get(bean.getId());
//                    templist.add(bean);
//                    temp.put(bean.getId(),templist);
//                }else {
//                    ArrayList<FenleiBaohuResultExt> templist = new ArrayList<FenleiBaohuResultExt>();
//                    templist.add(bean);
//                    temp.put(bean.getId(),templist);
//                }
//                resultMap.put(bean.getFldm(),temp);
//            }
//        }
        for(int i = 0; i<allList.size(); i++){
            FenleiBaohuResultExt bean = allList.get(i);
            if(resultMap.containsKey(bean.getId())){
                HashMap<String,ArrayList<FenleiBaohuResultExt>>  temp = resultMap.get(bean.getId());
                if(temp.containsKey(bean.getFldm())){
                    ArrayList<FenleiBaohuResultExt> templist = temp.get(bean.getFldm());
                    templist.add(bean);
                    temp.put(bean.getFldm(),templist);
                }else {
                    ArrayList<FenleiBaohuResultExt> templist = new ArrayList<FenleiBaohuResultExt>();
                    templist.add(bean);
                    temp.put(bean.getFldm(),templist);
                }
                resultMap.put(bean.getId(),temp);
            }else{
                HashMap<String,ArrayList<FenleiBaohuResultExt>>  temp = new HashMap<String,ArrayList<FenleiBaohuResultExt>>();
                if(temp.containsKey(bean.getFldm())){
                    ArrayList<FenleiBaohuResultExt> templist = temp.get(bean.getFldm());
                    templist.add(bean);
                    temp.put(bean.getFldm(),templist);
                }else {
                    ArrayList<FenleiBaohuResultExt> templist = new ArrayList<FenleiBaohuResultExt>();
                    templist.add(bean);
                    temp.put(bean.getFldm(),templist);
                }
                resultMap.put(bean.getId(),temp);
            }
        }
        //处理数据获取出案点数
        HashMap<String,FenleiBaohuResultExt>  accountPointMap = new HashMap<String,FenleiBaohuResultExt>();
        if(resultMap != null && resultMap.size() > 0){
            for(Map.Entry<String,HashMap<String,ArrayList<FenleiBaohuResultExt>>> entry : resultMap.entrySet()){
                HashMap<String,ArrayList<FenleiBaohuResultExt>> tempMap = entry.getValue();
                //一条ID只有一条数据
                if(tempMap != null && tempMap.size() == 1){
                    for(Map.Entry<String,ArrayList<FenleiBaohuResultExt>> tempEntry : tempMap.entrySet()){
                        ArrayList<FenleiBaohuResultExt> tempList = tempEntry.getValue();
                        FenleiBaohuResultExt fenleiBaohuResultExt = tempList.get(0);
                        //全部为空
                        if(StringUtils.isBlank(fenleiBaohuResultExt.getIPCMI()) && StringUtils.isBlank(fenleiBaohuResultExt.getIpca()) && StringUtils.isBlank(fenleiBaohuResultExt.getIpci()) && StringUtils.isBlank(fenleiBaohuResultExt.getIPCOI())
                                && StringUtils.isBlank(fenleiBaohuResultExt.getCci()) && StringUtils.isBlank(fenleiBaohuResultExt.getCca()) && StringUtils.isBlank(fenleiBaohuResultExt.getCsets())){
                            continue;
                        }
                        if(StringUtils.isBlank(fenleiBaohuResultExt.getIPCMI()) && (StringUtils.isNotBlank(fenleiBaohuResultExt.getIpca()) || StringUtils.isNotBlank(fenleiBaohuResultExt.getIpci()) || StringUtils.isNotBlank(fenleiBaohuResultExt.getIPCOI())
                                || StringUtils.isNotBlank(fenleiBaohuResultExt.getCci()) || StringUtils.isNotBlank(fenleiBaohuResultExt.getCca()) || StringUtils.isNotBlank(fenleiBaohuResultExt.getCsets())) ){
                            System.out.println(fenleiBaohuResultExt.getId() + "仅给出一条副分");
                        }
                        if(accountPointMap.containsKey(tempEntry.getKey())){
                            FenleiBaohuResultExt newfenleiBaohuResultExt = accountPointMap.get(tempEntry.getKey());
                            if(StringUtils.equals("FM",fenleiBaohuResultExt.getTypeVal())){
                                String fmzfPoint = newfenleiBaohuResultExt.getFmzfPoint();
                                newfenleiBaohuResultExt.setFmzfPoint(addDouble(fmzfPoint, "3"));
                            }else if(StringUtils.equals("XX",fenleiBaohuResultExt.getTypeVal())){
                                String xxzfPoint = newfenleiBaohuResultExt.getXxzfPoint();
                                newfenleiBaohuResultExt.setXxzfPoint(addDouble(xxzfPoint, "1"));
                            }
                            accountPointMap.put(tempEntry.getKey(),newfenleiBaohuResultExt);
                        }else{
                            FenleiBaohuResultExt newfenleiBaohuResultExt = new FenleiBaohuResultExt();
                            if(StringUtils.equals("FM",fenleiBaohuResultExt.getTypeVal())){
                                String fmzfPoint = newfenleiBaohuResultExt.getFmzfPoint();
                                newfenleiBaohuResultExt.setFmzfPoint(addDouble(fmzfPoint, "3"));
                            }else if(StringUtils.equals("XX",fenleiBaohuResultExt.getTypeVal())){
                                String xxzfPoint = newfenleiBaohuResultExt.getXxzfPoint();
                                newfenleiBaohuResultExt.setXxzfPoint(addDouble(xxzfPoint, "1"));
                            }
                            accountPointMap.put(tempEntry.getKey(),newfenleiBaohuResultExt);
                        }
                    }

                }else{// 多条数据
                    HashMap<String,String> ffWorkerAndType = new HashMap<String,String>();
                    String zfWorker = "";
                    String zfType = "";
                    int ffCount = 0;
                    for(Map.Entry<String,ArrayList<FenleiBaohuResultExt>> tempEntry : tempMap.entrySet()) {
                        ArrayList<FenleiBaohuResultExt> tempList = tempEntry.getValue();
                        if(tempList != null && tempList.size() > 0){
                            for(FenleiBaohuResultExt tempFenleiBaohuResultExt : tempList){
                                //全部为空
                                if(StringUtils.isBlank(tempFenleiBaohuResultExt.getIPCMI()) && StringUtils.isBlank(tempFenleiBaohuResultExt.getIpca()) && StringUtils.isBlank(tempFenleiBaohuResultExt.getIpci()) && StringUtils.isBlank(tempFenleiBaohuResultExt.getIPCOI())
                                        && StringUtils.isBlank(tempFenleiBaohuResultExt.getCci()) && StringUtils.isBlank(tempFenleiBaohuResultExt.getCca()) && StringUtils.isBlank(tempFenleiBaohuResultExt.getCsets())){
                                    continue;
                                }
                                //主分
                                if (StringUtils.isNotBlank(tempFenleiBaohuResultExt.getIPCMI())) {
                                    zfWorker = tempEntry.getKey();
                                    zfType = tempFenleiBaohuResultExt.getTypeVal();
                                    break;//直接退出本次循环
                                }
                                //副分
                                if (StringUtils.isBlank(tempFenleiBaohuResultExt.getIPCMI()) && (StringUtils.isNotBlank(tempFenleiBaohuResultExt.getIpca()) || StringUtils.isNotBlank(tempFenleiBaohuResultExt.getIpci()) || StringUtils.isNotBlank(tempFenleiBaohuResultExt.getIPCOI())
                                        || StringUtils.isNotBlank(tempFenleiBaohuResultExt.getCci()) || StringUtils.isNotBlank(tempFenleiBaohuResultExt.getCca()) || StringUtils.isNotBlank(tempFenleiBaohuResultExt.getCsets()))) {
                                    ffCount++;
                                    ffWorkerAndType.put(tempEntry.getKey(),tempFenleiBaohuResultExt.getTypeVal());
                                }
                            }
                        }
                    }
                    if(ffCount != 0){
                        //有其他副分的话，主分计数
                        if(accountPointMap.containsKey(zfWorker)){
                            FenleiBaohuResultExt newfenleiBaohuResultExt = accountPointMap.get(zfWorker);
                            if(StringUtils.equals("FM",zfType)){
                                String fmzfPoint = newfenleiBaohuResultExt.getFmzfPoint();
                                newfenleiBaohuResultExt.setFmzfPoint(addDouble(fmzfPoint, "2.4"));
                            }else if(StringUtils.equals("XX",zfType)){
                                String xxzfPoint = newfenleiBaohuResultExt.getXxzfPoint();
                                newfenleiBaohuResultExt.setXxzfPoint(addDouble(xxzfPoint, "0.7"));
                            }
                            accountPointMap.put(zfWorker,newfenleiBaohuResultExt);
                        }else{
                            FenleiBaohuResultExt newfenleiBaohuResultExt = new FenleiBaohuResultExt();
                            if(StringUtils.equals("FM", zfType)){
                                String fmzfPoint = newfenleiBaohuResultExt.getFmzfPoint();
                                newfenleiBaohuResultExt.setFmzfPoint(addDouble(fmzfPoint, "2.4"));
                            }else if(StringUtils.equals("XX", zfType)){
                                String xxzfPoint = newfenleiBaohuResultExt.getXxzfPoint();
                                newfenleiBaohuResultExt.setXxzfPoint(addDouble(xxzfPoint, "0.7"));
                            }
                            accountPointMap.put(zfWorker,newfenleiBaohuResultExt);
                        }
                        //取两位小数
                        DecimalFormat df = new DecimalFormat("###.000");
                        for(Map.Entry<String,String> temp : ffWorkerAndType.entrySet()) {
                            if(accountPointMap.containsKey(temp.getKey())){
                                FenleiBaohuResultExt newfenleiBaohuResultExt = accountPointMap.get(temp.getKey());
                                if(StringUtils.equals("FM",temp.getValue())){
                                    String fmffPoint = newfenleiBaohuResultExt.getFmffPoint();
                                    newfenleiBaohuResultExt.setFmffPoint(addDouble(fmffPoint, df.format(0.6/ffCount)));
                                }else if(StringUtils.equals("XX",temp.getValue())){
                                    String xxffPoint = newfenleiBaohuResultExt.getXxffPoint();
                                    newfenleiBaohuResultExt.setXxffPoint(addDouble(xxffPoint, df.format(0.3/ffCount)));
                                }
                                accountPointMap.put(temp.getKey(),newfenleiBaohuResultExt);
                            }else{
                                FenleiBaohuResultExt newfenleiBaohuResultExt = new FenleiBaohuResultExt();
                                if(StringUtils.equals("FM",temp.getValue())){
                                    String fmffPoint = newfenleiBaohuResultExt.getFmffPoint();
                                    newfenleiBaohuResultExt.setFmffPoint(addDouble(fmffPoint, df.format(0.6/ffCount)));
                                }else if(StringUtils.equals("XX",temp.getValue())){
                                    String xxffPoint = newfenleiBaohuResultExt.getXxffPoint();
                                    newfenleiBaohuResultExt.setXxffPoint(addDouble(xxffPoint, df.format(0.3/ffCount)));
                                }
                                accountPointMap.put(temp.getKey(),newfenleiBaohuResultExt);
                            }
                        }
                    }else{
                        if(accountPointMap.containsKey(zfWorker)){
                            FenleiBaohuResultExt newfenleiBaohuResultExt = accountPointMap.get(zfWorker);
                            if(StringUtils.equals("FM",zfType)){
                                String fmzfPoint = newfenleiBaohuResultExt.getFmzfPoint();
                                newfenleiBaohuResultExt.setFmzfPoint(addDouble(fmzfPoint, "3"));
                            }else if(StringUtils.equals("XX",zfType)){
                                String xxzfPoint = newfenleiBaohuResultExt.getXxzfPoint();
                                newfenleiBaohuResultExt.setXxzfPoint(addDouble(xxzfPoint, "1"));
                            }
                            accountPointMap.put(zfWorker,newfenleiBaohuResultExt);
                        }else{
                            FenleiBaohuResultExt newfenleiBaohuResultExt = new FenleiBaohuResultExt();
                            if(StringUtils.equals("FM",zfType)){
                                String fmzfPoint = newfenleiBaohuResultExt.getFmzfPoint();
                                newfenleiBaohuResultExt.setFmzfPoint(addDouble(fmzfPoint, "3"));
                            }else if(StringUtils.equals("XX",zfType)){
                                String xxzfPoint = newfenleiBaohuResultExt.getXxzfPoint();
                                newfenleiBaohuResultExt.setXxzfPoint(addDouble(xxzfPoint, "1"));
                            }
                            accountPointMap.put(zfWorker,newfenleiBaohuResultExt);
                        }
                    }
                }
            }
        }
//        if(resultMap != null && resultMap.size() > 0){
//            for(Map.Entry<String,HashMap<String,ArrayList<FenleiBaohuResultExt>>> entry : resultMap.entrySet()){
//                FenleiBaohuResultExt newfenleiBaohuResultExt = new FenleiBaohuResultExt();
//                newfenleiBaohuResultExt.setFldm(entry.getKey());
//                HashMap<String,ArrayList<FenleiBaohuResultExt>> tempMap = entry.getValue();
//                for(Map.Entry<String,ArrayList<FenleiBaohuResultExt>> tempEntry : tempMap.entrySet()){
//                    ArrayList<FenleiBaohuResultExt> idLists = tempEntry.getValue();
//                    //只有一条数据
//                    if(idLists.size() == 1){
//                        FenleiBaohuResultExt fenleiBaohuResultExt = idLists.get(0);
//                        //全部为空
//                        if(StringUtils.isBlank(fenleiBaohuResultExt.getIPCMI()) && StringUtils.isBlank(fenleiBaohuResultExt.getIpca()) && StringUtils.isBlank(fenleiBaohuResultExt.getIpci()) && StringUtils.isBlank(fenleiBaohuResultExt.getIPCOI())
//                                && StringUtils.isBlank(fenleiBaohuResultExt.getCci()) && StringUtils.isBlank(fenleiBaohuResultExt.getCca()) && StringUtils.isBlank(fenleiBaohuResultExt.getCsets())){
//                            continue;
//                        }
//                        //主分是自己
//                        if(StringUtils.isNotBlank(fenleiBaohuResultExt.getIPCMI()) && StringUtils.equals(entry.getKey(),fenleiBaohuResultExt.getFldm())){
//                            if(StringUtils.equals("FM",fenleiBaohuResultExt.getTypeVal())){
//                                String fmzfPoint = fenleiBaohuResultExt.getFmzfPoint();
//                                fenleiBaohuResultExt.setFmzfPoint(addDouble(fmzfPoint, "3"));
//                            }else if(StringUtils.equals("XX",fenleiBaohuResultExt.getTypeVal())){
//                                String xxzfPoint = fenleiBaohuResultExt.getXxzfPoint();
//                                fenleiBaohuResultExt.setXxzfPoint(addDouble(xxzfPoint, "1"));
//                            }
//                        }
//                        if(StringUtils.isBlank(fenleiBaohuResultExt.getIPCMI()) && (StringUtils.isNotBlank(fenleiBaohuResultExt.getIpca()) || StringUtils.isNotBlank(fenleiBaohuResultExt.getIpci()) || StringUtils.isNotBlank(fenleiBaohuResultExt.getIPCOI())
//                                || StringUtils.isNotBlank(fenleiBaohuResultExt.getCci()) || StringUtils.isNotBlank(fenleiBaohuResultExt.getCca()) || StringUtils.isNotBlank(fenleiBaohuResultExt.getCsets())) ){
//                            System.out.println(fenleiBaohuResultExt.getId() + "仅给出一条副分");
//                        }
//                    }else{
//                        int count = 0;
//                        for(int j = 0; j < idLists.size(); j++) {
//                            FenleiBaohuResultExt tempResultExt = idLists.get(j);
//                            if (StringUtils.isBlank(tempResultExt.getIPCMI()) && StringUtils.isBlank(tempResultExt.getIpca()) && StringUtils.isBlank(tempResultExt.getIpci()) && StringUtils.isBlank(tempResultExt.getIPCOI())
//                                    && StringUtils.isBlank(tempResultExt.getCci()) && StringUtils.isBlank(tempResultExt.getCca()) && StringUtils.isBlank(tempResultExt.getCsets())) {
//                                if (StringUtils.equals(entry.getKey(), tempResultExt.getFldm())) {
//                                    break;
//                                } else {
//                                    continue;
//                                }
//                            }
//                            if (StringUtils.isNotBlank(tempResultExt.getIPCMI()) && StringUtils.equals(entry.getKey(), tempResultExt.getFldm())) {
//                                if (StringUtils.equals("FM", tempResultExt.getTypeVal())) {
//                                    String fmzfPoint = idLists.get(0).getFmzfPoint();
//                                    tempResultExt.setFmzfPoint(addDouble(fmzfPoint,"2.4"));
//                                } else if (StringUtils.equals("XX", tempResultExt.getTypeVal())) {
//                                    String xxzfPoint = idLists.get(0).getXxzfPoint();
//                                    tempResultExt.setXxzfPoint(addDouble(xxzfPoint,"0.7"));
//                                }
//                                break;//直接退出本次循环
//                            }
//                            if (StringUtils.isBlank(tempResultExt.getIPCMI()) && (StringUtils.isNotBlank(tempResultExt.getIpca()) || StringUtils.isNotBlank(tempResultExt.getIpci()) || StringUtils.isNotBlank(tempResultExt.getIPCOI())
//                                    || StringUtils.isNotBlank(tempResultExt.getCci()) || StringUtils.isNotBlank(tempResultExt.getCca()) || StringUtils.isNotBlank(tempResultExt.getCsets()))) {
//                                count++;
//                            }
//                        }
//                        if(count != 0){
//                            //取两位小数
//                            DecimalFormat df = new DecimalFormat("###.000");
//                            if(StringUtils.equals("FM",idLists.get(0).getTypeVal())){
//                                String fmffPoint = idLists.get(0).getFmffPoint();
//                                idLists.get(0).setFmffPoint(addDouble(fmffPoint, df.format(0.6/count)));
//                            }else if(StringUtils.equals("XX",idLists.get(0).getTypeVal())){
//                                String xxffPoint = idLists.get(0).getXxffPoint();
//                                idLists.get(0).setXxffPoint(addDouble(xxffPoint, df.format(0.3/count)));
//                            }
//                        }
//                        }
//                        newfenleiBaohuResultExt.setFmzfPoint(addDouble(newfenleiBaohuResultExt.getFmzfPoint(),idLists.get(0).getFmzfPoint()));
//                        newfenleiBaohuResultExt.setFmffPoint(addDouble(newfenleiBaohuResultExt.getFmffPoint(),idLists.get(0).getFmffPoint()));
//                        newfenleiBaohuResultExt.setXxzfPoint(addDouble(newfenleiBaohuResultExt.getXxzfPoint(),idLists.get(0).getXxzfPoint()));
//                        newfenleiBaohuResultExt.setXxffPoint(addDouble(newfenleiBaohuResultExt.getXxffPoint(),idLists.get(0).getXxffPoint()));
//                    }
//                accountWorkMap.put(entry.getKey(),newfenleiBaohuResultExt);
//                }
//            }
        List<FenleiBaohuAdjudicationExt>  adjudicationNewList = adjudicationList.getRecords();
        List<FenleiBaohuResultExt> resultNewList = resultList.getRecords();
        for(int k = 0; k < resultNewList.size(); k++){
                FenleiBaohuResultExt fenleiBaohuResultExt = resultNewList.get(k);
                for(Map.Entry<String,FenleiBaohuResultExt> entry : accountPointMap.entrySet()){
                    String fldm = entry.getKey();
                    if(StringUtils.equals(fldm,fenleiBaohuResultExt.getFldm())){
                        fenleiBaohuResultExt.setFmzfPoint(entry.getValue().getFmzfPoint());
                        fenleiBaohuResultExt.setFmffPoint(entry.getValue().getFmffPoint());
                        fenleiBaohuResultExt.setXxzfPoint(entry.getValue().getXxzfPoint());
                        fenleiBaohuResultExt.setXxffPoint(entry.getValue().getXxffPoint());
                        break;
                    }
                }
                for(int m = 0; m < adjudicationNewList.size(); m++){
                    FenleiBaohuAdjudicationExt fenleiBaohuAdjudicationExt = adjudicationNewList.get(m);
                    if(StringUtils.equals(fenleiBaohuAdjudicationExt.getProcessingPerson(),fenleiBaohuResultExt.getFldm())) {
                        fenleiBaohuResultExt.setCjNum(fenleiBaohuAdjudicationExt.getCjNum());
                        fenleiBaohuResultExt.setCjPoint(fenleiBaohuAdjudicationExt.getCjNum());
                        break;
                    }
                }
            fenleiBaohuResultExt.setTotalPoint(addDouble(fenleiBaohuResultExt.getXxffPoint(),addDouble(fenleiBaohuResultExt.getXxzfPoint(),addDouble(fenleiBaohuResultExt.getFmzfPoint(),fenleiBaohuResultExt.getFmffPoint()))));
            }

        return resultList;
    }

    private String isDouble(String val){
        if(StringUtils.isBlank(val)){
            val = "0.00";
        }
        return val;
    }
    public String addDouble(String add1, String add2){
        double sum = 0;
        BigDecimal a1 = new BigDecimal(isDouble(add1));
        BigDecimal a2 = new BigDecimal(isDouble(add2));
        sum = a1.add(a2).doubleValue();
        return String.valueOf(sum);
    }

}
