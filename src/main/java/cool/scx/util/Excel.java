package cool.scx.util;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * todo : (注意 此工具类只支持单 sheet页的情况) 需要改为支持多个 sheet 页的情况
 * Excel 操作类
 * 对 生成 excel 进行一些简单的封装 方便使用
 *
 * @author 司昌旭
 * @version 0.9.1
 */
public class Excel {

    /**
     * Excel 实例
     */
    public final Workbook workbook;

    /**
     * 当前 sheet 后期会修改为 支持多个 sheet 页
     */
    public final Sheet sheet;

    /**
     * 行缓存 此处提前创建 row 方便后续直接根据索引使用
     */
    public final Map<Integer, Row> rowMap;

    private Excel(Workbook workbook, String sheetName, int rowSize) {
        this.workbook = workbook;
        this.sheet = workbook.createSheet(sheetName);
        this.rowMap = getRowMap(rowSize, sheet);
    }

    /**
     * 获取 03 版 excel (xls)
     *
     * @param sheetName sheet 名称
     * @param rowSize   初始的行大小 建议提前估算一个值 并设置为 估算值的 1.5倍
     * @return xls
     */
    public static Excel get03Excel(String sheetName, int rowSize) {
        return new Excel(new HSSFWorkbook(), sheetName, rowSize);
    }

    /**
     * 获取 07 版 excel (xlsx)
     *
     * @param sheetName sheet 名称
     * @param rowSize   初始的行大小 建议提前估算一个值 并设置为 估算值的 1.5倍
     * @return xlsx
     */
    public static Excel get07Excel(String sheetName, int rowSize) {
        return new Excel(new XSSFWorkbook(), sheetName, rowSize);
    }

    /**
     * 创建 行的 map 方便后续操作
     * key 是索引 , value 是 当前行
     *
     * @param size 创建的行数
     */
    private static Map<Integer, Row> getRowMap(int size, Sheet sheet) {
        var rowMap = new HashMap<Integer, Row>(size);
        for (int i = 0; i < size; i++) {
            rowMap.put(i, sheet.createRow(i));
        }
        return rowMap;
    }

    /**
     * 设置边框
     *
     * @param firstRow 起始行
     * @param lastRow  结束行
     * @param firstCol 起始列
     * @param lastCol  结束列
     * @return 返回坐标地址 方便链式调用
     */
    public CellRangeAddress setBorder(int firstRow, int lastRow, int firstCol, int lastCol) {
        return setBorder(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }

    /**
     * 设置边框 根据坐标
     *
     * @param cellAddresses 坐标
     * @return 返回坐标地址 方便链式调用
     */
    public CellRangeAddress setBorder(CellRangeAddress cellAddresses) {
        RegionUtil.setBorderTop(BorderStyle.THIN, cellAddresses, sheet); // 上边框
        RegionUtil.setBorderLeft(BorderStyle.THIN, cellAddresses, sheet); // 在边框
        RegionUtil.setBorderRight(BorderStyle.THIN, cellAddresses, sheet); // 右边框
        RegionUtil.setBorderBottom(BorderStyle.THIN, cellAddresses, sheet); // 下边框
        return cellAddresses;
    }

    /**
     * 设置边框 (只设置一个单元格)*
     *
     * @param firstRow 起始行
     * @param firstCol 起始列
     * @return 坐标
     */
    public CellRangeAddress setBorder(int firstRow, int firstCol) {
        return setBorder(firstRow, firstRow, firstCol, firstCol);
    }

    /**
     * 获取 sheet 页
     *
     * @return 当前 sheet
     */
    public Sheet getSheet() {
        return workbook.createSheet();
    }

    /**
     * 获取指定名称的 sheet
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link org.apache.poi.ss.usermodel.Sheet} object.
     */
    public Sheet getSheet(String name) {
        return workbook.createSheet(name);
    }

    /**
     * 合并单元格
     * 注意 right 和 down 为合并大小 及 最终表格大小会是 right 和 down + 1 因为包括原始行
     *
     * @param firstRow 起始行 (纵向)
     * @param firstCol 起始列 (横向)
     * @param right    向左合并几个单元格 可以为负数
     * @param down     向下合并几个单元格 可以为负数
     * @return a {@link org.apache.poi.ss.util.CellRangeAddress} object.
     */
    public CellRangeAddress mergedRegion(int firstRow, int firstCol, int down, int right) {
        int _firstRow = down >= 0 ? firstRow : firstRow + down;
        int _lastRow = down >= 0 ? firstRow + down : firstRow;
        int _firstCol = right >= 0 ? firstCol : firstCol + right;
        int _lastCol = right >= 0 ? firstCol + right : firstCol;
        return mergedRegion(new CellRangeAddress(_firstRow, _lastRow, _firstCol, _lastCol));
    }

    /**
     * 根据坐标合并单元格
     *
     * @param addresses a {@link org.apache.poi.ss.util.CellRangeAddress} object.
     * @return a {@link org.apache.poi.ss.util.CellRangeAddress} object.
     */
    public CellRangeAddress mergedRegion(CellRangeAddress addresses) {
        sheet.addMergedRegion(addresses);
        return addresses;
    }

    /**
     * 设置单元格数据
     *
     * @param firstRow      a int.
     * @param firstCol      a int.
     * @param value         a {@link java.lang.String} object.
     * @param xssfCellStyle a {@link org.apache.poi.ss.usermodel.CellStyle} object.
     * @return a {@link org.apache.poi.ss.usermodel.Cell} object.
     */
    public Cell setCellValue(int firstRow, int firstCol, String value, CellStyle xssfCellStyle) {
        Cell cell = rowMap.get(firstRow).createCell(firstCol);
        cell.setCellValue(value);
        cell.setCellStyle(xssfCellStyle);
        return cell;
    }

    /**
     * 设置单元格样式
     *
     * @param firstRow a int.
     * @param firstCol a int.
     * @param value    a {@link java.lang.String} object.
     * @return a {@link org.apache.poi.ss.usermodel.Cell} object.
     */
    public Cell setCellValue(int firstRow, int firstCol, String value) {
        Cell cell = rowMap.get(firstRow).createCell(firstCol);
        cell.setCellValue(value);
        return cell;
    }

    /**
     * 创建单元格样式
     *
     * @return a {@link org.apache.poi.ss.usermodel.CellStyle} object.
     */
    public CellStyle createCellStyle() {
        return workbook.createCellStyle();
    }

    /**
     * 创建字体
     *
     * @return a {@link org.apache.poi.ss.usermodel.Font} object.
     */
    public Font createFont() {
        return workbook.createFont();
    }

    /**
     * 根据索引获取行
     *
     * @param rowIndex a int.
     * @return a {@link org.apache.poi.ss.usermodel.Row} object.
     */
    public Row getRow(int rowIndex) {
        return rowMap.get(rowIndex);
    }

    public byte[] toBytes() {
        try (var o = new ByteArrayOutputStream()) {
            workbook.write(o);
            return o.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
