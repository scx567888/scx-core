package cool.scx.util.office;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.HashMap;
import java.util.Map;

/**
 * Excel 操作类 (注意 此工具类只支持单 sheet)
 *
 * @author 司昌旭
 * @version 0.9.1
 */
public class Excel {

    public final Workbook workbook;
    public final Sheet sheet;
    public final Map<Integer, Row> rowMap;

    private Excel(Workbook workbook, String sheetName, int rowSize) {
        this.workbook = workbook;
        this.sheet = workbook.createSheet(sheetName);
        this.rowMap = getRowMap(rowSize, sheet);
    }

    /**
     * 获取 03 版 excel (xls)
     *
     * @param sheetName a {@link java.lang.String} object.
     * @param rowSize   a int.
     * @return xls
     */
    public static Excel get03Excel(String sheetName, int rowSize) {
        return new Excel(new HSSFWorkbook(), sheetName, rowSize);
    }

    /**
     * 获取 07 版 excel (xlsx)
     *
     * @param sheetName a {@link java.lang.String} object.
     * @param rowSize   a int.
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
    private Map<Integer, Row> getRowMap(int size, Sheet sheet) {
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
     * @return 坐标
     */
    public CellRangeAddress setBorder(int firstRow, int lastRow, int firstCol, int lastCol) {
        var cellAddresses = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        RegionUtil.setBorderTop(BorderStyle.THIN, cellAddresses, sheet); // 下边框
        RegionUtil.setBorderLeft(BorderStyle.THIN, cellAddresses, sheet); // 下边框
        RegionUtil.setBorderRight(BorderStyle.THIN, cellAddresses, sheet); // 下边框
        RegionUtil.setBorderBottom(BorderStyle.THIN, cellAddresses, sheet); // 下边框
        return cellAddresses;
    }

    /**
     * <p>setBorder.</p>
     *
     * @param cellAddresses a {@link org.apache.poi.ss.util.CellRangeAddress} object.
     * @return a {@link org.apache.poi.ss.util.CellRangeAddress} object.
     */
    public CellRangeAddress setBorder(CellRangeAddress cellAddresses) {
        RegionUtil.setBorderTop(BorderStyle.THIN, cellAddresses, sheet); // 下边框
        RegionUtil.setBorderLeft(BorderStyle.THIN, cellAddresses, sheet); // 下边框
        RegionUtil.setBorderRight(BorderStyle.THIN, cellAddresses, sheet); // 下边框
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
     * <p>Getter for the field <code>sheet</code>.</p>
     *
     * @return a {@link org.apache.poi.ss.usermodel.Sheet} object.
     */
    public Sheet getSheet() {
        return workbook.createSheet();
    }

    /**
     * <p>Getter for the field <code>sheet</code>.</p>
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
        var cellAddresses = new CellRangeAddress(_firstRow, _lastRow, _firstCol, _lastCol);
        sheet.addMergedRegion(cellAddresses);
        return cellAddresses;
    }

    /**
     * <p>mergedRegion.</p>
     *
     * @param addresses a {@link org.apache.poi.ss.util.CellRangeAddress} object.
     * @return a {@link org.apache.poi.ss.util.CellRangeAddress} object.
     */
    public CellRangeAddress mergedRegion(CellRangeAddress addresses) {
        sheet.addMergedRegion(addresses);
        return addresses;
    }

    //添加数据

    /**
     * <p>setCellValue.</p>
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

    //添加数据

    /**
     * <p>setCellValue.</p>
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
     * <p>createCellStyle.</p>
     *
     * @return a {@link org.apache.poi.ss.usermodel.CellStyle} object.
     */
    public CellStyle createCellStyle() {
        return workbook.createCellStyle();
    }

    /**
     * <p>createFont.</p>
     *
     * @return a {@link org.apache.poi.ss.usermodel.Font} object.
     */
    public Font createFont() {
        return workbook.createFont();
    }

    /**
     * <p>getRow.</p>
     *
     * @param rowIndex a int.
     * @return a {@link org.apache.poi.ss.usermodel.Row} object.
     */
    public Row getRow(int rowIndex) {
        return rowMap.get(rowIndex);
    }
}