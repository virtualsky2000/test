package system.utils;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import system.exception.ApplicationException;

public abstract class AbstractWorkbookReader {

    private static final Pattern pCell = Pattern.compile("^([A-Z]+)(\\d+)$");

    private static final Pattern pRange = Pattern.compile("^(([A-Z]+)(\\d+)):(([A-Z]+)(\\d+))$");

    private static DateFormat dfDay = new SimpleDateFormat("yyyy/MM/dd");

    private static DateFormat dfSec = new SimpleDateFormat("hh:mm:ss");

    private static Calendar calDay = Calendar.getInstance();

    private static Calendar calSec = Calendar.getInstance();

    private static Date dtInitDay;

    private static Date dtInitSec;

    protected Workbook workbook;

    protected File file;

    protected String fileName;

    static {
        try {
            dtInitDay = dfDay.parse("1899/12/30");
            dtInitSec = dfSec.parse("00:00:00");
        } catch (ParseException e) {
            throw new ApplicationException(e);
        }
    }

    public Iterator<Sheet> sheetIterator() {
        return workbook.iterator();
    }

    public Sheet getSheet(String name) {
        return workbook.getSheet(name);
    }

    public Sheet getSheet(int index) {
        return workbook.getSheetAt(index);
    }

    @SuppressWarnings("deprecation")
    public int findEndRow(Sheet sheet, int rowIndex, int colIndex) {
        while (true) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                break;
            }
            Cell cell = row.getCell(colIndex);
            if (cell == null) {
                break;
            }
            if (cell.getCellTypeEnum() == CellType.BLANK) {
                break;
            }
            rowIndex++;
        }

        return rowIndex - 1;
    }

    public int findEndRow(Sheet sheet, String cell) {
        Point p = getPoint(cell);
        return findEndRow(sheet, p.row, p.col);
    }

    public int findEndCol(Sheet sheet, int rowIndex, int colIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            return rowIndex;
        }

        while (true) {
            Cell cell = row.getCell(colIndex);
            if (cell == null) {
                break;
            }
            colIndex++;
        }

        return colIndex;
    }

    public String getCellValue(Sheet sheet, int rowIndex, int colIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            return null;
        }
        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            return null;
        }
        return cell.getStringCellValue();
    }

    public String getCellValue(Sheet sheet, String cell) {
        Point p = getPoint(cell);
        return getCellValue(sheet, p.row, p.col);
    }

    @SuppressWarnings("deprecation")
    public Object[][] readData(Sheet sheet, int startRow, int startCol, int endRow, int endCol) {
        Object[][] data = new Object[endRow - startRow + 1][endCol - startCol + 1];
        int i, j;

        for (int rowIndex = startRow; rowIndex <= endRow; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            i = rowIndex - startRow;
            if (row != null) {
                for (int colIndex = startCol; colIndex <= endCol; colIndex++) {
                    Cell cell = row.getCell(colIndex);
                    j = colIndex - startCol;
                    if (cell != null) {
                        switch (cell.getCellTypeEnum()) {
                        case STRING:
                            data[i][j] = cell.getStringCellValue();
                            break;
                        case NUMERIC:
                            data[i][j] = cell.getNumericCellValue();
                            break;
                        case BOOLEAN:
                            data[i][j] = cell.getBooleanCellValue();
                            break;
                        case BLANK:
                            data[i][j] = "";
                            break;
                        default:
                            break;
                        }
                    } else {
                        data[i][j] = null;
                    }
                }
            } else {
                for (int colIndex = startCol; colIndex <= endCol; colIndex++) {
                    j = colIndex - startCol;
                    data[i][j] = null;
                }
            }
        }

        return data;
    }

    @SuppressWarnings("deprecation")
    public List<Object[]> readValue(Sheet sheet, int startRow, int startCol, int endRow, int endCol) {
        List<Object[]> lstData = new ArrayList<>();
        int i;

        for (int rowIndex = startRow; rowIndex <= endRow; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            Object[] rowData = new Object[endCol - startCol + 1];
            if (row != null) {
                for (int colIndex = startCol; colIndex <= endCol; colIndex++) {
                    Cell cell = row.getCell(colIndex);
                    i = colIndex - startCol;
                    if (cell != null) {
                        switch (cell.getCellTypeEnum()) {
                        case STRING:
                            rowData[i] = cell.getStringCellValue();
                            break;
                        case NUMERIC:
                            rowData[i] = cell.getNumericCellValue();
                            break;
                        case BOOLEAN:
                            rowData[i] = cell.getBooleanCellValue();
                            break;
                        case BLANK:
                            rowData[i] = "";
                            break;
                        default:
                            break;
                        }
                    } else {
                        rowData[i] = null;
                    }
                }
            } else {
                System.out.println("here");

            }

            lstData.add(rowData);
        }

        return lstData;
    }

    @SuppressWarnings("deprecation")
    public List<String[]> readTextValue(Sheet sheet, int startRow, int startCol, int endRow, int endCol) {
        List<String[]> lstData = new ArrayList<>();
        int i;

        for (int rowIndex = startRow; rowIndex <= endRow; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            String[] rowData = new String[endCol - startCol + 1];
            if (row != null) {
                for (int colIndex = startCol; colIndex <= endCol; colIndex++) {
                    Cell cell = row.getCell(colIndex);
                    i = colIndex - startCol;
                    if (cell != null) {
                        switch (cell.getCellTypeEnum()) {
                        case STRING:
                            rowData[i] = cell.getStringCellValue();
                            break;
                        case NUMERIC:
                            rowData[i] = Double.toString(cell.getNumericCellValue());
                            break;
                        case BOOLEAN:
                            rowData[i] = Boolean.toString(cell.getBooleanCellValue());
                            break;
                        case BLANK:
                            rowData[i] = "";
                            break;
                        default:
                            break;
                        }
                    } else {
                        rowData[i] = null;
                    }
                }
            } else {
                System.out.println("here");

            }

            lstData.add(rowData);
        }

        return lstData;
    }

    @SuppressWarnings("deprecation")
    public List<String[]> readTextValue(Sheet sheet, int startRow, int startCol, int endRow, int endCol, int[] cols) {
        List<String[]> lstData = new ArrayList<>();

        for (int rowIndex = startRow; rowIndex <= endRow; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            String[] rowData = new String[cols.length];
            if (row != null) {
                for (int i = 0; i < cols.length; i++) {
                    int colIndex = cols[i];
                    Cell cell = row.getCell(colIndex);
                    if (cell != null) {
                        switch (cell.getCellTypeEnum()) {
                        case STRING:
                            rowData[i] = cell.getStringCellValue();
                            break;
                        case NUMERIC:
                            rowData[i] = Double.toString(cell.getNumericCellValue());
                            break;
                        case BOOLEAN:
                            rowData[i] = Boolean.toString(cell.getBooleanCellValue());
                            break;
                        case BLANK:
                            rowData[i] = "";
                            break;
                        default:
                            break;
                        }
                    } else {
                        rowData[i] = null;
                    }
                }
            } else {
                System.out.println("here");

            }

            lstData.add(rowData);
        }

        return lstData;
    }

    public List<String[]> readTextValue(Sheet sheet, String range) {
        Rect r = getRect(range);
        return readTextValue(sheet, r.x.row, r.x.col, r.y.row, r.y.col);
    }


    public List<String[]> readTextValue(Sheet sheet, String range, String[] columns) {
        Rect r = getRect(range);
        int len = columns.length;
        int[] cols = new int[len];
        for (int i = 0; i < len; i++) {
            cols[i] = getColumnIndex(columns[i]);
        }

        return readTextValue(sheet, r.x.row, r.x.col, r.y.row, r.y.col, cols);
    }

    public static int getColumnIndex(String str) {
        int col = 0;
        for (int i = 0, len = str.length(); i < len; i++) {
            char c = str.charAt(i);
            if (c >= 'A' && c <= 'Z') {
                col = col * 26 + c - 64;
            } else {
                break;
            }
        }

        return col - 1;
    }

    public static String getDay(int days) {
        calDay.setTime(dtInitDay);
        calDay.add(Calendar.DATE, days);

        return dfDay.format(calDay.getTime());
    }

    public static String getSec(int sec) {
        calSec.setTime(dtInitSec);
        calSec.add(Calendar.SECOND, sec);

        return dfSec.format(calSec.getTime());
    }

    private class Point {
        int row;
        int col;
    }

    private class Rect {
        Point x;
        Point y;
    }

    private Point getPoint(String cell) {
        Matcher m = pCell.matcher(cell);
        if (m.find()) {
            Point p = new Point();
            p.row = Integer.parseInt(m.group(2)) - 1;
            p.col = getColumnIndex(m.group(1));
            return p;
        }

        throw new ApplicationException("invalid cell string");
    }

    private Rect getRect(String range) {
        Matcher m = pRange.matcher(range);
        if (m.find()) {
            Rect r = new Rect();
            r.x = getPoint(m.group(1));
            r.y = getPoint(m.group(4));
            return r;
        }

        throw new ApplicationException("invalid range string");
    }

}
