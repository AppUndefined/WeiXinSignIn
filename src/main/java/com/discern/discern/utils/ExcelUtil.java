package com.discern.discern.utils;
import com.discern.discern.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.solr.common.util.Hash;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public  class ExcelUtil {




    /**
     * 判断是否是Excel文件
     * @param filePath
     * @return
     */
    public static boolean isExcel2003(String filePath)
    {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    public static boolean isExcel2007(String filePath)
    {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }
    private static FormulaEvaluator evaluator;
    //获取单元格各类型值，返回字符串类型
    private static String getCellValueByCell(Cell cell) {
        //判断是否为null或空串
        if (cell==null || cell.toString().trim().equals("")) {
            return "";
        }
        String cellValue = "";
        int cellType=cell.getCellType();
        if(cellType==Cell.CELL_TYPE_FORMULA){ //表达式类型
            cellType=evaluator.evaluate(cell).getCellType();
        }

        switch (cellType) {
            case Cell.CELL_TYPE_STRING: //字符串类型
                cellValue= cell.getStringCellValue().trim();
                cellValue=StringUtils.isEmpty(cellValue) ? "" : cellValue;
                break;
            case Cell.CELL_TYPE_BOOLEAN:  //布尔类型
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC: //数值类型
                if (HSSFDateUtil.isCellDateFormatted(cell)) {  //判断日期类型
                    Date data = cell.getDateCellValue();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd ");
                    cellValue =format.format(data);
                } else {  //否
                    cellValue = new DecimalFormat("#.######").format(cell.getNumericCellValue());
                }
                break;
            default: //其它类型，取空串吧
                cellValue = "";
                break;
        }
        return cellValue;
    }

    public static Map ExcelDispose(Sheet sheet, File[] files, User user, String host) throws Exception {
        int success = 0;
        int fail = 0;
        int index = 0;
        HashMap<String, Object> map = new HashMap<>();
        ArrayList<String> successList = new ArrayList<>();
        ArrayList<String> failList = new ArrayList<>();
        for (int i = 1; i < sheet.getLastRowNum()+1; i++) {
            //获取索引为i的行，以0开始
            Row row = sheet.getRow(i);
            //获取第i行的索引为0的单元格数据
            String imageName = row.getCell(0).toString();
            String name = row.getCell(1).toString();
            String age = row.getCell(2).toString().replaceAll(".0","");
            String sex = row.getCell(3).toString();
            System.out.println(imageName+name+age+sex);
            for (File file : files) {
                String fileName = file.getName();
                if(fileName.equals(imageName)){
                    String face = Base64Utils.ImageToBase64ByLocal(new FileInputStream(file));
                    //向底库添加人脸
                    HashMap<String, String> param = new HashMap<>();
                    param.put("face_base64",face);
                    param.put("name",name);
                    //添加人姓名
                    param.put("user",user.getUsername());
                    param.put("sex",sex);
                    param.put("age",age);
                    String s = HttpUtils.sendPost(host+"/baseface/", param);
                    success++;
                    System.out.println(s);
                }else{

                }
            }
            if(success==index){
                fail++;
                failList.add(imageName);
            }
            index = success;

        }
        map.put("success",success);
        map.put("fail",fail);
        map.put("failList",failList);
        return map;
    }
}
