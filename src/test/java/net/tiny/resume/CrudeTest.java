package net.tiny.resume;

import static org.junit.jupiter.api.Assertions.*;

import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import net.tiny.config.JsonParser;

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
        Crude crude = new Crude();
        assertEquals("ab cde 123 xyz XWZ #$!-= 987", crude.toLongText("ab\tcde\n123 \r\nxyz\tXWZ    #$!-=\r987"));
    }

}
