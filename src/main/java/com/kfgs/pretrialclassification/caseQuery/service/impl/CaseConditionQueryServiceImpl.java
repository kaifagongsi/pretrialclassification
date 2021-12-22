package com.kfgs.pretrialclassification.caseQuery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kfgs.pretrialclassification.caseQuery.service.CaseConditionQueryService;
import com.kfgs.pretrialclassification.common.utils.ExcelUtils;
import com.kfgs.pretrialclassification.common.utils.ListUtils;
import com.kfgs.pretrialclassification.dao.*;
import com.kfgs.pretrialclassification.domain.*;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuMainResultExt;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuResultExt;
import com.kfgs.pretrialclassification.domain.response.CommonCode;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.Servlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class CaseConditionQueryServiceImpl implements CaseConditionQueryService {

    @Autowired
    FenleiBaohuMainMapper fenleiBaohuMainMapper;

    @Autowired
    FenleiBaohuResultMapper fenleiBaohuResultMapper;

    @Autowired
    FenleiBaohuUpdateipcMapper fenleiBaohuUpdateipcMapper;

    @Autowired
    FenleiBaohuUserinfoMapper fenleiBaohuUserinfoMapper;

    @Autowired
    FenleiBaohuAdjudicationMapper fenleiBaohuAdjudicationMapper;

    @Override
    @Transactional
    //查询所有案件
    public IPage findAll(String pageNo, String limit,String id,String name,String oraginization,String sqr,String sqh,String worker,String state,String begintime,String endtime) {
        Map resultMap = new HashMap();
        Page<FenleiBaohuMainResultExt> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(limit));
        //IPage<FenleiBaohuMain> iPage = fenleiBaohuMainMapper.selectPage(page, null);
        IPage<FenleiBaohuMainResultExt> iPage = fenleiBaohuMainMapper.selectByCondition(page,id,name,oraginization,sqr,sqh,worker,state,begintime,endtime);
        return iPage;
    }

    @Override
    @Transactional
    //根据查询条件查询案件
    public IPage findByCondition(String pageNo,String limit,Map conList){
        QueryWrapper queryWrapper = new QueryWrapper();
        if(conList != null) {
            if (conList.get("id").toString() != "") {
                String id = conList.get("id").toString();
                queryWrapper.like("id", id);
            }
            if ( conList.get("name").toString() != "") {
                String name = conList.get("name").toString();
                queryWrapper.like("name", name);
            }
        }
        Page<FenleiBaohuMain> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(limit));
        IPage<FenleiBaohuMain> iPage = fenleiBaohuMainMapper.selectPage(page, queryWrapper);
        return iPage;

    }
    @Override
    @Transactional
    //根据案件状态查询案件
    public IPage findCaseByState(String pageNo,String limit,String state){
        Map resultMap = new HashMap();
        QueryWrapper queryWrapper = new QueryWrapper();
        /*state:
        0:进案未分配
        1：进案已分配未出案
        2：已出案
         */
        queryWrapper.eq("state",state);
        Page<FenleiBaohuMain> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(limit));
        IPage<FenleiBaohuMain> iPage = fenleiBaohuMainMapper.selectPage(page,queryWrapper);
        return iPage;
    }

    @Override
    @Transactional
    //根据预审编号查询案件
    public List<FenleiBaohuResult> findClassInfoByID(String id) {
        QueryWrapper queryWrapper = new QueryWrapper();
        //模糊查询
        queryWrapper.eq("id",id);
        Map resultMap = new HashMap();
        //List<FenleiBaohuResult> list = fenleiBaohuResultMapper.selectList(queryWrapper);
        List<FenleiBaohuResult> list = fenleiBaohuResultMapper.selectListByID(id);
        return list;
    }

    @Override
    public List findUpdateInfoByID(String id, String worker) {
        QueryWrapper queryWrapper = new QueryWrapper();
        //精确查询
        queryWrapper.eq("id",id);
        String name = fenleiBaohuUserinfoMapper.selectUpdateWorkerName(worker);
        queryWrapper.eq("worker",name);
        queryWrapper.eq("state",0);
        List<FenleiBaohuUpdateIpc> list = fenleiBaohuUpdateipcMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    @Transactional
    //根据申请号查询案件
    public IPage findBySQH(String pageNo, String limit,String sqh) {
        Map resultMap = new HashMap();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.like("sqh",sqh);
        Page<FenleiBaohuMain> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(limit));
        IPage<FenleiBaohuMain> iPage = fenleiBaohuMainMapper.selectPage(page, queryWrapper);
        return iPage;
    }

    @Override
    @Transactional
    //根据案件名称查询案件
    public IPage findByName(String pageNo, String limit,String name) {
        Map resultMap = new HashMap();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.like("mingcheng",name);
        Page<FenleiBaohuMain> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(limit));
        IPage<FenleiBaohuMain> iPage = fenleiBaohuMainMapper.selectPage(page, queryWrapper);
        return iPage;
    }

    @Override
    @Transactional
    //根据申请人查询案件
    public IPage findBySQR(String pageNo, String limit,String sqr) {
        Map resultMap = new HashMap();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.like("sqr",sqr);
        Page<FenleiBaohuMain> page = new Page<>(Long.parseLong(pageNo),Long.parseLong(limit));
        IPage<FenleiBaohuMain> iPage = fenleiBaohuMainMapper.selectPage(page, queryWrapper);
        return iPage;
    }


    @Override
    public QueryResponseResult exportExcel(List<String> idlist, HttpServletResponse response) {
        List<List<String>> dataList = new ArrayList<>();
        if (idlist == null || idlist.size() == 0){
            return new QueryResponseResult(CommonCode.INVALID_PARAM,null);
        }else {
            for (int i=0;i<idlist.size();i++){
                List<String> row = new ArrayList<>();
                FenleiBaohuMain fenleiBaohuMain = fenleiBaohuMainMapper.selectById(idlist.get(i));
                String sqr = fenleiBaohuMain.getSqr();
                String mingcheng = fenleiBaohuMain.getMingcheng();
                String type = fenleiBaohuMain.getType();
                String oraginization = fenleiBaohuMain.getOraginization();
                String jinantime = fenleiBaohuMain.getJinantime().toString();
                String ipci = fenleiBaohuMain.getIpci();
                String cci = fenleiBaohuMain.getCci();
                String cca = fenleiBaohuMain.getCca();
                String csets = fenleiBaohuMain.getCsets();
                // 获取主副分类员信息
                /**
                 * 当有裁决情况时，主副分分类员均为裁决员？
                 * 主副分类员
                 */
                String mainworker = fenleiBaohuResultMapper.findMainWorkerByID(idlist.get(i));
                String assworker = "";
                List<String> assList = fenleiBaohuResultMapper.findAssWorkerByID(idlist.get(i));
                if (assList != null && assList.size() > 0){
                    if (assList.size() == 1){
                        assworker = assList.get(0);
                    }else {
                        for (int j=0;j<assList.size()-1;j++){
                            assworker += assList.get(j);
                            assworker += ",";
                        }
                        assworker += assList.get(assList.size()-1);
                    }

                }
                row = Arrays.asList(idlist.get(i),sqr,mingcheng,type,oraginization,jinantime,ipci,cci,cca,csets,mainworker,assworker);
                dataList.add(row);
                System.out.println(row);
            }
        }
        List<String> titleList = Arrays.asList("预审申请号","申请主体","发明名称","发明类型","所属保护中心","预审申请日","分类号","CCI","CCA","C-Sets","主分类员","副分类员");
        Boolean export = ExcelUtils.exportExcelByAdmin(response,"bhzx.xls",titleList,dataList);
        if (export){
            return new QueryResponseResult(CommonCode.SUCCESS,null);
        }else {
            return new QueryResponseResult(CommonCode.FAIL,null);
        }
    }

    /**
     * 2021.11.11修改  批量拆分Excel压缩导出
     * @param idlist
     * @param response
     * @return
     */
    @Override
    public QueryResponseResult exportExcelToZip(List<String> idlist, HttpServletResponse response) {
        if (idlist == null || idlist.size() == 0){
            return new QueryResponseResult(CommonCode.INVALID_PARAM,null);
        }else {
            String[] headers = new String[]{"预审申请号","申请主体","发明名称","发明类型","所属保护中心","预审接收日","分类号"/*,"CCI","CCA","C-Sets","主分类员","副分类员"*/};
            String[] headersKey = new String[]{"id","sqr","mingcheng","type","oraginization","jinantime","ipci"/*,"cci","cca","csets","mainworker","assworker"*/};
            try {
                //生成要导出的数据
                Map<String, List<String>> map = createExcelInfo(idlist);
                //导出
                excelsToZip(response,map,headers,headersKey);
                return new QueryResponseResult(CommonCode.SUCCESS,null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Map<String,List<String>> createExcelInfo(List<String> idlist) throws ParseException {
        Map<String,List<String>> dataMap = new LinkedHashMap<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        //遍历获取Excel内容
        for (int i = 0; i < idlist.size(); i++) {
            FenleiBaohuMain fenleiBaohuMain = fenleiBaohuMainMapper.selectById(idlist.get(i));
            FenleiBaohuAdjudication fenleiBaohuAdjudication = fenleiBaohuAdjudicationMapper.selectById(idlist.get(i));
            String sqr = fenleiBaohuMain.getSqr()==null?null:fenleiBaohuMain.getSqr();
            String mingcheng = fenleiBaohuMain.getMingcheng()==null?null:fenleiBaohuMain.getMingcheng();
            String type = fenleiBaohuMain.getType()==null?null:fenleiBaohuMain.getType();
            String oraginization = fenleiBaohuMain.getOraginization()==null?null:fenleiBaohuMain.getOraginization();
            String jinantime = fenleiBaohuMain.getJinantime().toString();
            LocalDateTime ldt = LocalDateTime.parse(jinantime,dtf);
            jinantime = ldt.format(formatter);//日期转换为yyyyMMdd格式
            String ipci = fenleiBaohuMain.getIpci();
            String cci = fenleiBaohuMain.getCci()==null?null:fenleiBaohuMain.getCci();
            String cca = fenleiBaohuMain.getCca()==null?null:fenleiBaohuMain.getCca();
            String csets = fenleiBaohuMain.getCsets()==null?null:fenleiBaohuMain.getCsets();
            String mainworker = "";
            String assworker = "";
            // 获取主副分类员信息
            /**
             * 当有裁决情况时，主副分分类员均为裁决员?
             */
            if (fenleiBaohuAdjudication != null){ //裁决案件
                mainworker = fenleiBaohuAdjudication.getProcessingPerson()==null?null:fenleiBaohuAdjudication.getProcessingPerson();
                assworker = fenleiBaohuAdjudication.getProcessingPerson()==null?null:fenleiBaohuAdjudication.getProcessingPerson();
            }else{
                mainworker = fenleiBaohuResultMapper.findMainWorkerByID(idlist.get(i));
                List<String> assList = fenleiBaohuResultMapper.findAssWorkerByID(idlist.get(i));
                if (assList != null && assList.size() > 0) {
                    if (assList.size() == 1) {
                        assworker = assList.get(0);
                    } else {
                        for (int j = 0; j < assList.size() - 1; j++) {
                            assworker += assList.get(j);
                            assworker += ",";
                        }
                        assworker += assList.get(assList.size() - 1);
                    }
                }
            }
            List<String> row = Arrays.asList(idlist.get(i), sqr, mingcheng, type, oraginization, jinantime, ipci, cci, cca, csets, mainworker, assworker);
            //List dataList = new ArrayList(row);
            if (dataMap.containsKey(oraginization)){
                System.out.println(row.toString());
                dataMap.get(oraginization).add(row.toString());
            }else {
                List<String> rowlist = new ArrayList<>();
                System.out.println(row.toString());
                rowlist.add(row.toString());
                dataMap.put(oraginization,rowlist);
            }
        }
        return dataMap;
    }

    private static void excelsToZip(HttpServletResponse response,Map<String,List<String>> map,String[] headers,String[] headersKey) throws IOException{
        ServletOutputStream outputStream = response.getOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        try{
            map.forEach((k,v) -> {
                //新建一个Excel并设置sheet头
                HSSFWorkbook workbook = ExcelUtils.createExcelAndSetHeaders(headers,k);
                //向sheet中填充对象的数据
                ExcelUtils.setSheetCellValue(workbook.getSheet(k),v,headersKey);
                try{
                    //创建压缩文件
                    ZipEntry zipEntry = new ZipEntry(k +"" + ".xls");
                    zipOutputStream.putNextEntry(zipEntry);
                }catch (IOException e){
                    try {
                        throw new Exception("向压缩包中添加Excel失败");
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                }
                try {
                    //写入一个压缩文件
                    workbook.write(zipOutputStream);
                }catch (IOException e){
                    try {
                        throw new Exception("向zip输出流中写入流数据失败");
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                }
            });
            zipOutputStream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            //关闭数据流
            zipOutputStream.close();
            outputStream.close();
        }
    }
}
