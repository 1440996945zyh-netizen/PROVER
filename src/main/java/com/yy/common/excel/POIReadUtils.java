package com.yy.common.excel;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 * @Author linqi
 * @Description POI读数据工具类
 * @Date 2023-06-12 09:48
 */
public class POIReadUtils {

    /**
     * 使用引用样式获取行，空安全
     *
     * @param sheet
     * @param rowNum
     * @return
     */
    public static XSSFRow getRowByRefStyle(XSSFSheet sheet, int rowNum) {
        int rowIndex = rowNum - 1;
        return getRow(sheet, rowIndex);
    }

    /**
     * 使用引用样式获取单元格，空安全
     *
     * @param row
     * @param columnNum
     * @return
     */
    public static XSSFCell getCellByRefStyle(XSSFRow row, String columnNum) {
        int columnIndex = 0;
        char[] charArray = columnNum.toUpperCase().toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char _char = charArray[i];
            if (_char < 65 || _char > 90) {
                throw new IllegalArgumentException("错误的列号");
            }
            columnIndex += (_char - 65) + i * 65;
        }
        return getCell(row, columnIndex);
    }

    /**
     * 使用引用样式获取单元格，空安全
     *
     * @param sheet
     * @param rowNum
     * @param columnNum
     * @return
     */
    public static XSSFCell getCellByRefStyle(XSSFSheet sheet, int rowNum, String columnNum) {
        int rowIndex = rowNum - 1;
        int columnIndex = 0;
        char[] charArray = columnNum.toUpperCase().toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char _char = charArray[i];
            if (_char < 65 || _char > 90) {
                throw new IllegalArgumentException("错误的列号");
            }
            columnIndex += (_char - 65) + i * 65;
        }
        return getCell(sheet, rowIndex, columnIndex);
    }

    /**
     * 返回指定行，空安全
     *
     * @param sheet
     * @param rowIndex
     * @return
     */
    public static XSSFRow getRow(XSSFSheet sheet, int rowIndex) {
        XSSFRow row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        return row;
    }

    /**
     * 返回指定单元格，空安全
     *
     * @param row
     * @param columnIndex
     * @return
     */
    public static XSSFCell getCell(XSSFRow row, int columnIndex) {
        XSSFCell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        return cell;
    }

    /**
     * 返回指定单元格，空安全
     *
     * @param sheet
     * @param rowIndex
     * @param columnIndex
     * @return
     */
    public static XSSFCell getCell(XSSFSheet sheet, int rowIndex, int columnIndex) {
        return getCell(getRow(sheet, rowIndex), columnIndex);
    }
}
