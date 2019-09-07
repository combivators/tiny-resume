package net.tiny.resume;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Section;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import net.tiny.excel.CellRange;
import net.tiny.excel.SheetTool;
import net.tiny.excel.WorkbookTool;

/**
 * @see https://resume.io/create-resume?template=london
 * @see https://www.resumeviking.com/templates/
 */
public class DocumentReaderTest {

    @Test
    public void testExcelResume() throws Exception {
        File book = new File("src/test/resources/samples/resume-excel.xls");
        Workbook workbook = WorkbookFactory.create(book);
        List<Sheet> sheets = WorkbookTool.getAllSheets(workbook);
        assertEquals(1, sheets.size());
        Sheet sheet = sheets.get(0);
        int row = SheetTool.getLastRow(sheet);
        int col = SheetTool.getLastColumn(sheet);
        assertEquals(74, row);
        assertEquals(33, col);

        CellRange range = SheetTool.getFullCellRange(sheet);
        assertNotNull(range);
        assertEquals(34, range.getWidth());
        assertEquals(75, range.getHeight());

        String[] data = range.toArray();
        for (String d : data) {
            if (d.trim().isEmpty())
                continue;
            System.out.println(d);
        }
    }

    @Test
    public void testWordResume() throws Exception {
        File file = new File("src/test/resources/samples/resume-word.doc");

        POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
        HWPFDocument document = new HWPFDocument(fs);

        // Read all document data in Word can be acquired in the hierarchy structure of
        // Range> Section> Paragraph> CharacterRun
        Range text = document.getRange();
        IntStream.range(0, text.numSections()).forEach(sNo -> {
            Section section = text.getSection(sNo);
            IntStream.range(0, section.numParagraphs()).forEach(pNo -> {
                Paragraph paragraph = section.getParagraph(pNo);
                IntStream.range(0, paragraph.numCharacterRuns()).forEach(cNo -> {
                    CharacterRun characterRun = paragraph.getCharacterRun(cNo);
                    final String d = characterRun.text();
                    if (!d.trim().isEmpty()) {
                        System.out.printf("%d:%d:%d: %s", sNo, pNo, cNo, characterRun.text());
                        System.out.println();
                    }
                });
            });
        });

        document.close();
        fs.close();
    }


    @Test
    public void testWordExtractor() throws Exception {
        File file = new File("src/test/resources/samples/skill-word.doc");

        POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
        HWPFDocument document = new HWPFDocument(fs);
        WordExtractor extractor = new WordExtractor(document);
        String[] fileData = extractor.getParagraphText();
        for (int i = 0; i < fileData.length; i++) {
            if (fileData[i] != null)
                System.out.println(fileData[i]);
        }

        extractor.close();
        document.close();
        fs.close();
    }

    @Test
    public void testWordDocx() throws Exception {
        File file = new File("src/test/resources/samples/disruptor.docx");

        XWPFDocument doc = new XWPFDocument (new FileInputStream(file));
        XWPFWordExtractor ex = new XWPFWordExtractor(doc);
        String tx = ex.getText();
        System.out.println(tx);
        ex.close();
        doc.close();
    }

    @Test
    public void testDocx() throws Exception {
        File file = new File("src/test/resources/samples/disruptor.docx");
        XWPFDocument document = new XWPFDocument (new FileInputStream(file));
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (XWPFParagraph para : paragraphs) {
            System.out.println(para.getText());
        }

        document.close();
    }

    @Test
    public void testReadPDF() throws Exception {
        File file = new File("src/test/resources/samples/assess-sample.pdf");
        PDDocument document = PDDocument.load(file);
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(1);
        stripper.setEndPage(5);
        String text = stripper.getText(document);
        System.out.println(text);
        document.close();
    }

}
