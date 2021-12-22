package com.kfgs.pretrialclassification.caseQuery.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kfgs.pretrialclassification.caseQuery.service.CaseConditionQueryService;
import com.kfgs.pretrialclassification.common.exception.PretrialClassificationException;
import com.kfgs.pretrialclassification.common.controller.BaseController;

import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author mango
 */
@Api("分类main表的检索测试")
@RestController
@RequestMapping("/caseQuery/caseConditionQuery")
public class caseConditionQueryController extends BaseController{

    @Autowired
    CaseConditionQueryService caseConditionQueryService;

    @ApiOperation("main表，查询所有案件")
    @GetMapping("/findAllCase")
    public Map findAllCase(String page,String limit,String id,String mingcheng,String oraginization,String sqr,String sqh,String worker,String state,String beginTime,String endTime) throws PretrialClassificationException {
        //获取查询条件
        Map resultMap = new HashMap();
        if(state == null || state == "all" || state.equals("all") || state==""){
            state = "";
        }
        if (id == null){
            id = "";
        }
        if(mingcheng == null){
            mingcheng = "";
        }
        if(sqr == null){
            sqr = "";
        }
        if(sqh == null){
            sqh = "";
        }
        if (worker == null){
            worker = "";
        }
        if(beginTime == null){
            beginTime = "";
        }
        if (endTime == null){
            endTime = "";
        }
        if(oraginization == null){
            oraginization = "";
        }
        if(beginTime == "" && endTime != ""){
            beginTime = "19000000000000";
            endTime = endTime.replace("-","")+"235959";
        }else if(beginTime != "" && endTime == ""){
            beginTime = beginTime.replace("-","")+"000000";
            endTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
            endTime = endTime.replace("-","")+"235959";
        }else if(beginTime != "" && endTime != ""){
            beginTime = beginTime.replace("-","")+"000000";
            endTime = endTime.replace("-","")+"235959";
        }else{
            beginTime="";
            endTime="";
        }
        Map<String, Object> dataTable = getDataTable(caseConditionQueryService.findAll(page,limit,id,mingcheng,oraginization,sqr,sqh,worker,state,beginTime,endTime));
        resultMap.put("code",20000);
        resultMap.put("data",dataTable);
        return resultMap;
    }

    @ApiOperation("main表，按案件状态查询案件")
    @GetMapping("/findCaseByState")
    public Map findCaseByState(String page,String limit,String state) throws PretrialClassificationException{
        Map resultMap = new HashMap();
        Map<String,Object> dataTable = getDataTable(caseConditionQueryService.findCaseByState(page,limit,state));
        return resultMap;
    }

    @ApiOperation("main表，根据预审编号查询案件分类信息")
    @GetMapping("/findClassInfoByID")
    public Map findClassInfoByID(String id) throws PretrialClassificationException{

        Map resultMap = new HashMap();
        List list = caseConditionQueryService.findClassInfoByID(id);
        resultMap.put("code",20000);
        resultMap.put("data",list);
        return resultMap;
    }

    @ApiOperation("更正列表,根据预审编号查询案件分类信息")
    @GetMapping("/findUpdateInfoByID")
    public Map findUpdateInfoByID(String id, String worker) throws PretrialClassificationException{
        Map resultMap = new HashMap();
        List list = caseConditionQueryService.findUpdateInfoByID(id,worker);
        resultMap.put("code",20000);
        resultMap.put("data",list);
        return resultMap;
    }

    @ApiOperation("main表，根据申请号查询案件")
    @GetMapping("/findBySQH")
    public Map findBySQH(String page,String limit,String sqh) throws PretrialClassificationException{
        Map resultMap = new HashMap();
        Map<String,Object> dataTable = getDataTable(caseConditionQueryService.findBySQH(page,limit,sqh));
        resultMap.put("code",20000);
        resultMap.put("data",dataTable);
        return resultMap;
    }

    @ApiOperation("main表，根据案件名称查询案件")
    @GetMapping("/findByName")
    public Map findByName(String page,String limit,String name) throws PretrialClassificationException{
        Map resultMap = new HashMap();
        Map<String,Object> dataTable = getDataTable(caseConditionQueryService.findByName(page,limit,name));
        resultMap.put("code",20000);
        resultMap.put("data",dataTable);
        return resultMap;
    }

    @ApiOperation("main表，根据申请人查询案件")
    @GetMapping("/findBySQR")
    public Map findBySQR(String page,String limit,String sqr) throws PretrialClassificationException{
        Map resultMap = new HashMap();
        Map<String,Object> dataTable = getDataTable(caseConditionQueryService.findBySQR(page,limit,sqr));
        resultMap.put("code",20000);
        resultMap.put("data",dataTable);
        return resultMap;
    }

    @ApiOperation("更新案件导出状态")
    @PostMapping("/caseExportFinish")
    public QueryResponseResult caseExportFinish(String list){
        JSONArray jsonArray = JSONArray.parseArray(list);
        for (int i=0;i<jsonArray.size();i++){
            JSONObject obj = jsonArray.getJSONObject(i);
            String id = obj.getString("id");
            System.out.println(id);
        }
        return null;
    }

    //@ApiOperation(value = "Excel导出",produces = "application/octet-stream")
    @RequestMapping(value = "/exportExcel", method = RequestMethod.POST)
    //@ResponseBody
    public QueryResponseResult exportExcel(String list, HttpServletResponse response) throws IOException {
        // 解析要导出的案件id
        JSONArray jsonArray = JSONArray.parseArray(list);
        List<String> idList = new ArrayList<>();
        for(int i=0;i<jsonArray.size();i++){
            idList.add(jsonArray.get(i).toString());
        }
        return caseConditionQueryService.exportExcel(idList,response);
    }

    /**
     * 2021.11.11修改 exportExcelToZip
     */
    @RequestMapping(value = "/exportExcelToZip", method = RequestMethod.POST)
    public QueryResponseResult exportExcelToZip(String list,HttpServletResponse response) throws  IOException {
        //解析要导出的案件id
        JSONArray jsonArray = JSONArray.parseArray(list);
        List<String> idList = new ArrayList<>();
        for(int i=0;i<jsonArray.size();i++){
            idList.add(jsonArray.get(i).toString());
        }
        return caseConditionQueryService.exportExcelToZip(idList,response);
    }
}
