package net.tiny.excel;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public final class WorkbookTool {

    private WorkbookTool() {}

    public static List<Sheet> getAllSheets(final Workbook workbook) {
        List<Sheet> sheets = new ArrayList<>();
        int n = 0;
        boolean eol = false;
        while (!eol) {
            try {
                final String name = workbook.getSheetName(n++);
                Sheet sheet = workbook.getSheet(name);
                sheets.add(sheet);
            } catch(IllegalArgumentException e) {
                eol = true;
            }
        }
        return sheets;
    }

}
