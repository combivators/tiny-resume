package net.tiny.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public abstract class AbstractDataSheet {

    public static enum CancelType {
        NORMAL,
        CANCEL,
        TODAY_CANCEL
    }

    Workbook workbook;
    CellRange header;
    CellRange footer;
    List<CellRange> bodies = new ArrayList<CellRange>();
    Map<String, String> fixValues = new HashMap<String, String>();
    String dataIndex;

    protected abstract String getBookName();
    protected abstract String getTempSheetName();
    protected abstract void parseSheet(Sheet sheet) throws Exception;

    public AbstractDataSheet() throws Exception {
        URL url = getClass().getResource(getBookName());
        InputStream stream = url.openStream();
        this.workbook = WorkbookFactory.create(stream);
        parseSheet(this.workbook.getSheet(getTempSheetName()));
        stream.close();
    }

    public Workbook getWorkbook() {
        return workbook;
    }

    public CellRange getFooter() {
        return footer;
    }

    public CellRange getHeader() {
        return header;
    }

    public void setFixValues(Map<String, String> values) {
        fixValues.putAll(values);
    }

    public void addBody(CellRange range) {
        range.setValue(0, 0, "ボディ" + (bodies.size()+1));
        bodies.add(range);
    }

    public void writeTo(File excel) throws Exception {
        generatorSheet();
        //Output Excel file
        FileOutputStream out = new FileOutputStream(excel);
        workbook.write(out);
        out.flush();
        out.close();
    }

    public void generatorSheet() throws Exception {
        Sheet sheet = workbook.getSheet(getTempSheetName());
        Row row = sheet.createRow(30);
        header.writeTo(row);
        row = sheet.createRow(sheet.getLastRowNum()+1);

        for(CellRange body : bodies) {
            body.writeTo(row);
            row = sheet.createRow(sheet.getLastRowNum());
        }

        row = sheet.createRow(sheet.getLastRowNum()+1);
        footer.setValue(4, 4, String.format("%1$015d(レコード件数)", bodies.size()));
        footer.writeTo(row);
    }

    protected void finalize() throws Throwable {
        if(null != workbook)
            workbook.close();
    }

    public static Row toRow(Row row, int offset) {
        Sheet sheet = row.getSheet();
        return sheet.getRow(row.getRowNum() + offset);
    }
}
