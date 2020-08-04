package com.kfgs.pretrialclassification.updateIpc.controller;


import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;
import com.kfgs.pretrialclassification.updateIpc.service.FenleiBaohuUpdateipcService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mango
 */
@RestController
@RequestMapping("/updateipc")
public class FenleiBaohuUpdateipcController {

    @Autowired
    FenleiBaohuUpdateipcService fenleiBaohuUpdateipcService;

    @ApiOperation("获取待处理列表")
    @GetMapping("/selectInitList/{pageNum}/{size}/{state}")
    public QueryResponseResult selectInitList(@PathVariable("pageNum") int pageNum, @PathVariable("size")int size,@PathVariable("state") String state){
        return fenleiBaohuUpdateipcService.selectInitList(pageNum, size, state);
    }
    @ApiOperation("更新案件状态")
    @GetMapping("/updateIpcState/{id}/{state}")
    public QueryResponseResult updateIpcState(@PathVariable("id") String id, @PathVariable("state") String state){
        return fenleiBaohuUpdateipcService.updateIpcState(id, state);
    }
}
