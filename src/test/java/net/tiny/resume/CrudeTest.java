package net.tiny.resume;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import net.tiny.config.JsonParser;
import net.tiny.nlp.open.OpenNLP;

public class CrudeTest {

    @Test
    public void testCrudeSorted() throws Exception {
        TreeSet<Crude.Content> set = new TreeSet<>();
        set.add(new Crude.Content(0, 0, "a"));
        set.add(new Crude.Content(1, 0, "b"));
        set.add(new Crude.Content(1, 1, "c"));
        set.add(new Crude.Content(3, 1, "d"));
        set.add(new Crude.Content(2, 3, "e"));
        set.add(new Crude.Content(1, 4, "f"));
        set.add(new Crude.Content(1, 2, "g"));
        set.add(new Crude.Content(2, 1, "h"));
        assertEquals("[0:0 a, 1:0 b, 1:1 c, 2:1 h, 3:1 d, 1:2 g, 2:3 e, 1:4 f]", set.toString());

        Crude crude = new Crude();
        crude.append(0, 0, "a");
        crude.append(1, 0, "b");
        crude.append(1, 1, "c");
        crude.append(3, 1, "d");
        crude.append(2, 3, "e");
        crude.append(1, 4, "f");
        crude.append(1, 2, "g");
        crude.append(2, 1, "h");
        String json = JsonParser.marshal(crude);
        System.out.print(json);
    }

    @Test
    public void testToLongText() throws Exception {
        assertEquals("ab cde 123 xyz XWZ #$!-= 987", Crude.toLongText("ab\tcde\n123 \r\nxyz\tXWZ    #$!-=\r987"));
    }

    @Test
    public void testCrudeWord() throws Exception {
        Reader reader = new FileReader(new File("src/test/resources/json/skill-word.json"));
        Crude crude = JsonParser.unmarshal(reader, Crude.class);
        reader.close();
        assertNotNull(crude);

        String lang = OpenNLP.guestLanguage(crude.toString());
        assertEquals("Japanese", lang);
        System.out.println(crude.toString());
    }

    @Test
    public void testCrudeExcel() throws Exception {
        Reader reader = new FileReader(new File("src/test/resources/json/resume-excel.json"));
        Crude crude = JsonParser.unmarshal(reader, Crude.class);
        reader.close();
        assertNotNull(crude);

        String lang = OpenNLP.guestLanguage(crude.toString());
        assertEquals("Japanese", lang);

        System.out.println(crude.toString());
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

        for (int i=0; i<crude.contents.size(); i++) {
            Crude.Content c = crude.contents.get(i);
            if (Crude.isSymbol(c.t)) {
                System.out.println(String.format("%d : %s", i, c.t));
            }
        }
        Crude.Content c = crude.contents.get(23);
        assertTrue(Crude.isSymbol(c.t));
        String m = crude.meaning(c);
        assertNotNull(m);
        assertEquals("Test", m);
        assertEquals("Plane", crude.meaning(crude.contents.get(25)));

        assertEquals("Title5", crude.meaning(crude.contents.get(4)));
        assertEquals("Development", crude.meaning(crude.contents.get(7)));
    }
}
