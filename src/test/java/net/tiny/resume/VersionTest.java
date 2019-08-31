package net.tiny.resume;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class VersionTest {

    @Test
    public void testCurrentVersion() throws Exception {
        assertEquals("0.9", Version.CURRENT.ver());
    }

    @Test
    public void testVersionCode() throws Exception {
        Version.Code v09 = Version.Code.VERSION_0_9;
        assertEquals(0,  v09.major());
        assertEquals(9,  v09.minor());
        assertEquals("0.9",  v09.ver());

        Version.Code v091 = Version.Code.VERSION_0_91;
        assertEquals(0,  v091.major());
        assertEquals(91,  v091.minor());
        assertEquals("0.91",  v091.ver());

        Version.Code v10 = Version.Code.VERSION_1_0;
        assertEquals(1,  v10.major());
        assertEquals(0,  v10.minor());
        assertEquals("1.0",  v10.ver());

        Version.Code v11 = Version.Code.VERSION_1_1;
        assertEquals(1,  v11.major());
        assertEquals(1,  v11.minor());
        assertEquals("1.1",  v11.ver());
    }
}
