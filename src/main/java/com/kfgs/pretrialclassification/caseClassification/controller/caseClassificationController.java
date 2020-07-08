package com.kfgs.pretrialclassification.caseClassification.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kfgs.pretrialclassification.caseClassification.service.CaseClassificationService;
import com.kfgs.pretrialclassification.common.controller.BaseController;
import com.kfgs.pretrialclassification.common.utils.DateUtil;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import com.kfgs.pretrialclassification.domain.FenleiBaohuResult;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuMainResultExt;
import com.kfgs.pretrialclassification.userinfo.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
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
            //已分待出
            if (state == "1" || state.equals("1")) {
                classtype = "主";
            }
            //转案待审,作为副分，状态不为2
            if (state == "2" || state.equals("2")) {
                state = "";
                classtype = "副";
            }
            //已出案,状态为2
            //只在出案案件中按时间查询
            if (state == "3" || state.equals("3")) {
                state = "2";
                classtype = "";
            }
            //分类号更正待审
            if (state == "4" || state.equals("4")) {

            }
            //分类号裁决待审
            if (state == "5" || state.equals("5")) {

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
    public Map searchByCondition(String id,String sqh,String mingcheng){
        if (id == null){
            id = "";
        }
        if (sqh == null){
            sqh = "";
        }
        if (mingcheng == null){
            mingcheng = "";
        }
        Map resultMap = new HashMap();
        FenleiBaohuMain fenleiBaohuMain = new FenleiBaohuMain();
        fenleiBaohuMain = caseClassificationService.searchByCondition(id, sqh, mingcheng);
        if(fenleiBaohuMain != null){
            if (id == ""){
                id = fenleiBaohuMain.getId();
            }
            List<FenleiBaohuResult> list = new ArrayList<>();
            list = caseClassificationService.getSingleResult(id,sqh,mingcheng);
            resultMap.put("singleInfo",list);
            resultMap.put("case",fenleiBaohuMain);
            return resultMap;
        }
        else {
            return null;
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
    public Map caseTransfer(String list){
        Map resultMap = new HashMap();
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
            fenleiBaohuResult.setState("1");
            fenleiBaohuResult.setClasstype("副");
            transList.add(fenleiBaohuResult);
        }
        boolean retrn = caseClassificationService.caseTrans(transList);
        if (retrn){
            resultMap.put("code",20000);
            resultMap.put("message","转案成功");
        }else {
            resultMap.put("code",20000);
            resultMap.put("message","转案失败");
        }
        return resultMap;
    }

    @ApiOperation("result表，根据案件id和人员id出案")
    @GetMapping("/caseFinish")
    public void caseFinish(String ids,String user){

        String[] list = ids.split(",");
        String id = "";
        for(int i=0;i<list.length;i++){
            id = list[i];
            //是否可以出案
            boolean flag = false;
            //获取案件详情
            Map<String,String> map = new HashMap<>();
            map = caseClassificationService.getCaseInfo(id,user);

            String classtype = map.get("CLASSTYPE");
            String ipc = map.get("IPCI");
            String cca = map.get("CCA");
            String cci = map.get("CCI");
            String csets = map.get("CSETS");
            String type = map.get("TYPE");
            String state = map.get("STATE");
            /*
            根据案件类型确定出案标准：
            FM的IPC和CCI不能为空，XX的IPC必须不为空，CPC必须全为空
             */
            if(classtype.equals("副")){
                flag = true;
            }else{
                if (type.equals("FM")){
                    if (ipc != null && cci != null){
                        flag = true;
                    }
                }else if(type.equals("XX")){
                    if (ipc != null && cci == null && cca==null && csets==null){
                        flag = true;
                    }
                }
            }
            if (!flag){
                System.out.println("出案失败:FM的IPC和CCI不能为空，XX的IPC必须不为空，CPC必须全为空");
                return;
            }
            /*//个人单一出案
            int unfinish = caseClassificationService.updateResult(id,user,state);

            //判断是否都出案了，若是则更新main表中的state，出案结束
            if (unfinish == 0){
                //在main表中出案
                caseClassificationService.updateMain(id);
            }*/
        }

    }

    private String checkCode(String str,String codeName){
        if (str.equals("")){
            return str;
        }
        return str;
    }

}
