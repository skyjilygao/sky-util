package com.skyjilygao.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Excel生成工具
 */
public class ExcelUtil {
    public static String NO_DEFINE = "no_define";//未定义的字段
    public static String DEFAULT_DATE_PATTERN = "yyyy年MM月dd日";//默认日期格式
    public static int DEFAULT_COLOUMN_WIDTH = 17;

    /**
     * 导出Excel 2007 OOXML (.xlsx)格式
     *
     * @param headMap     key: 属性(etc: key10), value: 列头名称(etc: 示例1)
     * @param jsonArray   数据集(etc: [{"key10":"v10","key11":"v11"},{"key20":"v20","key21":"v21"}])
     * @param colWidth    列宽 默认 至少17个字节
     * @param out         输出流
     */
    public static void exportExcelX(Map<String, String> headMap, JSONArray jsonArray, int colWidth, OutputStream out) {
        exportExcelX(null, headMap, jsonArray, null, colWidth, out);
    }
    /**
     * 导出Excel 2007 OOXML (.xlsx)格式
     *
     * @param headMap     key: 属性(etc: key10), value: 列头名称(etc: 示例1)
     * @param jsonArray   数据集(etc: [{"key10":"v10","key11":"v11"},{"key20":"v20","key21":"v21"}])
     * @param datePattern 日期格式，传null值则默认 年月日
     * @param colWidth    列宽 默认 至少17个字节
     * @param out         输出流
     */
    public static void exportExcelX(Map<String, String> headMap, JSONArray jsonArray, String datePattern, int colWidth, OutputStream out) {
        exportExcelX(null, headMap, jsonArray, datePattern, colWidth, out);
    }

    /**
     * 导出Excel 2007 OOXML (.xlsx)格式
     *
     * @param title       标题行
     * @param headMap     key: 属性(etc: key10), value: 列头名称(etc: 示例1)
     * @param content   数据集(etc: [{"key10":"v10","key11":"v11"},{"key20":"v20","key21":"v21"}])
     * @param datePattern 日期格式，传null值则默认 年月日
     * @param colWidth    列宽 默认 至少17个字节
     * @param out         输出流
     */
    public static void exportExcelX(String title, Map<String, String> headMap, JSONArray content, String datePattern, int colWidth, OutputStream out) {
        if (null == headMap || headMap.size() == 0) {
                throw new NullPointerException("headMap cannot be null");
            } else if (null == content || content.size() == 0) {
                throw new NullPointerException("content cannot be null");
            } else if (colWidth < 0) {
                throw new IllegalArgumentException("colWidth incorrect");
            } else if (out == null) {
                throw new NullPointerException("colWidth incorrect");
            }

            if (datePattern == null) {
                datePattern = DEFAULT_DATE_PATTERN;
            }
            // 声明一个工作薄
            SXSSFWorkbook workbook = new SXSSFWorkbook(1000);//缓存
            workbook.setCompressTempFiles(true);


            //表头样式
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            Font titleFont = workbook.createFont();
            titleFont.setFontHeightInPoints((short) 20);
            titleFont.setBold(true);
            titleStyle.setFont(titleFont);
            // 列头样式
            CellStyle headerStyle = workbook.createCellStyle();
//        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        headerStyle.setBorderBottom(BorderStyle.NONE);
//        headerStyle.setBorderLeft(BorderStyle.NONE);
//        headerStyle.setBorderRight(BorderStyle.NONE);
//        headerStyle.setBorderTop(BorderStyle.NONE);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            Font headerFont = workbook.createFont();
            headerFont.setFontHeightInPoints((short) 12);
            headerFont.setBold(true);
            HSSFColor c = new HSSFColor(64, -1, java.awt.Color.red);
            headerFont.setColor(c.getIndex());
            headerStyle.setFont(headerFont);
            // 单元格样式
            CellStyle cellStyle = workbook.createCellStyle();
//        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        cellStyle.setBorderBottom(BorderStyle.DOUBLE);
//        cellStyle.setBorderLeft(BorderStyle.DOUBLE);
//        cellStyle.setBorderRight(BorderStyle.DOUBLE);
//        cellStyle.setBorderTop(BorderStyle.DOUBLE);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            Font cellFont = workbook.createFont();
//        cellFont.setBold(true);
            cellStyle.setFont(cellFont);

            ExcelDTO dto = new ExcelDTO();
            dto.setTitle(title);
            dto.setHeadMap(headMap);
            dto.setContent(content);
            dto.setDatePattern(datePattern);
            dto.setColWidth(colWidth);
            sheetParse(workbook, headerStyle, cellStyle, dto);
            // 自动调整宽度
        /*for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }*/
            try {
                workbook.write(out);
                workbook.close();
                workbook.dispose();
            } catch (IOException e) {
                e.printStackTrace();
        }
    }

    /**
     * sheetList 多个sheet表内容
     * 导出Excel 2007 OOXML (.xlsx)格式
     * @param sheetList {"title":"String title","headMap":" Map headMap","content":"JSONArray content","datePattern":"String datePattern","colWidth":"int colWidth"}
     * @param out 输出流
     */
    public static void exportExcelX(List<JSONObject> sheetList, OutputStream out) {
        // 声明一个工作薄
        if (out == null) {
            throw new NullPointerException("file path not null");
        }
        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);//缓存
        workbook.setCompressTempFiles(true);

        // 列头样式
        CellStyle headerStyle = workbook.createCellStyle();
//        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        headerStyle.setBorderBottom(BorderStyle.NONE);
//        headerStyle.setBorderLeft(BorderStyle.NONE);
//        headerStyle.setBorderRight(BorderStyle.NONE);
//        headerStyle.setBorderTop(BorderStyle.NONE);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setBold(true);
        HSSFColor c = new HSSFColor(64, -1, java.awt.Color.red);
        headerFont.setColor(c.getIndex());
        headerStyle.setFont(headerFont);
        // 单元格样式
        CellStyle cellStyle = workbook.createCellStyle();
//        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        cellStyle.setBorderBottom(BorderStyle.DOUBLE);
//        cellStyle.setBorderLeft(BorderStyle.DOUBLE);
//        cellStyle.setBorderRight(BorderStyle.DOUBLE);
//        cellStyle.setBorderTop(BorderStyle.DOUBLE);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font cellFont = workbook.createFont();
//        cellFont.setBold(true);
        cellStyle.setFont(cellFont);

        for (int i1 = 0; i1 < sheetList.size(); i1++) {
            JSONObject sheetMap = sheetList.get(i1);

            sheetParse(workbook, headerStyle, cellStyle, sheetMap);
        }
        // 自动调整宽度
        /*for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }*/
        try {
            workbook.write(out);
            workbook.close();
            workbook.dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成sheet表
     * @param workbook
     * @param headerStyle
     * @param cellStyle
     * @param sheetMap
     */
    private static void sheetParse(SXSSFWorkbook workbook, CellStyle headerStyle, CellStyle cellStyle, JSONObject sheetMap){
        String title = sheetMap.getString("title");
        Map<String, String> headMap = sheetMap.getObject("headMap", Map.class);
        JSONArray content = sheetMap.getJSONArray("content");
        String datePattern = sheetMap.getString("datePattern");
        int colWidth = sheetMap.getIntValue("colWidth");
        String sheetName = sheetMap.getString("sheetName");
        ExcelDTO dto = new ExcelDTO();
        dto.setTitle(title);
        dto.setHeadMap(headMap);
        dto.setContent(content);
        dto.setDatePattern(datePattern);
        dto.setColWidth(colWidth);
        dto.setSheetName(sheetName);
        sheetParse(workbook, headerStyle, cellStyle, dto);
    }

    /**
     * 生成sheet表
     * @param workbook
     * @param headerStyle
     * @param cellStyle
     * @param dto 表格数据ExcelDTO
     */
    private static void sheetParse(SXSSFWorkbook workbook, CellStyle headerStyle, CellStyle cellStyle, ExcelDTO dto){
        String title = dto.getTitle();
        Map<String, String> headMap = dto.getHeadMap();
        JSONArray content = dto.getContent();
        String datePattern = dto.getDatePattern();
        int colWidth = dto.getColWidth();
        String sheetName = dto.getSheetName();
        if (null == headMap || headMap.size() == 0) {
            throw new NullPointerException("headMap cannot be null");
        } else if (null == content || content.size() == 0) {
            throw new NullPointerException("content cannot be null");
        } else if (colWidth < 0) {
            throw new IllegalArgumentException("colWidth incorrect");
        }

        datePattern = StringUtils.isBlank(datePattern) ? DEFAULT_DATE_PATTERN : datePattern;

        // 生成一个(带标题)表格
        SXSSFSheet sheet;
        String stName = sheetName;
        if(StringUtils.isNotBlank(stName)){
            sheet = workbook.createSheet(stName);
        }else{
            sheet = workbook.createSheet();
            stName = sheet.getSheetName();
        }

        //设置列宽
        int minBytes = colWidth < DEFAULT_COLOUMN_WIDTH ? DEFAULT_COLOUMN_WIDTH : colWidth;//至少字节数
        int[] arrColWidth = new int[headMap.size()];
        // 产生表格标题行,以及设置列宽
        String[] properties = new String[headMap.size()];
        String[] headers = new String[headMap.size()];
        int ii = 0;
        for (Iterator<String> iter = headMap.keySet().iterator(); iter
                .hasNext(); ) {
            String fieldName = iter.next();

            properties[ii] = fieldName;
            headers[ii] = headMap.get(fieldName);

            int bytes = fieldName.getBytes().length;
            arrColWidth[ii] = bytes < minBytes ? minBytes : bytes;
            sheet.setColumnWidth(ii, arrColWidth[ii] * 256);
            ii++;
        }
        // 遍历集合数据，产生数据行
        int rowIndex = 0;
        int sheetIdx = 1;
        for (Object obj : content) {
            if (rowIndex == 1048576 || rowIndex == 0) {
                if (rowIndex != 0) {
                    sheet = workbook.createSheet(stName + "-" + sheetIdx);//如果数据超过了，则在第二页显示
                    sheetIdx++;
                }
                int heardRowNum = 0;
                if (StringUtils.isNotBlank(title)) {
                    // 标题样式
                    CellStyle titleStyle = workbook.createCellStyle();
                    titleStyle.setAlignment(HorizontalAlignment.CENTER);
                    Font titleFont = workbook.createFont();
                    titleFont.setFontHeightInPoints((short) 20);
                    titleFont.setBold(true);
                    titleStyle.setFont(titleFont);
                    // 标题
                    SXSSFRow titleRow = sheet.createRow(0);//表头 rowIndex=0
                    titleRow.createCell(0).setCellValue(title);
                    titleRow.getCell(0).setCellStyle(titleStyle);
                    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headMap.size() - 1));
                    heardRowNum = 1;
                }

                SXSSFRow headerRow = sheet.createRow(heardRowNum); //列头 rowIndex =1
                for (int i = 0; i < headers.length; i++) {
                    headerRow.createCell(i).setCellValue(headers[i]);
                    headerRow.getCell(i).setCellStyle(headerStyle);

                }
                // 数据内容从 rowIndex=2开始
                rowIndex = heardRowNum + 1;
            }
            JSONObject jo = (JSONObject) JSONObject.toJSON(obj);
            SXSSFRow dataRow = sheet.createRow(rowIndex);
            for (int i = 0; i < properties.length; i++) {
                SXSSFCell newCell = dataRow.createCell(i);

                Object o = jo.get(properties[i]);
                Object cellValue;
                if (o == null) {
                    cellValue = "";
                    newCell.setCellValue((String) cellValue);
                } else if (o instanceof Date) {
                    // 时间类型
                    cellValue = new SimpleDateFormat(datePattern).format(o);
                    newCell.setCellValue((String)cellValue);
                } else if (o instanceof Float || o instanceof Double) {
                    // 浮点类型
                    cellValue = new BigDecimal(o.toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal d = ((BigDecimal) cellValue);
                    newCell.setCellValue(d.doubleValue());
                } else if (o instanceof Integer) {
                    // 整型
                    cellValue = new BigDecimal(o.toString());
                    BigDecimal d = ((BigDecimal) cellValue);
                    newCell.setCellValue(d.intValue());
                } else {
                    cellValue = o.toString();
                    String cvStr = (String)cellValue;
                    // 判断是否是数值或浮点型字符串
                    if(StringUtilsExt.isNumber(cvStr)){
                        if(StringUtils.isNumeric(cvStr)){
                            // 数值
                            cellValue = new BigDecimal(cvStr);
                            BigDecimal d = ((BigDecimal) cellValue);
                            int intv = d.intValue();
                            // 当数值属于大数时，如：12345679835485613，d.intValue()会变成-xxx字样，已经变味，此时还是采用字符串
                            if(String.valueOf(intv).equals(cvStr)){
                                newCell.setCellValue(intv);
                            }else{
                                // 数值太长，采用字符串
                                newCell.setCellValue(cvStr);
                            }

                        }else{
                            // 浮点
                            String[] cvStrs = cvStr.split("\\.");
                            int newScale = 2;
                            if(cvStrs != null && cvStrs.length == 2 && StringUtils.isNotBlank(cvStrs[1])){
                                int len = cvStrs[1].length();
                                // 保留小数点位数，防止有特殊要求保留3位，在此允许。超过等于4位，默认保留2位
                                newScale = len > 4 ? newScale : len;
                            }
                            cellValue = new BigDecimal(cvStr).setScale(newScale, BigDecimal.ROUND_HALF_UP);
                            BigDecimal d = ((BigDecimal) cellValue);
                            newCell.setCellValue(d.doubleValue());
                        }
                    }else{
                        // 字符串
                        newCell.setCellValue(cvStr);
                    }
                }
                newCell.setCellStyle(cellStyle);
            }
            rowIndex++;
        }

    }
    /**
     * 生成sheet表
     * @param workbook
     * @param headerStyle
     * @param cellStyle
     * @param title
     * @param headMap
     * @param content
     * @param datePattern
     * @param colWidth
     */
    private static void sheetParse(SXSSFWorkbook workbook, CellStyle headerStyle, CellStyle cellStyle, String title, Map<String, String> headMap, JSONArray content, String datePattern, int colWidth, String sheetName){
        if (null == headMap || headMap.size() == 0) {
            throw new NullPointerException("headMap cannot be null");
        } else if (null == content || content.size() == 0) {
            throw new NullPointerException("content cannot be null");
        } else if (colWidth < 0) {
            throw new IllegalArgumentException("colWidth incorrect");
        }
        datePattern = StringUtils.isBlank(datePattern) ? DEFAULT_DATE_PATTERN : datePattern;
        // 生成一个(带标题)表格
        SXSSFSheet sheet;
        String stName = sheetName;
        if(StringUtils.isNotBlank(stName)){
            sheet = workbook.createSheet(stName);
        }else{
            sheet = workbook.createSheet();
        }

        //设置列宽
        int minBytes = colWidth < DEFAULT_COLOUMN_WIDTH ? DEFAULT_COLOUMN_WIDTH : colWidth;//至少字节数
        int[] arrColWidth = new int[headMap.size()];
        // 产生表格标题行,以及设置列宽
        String[] properties = new String[headMap.size()];
        String[] headers = new String[headMap.size()];
        int ii = 0;
        for (Iterator<String> iter = headMap.keySet().iterator(); iter
                .hasNext(); ) {
            String fieldName = iter.next();

            properties[ii] = fieldName;
            headers[ii] = headMap.get(fieldName);

            int bytes = fieldName.getBytes().length;
            arrColWidth[ii] = bytes < minBytes ? minBytes : bytes;
            sheet.setColumnWidth(ii, arrColWidth[ii] * 256);
            ii++;
        }
        // 遍历集合数据，产生数据行
        int rowIndex = 0;
        for (Object obj : content) {
            if (rowIndex == 1048576 || rowIndex == 0) {
                if (rowIndex != 0) {
                    sheet = workbook.createSheet();//如果数据超过了，则在第二页显示
                }

                int heardRowNum = 0;
                if (StringUtils.isNotBlank(title)) {
                    // 标题样式
                    CellStyle titleStyle = workbook.createCellStyle();
                    titleStyle.setAlignment(HorizontalAlignment.CENTER);
                    Font titleFont = workbook.createFont();
                    titleFont.setFontHeightInPoints((short) 20);
                    titleFont.setBold(true);
                    titleStyle.setFont(titleFont);
                    // 标题
                    SXSSFRow titleRow = sheet.createRow(0);//表头 rowIndex=0
                    titleRow.createCell(0).setCellValue(title);
                    titleRow.getCell(0).setCellStyle(titleStyle);
                    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headMap.size() - 1));
                    heardRowNum = 1;
                }

                SXSSFRow headerRow = sheet.createRow(heardRowNum); //列头 rowIndex =1
                for (int i = 0; i < headers.length; i++) {
                    headerRow.createCell(i).setCellValue(headers[i]);
                    headerRow.getCell(i).setCellStyle(headerStyle);

                }
                // 数据内容从 rowIndex=2开始
                rowIndex = heardRowNum + 1;
            }
            JSONObject jo = (JSONObject) JSONObject.toJSON(obj);
            SXSSFRow dataRow = sheet.createRow(rowIndex);
            for (int i = 0; i < properties.length; i++) {
                SXSSFCell newCell = dataRow.createCell(i);

                Object o = jo.get(properties[i]);
                Object cellValue;
                if (o == null) {
                    cellValue = "";
                    newCell.setCellValue((String) cellValue);
                } else if (o instanceof Date) {
                    // 时间类型
                    cellValue = new SimpleDateFormat(datePattern).format(o);
                    newCell.setCellValue((String)cellValue);
                } else if (o instanceof Float || o instanceof Double) {
                    // 浮点类型
                    cellValue = new BigDecimal(o.toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal d = ((BigDecimal) cellValue);
                    newCell.setCellValue(d.doubleValue());
                } else if (o instanceof Integer) {
                    // 整型
                    cellValue = new BigDecimal(o.toString());
                    BigDecimal d = ((BigDecimal) cellValue);
                    newCell.setCellValue(d.intValue());
                } else {
                    cellValue = o.toString();
                    String cvStr = (String)cellValue;
                    // 判断是否是数值或浮点型字符串
                    if(StringUtilsExt.isNumber(cvStr)){
                        if(StringUtils.isNumeric(cvStr)){
                            // 数值
                            cellValue = new BigDecimal(cvStr);
                            BigDecimal d = ((BigDecimal) cellValue);
                            int intv = d.intValue();
                            // 当数值属于大数时，如：12345679835485613，d.intValue()会变成-xxx字样，已经变味，此时还是采用字符串
                            if(String.valueOf(intv).equals(cvStr)){
                                newCell.setCellValue(intv);
                            }else{
                                // 数值太长，采用字符串
                                newCell.setCellValue(cvStr);
                            }

                        }else{
                            // 浮点
                            String[] cvStrs = cvStr.split("\\.");
                            int newScale = 2;
                            if(cvStrs != null && cvStrs.length == 2 && StringUtils.isNotBlank(cvStrs[1])){
                                int len = cvStrs[1].length();
                                // 保留小数点位数，防止有特殊要求保留3位，在此允许。超过等于4位，默认保留2位
                                newScale = len >= 4 ? newScale : len;
                            }
                            cellValue = new BigDecimal(cvStr).setScale(newScale, BigDecimal.ROUND_HALF_UP);
                            BigDecimal d = ((BigDecimal) cellValue);
                            newCell.setCellValue(d.doubleValue());
                        }
                    }else{
                        // 字符串
                        newCell.setCellValue(cvStr);
                    }
                }
                newCell.setCellStyle(cellStyle);
            }
            rowIndex++;
        }

    }

    /**
     * 导出Excel 2007 OOXML (.xlsx)格式
     * @param sheetList {ExcelDTO List}
     * @param out 输出流
     */
    public static void exportExcelXByDto(List<ExcelDTO> sheetList, OutputStream out) {
        List<JSONObject> list = new ArrayList<>();
        sheetList.forEach(dto -> list.add((JSONObject)JSONObject.toJSON(dto)));
        ExcelUtil.exportExcelX(list, out);
    }

    /**
     * 导出Excel 2007 OOXML (.xlsx)格式
     * @param sheetDTO ExcelDTO
     * @param out 输出流
     */
    public static void exportExcelX(ExcelDTO sheetDTO, OutputStream out) {
        List<JSONObject> list = new ArrayList<>();
        list.add((JSONObject)JSONObject.toJSON(sheetDTO));
        ExcelUtil.exportExcelX(list, out);
    }

    /**
     * 导出Excel 2007 OOXML (.xlsx)格式
     * @param headMap headMap 表头
     * @param content content 数据
     * @param out 输出流
     */
    public static void exportExcelX(Map<String, String> headMap, JSONArray content, OutputStream out) {
        ExcelDTO dto = new ExcelDTO();
        dto.setHeadMap(headMap);
        dto.setContent(content);
        exportExcelX(dto, out);
    }

    /**
     * <p> 为xls格式，不推荐使用。
     * <p> 导出Excel 97(.xls)格式 ，少量数据
     *
     * @param title       标题行
     * @param headMap     属性-列名
     * @param jsonArray   数据集
     * @param datePattern 日期格式，null则用默认日期格式
     * @param colWidth    列宽 默认 至少17个字节
     * @param out         输出流
     */
    @Deprecated
    public static void exportExcel(String title, Map<String, String> headMap, JSONArray jsonArray, String datePattern, int colWidth, OutputStream out) {
        if (datePattern == null) {
            datePattern = DEFAULT_DATE_PATTERN;
        }
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        workbook.createInformationProperties();
        workbook.getDocumentSummaryInformation().setCompany("*****公司");
        SummaryInformation si = workbook.getSummaryInformation();
        si.setAuthor("skyjilygao");  //填加xls文件作者信息
        si.setApplicationName("导出程序"); //填加xls文件创建程序信息
        si.setLastAuthor("最后保存者信息"); //填加xls文件最后保存者信息
        si.setComments("JACK is a programmer!"); //填加xls文件作者信息
        si.setTitle("POI导出Excel"); //填加xls文件标题信息
        si.setSubject("POI导出Excel");//填加文件主题信息
        si.setCreateDateTime(new Date());
        //表头样式
        HSSFCellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        HSSFFont titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 20);
        titleFont.setBold(true);
//        titleFont.setBoldweight((short) 700);
        titleStyle.setFont(titleFont);
        // 列头样式
        HSSFCellStyle headerStyle = workbook.createCellStyle();

        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.DOUBLE);
        headerStyle.setBorderLeft(BorderStyle.NONE);
        headerStyle.setBorderRight(BorderStyle.NONE);
        headerStyle.setBorderTop(BorderStyle.DOUBLE);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        HSSFFont headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setBold(true);
//        headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        headerStyle.setFont(headerFont);
        // 单元格样式
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      /*  cellStyle.setBorderBottom(BorderStyle.DOUBLE);
        cellStyle.setBorderLeft(BorderStyle.DOUBLE);
        cellStyle.setBorderRight(BorderStyle.DOUBLE);
        cellStyle.setBorderTop(BorderStyle.DOUBLE);*/

        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());

        HSSFFont cellFont = workbook.createFont();
        cellFont.setBold(true);
        cellStyle.setFont(cellFont);
        // 生成一个(带标题)表格
        HSSFSheet sheet = workbook.createSheet();
        // 声明一个画图的顶级管理器
        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
        // 定义注释的大小和位置,详见文档
        HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0,
                0, 0, 0, (short) 4, 2, (short) 6, 5));
        // 设置注释内容
        comment.setString(new HSSFRichTextString("可以在POI中添加注释！"));
        // 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
        comment.setAuthor("skyjilygao");
        //设置列宽
        int minBytes = colWidth < DEFAULT_COLOUMN_WIDTH ? DEFAULT_COLOUMN_WIDTH : colWidth;//至少字节数
        int[] arrColWidth = new int[headMap.size()];
        // 产生表格标题行,以及设置列宽
        String[] properties = new String[headMap.size()];
        String[] headers = new String[headMap.size()];
        int ii = 0;
        for (Iterator<String> iter = headMap.keySet().iterator(); iter
                .hasNext(); ) {
            String fieldName = iter.next();

            properties[ii] = fieldName;
            headers[ii] = fieldName;

            int bytes = fieldName.getBytes().length;
            arrColWidth[ii] = bytes < minBytes ? minBytes : bytes;
            sheet.setColumnWidth(ii, arrColWidth[ii] * 256);
            ii++;
        }
        // 遍历集合数据，产生数据行
        int rowIndex = 0;
        for (Object obj : jsonArray) {
            if (rowIndex == 65535 || rowIndex == 0) {
                if (rowIndex != 0) {
                    sheet = workbook.createSheet();//如果数据超过了，则在第二页显示
                }

                HSSFRow titleRow = sheet.createRow(0);//表头 rowIndex=0
                titleRow.createCell(0).setCellValue(title);
                titleRow.getCell(0).setCellStyle(titleStyle);
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headMap.size() - 1));

                HSSFRow headerRow = sheet.createRow(1); //列头 rowIndex =1
                for (int i = 0; i < headers.length; i++) {
                    headerRow.createCell(i).setCellValue(headers[i]);
                    headerRow.getCell(i).setCellStyle(headerStyle);

                }
                rowIndex = 2;//数据内容从 rowIndex=2开始
            }
            JSONObject jo = (JSONObject) JSONObject.toJSON(obj);
            HSSFRow dataRow = sheet.createRow(rowIndex);
            for (int i = 0; i < properties.length; i++) {
                HSSFCell newCell = dataRow.createCell(i);

                Object o = jo.get(properties[i]);
                String cellValue = "";
                if (o == null) {
                    cellValue = "";
                } else if (o instanceof Date) {
                    cellValue = new SimpleDateFormat(datePattern).format(o);
                } else {
                    cellValue = o.toString();
                }

                newCell.setCellValue(cellValue);
                newCell.setCellStyle(cellStyle);
            }
            rowIndex++;
        }
        // 自动调整宽度
        /*for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }*/
        try {
            workbook.write(out);
//            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Web 导出excel
     * @param title
     * @param headMap
     * @param ja
     * @param response
     * @throws IOException
     */
    public static void downloadExcelFile(String title, Map<String, String> headMap, JSONArray ja, HttpServletResponse response) throws IOException {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            exportExcelX(title, headMap, ja, null, 0, os);
            byte[] content = os.toByteArray();
            InputStream is = new ByteArrayInputStream(content);
            // 设置response参数，可以打开下载页面
            response.reset();

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String((title + ".xlsx").getBytes(), "iso-8859-1"));
            response.setContentLength(content.length);
            ServletOutputStream outputStream = response.getOutputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            BufferedOutputStream bos = new BufferedOutputStream(outputStream);
            byte[] buff = new byte[8192];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);

            }
            bis.close();
            bos.close();
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     *
     * 表格数据转置（行列转换）
     * @param headMap LinkedHashMap type, controler the rows sort
     * @param content 表格内容
     * @return json object,{"headMap":{},"content":[json array]}
     */
    public static JSONObject transposeRowAndColumn(Map<String, String> headMap, JSONArray content){
        if(headMap == null || headMap.size() == 0 || content == null || content.size() == 0){
            return null;
        }

        String pk=null,pv=null;
        for (Map.Entry<String, String> entry : headMap.entrySet()) {
            pk = entry.getKey();
            pv = entry.getValue();
            break;
        }
        String primaryKey = pk;
        String primaryVal = pv;
       /* headMap.forEach((key,val) ->{
            // 决定列顺序
            LinkedHashMap<String, String> lm = new LinkedHashMap();
            if(!primaryKey.equalsIgnoreCase(key)){
                lm.put(primaryVal, val);
                Iterator it = content.iterator();
                while (it.hasNext()){
                    JSONObject itJson = (JSONObject)it.next();
                    String v = itJson.getString(key);
                    lm.put(itJson.getString(primaryKey), v);
                }
                allData0.add(lm);
            }
        });*/
        JSONArray allData0 = transposeProcessingData(pk, pv, headMap, content);
        Map<String,String> headMapVertical = getHeadMapVertical(allData0);
        JSONObject allDataJson = new JSONObject();
        allDataJson.put("headMap", headMapVertical);
        allDataJson.put("content", allData0);
        return allDataJson;
    }

    /**
     * 表格数据转置时，配合 transposeRowAndColumn 方法使用。获取转置后的数据
     * @param primaryKey 主key，即第一行，第一列的key
     * @param primaryVal 主key对于的val，即第一行，第一列的val
     * @param headMap 原表格数据的headMap
     * @param content 原表格数据
     * @return
     */
    private static JSONArray transposeProcessingData(String primaryKey, String primaryVal, Map<String, String> headMap, JSONArray content){
        JSONArray allData0 = new JSONArray();
        headMap.forEach((key,val) ->{
            // 决定列顺序
            LinkedHashMap<String, String> lm = new LinkedHashMap();
            if(!primaryKey.equalsIgnoreCase(key)){
                lm.put(primaryVal, val);
                Iterator it = content.iterator();
                while (it.hasNext()){
                    JSONObject itJson = (JSONObject)it.next();
                    String v = itJson.getString(key);
                    lm.put(itJson.getString(primaryKey), v);
                }
                allData0.add(lm);
            }
        });
        return allData0;
    }
    /**
     * 表格数据转置时，配合 transposeRowAndColumn 方法使用获取表头
     * @param content
     * @return LinkedHashMap<String, String>
     */
    public static Map<String, String> getHeadMapVertical(JSONArray content) {
        Map<String, String> headMap = new LinkedHashMap<>();
        JSONObject json = content.getJSONObject(0);
        json.keySet().forEach(key -> headMap.put(key, key));
        return headMap;
    }

    public static void main(String[] args) {
        String fileName = "report-retargeting-";
        fileName += LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-" + System.currentTimeMillis();
        String suffix = ".xlsx";
        fileName += suffix;
        String fullpath = "/file1/tmp" + fileName;
        File parent = new File(fullpath);
        parent.getParentFile().mkdirs();
        FileOutputStream outXlsx = null;
        try {
            List<JSONObject> list = new ArrayList<>();
            for(int i=0;i<5;i++){
                JSONObject json = new JSONObject();
                json.put("title",null);
                json.put("sheetName","stName-"+i);
                JSONArray array = new JSONArray();
                JSONObject v1 = new JSONObject();
                v1.put("k"+i+"-1", "v"+i+"-1");
                v1.put("k"+i+"-2", "v"+i+"-2");
                array.add(v1);
                json.put("headMap",getHeadMap(array));
                json.put("content",array);
                json.put("datePattern","yyyy-MM-dd");
                json.put("colWidth","0");
                list.add(json);
            }

            outXlsx = new FileOutputStream(fullpath);
            ExcelUtil.exportExcelX(list, outXlsx);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> getHeadMap(JSONArray ja) {
        Map<String, String> headMap = new LinkedHashMap<>();
        JSONObject json = ja.getJSONObject(0);
        json.keySet().forEach(key -> {
            headMap.put(key, key);
        });
        return headMap;
    }


    public static class ExcelDTO{
        private String title;
        private String sheetName;
        private Map<String, String> headMap;
        private JSONArray content;
        private String datePattern;
        private int colWidth;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSheetName() {
            return sheetName;
        }

        public void setSheetName(String sheetName) {
            this.sheetName = sheetName;
        }

        public Map<String, String> getHeadMap() {
            return headMap;
        }

        public void setHeadMap(Map<String, String> headMap) {
            this.headMap = headMap;
        }

        public JSONArray getContent() {
            return content;
        }

        public void setContent(JSONArray content) {
            this.content = content;
        }

        public String getDatePattern() {
            return datePattern;
        }

        public void setDatePattern(String datePattern) {
            this.datePattern = datePattern;
        }

        public int getColWidth() {
            return colWidth;
        }

        public void setColWidth(int colWidth) {
            this.colWidth = colWidth;
        }
    }
    /**
     * 生成透视Excel
     */
    public static void pivotTable(String title, Map<String, String> headMap, JSONArray content, String datePattern, int colWidth, OutputStream out,JSONArray filterArray,JSONArray sumArray,JSONArray averageArray){
        if (null == headMap || headMap.size() == 0) {
            throw new NullPointerException("headMap cannot be null");
        } else if (null == content || content.size() == 0) {
            throw new NullPointerException("content cannot be null");
        } else if (colWidth < 0) {
            throw new IllegalArgumentException("colWidth incorrect");
        } else if (out == null) {
            throw new NullPointerException("colWidth incorrect");
        }

        if (datePattern == null) {
            datePattern = DEFAULT_DATE_PATTERN;
        }
        // 声明一个工作薄
        XSSFWorkbook workbook = new XSSFWorkbook();
        //表头样式
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        Font titleFont = workbook.createFont();
        titleFont.setFontHeightInPoints((short) 20);
        titleFont.setBold(true);
        titleStyle.setFont(titleFont);
        // 列头样式
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        Font headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setBold(true);
        HSSFColor c = new HSSFColor(64, -1, java.awt.Color.red);
        headerFont.setColor(c.getIndex());
        headerStyle.setFont(headerFont);
        // 单元格样式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        Font cellFont = workbook.createFont();
        cellStyle.setFont(cellFont);
        // 生成一个(带标题)表格
        XSSFSheet sheet = workbook.createSheet();
        //设置列宽
        int minBytes = colWidth < DEFAULT_COLOUMN_WIDTH ? DEFAULT_COLOUMN_WIDTH : colWidth;//至少字节数
        int[] arrColWidth = new int[headMap.size()];
        // 产生表格标题行,以及设置列宽
        String[] properties = new String[headMap.size()];
        String[] headers = new String[headMap.size()];
        int ii = 0;
        for (Iterator<String> iter = headMap.keySet().iterator(); iter
                .hasNext(); ) {
            String fieldName = iter.next();

            properties[ii] = fieldName;
            headers[ii] = headMap.get(fieldName);

            int bytes = fieldName.getBytes().length;
            arrColWidth[ii] = bytes < minBytes ? minBytes : bytes;
            sheet.setColumnWidth(ii, arrColWidth[ii] * 256);
            ii++;
        }
        // 遍历集合数据，产生数据行
        int rowIndex = 0;
        for (Object obj : content) {
            if (rowIndex == 1048576 || rowIndex == 0) {
                if (rowIndex != 0) {
                    sheet = workbook.createSheet();//如果数据超过了，则在第二页显示
                }

                int heardRowNum = 0;
                if (StringUtils.isNotBlank(title)) {
                    // 标题
                    XSSFRow titleRow = sheet.createRow(0);//表头 rowIndex=0
                    titleRow.createCell(0).setCellValue(title);
                    titleRow.getCell(0).setCellStyle(titleStyle);
                    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headMap.size() - 1));
                    heardRowNum = 1;
                }

                XSSFRow headerRow = sheet.createRow(heardRowNum); //列头 rowIndex =1
                for (int i = 0; i < headers.length; i++) {
                    headerRow.createCell(i).setCellValue(headers[i]);
                    headerRow.getCell(i).setCellStyle(headerStyle);

                }
                // 数据内容从 rowIndex=2开始
                rowIndex = heardRowNum + 1;
            }
            JSONObject jo = (JSONObject) JSONObject.toJSON(obj);
            XSSFRow dataRow = sheet.createRow(rowIndex);
            for (int i = 0; i < properties.length; i++) {
                XSSFCell newCell = dataRow.createCell(i);

                Object o = jo.get(properties[i]);
                Object cellValue;
                if (o == null) {
                    cellValue = "";
                    newCell.setCellValue((String) cellValue);
                } else if (o instanceof Date) {
                    // 时间类型
                    cellValue = new SimpleDateFormat(datePattern).format(o);
                    newCell.setCellValue((String)cellValue);
                } else if (o instanceof Float || o instanceof Double) {
                    // 浮点类型
                    cellValue = new BigDecimal(o.toString()).setScale(2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal d = ((BigDecimal) cellValue);
                    newCell.setCellValue(d.doubleValue());
                } else if (o instanceof Integer) {
                    // 整型
                    cellValue = new BigDecimal(o.toString());
                    BigDecimal d = ((BigDecimal) cellValue);
                    newCell.setCellValue(d.intValue());
                } else {
                    cellValue = o.toString();
                    String cvStr = (String)cellValue;
                    // 判断是否是数值或浮点型字符串
                    if(StringUtilsExt.isNumber(cvStr)){
                        if(StringUtils.isNumeric(cvStr)){
                            // 数值
                            cellValue = new BigDecimal(cvStr);
                            BigDecimal d = ((BigDecimal) cellValue);
                            int intv = d.intValue();
                            // 当数值属于大数时，如：12345679835485613，d.intValue()会变成-xxx字样，已经变味，此时还是采用字符串
                            if(String.valueOf(intv).equals(cvStr)){
                                newCell.setCellValue(intv);
                            }else{
                                // 数值太长，采用字符串
                                newCell.setCellValue(cvStr);
                            }

                        }else{
                            // 浮点
                            String[] cvStrs = cvStr.split("\\.");
                            int newScale = 2;
                            if(cvStrs != null && cvStrs.length == 2 && StringUtils.isNotBlank(cvStrs[1])){
                                int len = cvStrs[1].length();
                                // 保留小数点位数，防止有特殊要求保留3位，在此允许。超过等于4位，默认保留2位
                                newScale = len >= 4 ? newScale : len;
                            }
                            cellValue = new BigDecimal(cvStr).setScale(newScale, BigDecimal.ROUND_HALF_UP);
                            BigDecimal d = ((BigDecimal) cellValue);
                            newCell.setCellValue(d.doubleValue());
                        }
                    }else{
                        // 字符串
                        newCell.setCellValue(cvStr);
                    }
                }
                newCell.setCellStyle(cellStyle);
            }
            rowIndex++;
        }
        /**
         *  生成透视
         */
        //获取指定行，索引从0开始
        XSSFRow hssfRow = sheet.getRow(1);
        //获取指定列，索引从0开始
        XSSFCell xssfCell = hssfRow.getCell((short)1);
        XSSFSheet pivotTableSheet = workbook.createSheet();
        int rowNum = content.size();
        System.out.println(sheet.getRow(rowNum));
        String reference = sheet.getSheetName()+"!A1:"+sheet.getRow(rowNum).getCell((short)headMap.size()-1).getAddress();
        System.out.println("!__________reference======="+reference);
        AreaReference source = new AreaReference(reference, SpreadsheetVersion.EXCEL2007);
        CellReference position = new CellReference("A2");
        // Create a pivot table on this sheet, with H5 as the top-left cell..
        // The pivot table's data source is on the same sheet in A1:D4
        XSSFPivotTable pivotTable = pivotTableSheet.createPivotTable(source, position);

        //Configure the pivot table
        //Use first column as row label
//        pivotTable.addRowLabel(0);
        //Sum up the second column
        if(sumArray != null && sumArray.size() != 0){
            for (int i = 0; i < sumArray.size(); i++) {
                String headName = sumArray.getString(i);
                Integer sumRow = getRowNum(headName,headMap);
                if(sumRow!=null){

                    System.out.println("!___________ filterRow ===="+sumRow);
                    pivotTable.addColumnLabel(DataConsolidateFunction.SUM, sumRow);
                }
            }
        }
        if(averageArray != null && averageArray.size() != 0){
            for (int i = 0; i < averageArray.size(); i++) {
                String headName = averageArray.getString(i);
                Integer averageRow = getRowNum(headName,headMap);
                if(averageRow != null){

                    System.out.println("!___________ filterRow ===="+averageRow);
                    pivotTable.addColumnLabel(DataConsolidateFunction.AVERAGE, averageRow);
                }
            }
        }
        if(filterArray != null && filterArray.size() != 0){
            for (int i = 0; i < filterArray.size(); i++) {
                String headName = filterArray.getString(i);
                Integer filterRow = getRowNum(headName,headMap);
                if(filterRow != null){
                    System.out.println("!___________ filterRow ===="+headMap);
                    System.out.println("!___________ filterRow ===="+filterRow);
                    pivotTable.addReportFilter(filterRow);
                }
            }
        }

        //Add filter on forth column

        try {
            workbook.write(out);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取字段对应的行号
     */
    private static Integer getRowNum(String headName, Map<String,String> headMap) {
        Iterator iterator = headMap.keySet().iterator();
        int i = 0;
        while (iterator.hasNext()){
            String key = (String)iterator.next();
            if(key.equalsIgnoreCase(headName)){
                return i;
            }
            i++;
        }
        return null;
    }
}
