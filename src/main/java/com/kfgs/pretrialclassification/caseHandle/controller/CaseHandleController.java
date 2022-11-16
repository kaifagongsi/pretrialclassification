package com.kfgs.pretrialclassification.caseHandle.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kfgs.pretrialclassification.caseHandle.service.CaseHandleService;
import com.kfgs.pretrialclassification.common.controller.BaseController;
import com.kfgs.pretrialclassification.domain.FenleiBaohuMain;
import com.kfgs.pretrialclassification.domain.ext.FenleiBaohuMainResultExt;
import com.kfgs.pretrialclassification.domain.response.QueryResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import oracle.jdbc.proxy.annotation.Post;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author mango
 */
@Api("案件入库测试")
@RestController
@RequestMapping("/caseHandle")
public class  CaseHandleController extends BaseController {

    @Autowired
    CaseHandleService caseHandleService;

    @ApiOperation("导入xml案件清单")
    @RequestMapping("/insertData")
    public Map insertData(String type, String region, String page, String limit, MultipartFile file ){
        Map resultMap = new HashMap();
        if (type == null){
            type = "";
        }
        if (region == null){
            region = "";
        }
        File newFile = null;
        if(file != null){
            try {
                String fileRealName = file.getOriginalFilename();//获得原始文件名;
                int pointIndex =  fileRealName.lastIndexOf(".");//点号的位置
                String fileSuffix = fileRealName.substring(pointIndex);//截取文件后缀

                //用缓冲区来实现这个转换即使用java 创建的临时文件
                newFile = File.createTempFile("tmp", fileSuffix);
                file.transferTo(newFile);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(newFile != null && newFile.exists()) {
            HashMap<String,FenleiBaohuMain> map = readExcelXml(newFile);
            if(map.isEmpty()){
                System.out.println(" 获取Excel文件内容失败");
            }
            //删除临时文件
            newFile.delete();
            for (Iterator<String> iterator = map.keySet().iterator(); iterator.hasNext();) {
                String key = iterator.next();
                FenleiBaohuMain bean = map.get(key);
                bean.setState("0");
                bean.setPdfPath(bean.getId() + type);
                bean.setOraginization(region);
                caseHandleService.saveInfo(bean);
            }
            System.out.println(" 导入Excel完成！");
        }
        //导入excel完成后，刷新列表页未分配案件清单
        Map<String, Object> dataTable = getDataTable(caseHandleService.findByState(page,limit));
        resultMap.put("code",20000);
        resultMap.put("data",dataTable);
        return resultMap;
    }

    @ApiOperation(value = "跟据id，删除数据" )
    @DeleteMapping("/deleteDataByID/{id}")
    public QueryResponseResult deleteDataById(@PathVariable("id") String id){
        return  caseHandleService.deleteDataById(id);
    }

    @ApiOperation("更新粗分号")
    @RequestMapping("/updateSimpleclasscode")
    public QueryResponseResult updateSimpleclasscode(String id, String simpleclasscode){
        return  caseHandleService.updateSimpleclasscode(id, simpleclasscode);
    }


    @ApiOperation("获取未完成案件清单")
    @RequestMapping("/getUnfinishedList")
    public Map getUnfinishedList(String page, String limit){
        Map resultMap = new HashMap();
        //导入excel完成后，刷新列表页未分配案件清单
        Map<String, Object> dataTable = getDataTable(caseHandleService.selectByCondition(page,limit));
        resultMap.put("code",20000);
        resultMap.put("data",dataTable);
        return resultMap;
    }


    //解析excel文件
    private HashMap<String, FenleiBaohuMain> readExcelXml(File excelFile) {
        String fileName = excelFile.getName();
        HashMap<String,FenleiBaohuMain> map = new HashMap<String,FenleiBaohuMain>();
        try {
            FileInputStream is = new FileInputStream(excelFile);
            Workbook wb = null;
            if(fileName.endsWith(".xls")){
                wb = new HSSFWorkbook(is);
            }else if(fileName.endsWith(".xlsx")){
                wb = new XSSFWorkbook(is);
            }
            if(wb != null){
                Sheet rs = wb.getSheetAt(0);
                //预审申请号	申请主体	发明名称	发明类型	所属保护中心	预审申请日	小类分类号
                for (int i = 1; i < rs.getPhysicalNumberOfRows(); i++) {
                    Row row = rs.getRow(i);
                    if(row == null){
                        continue;
                    }
                    FenleiBaohuMain bean = new FenleiBaohuMain();
                    Cell cellId = row.getCell(0);
                    String id = "";
                    if(cellId != null){//预审编号，预审申请号
                        id = cellId.getStringCellValue();
                        if("".equals(id.trim())){
                            continue;
                        }
                        bean.setId(id);
                    }else{
                        continue;
                    }
                    Cell cellSqr = row.getCell(1);
                    if(cellSqr != null){//申请主题，申请人
                        bean.setSqr(cellSqr.getStringCellValue());
                    }
                    Cell cellTitle = row.getCell(2);
                    if(cellTitle != null){//发明名称
                        bean.setMingcheng(cellTitle.getStringCellValue());
                    }
                    Cell cellType = row.getCell(3);
                    if(cellType != null){//发明类型
                        String typeVal = cellType.getStringCellValue().trim();
                        if("发明".equals(typeVal) || "发明专利".equals(typeVal) ||"发明".equals(typeVal.trim())||"fm".equals(typeVal.toLowerCase())){
                            typeVal = "FM";
                        }else if("新型".equals(typeVal) || "实用新型".equals(typeVal) || "实用新型专利".equals(typeVal) ||"xx".equals(typeVal.toLowerCase())){
                            typeVal = "XX";
                        }else if("外观设计".equals(typeVal)){
                            continue;
                        }
                        bean.setType(typeVal);
                    }
                    map.put(id, bean);
                }
            }
            wb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

}