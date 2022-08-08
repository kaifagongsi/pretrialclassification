package com.kfgs.pretrialclassification.fuzzymatch.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.kfgs.pretrialclassification.common.utils.MultipartFileToFile;
import com.kfgs.pretrialclassification.dao.FenleiBaohuUserinfoMapper;
import com.kfgs.pretrialclassification.domain.FuzzyMatchReadExcel;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuUserinfoExt;
import com.kfgs.pretrialclassification.domain.response.CommonCode;
import com.kfgs.pretrialclassification.domain.response.FuzzyMatchEnum;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;
import com.kfgs.pretrialclassification.fuzzymatch.service.FuzzyMatchService;
import com.kfgs.pretrialclassification.userinfo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/fuzzymactch")
public class FuzzyMatchController {

    @Autowired
    FuzzyMatchService fuzzyMatchService;

    @Autowired
    UserService userService;

    @NacosValue(value = "${pretrialclassification.fileSave.excel:C:/20210111_bhzx/bhzx/excel/}",autoRefreshed = true)
    private String fileSve;


    @GetMapping("/matchAll")
    public QueryResponseResult matchAll(){
        FenleiBaohuUserinfoExt user = userService.findUserWorkerName();
        if(user.getDep1().equals("系统建设与运维部")){
            if(fuzzyMatchService.getState() == 0){
                fuzzyMatchService.matchAll();
                try {
                    //等待异步线程执行
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return new QueryResponseResult(CommonCode.SUCCESS,null);
            }else{
                return new QueryResponseResult(FuzzyMatchEnum.FUZZY_MATCH_ERROR,null);
            }
        }else{
            return new QueryResponseResult(FuzzyMatchEnum.NO_PERMISSION_OPERATION,null);
        }


    }

    @GetMapping("/getMatchState")
    public String getMatchState(){
        return fuzzyMatchService.getProgressState();
    }

    @PostMapping("/uploadFile")
    public void uploadFile(MultipartFile file, HttpServletResponse response){
        FileInputStream in = null;
        OutputStream out = null;
        try {
            List<FuzzyMatchReadExcel> list = new ArrayList<>();
            MultipartFileToFile.multipartFileToFile(fileSve,file);
            EasyExcel.read(fileSve + file.getOriginalFilename(), FuzzyMatchReadExcel.class, new ReadListener<FuzzyMatchReadExcel>() {
                @Override
                public void invoke(FuzzyMatchReadExcel excel, AnalysisContext analysisContext) {
                    list.add(excel);
                }
                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                }
            }).sheet().doRead();
            //0.处理文件
            String filePath = fuzzyMatchService.matchExcel(list,file.getOriginalFilename());

            //1.开始响应文件
            String filename = filePath.substring(filePath.lastIndexOf("/")+1);
            filename = new String(filename.getBytes("iso8859-1"),"UTF-8");

            String downloadpath = filePath;
            //如果文件不存在
			/*if(!file.exists()){
			    return false;
			}*/
            //设置响应头，控制浏览器下载该文件
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
            //读取要下载的文件，保存到文件输入流
            in= new FileInputStream(downloadpath);
            //创建输出流
            out= response.getOutputStream();
            //缓存区
            byte buffer[] = new byte[1024];
            int len = 0;
            //循环将输入流中的内容读取到缓冲区中
            while((len = in.read(buffer)) > 0){
                out.write(buffer, 0, len);
            }
         }catch (Exception e){
            e.printStackTrace();
         } finally {
            try {
                in.close();
                out.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



}
