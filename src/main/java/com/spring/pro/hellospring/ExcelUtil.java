package com.spring.pro.hellospring;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.BLACK;
import org.apache.poi.hssf.util.HSSFColor.LIGHT_YELLOW;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @Title ExcelUtil
 * @Description Excel 2007+ 导入导出工具
 * @Author xiang.zhao
 * @date: 2018/8/13 23:29
 */
public class ExcelUtil {
    private static Logger LOG = LoggerFactory.getLogger(ExcelUtil.class);

    public static Workbook getWorkBook(InputStream in) throws IOException, InvalidFormatException {
        if (!in.markSupported()) {
            in = new PushbackInputStream(in, 8);
        }
        if (POIFSFileSystem.hasPOIFSHeader(in)) {
            return new HSSFWorkbook(in);
        }
        if (POIXMLDocument.hasOOXMLHeader(in)) {
            return new XSSFWorkbook(OPCPackage.open(in));
        }
        throw new IllegalArgumentException("你的excel版本目前poi解析不了");
    }

    /**
     * 导出一个Excel
     *
     * @param mapping  data中JSONObject key和表格header的对应关系   eg: orderCode,订单号
     * @param datas    需要导出的数据
     * @param fileName 文件名
     */
    public void exportExcel(LinkedHashMap<String, String> mapping, JSONArray datas,
                            String fileName) throws IOException {
        // 创建工作簿对象
        SXSSFWorkbook workbook = new SXSSFWorkbook(60000);
        // 创建工作表
        Sheet sheet = workbook.createSheet("sheet1");

        // 产生大标题行
//        Row rowm = sheet.createRow(0);
//        Cell cellTiltle = rowm.createCell(0);

        //获取大标题样式对象
//        CellStyle columnTopStyle = this.getColumnTopStyle(workbook);

        //获取标题样式对象
        CellStyle titleStyle = this.getTitleStyle(workbook);

        //单元格样式对象
        CellStyle style = this.getStyle(workbook);

        //自动换行的设置
        style.setWrapText(true);

        //列游标
        int colNum = 0;
        //行游标
        int rowNum = 0;

        //大标题
//        sheet.addMergedRegion(new CellRangeAddress(0, rowNum++, 0, (mapping.size() - 1)));
//        cellTiltle.setCellStyle(columnTopStyle);
//        cellTiltle.setCellValue(fileName);


        //行
        Row row;
        Cell cell;

        //读取表头Mapping数据
        Set<Entry<String, String>> entries = mapping.entrySet();

        //写入标题行
        row = sheet.createRow(rowNum++);

        for (Entry<String, String> entry : entries) {
            cell = row.createCell(colNum, SXSSFCell.CELL_TYPE_STRING);
            //设置标题单元格的值
            cell.setCellValue(entry.getValue());
            //设置标题单元格样式
            cell.setCellStyle(titleStyle);
            //设置标题单元格宽度
            int basic = cell.getStringCellValue().matches("[\\u4e00-\\u9fa5]") ? 512 : 256;
            int columnWidth = sheet.getColumnWidth(colNum) / basic;
            int width = (cell.getStringCellValue().getBytes().length + 4);
            if (columnWidth < width) {
                if (( width * basic) < 255 * 256)
                {
                    sheet.setColumnWidth(colNum, ( width * basic) > 65200 ? 65200 : ( width * basic));
                }
                else {
                    sheet.setColumnWidth(colNum, 65200);
                }
//                sheet.setColumnWidth(colNum, width * basic);
            }
            colNum++;
        }
        colNum = 0;
        //有序写入data中所有的数据
        if (datas!=null){
            for (Object data : datas) {
                try {
                    JSONObject object = (JSONObject) data;
                    row = sheet.createRow(rowNum++);
                    for (Entry<String, String> entry : entries) {
                        try {
                            cell = row.createCell(colNum, SXSSFCell.CELL_TYPE_STRING);
                            //设置单元格的值
                            cell.setCellValue(object.getString(entry.getKey()));
                            //设置单元格样式
                            cell.setCellStyle(style);
                            //设置单元格宽度
                            int basic = "[\\u4e00-\\u9fa5]".matches(StringUtils.isEmpty(cell.getStringCellValue()) ? cell.getStringCellValue() : cell.getStringCellValue().replaceAll("\\)", "")) ? 512 : 256;
                            int columnWidth = sheet.getColumnWidth(colNum) / basic;
                            int width = (cell.getStringCellValue().getBytes().length + 4);
                            if (columnWidth < width) {
                                if (( width * basic) < 255 * 256)
                                {
                                    sheet.setColumnWidth(colNum, ( width * basic) > 65200 ? 65200 : ( width * basic));
                                }
                                else {
                                    sheet.setColumnWidth(colNum, 65200);
                                }
//                        sheet.setColumnWidth(colNum, width * basic);
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                            cell = row.createCell(colNum, SXSSFCell.CELL_TYPE_STRING);
                            //设置单元格样式
                            cell.setCellStyle(style);

                        }
                        colNum++;

                    }
                    colNum = 0;
                }catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }

            }
        }

        fileName = fileName.replaceAll(".xlsx", "").replaceAll(".xls", "")
                .concat(".xlsx");
        FileOutputStream fos = null;
        fos = new FileOutputStream(new File(fileName));
        workbook.write(fos);
        fos.close();
    }

    /**
     * @param mapping 表格列数字与 T 中对应的fieldname  eg: 1,orderCode
     * @param data    从Request中读到的FileItem的数据流或Excel文件数据流
     * @param <T>     要返回的Data类型
     */
    public <T> List<T> importExcel(LinkedHashMap<Integer, String> mapping, InputStream data,
                                   Class<T> type)
            throws IOException, InvalidFormatException {
        //通过流获取Workbook
        Workbook rwb = getWorkBook(data);
        Sheet sheet = rwb.getSheetAt(0);

        ArrayList<T> list = new ArrayList<>();
        Row row;
        Cell cell;
        //读取行
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            //读取该行所有列为一条数据
            JSONObject obj = new JSONObject();
            row = sheet.getRow(i);
            for (int j = 0; j < row.getLastCellNum(); j++) {
                cell = row.getCell(j);
                obj.put(mapping.get(j), cell.getStringCellValue());
            }
            list.add(JSON.toJavaObject(obj, type));
        }
        return list;
    }

    /**
     * @param mapping 表格列数字与 T 中对应的fieldname  eg: 1,orderCode
     * @param data    从Request中读到的FileItem的数据流或Excel文件数据流
     */
    public JSONArray getExcelDatas(LinkedHashMap<Integer, String> mapping, InputStream data)
            throws IOException, InvalidFormatException {
        //通过流获取Workbook
        Workbook rwb = getWorkBook(data);
        JSONArray list = new JSONArray();
        if(rwb instanceof XSSFWorkbook) {
            XSSFSheet sheet = (XSSFSheet)rwb.getSheetAt(0);
            XSSFRow row;
            XSSFCell cell;
            //读取行
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                //读取该行所有列为一条数据
                JSONObject obj = new JSONObject();
                row = sheet.getRow(i);
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    cell = row.getCell(j);
                    if(cell != null) {
                        int cellType = cell.getCellType();
                        switch (cellType) {
                            case XSSFCell.CELL_TYPE_NUMERIC:
                                obj.put(mapping.get(j), cell.getNumericCellValue());
                                break;
                            case XSSFCell.CELL_TYPE_STRING:
                                obj.put(mapping.get(j), cell.getStringCellValue());
                                break;
                            case XSSFCell.CELL_TYPE_BLANK:
                                obj.put(mapping.get(j), "");
                                break;
                            default:
                                obj.put(mapping.get(j), cell.getRichStringCellValue().getString());
                                break;
                        }
                    }
                }
                list.add(obj);
            }
        }else {
            Sheet sheet = rwb.getSheetAt(0);
            Row row;
            Cell cell;
            //读取行
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                //读取该行所有列为一条数据
                JSONObject obj = new JSONObject();
                row = sheet.getRow(i);
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    cell = row.getCell(j);
                    obj.put(mapping.get(j), cell.getStringCellValue());
                }
                list.add(obj);
            }
        }
        return list;
    }


    /**
     * 列头单元格样式
     *
     * @param workbook 工作表
     */
    private CellStyle getColumnTopStyle(SXSSFWorkbook workbook) {

        // 设置字体
        Font font = workbook.createFont();
        //设置字体大小
        font.setFontHeightInPoints((short) 24);
        //字体加粗
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        //设置字体名字
//        font.setFontName("Courier New");
        //设置样式;
        CellStyle style = workbook.createCellStyle();
        //设置底边框;
        style.setBorderBottom(CellStyle.BORDER_THIN);
        //设置底边框颜色;
        style.setBottomBorderColor(BLACK.index);
        //设置左边框;
        style.setBorderLeft(CellStyle.BORDER_THIN);
        //设置左边框颜色;
        style.setLeftBorderColor(BLACK.index);
        //设置右边框;
        style.setBorderRight(CellStyle.BORDER_THIN);
        //设置右边框颜色;
        style.setRightBorderColor(BLACK.index);
        //设置顶边框;
        style.setBorderTop(CellStyle.BORDER_THIN);
        //设置顶边框颜色;
        style.setTopBorderColor(BLACK.index);
        //在样式用应用设置的字体;
        style.setFont(font);
        //设置自动换行;
        style.setWrapText(false);
        //设置水平对齐的样式为居中对齐;
        style.setAlignment(CellStyle.ALIGN_CENTER);
        //设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        return style;

    }

    /**
     * 列头单元格样式
     *
     * @param workbook 工作表
     */
    private CellStyle getTitleStyle(SXSSFWorkbook workbook) {

        // 设置字体
        Font font = workbook.createFont();
        //设置字体大小
        font.setFontHeightInPoints((short) 14);
        font.setFontName("Arial");
        //字体加粗
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        //设置样式;
        CellStyle style = workbook.createCellStyle();
        //设置底边框;
        style.setBorderBottom(CellStyle.BORDER_THIN);
        //设置底边框颜色;
        style.setBottomBorderColor(BLACK.index);
        //设置左边框;
        style.setBorderLeft(CellStyle.BORDER_THIN);
        //设置左边框颜色;
        style.setLeftBorderColor(BLACK.index);
        //设置右边框;
        style.setBorderRight(CellStyle.BORDER_THIN);
        //设置右边框颜色;
        style.setRightBorderColor(BLACK.index);
        //设置顶边框;
        style.setBorderTop(CellStyle.BORDER_THIN);
        //设置顶边框颜色;
        style.setTopBorderColor(BLACK.index);
        //在样式用应用设置的字体;
        style.setFont(font);
        //设置自动换行;
        style.setWrapText(false);
        //设置水平对齐的样式为居中对齐;
        style.setAlignment(CellStyle.ALIGN_CENTER);
        //设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFillForegroundColor(LIGHT_YELLOW.index);
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        return style;

    }

    /**
     * 列数据信息单元格样式
     *
     * @param workbook 工作表
     */
    private CellStyle getStyle(SXSSFWorkbook workbook) {
        // 设置字体
        Font font = workbook.createFont();
        //设置字体大小
        font.setFontHeightInPoints((short) 10);
        font.setFontName("Arial");
        //字体加粗
        //font.setBoldweight(SXSSFFont.BOLDWEIGHT_BOLD);
        //设置样式;
        CellStyle style = workbook.createCellStyle();
        //设置底边框;
        style.setBorderBottom(CellStyle.BORDER_THIN);
        //设置底边框颜色;
        style.setBottomBorderColor(BLACK.index);
        //设置左边框;
        style.setBorderLeft(CellStyle.BORDER_THIN);
        //设置左边框颜色;
        style.setLeftBorderColor(BLACK.index);
        //设置右边框;
        style.setBorderRight(CellStyle.BORDER_THIN);
        //设置右边框颜色;
        style.setRightBorderColor(BLACK.index);
        //设置顶边框;
        style.setBorderTop(CellStyle.BORDER_THIN);
        //设置顶边框颜色;
        style.setTopBorderColor(BLACK.index);
        //在样式用应用设置的字体;
        style.setFont(font);
        //设置自动换行;
        style.setWrapText(false);
        //设置水平对齐的样式为居中对齐;
        style.setAlignment(CellStyle.ALIGN_CENTER);
        //设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        return style;

    }
}
