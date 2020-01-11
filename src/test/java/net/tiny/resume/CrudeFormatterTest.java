package net.tiny.resume;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.Character.UnicodeBlock;
import java.util.ArrayList;
import java.util.List;


import org.junit.jupiter.api.Test;

import net.tiny.config.JsonParser;

public class CrudeFormatterTest {

    @Test
    public void testMergeWord() throws Exception {
        List<Crude.Content> contents = new ArrayList<>();
        contents.add(new Crude.Content(0, 0, "a"));
        contents.add(new Crude.Content(1, 0, "b"));
        contents.add(new Crude.Content(1, 1, "c"));
        contents.add(new Crude.Content(3, 1, "d"));
        contents.add(new Crude.Content(2, 3, "e"));
        contents.add(new Crude.Content(1, 4, "f"));
        contents.add(new Crude.Content(3, 4, "g"));
        contents.add(new Crude.Content(4, 4, "h"));
        contents.add(new Crude.Content(5, 4, "i"));
        List<Crude.Content> formatted = CrudeFormatter.mergeWord(contents);
        assertEquals("[0:0 ab, 1:1 c, 3:1 d, 2:3 e, 1:4 f, 3:4 ghi]", formatted.toString());
    }

    @Test
    public void testFormatWords() throws Exception {
        Reader reader = new FileReader(new File("src/test/resources/json/skill-word.json"));
        Crude crude = JsonParser.unmarshal(reader, Crude.class);
        reader.close();
        assertNotNull(crude);
        assertEquals("鹿", crude.contents.get(8).t);
        assertEquals("　　", crude.contents.get(9).t);
        assertEquals("子孝", crude.contents.get(10).t);
        Crude formatted = CrudeFormatter.format(crude);
        assertEquals("　", formatted.contents.get(6).t);
        assertEquals("鹿　　子孝", formatted.contents.get(7).t);

        formatted = CrudeFormatter.clean(formatted);
        assertEquals("鹿子孝", formatted.contents.get(6).t);
        System.out.println(formatted.toString());

        String json = JsonParser.marshal(formatted);
        System.out.print(json);
     }

    @Test
    public void testUnicodeBlock() throws Exception {
        String symblos = " 、。～（）⑦○◎●▲△☓✗■◆□◇";
        char[] array = symblos.toCharArray();
        for (char c : array) {
            System.out.println(c + " UnicodeBlock " + UnicodeBlock.of( c ));
        }
        assertEquals(UnicodeBlock.of( '。' ),  UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION);
        assertEquals(UnicodeBlock.of( '、' ),  UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION);
        assertEquals(UnicodeBlock.of( '～' ),  UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS);
        assertEquals(UnicodeBlock.of( '⑦' ),  UnicodeBlock.ENCLOSED_ALPHANUMERICS);
        assertEquals(UnicodeBlock.of( '○' ),  UnicodeBlock.GEOMETRIC_SHAPES);
        assertEquals(UnicodeBlock.of( '◎' ),  UnicodeBlock.GEOMETRIC_SHAPES);
        assertEquals(UnicodeBlock.of( '●' ),  UnicodeBlock.GEOMETRIC_SHAPES);
        assertEquals(UnicodeBlock.of( '▲' ),  UnicodeBlock.GEOMETRIC_SHAPES);
        assertEquals(UnicodeBlock.of( '◆' ),  UnicodeBlock.GEOMETRIC_SHAPES);
        assertEquals(UnicodeBlock.of( '△' ),  UnicodeBlock.GEOMETRIC_SHAPES);
        assertEquals(UnicodeBlock.of( '□' ),  UnicodeBlock.GEOMETRIC_SHAPES);
        assertEquals(UnicodeBlock.of( '◇' ),  UnicodeBlock.GEOMETRIC_SHAPES);
        assertEquals(UnicodeBlock.of( '~' ),  UnicodeBlock.BASIC_LATIN);
        assertEquals(UnicodeBlock.of( ',' ),  UnicodeBlock.BASIC_LATIN);
        assertEquals(UnicodeBlock.of( '+' ),  UnicodeBlock.BASIC_LATIN);
        assertEquals(UnicodeBlock.of( '*' ),  UnicodeBlock.BASIC_LATIN);
        assertEquals(UnicodeBlock.of( '(' ),  UnicodeBlock.BASIC_LATIN);
    }


    @Test
    public void testSymbolMean() throws Exception {
        Crude crude = new Crude();
        crude.language = "ja";
        crude.format = "xls";
        List<Crude.Content> contents = new ArrayList<>();

        contents.add(new Crude.Content(0, 0, "Title1"));
        contents.add(new Crude.Content(2, 0, "Title2"));
        contents.add(new Crude.Content(4, 0, "Title3"));
        contents.add(new Crude.Content(6, 0, "Title4"));
        contents.add(new Crude.Content(8, 0, "Title5"));

        contents.add(new Crude.Content(0, 1, "Plane"));
        contents.add(new Crude.Content(2, 1, "Design"));
        contents.add(new Crude.Content(4, 1, "Development"));
        contents.add(new Crude.Content(6, 1, "Test"));
        contents.add(new Crude.Content(8, 1, "Maintenance"));
        contents.add(new Crude.Content(0, 3, ""));
        contents.add(new Crude.Content(2, 3, "○"));
        contents.add(new Crude.Content(4, 3, "○"));
        contents.add(new Crude.Content(6, 3, ""));
        contents.add(new Crude.Content(8, 3, ""));

        contents.add(new Crude.Content(0, 5, ""));
        contents.add(new Crude.Content(2, 5, ""));
        contents.add(new Crude.Content(4, 5, "○"));
        contents.add(new Crude.Content(6, 5, "○"));
        contents.add(new Crude.Content(8, 5, ""));

        contents.add(new Crude.Content(0, 7, ""));
        contents.add(new Crude.Content(2, 7, ""));
        contents.add(new Crude.Content(4, 7, ""));
        contents.add(new Crude.Content(6, 7, "◎"));
        contents.add(new Crude.Content(8, 7, "◎"));

        contents.add(new Crude.Content(0, 9, "●"));
        contents.add(new Crude.Content(2, 9, ""));
        contents.add(new Crude.Content(4, 9, ""));
        contents.add(new Crude.Content(6, 9, ""));
        contents.add(new Crude.Content(8, 9, ""));

        crude.contents = contents;

        Crude formatted = CrudeFormatter.clean(crude);
        Crude.Content c = formatted.contents.get(15);
        assertTrue(Crude.isSymbol(c.t));
        assertNull(c.k);
        formatted = CrudeFormatter.meanings(crude);
        c = formatted.contents.get(15);
        assertEquals("Symbol:Maintenance", c.a);

        String json = JsonParser.marshal(formatted);
        System.out.print(json);
    }
}
