package net.tiny.excel;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import net.tiny.excel.CellRange;

import java.io.File;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class CellRangeTest {

    @Test
    public void testGetCellRange() throws Exception {
        File book = new File("src/test/resources/poi/DIFFERENCE.xls");
        Workbook workbook = WorkbookFactory.create(book);
        Sheet sheet = workbook.getSheet("DIFFERENT");
        assertNotNull(sheet);

        int last = sheet.getLastRowNum();
        assertEquals(13, last);
        CellRange range = new CellRange(sheet, 3, 12, 0, 1);
        assertNotNull(range);
        String[] data = range.toArray();
        assertEquals("カラム名(日本語)", data[0]);
        assertEquals("カラム名(日本語) - 年月 - 3", range.toString());

    }
}
