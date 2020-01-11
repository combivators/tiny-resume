package net.tiny.resume;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Section;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import net.tiny.excel.CellRange;
import net.tiny.excel.SheetTool;
import net.tiny.excel.WorkbookTool;

/**
 * 解析原始简历文档(支持Excel，Word，PDF)
 * 输出JSON格式的位置和文本信息
 * <code>
 * {
 *   "contents" : [
 *    {
 *     "p" : [1, 3],
 *     "s" : "氏名"
 *     "a" : "Name:0.95"
 *    },
 *   ]
 * }
 * </code>
 */
public class Parser {

    public static enum Type {
        Excel,
        Word,
        Docx,
        PDF
    }

    public Crude parse(File file) throws IOException {
        final String name = file.getName();
        final String suffix = name.substring(name.lastIndexOf(".") + 1);
        if(suffix == null)
            throw new IllegalArgumentException(String.format("Unknow file '%s' type.", name));
        RuntimeException error = null;
        Type type = null;
        switch(suffix.toLowerCase()) {
        case "xls":
        case "xlsx":
            type = Type.Excel;
            break;
        case "doc":
            type = Type.Word;
            break;
        case "docx":
            type = Type.Docx;
            break;
        case "pdf":
            type = Type.PDF;
            break;
        default:
            error = new IllegalArgumentException(String.format("Unknow file '%s' type.", name));
            break;
        }
        if (error != null)
            throw error;
        Crude crude = null;
        InputStream in = new FileInputStream(file);
        try {
            crude = parse(in, type);
        } finally {
            in.close();
        }
        return crude;
    }

    public Crude parse(InputStream in, Type type) throws IOException {
        Crude crude = null;
        switch(type) {
        case Excel:
            crude = parseExcel(in);
            break;
        case Word:
            crude = parseDoc(in);
            break;
        case Docx:
            crude = parseDocx(in);
            break;
        case PDF:
            crude = parsePDF(in);
            break;
        }
        return crude;
    }

    Crude parseExcel(InputStream in) throws IOException {
        Workbook workbook = WorkbookFactory.create(in);
        final List<Sheet> sheets = WorkbookTool.getAllSheets(workbook);
        final Sheet sheet = sheets.get(0);
        final int row = SheetTool.getLastRow(sheet);
        final int col = SheetTool.getLastColumn(sheet);

        final Crude crude = new Crude();
        final CellRange range = SheetTool.getFullCellRange(sheet);
        String value;
        for (int y=0; y<row; y++) {
            for (int x=0; x<col; x++) {
                value = range.getValue(x, y);
                if( null == value) continue;
                value = value.trim();
                if (value.isEmpty())
                    continue;
                crude.append(x, y, value);
                //System.out.println(String.format("%d:%d: %s", y,x, value));
            }
        }
        workbook.close();
        crude.format = "xls";
        return crude;
    }

    Crude parseDoc(InputStream in) throws IOException {
        POIFSFileSystem fs = new POIFSFileSystem(in);
        HWPFDocument document = new HWPFDocument(fs);

        final Crude crude = new Crude();
        // Read all document data in Word can be acquired in the hierarchy structure of
        // Range> Section> Paragraph> CharacterRun
        final Range text = document.getRange();
        IntStream.range(0, text.numSections()).forEach(sNo -> {
            final Section section = text.getSection(sNo);
            IntStream.range(0, section.numParagraphs()).forEach(pNo -> {
                final Paragraph paragraph = section.getParagraph(pNo);
                IntStream.range(0, paragraph.numCharacterRuns()).forEach(cNo -> {
                    final CharacterRun characterRun = paragraph.getCharacterRun(cNo);
                    String txt = characterRun.text();
                    if (null != txt) {
                        txt = txt.trim();
                        if (!txt.isEmpty()) {
                            crude.append(cNo, pNo, txt);
                        }
                    }
                });
            });
        });

        document.close();
        fs.close();
        crude.format = "doc";
        return crude;

    }

    Crude parseDocx(InputStream in) throws IOException {
        final XWPFDocument doc = new XWPFDocument(in);
        final XWPFWordExtractor ex = new XWPFWordExtractor(doc);
        final Crude crude = paserText(ex.getText());
        ex.close();
        doc.close();
        crude.format = "docx";
        return crude;
    }

    Crude parsePDF(InputStream in) throws IOException {
        final PDDocument document = PDDocument.load(in);
        AccessPermission ap = document.getCurrentAccessPermission();
        if (!ap.canExtractContent()) {
            document.close();
            throw new IOException("Not have permission to extract pdf text.");
        }
        final int end = document.getNumberOfPages();
        final PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(1);
        stripper.setEndPage(end);
        final Crude crude = paserText(stripper.getText(document));
        document.close();
        crude.format = "pdf";
        return crude;
    }

    private Crude paserText(String text) throws IOException {
        final LineNumberReader reader = new LineNumberReader(new StringReader(text));
        final Crude crude = new Crude();
        String line;
        while ( (line = reader.readLine()) != null) {
            line = line.trim();
            if( !line.isEmpty()) {
                crude.append(0, reader.getLineNumber(), line);
            }
        }
        reader.close();
        crude.format = "txt";
        return crude;
    }


    public static Type guestType(String name) {
        Type type = null;
        int pos = name.lastIndexOf(".");
        if (pos > 0) {
            String ext = name.substring(pos+1).toLowerCase();
            switch (ext) {
            case "xls":
            case "xlsx":
                type = Type.Excel;
                break;
            case "doc":
                type = Type.Word;
                break;
            case "docx":
                type = Type.Docx;
                break;
            case "pdf":
                type = Type.PDF;
                break;
            default:
                type = null;
                break;
            }
        }
        return type;
    }
}
