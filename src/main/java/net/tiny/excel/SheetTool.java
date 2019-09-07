package net.tiny.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public final class SheetTool {

    private SheetTool() {}

    public static Row toRow(Row row, int offset) {
        Sheet sheet = row.getSheet();
        return sheet.getRow(row.getRowNum() + offset);
    }

    public static String getValue(Row row, int x) {
        Cell cell = row.getCell(x);
        if(null == cell) {
            return null;
        }
        return cell.getRichStringCellValue().getString();
    }

    public static CellRange copy(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        CellRange range = new CellRange(sheet, firstRow, lastRow, firstCol, lastCol);
        return range;
    }

    public static int getLastRow(final Sheet sheet) {
        return sheet.getLastRowNum();
    }

    public static int getLastColumn(final Sheet sheet) {
        final int lastRow = sheet.getLastRowNum();
        int column = 0;
        for(int y=0; y<lastRow; y++) {
            Row row = sheet.getRow(y);
            int x = 0;
            boolean eol = false;
            while (!eol) {
                try {
                    Cell c = row.getCell(x++);
                    if (null == c) {
                        eol = true;
                    }
                } catch(Exception e) {
                    eol = true;
                }
            }
            int max = x -1;
            if (column < max) {
                column = max;
            }
        }
        return column;
    }

    public static CellRange getFullCellRange(final Sheet sheet) {
        return new CellRange(sheet, 0, getLastRow(sheet), 0, getLastColumn(sheet));
    }

}
