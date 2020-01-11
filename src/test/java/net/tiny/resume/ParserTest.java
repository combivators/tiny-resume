package net.tiny.resume;

import java.io.File;
import java.util.logging.LogManager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.tiny.config.JsonParser;

public class ParserTest {

    @BeforeAll
    public static void beforeAll() throws Exception {
        LogManager.getLogManager().readConfiguration(
                Thread.currentThread().getContextClassLoader().getResourceAsStream("logging.properties"));
    }


    @Test
    public void testParseExcelResume() throws Exception {
        File file = new File("src/test/resources/samples/resume-excel.xls");
        Parser parser = new Parser();
        Crude crude = parser.parse(file);
        String json = JsonParser.marshal(crude);
        System.out.print(json);
    }


    @Test
    public void testParseWordResume() throws Exception {
        File file = new File("src/test/resources/samples/resume-word.doc");
        Parser parser = new Parser();
        Crude crude = parser.parse(file);
        String json = JsonParser.marshal(crude);
        System.out.print(json);
    }

    @Test
    public void testParseWordSkill() throws Exception {
        File file = new File("src/test/resources/samples/skill-word.doc");
        Parser parser = new Parser();
        Crude crude = parser.parse(file);
        String json = JsonParser.marshal(crude);
        System.out.print(json);
    }

    @Test
    public void testParseDocxResume() throws Exception {
        File file = new File("src/test/resources/samples/disruptor.docx");
        Parser parser = new Parser();
        Crude crude = parser.parse(file);
        String json = JsonParser.marshal(crude);
        System.out.print(json);
    }

    @Test
    public void testParsePDFResume() throws Exception {
        File file = new File("src/test/resources/samples/assess-sample.pdf");
        Parser parser = new Parser();
        Crude crude = parser.parse(file);
        String json = JsonParser.marshal(crude);
        System.out.print(json);
    }
}
