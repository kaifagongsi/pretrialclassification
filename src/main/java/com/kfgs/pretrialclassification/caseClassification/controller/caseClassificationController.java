package com.kfgs.pretrialclassification.caseClassification.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kfgs.pretrialclassification.caseClassification.service.CaseClassificationService;
import com.kfgs.pretrialclassification.common.controller.BaseController;
import com.kfgs.pretrialclassification.common.log.Log;
import com.kfgs.pretrialclassification.common.repeatsubmit.NoRepeatSubmit;
import com.kfgs.pretrialclassification.common.utils.DateUtil;
import com.kfgs.pretrialclassification.dao.FenleiBaohuResultMapper;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuMainResultExt;
import com.kfgs.pretrialclassification.domain.response.CommonCode;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;
import com.kfgs.pretrialclassification.domain.response.QueryResult;
import com.kfgs.pretrialclassification.userinfo.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Api("分类员查询的测试")
@RestController
@RequestMapping("/caseClassification")
public class caseClassificationController extends BaseController {

    @Autowired
    CaseClassificationService caseClassificationService;

    @Autowired
    UserService userService;

    @ApiOperation("result表，按状态查询案件")
    @GetMapping("/findCaseByState")
    public Map findCaseByState(String page,String limit,String state,String beginTime,String endTime){
        Map resultMap = new HashMap();
        //获取当前登录用户信息
        Map usermap = userService.findUserInfo();
        if(null == usermap){
            return null;
        }
        String user = usermap.get("name").toString();
        String role = usermap.get("introduction").toString();

        if (role != "admin") {//分类员
            if (state == null) {
                state = "0";
            }
            String classtype = "主";
            //新分待审
            if (state == "0" || state.equals("0")) {
                classtype = "主";
            }
            //转案待审,作为副分，状态为0
            if (state == "1" || state.equals("1")) {
                state = "0";
                classtype = "副";
            }
            //已分待出
            if (state == "2" || state.equals("2")) {
                state = "1";
                classtype = "";
            }
            //已出案,状态为2
            //只在出案案件中按时间查询
            if (state == "3" || state.equals("3")) {
                state = "2";
                classtype = "";
            }
            //分类号更正待审
            if (state == "4" || state.equals("4")) {
                state = "9";
                classtype = "";
            }
            //分类号裁决待审
            if (state == "5" || state.equals("5")) {
                state = "7";
                classtype = "";
            }
            if(beginTime == null){
                beginTime = "";
            }
            if (endTime == null){
                endTime = "";
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
            Map<String, Object> dataTable = getDataTable(caseClassificationService.findCaseByState(page, limit, state, classtype, user,beginTime,endTime));
            resultMap.put("code", 20000);
            resultMap.put("data", dataTable);
            resultMap.put("user",user);
        }
        return resultMap;
    }

    @ApiOperation("分类员界面条件查询，三个查询参数")
    @GetMapping("/searchByCondition")
    public Map searchByCondition(String id,String sqr,String mingcheng){
        if (id == null){
            id = "";
        }
        if (sqr == null){
            sqr = "";
        }
        if (mingcheng == null){
            mingcheng = "";
        }
        Map resultMap = new HashMap();
        FenleiBaohuMain fenleiBaohuMain = new FenleiBaohuMain();
        fenleiBaohuMain = caseClassificationService.searchByCondition(id, sqr, mingcheng);
        if(fenleiBaohuMain != null){
            if (id == ""){
                id = fenleiBaohuMain.getId();
            }
            List<FenleiBaohuResult> list = new ArrayList<>();
            list = caseClassificationService.getSingleResult(id,sqr,mingcheng);
            resultMap.put("singleInfo",list);
            resultMap.put("case",fenleiBaohuMain);
            return resultMap;
        }
        else {
            resultMap.put("case",null);
            return resultMap;
        }
    }

    @ApiOperation("result表，根据案件id查询已存在转案人员列表")
    @GetMapping("/getTransWorkerList")
    public Map getTransWorkerList(String id){
        Map resultMap = new HashMap();
        List<String> list = new ArrayList<>();
        List<String> idList = new ArrayList<>();
        list = caseClassificationService.getTransWorkerList(id);
        for (int i=0;i<list.size();i++){
            String workerid = list.get(i).substring(0,6);
            idList.add(workerid);
        }
        resultMap.put("code",20000);
        resultMap.put("workerlist",idList);
        return resultMap;
    }

    @ApiOperation("result表，转案")
    @PostMapping("/caseTransfer")
    @NoRepeatSubmit
    @Log
    public QueryResponseResult caseTransfer(String list){
        //Map resultMap = new HashMap();
        //转案
        //解析转案人员列表及留言
        JSONArray jsonArray = JSONArray.parseArray(list);
        System.out.println(jsonArray.size());
        List<FenleiBaohuResult> transList = new ArrayList<>();
        for (int i=0;i<jsonArray.size();i++){
            JSONObject obj = jsonArray.getJSONObject(i);
            FenleiBaohuResult fenleiBaohuResult = new FenleiBaohuResult();
            String str = obj.getString("name");
            //截取姓名
            String name = str.substring(0,str.indexOf("["));
            String message = obj.getString("message");
            String id = obj.getString("id");
            String worker = obj.getString("worker");
            fenleiBaohuResult.setId(id);
            fenleiBaohuResult.setWorker(name);
            fenleiBaohuResult.setFenpeitime(DateUtil.formatFullTime(LocalDateTime.now()));
            fenleiBaohuResult.setFenpeiren(worker);
            fenleiBaohuResult.setMESSAGE(message);
            fenleiBaohuResult.setState("0");
            fenleiBaohuResult.setClasstype("副");
            transList.add(fenleiBaohuResult);
        }
        QueryResponseResult retrn = caseClassificationService.caseTrans(transList);

        /*if (retrn.isSuccess()){
            return new QueryResponseResult(CommonCode.SUCCESS,null);
            *//*resultMap.put("code",20000);
            resultMap.put("message","转案成功");*//*
        }else {
            return new QueryResponseResult(CommonCode.FAIL,null);
            *//*resultMap.put("code",20000);
            resultMap.put("message","转案失败");*//*
        }*/
        return retrn;
    }

    @ApiOperation("result表,保存案件分类信息")
    @PostMapping("/updateClassificationInfo")
    @Log
    public QueryResponseResult updateClassificationInfo(@RequestBody FenleiBaohuResult fenleiBaohuResult){
        //String id = fenleiBaohuResult.getId();
        return caseClassificationService.saveClassificationInfo(fenleiBaohuResult);
    }

    @ApiOperation("个人出案")
    @GetMapping("/caseFinish")
    @Log
    public QueryResponseResult caseFinishTest(String ids,String user){
        return  caseClassificationService.caseFinish(ids,user);
    }

    @ApiOperation("分类号更正")
    @PostMapping("/caseCorrect")
    @Log
    public QueryResponseResult caseCorrect(@RequestBody FenleiBaohuResult fenleiBaohuResult){
        return caseClassificationService.caseCorrect(fenleiBaohuResult);
    }
    @ApiOperation("判断是否最后一个出案，是否有人有主分号")
    @GetMapping("/lastFinishAndMoreIPCMI")
    public QueryResponseResult judgeIfLastFinish(String id ){
        return caseClassificationService.judgeIfLastFinish(id);
    }

   /* @ApiOperation("result表，根据案件id和人员id出案")
    @GetMapping("/caseFinish")
    public QueryResponseResult caseFinish(String ids, String user){

        //待出案的id
        String[] list = ids.split(",");
        String id = "";
        for(int i=0;i<list.length;i++){
            int res = 0;
            id = list[i];
            //是否最后一个出案
            //查询除自己以外其他未出案
            boolean isLast = false;
            List<String> unFinish = new ArrayList<>();
            unFinish = caseClassificationService.getCaseUnFinish(id);
            if (unFinish.size() == 1){
                isLast = true;
                //最后一个出案,判断是否进裁决,不用裁决则出案完成更改main表案件状态，否则进入裁决
                //校验：无主分、多个主分、超过两人给出组合码且总数大于99组,FM案件CPC为空
                *//*String test="";
                test = caseClassificationService.caseRule(id);*//*
                QueryResponseResult result = caseClassificationService.caseRule(id);
                QueryResult queryResult = new QueryResult();
                int test = result.getCode();
                String message = result.getMessage();
                Map map = new HashMap();
                queryResult = result.getQueryResult();
                map = queryResult.getMap();
                if (test == 20000){
                    FenleiBaohuMain fenleiBaohuMain = new FenleiBaohuMain();
                    fenleiBaohuMain = (FenleiBaohuMain)map.get("mainUpdateInfo");
                    //分类号校验通过，完成个人出案
                    res += caseClassificationService.updateResult(id,user,"2");
                    //更新main表信息
                    res += caseClassificationService.updateMain(fenleiBaohuMain);
                    //分类号校验通过，完成个人出案
                    //caseClassificationService.updateResult(id,user,"2");
                    //更改main表状态出案
                    //caseClassificationService.updateMain(id,"2");
                    if (res == 2){
                        //出案成功
                        return new QueryResponseResult(CommonCode.SUCCESS,null);
                    }
                }else{
                    //进入裁决，传id和理由

                    //更改案件状态为裁决
                    String ruleState = "7";
                    res = caseClassificationService.updateCaseRule(id,ruleState);
                    if (res == 1){
                        return result;
                    }
                }
            }else {
                //不是最后一个出案,个人直接出案,不改变main表
                res = caseClassificationService.updateResult(id,user,"2");
            }
        }
        return null;
    }*/

    @ApiOperation("CPC转IPC")
    @GetMapping("/cpc2ipc")
    public QueryResponseResult cpcToIpc(String cci,String cca ){
        return caseClassificationService.cpcToIpc(cci,cca);
    }

}
