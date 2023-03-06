package com.kfgs.pretrialclassification.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/*public class ExcelUtils {
    public static void exportExcelByAdmin(HttpServletResponse response, String fileName, List<String> columnList,List<List<String>> dataList) throws  Exception{
        // 告诉浏览器用什么软件可以打开此文件
        response.setHeader("content-Type", "application/vnd.ms-excel");
        // 下载文件的默认名称
        response.setHeader("Content-Disposition", "attachment;filename="+URLEncoder.encode(fileName, "utf-8"));
        exportExcel(fileName,columnList,dataList,response.getOutputStream());
    }

    public static void exportExcel(String fileName,List<String> columnList,List<List<String>> dataList,OutputStream outputStream) throws Exception{
        XSSFWorkbook workbook = new XSSFWorkbook();
        try {
            String sheetName = "Sheet1";
            XSSFSheet sheet = workbook.createSheet(sheetName);
            writeExcel(workbook,sheet,columnList,dataList);
            workbook.write(outputStream);
        }finally {
            workbook.close();
        }
    }

    private static void writeExcel(XSSFWorkbook workbook,Sheet sheet,List<String> columnList,List<List<String>> dataList) {
        int excelRow = 0;
        Font titleFont = workbook.createFont();
        titleFont.setFontName("simsun");
        titleFont.setBold(true);
        titleFont.setColor(IndexedColors.BLACK.index);
        XSSFCellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        titleStyle.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
        titleStyle.setFillForegroundColor(new XSSFColor(new Color(182, 184, 192)));
        titleStyle.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        titleStyle.setFont(titleFont);
        setBorder(titleStyle, BorderStyle.THIN, new XSSFColor(new Color(0, 0, 0)));
        //创建标题行
        Row titleRow = sheet.createRow(excelRow++);
        for (int i=0;i<columnList.size();i++){
            //创建该行下的每一列，并写入标题数据
            Cell cell = titleRow.createCell(i);
            cell.setCellValue(columnList.get(i));
        }
        // 设置内容行
        if (dataList != null && dataList.size() > 0){
            //序号从1开始
            int count = 1;
            // 外层for循环创建行
            for (int i=0;i<dataList.size();i++){
                Row dataRow = sheet.createRow(excelRow++);
                // 内层for循环创建每行对应的列并赋值
                for (int j = 0;j<dataList.get(0).size();j++){
                    Cell cell = dataRow.createCell(j+1);
                    cell.setCellValue(dataList.get(i).get(j)==null?"":dataList.get(i).get(j));
                }
            }
        }

    }
    private static void setBorder(XSSFCellStyle style, BorderStyle border, XSSFColor color) {
        style.setBorderTop(border);
        style.setBorderLeft(border);
        style.setBorderRight(border);
        style.setBorderBottom(border);
        style.setBorderColor(XSSFCellBorder.BorderSide.TOP, color);
        style.setBorderColor(XSSFCellBorder.BorderSide.LEFT, color);
        style.setBorderColor(XSSFCellBorder.BorderSide.RIGHT, color);
        style.setBorderColor(XSSFCellBorder.BorderSide.BOTTOM, color);
    }
}*/
public class ExcelUtils {

    public static boolean exportExcelByAdmin(HttpServletResponse response, String fileName, List<String> columnList, List<List<String>> dataList){
        //声明输出流
        OutputStream outputStream = null;
        //响应头
        setResponseHeader(response,fileName);
        try {
            System.out.println("开始导出Excel...");
            //获取输出流
            outputStream = response.getOutputStream();
            // 内存中保留1000条数据，以免内存溢出，其余写入硬盘
            SXSSFWorkbook workbook = new SXSSFWorkbook(1000);
            //获取该工作区的第一个sheet
            Sheet sheet1 = workbook.createSheet("sheet1");
            int excelRow = 0;
            //创建标题行
            Row titleRow = sheet1.createRow(excelRow++);
            for (int i=0;i<columnList.size();i++){
                //创建该行下的每一列，并写入标题数据
                Cell cell = titleRow.createCell(i);
                //设置表中的编码
                cell.setCellValue(HSSFCell.ENCODING_UTF_16);
                cell.setCellValue(columnList.get(i));
            }
            // 设置内容行
            if (dataList != null && dataList.size() > 0){
                //序号从1开始
                int count = 1;
                // 外层for循环创建行
                for (int i=0;i<dataList.size();i++){
                    Row dataRow = sheet1.createRow(excelRow++);//创建内容行
                    // 内层for循环创建每行对应的列并赋值
                    for (int j = 0;j<dataList.get(0).size();j++){
                        Cell cell = dataRow.createCell(j);
                        //cell.setCellValue(HSSFCell.ENCODING_UTF_16);
                        cell.setCellValue(dataList.get(i).get(j)==null?"":dataList.get(i).get(j));
                    }
                }
            }
            //将整理好的Excel数据写入流中
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("导出Excel失败！");
            return false;
            //return new QueryResponseResult(CommonCode.FAIL,null);
        } finally {
            try {
                //关闭输出流
                if (outputStream != null){
                    outputStream.close();
                    System.out.println("**********导出Excel结束***********");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private static void setResponseHeader(HttpServletResponse response,String fileName) {
        try{
            try {
                fileName = new String(fileName.getBytes("GB2312"),"ISO_8859_1");
                fileName = URLEncoder.encode(fileName,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            response.setHeader("Content-Dispositon", "attachment;filename=" + fileName );
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 11.11更新 创建Excel并设置sheet头
     */
    public static HSSFWorkbook createExcelAndSetHeaders(String[] headers, String sheetName){
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        //HSSFCellStyle headstyle = createCellStyle(hssfWorkbook, (short) 18);
        //HSSFCellStyle style1 = createCellStyle(hssfWorkbook, (short) 13);
        HSSFSheet hssfSheet = hssfWorkbook.createSheet(sheetName);
        /*hssfSheet.setDefaultColumnWidth(15);
        hssfSheet.setDefaultRowHeightInPoints(15);
        hssfSheet.autoSizeColumn(1, true);
        HSSFRow row0 = hssfSheet.createRow(0);
        row0.setHeightInPoints(50);
        HSSFCell cell0 = row0.createCell(0);
        // 加载单元格样式
        cell0.setCellStyle(headstyle);
        String titleName = "保护中心 ("+sheetName.substring(sheetName.lastIndexOf("_")+1)+")";
        cell0.setCellValue(titleName);*/
        //hssfSheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length ));
        HSSFRow row1 = hssfSheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            HSSFCell cellHeader = row1.createCell(i);
            //cellHeader.setCellStyle(style1);
            cellHeader.setCellValue(headers[i]);
        }
        return hssfWorkbook;
    }

    public  static void setSheetCellValue(HSSFSheet hssfSheet,List<String> dtos,String[] headersKey) {
        int rows = dtos.size();
        for (int i=0;i<rows;i ++){
            //HSSFRow row = hssfSheet.createRow(hssfSheet.getLastRowNum() + 1);
            HSSFRow row = hssfSheet.createRow(hssfSheet.getLastRowNum() + 1);
            int count = -1;
            // 注意去掉空格
            String line = dtos.get(i).replaceAll("\\[","").replaceAll("\\]","").replaceAll(" ","");
            String[] lines = line.split(",");
            for (int j = 0; j < headersKey.length; j++) {
//                row.createCell(++count).setCellValue(lines[j] == null?"":lines[j]);
                if(StringUtils.isNotEmpty(lines[j])){
                    // ipci 特殊处理
                    if(lines[j].contains("***")){
                        lines[j] = lines[j].replaceAll("\\*\\*\\*",",");
                    }
                    row.createCell(++count).setCellValue(lines[j]);
                }else{
                    row.createCell(++count).setCellValue("");
                }
            }
        }
    }
    /**
     * 创建单元格样式
     *
     * @param workbook 工作簿
     * @param fontSize 字体大小
     * @return 单元格样式
     */
    private static HSSFCellStyle createCellStyle(HSSFWorkbook workbook, short fontSize) {
        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);// 水平居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直居中
        /*// 创建字体
        HSSFFont font = workbook.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);// 加粗字体
        font.setFontHeightInPoints(fontSize);
        // 加载字体
        style.setFont(font);*/
        return style;
    }
}

