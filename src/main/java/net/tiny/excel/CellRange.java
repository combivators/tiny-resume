package net.tiny.excel;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

public class CellRange {

    String[][] values;
    String[][] links;
    CellStyle[][] styles;
    final CreationHelper createHelper;

    public CellRange(String[][] values, CellStyle[][] styles, String[][] links, CreationHelper helper) {
        this.values = values;
        this.styles = styles;
        this.links  = links;
        this.createHelper = helper;
    }

    public CellRange(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        createHelper = sheet.getWorkbook().getCreationHelper();
        values = new String[Math.abs(lastCol-firstCol+1)][Math.abs(lastRow-firstRow+1)];
        links = new String[Math.abs(lastCol-firstCol+1)][Math.abs(lastRow-firstRow+1)];
        styles = new CellStyle[Math.abs(lastCol-firstCol+1)][Math.abs(lastRow-firstRow+1)];
        for(int y=firstRow; y<=lastRow; y++) {
            Row row = sheet.getRow(y);
            if(null == row) {
                break;
            }
            for(int x=firstCol; x<=lastCol; x++) {
                Cell c = row.getCell(x);
                if (null != c) {
                    values[x-firstCol][y-firstRow] = getCellValue(c);
                    styles[x-firstCol][y-firstRow]  = c.getCellStyle();
                    links[x-firstCol][y-firstRow] = getHyperlink(c);
                }
            }
        }
    }

    public CellStyle[] getStyles(int x) {
        return styles[x];
    }

    public CellStyle getStyle(int x, int y) {
        return styles[x][y];
    }

    public String[] getValues(int x) {
        return values[x];
    }

    public String getValue(int x, int y) {
        return values[x][y];
    }

    public String[] getLinks(int x) {
        return links[x];
    }

    public String getLink(int x, int y) {
        return links[x][y];
    }

    public String getFirstValue() {
        return values[0][0];
    }

    public String getLastValue() {
        return values[getWidth()-1][getHeight()-1];
    }

    public static String getValue(Row row, int x) {
        Cell cell = row.getCell(x);
        if(null == cell) {
            return null;
        }
        return getCellValue(cell);
    }

    public static String getHyperlink(Row row, int x) {
        Cell cell = row.getCell(x);
        if(null == cell) {
            return null;
        }
        return getHyperlink(cell);
    }

    public static String getCellValue(Cell cell) {
        if(null == cell) {
            return null;
        }
        String ret = "";
        final CellType cellType = cell.getCellType();
        if (CellType.NUMERIC.equals(cellType)) {
            ret = Long.toString(new Double(cell.getNumericCellValue()).longValue());
        } else if (CellType.STRING.equals(cellType)) {
            ret = cell.getRichStringCellValue().getString();
        }
        /*
        switch(cellType) {
        case CellType.NUMERIC:
            ret = Long.toString(new Double(cell.getNumericCellValue()).longValue());
            break;
        case Cell.CELL_TYPE_STRING:
            ret = cell.getRichStringCellValue().getString();
            break;
        case Cell.CELL_TYPE_BOOLEAN:
            break;
        case Cell.CELL_TYPE_BLANK:
        case Cell.CELL_TYPE_FORMULA:
        case Cell.CELL_TYPE_ERROR:
            break;
        }
        */
        return ret;
    }

    public static String getHyperlink(Cell cell) {
        if(null == cell) {
            return null;
        }
        Hyperlink link = cell.getHyperlink();
        if(null != link) {
            return link.getAddress();
        }
        return null;
    }
    public int getWidth() {
        return values.length;
    }

    public int getHeight() {
        if(getWidth()>0) {
            return values[0].length;
        }
        return 0;
    }

    public CellRange merge(CellRange range) {
        return merge(range, getWidth());
    }

    public CellRange merge(CellRange range, int offset) {
        int width = range.getWidth() + offset;
        int height = getHeight();
        String[][]  vArray = new String[width][height];
        String[][]  lArray = new String[width][height];
        CellStyle[][] sArray = new CellStyle[width][height];
        for(int y=0; y<height; y++) {
            for(int x=0; x<offset; x++) {
                vArray[x][y] = values[x][y];
                sArray[x][y] = styles[x][y];
                lArray[x][y] = links[x][y];
            }
            for(int x=offset; x<width; x++) {
                int n = x - offset;
                vArray[x][y] = range.values[n][y];
                sArray[x][y] = range.styles[n][y];
                lArray[x][y] = range.links[n][y];
            }
        }
        return new CellRange(vArray, sArray, lArray, this.createHelper);
    }

    @Override
    public CellRange clone() {
        int width = getWidth();
        int height = getHeight();
        String[][]  vArray = new String[width][height];
        String[][]  lArray  = new String[width][height];
        CellStyle[][] sArray = new CellStyle[width][height];
        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                vArray[x][y] = values[x][y];
                sArray[x][y] = styles[x][y];
                lArray[x][y] = links[x][y];
            }
        }
        return new CellRange(vArray, sArray, lArray, this.createHelper);
    }


    public void setValue(int x, int y, String value) {
        values[x][y] = value;
    }

    public void setValues(Map<String, String> map) {
        int y = getHeight() - 1;
        for(String key : map.keySet()) {
            int x = findIndex(key);
            if(x != -1) {
                values[x][y] = map.get(key);
            }
        }
    }

    public int findIndex(String name) {
        for(int x=0; x<getWidth()-1; x++) {
            if(name.equalsIgnoreCase(values[x][0])) {
                return x;
            }
        }
        return -1;
    }

    public String[] toArray() {
        List<String> list = new ArrayList<String>();
        int width = getWidth();
        int height = getHeight();
        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                if(null == values[x][y]) {
                    list.add("");
                } else {
                    list.add(values[x][y]);
                }
            }
        }
        return list.toArray(new String[list.size()]);
    }

    public void writeTo(Row row) {
        writeTo(row, 0, true);
    }

    public void writeTo(Row row, boolean merge) {
        writeTo(row, 0, merge);
    }

    public void writeTo(final Row row, int offset, boolean merge) {
        int width = getWidth();
        int height = getHeight();
        Row r = row;
        int last = r.getSheet().getLastRowNum();
        Cell cell;
        for(int y=0; y<height; y++) {
            for(int x=0; x<width; x++) {
                cell = r.createCell(x + offset);
                cell.setCellValue(values[x][y]);
                cell.setCellStyle(styles[x][y]);
                if(links[x][y] != null) {
                    try {
                        String hyper = links[x][y];
                        String format = URLDecoder.decode(links[x][y], "UTF-8");
                        if(format.indexOf("%1") != -1) {
                            hyper = String.format(format, values[x][y]);
                        }
                        Hyperlink link = cell.getHyperlink();
                        if(link == null) {
                            link = createHelper.createHyperlink(HyperlinkType.URL);
                        }
                        link.setAddress(hyper);
                        cell.setHyperlink(link);
                    } catch (UnsupportedEncodingException e) {
                        //Ignore
                    }
                }
            }
            r = r.getSheet().createRow(y+last+1);
        }
        if(merge) {
            int firstRow = row.getRowNum();
            int lastRow = firstRow+ 4;
            int firstCol = offset;
            int lastCol = offset;
            CellRangeAddress region
                = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
            r.getSheet().addMergedRegion(region);
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(values[0][0]);
        sb.append(" - ");
        sb.append(values[getWidth()-1][0]);
        sb.append(" - ");
        sb.append(values[getWidth()-1][getHeight()-1]);
        return sb.toString();
    }
}

