package net.tiny.excel;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import net.tiny.excel.DataType;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


public class DataTypeTest {


    public static enum TestType {
        index,
        productList,
        productSearch
    }

    @Test
    public void testGetDataType() throws Exception {
        DataType dataType = DataType.getDataType("abc");
        assertNotNull(dataType);
        assertTrue(DataType.STRING.equals(dataType));

        dataType = DataType.getDataType(123.456f);
        assertNotNull(dataType);
        assertTrue(DataType.FLOAT.equals(dataType));

        dataType = DataType.getDataType("abc".getBytes());
        assertNotNull(dataType);
        assertTrue(DataType.BYTE_ARRAY.equals(dataType));

        dataType = DataType.getDataType(new Date());
        assertNotNull(dataType);
        assertTrue(DataType.DATE.equals(dataType));

        dataType = DataType.getDataType(new Timestamp(new Date().getTime()));
        assertNotNull(dataType);
        assertTrue(DataType.TIMESTAMP.equals(dataType));

        dataType = DataType.getDataType(Calendar.getInstance());
        assertNotNull(dataType);
        assertTrue(DataType.CALENDAR.equals(dataType));

        dataType = DataType.getDataType(TestType.productList);
        assertNotNull(dataType);
        assertTrue(DataType.ENUM.equals(dataType));

        dataType = DataType.getDataType(new TestEntity("t1","t2"));
        assertNull(dataType);
    }

    @Test
    public void testIsValidType() throws Exception {
        assertTrue(DataType.isValidType("abcd"));
        assertTrue(DataType.isValidType("abcd".getBytes()));
        assertTrue(DataType.isValidType(1234));
        assertTrue(DataType.isValidType(123.456f));
        assertTrue(DataType.isValidType(new Date()));
        assertTrue(DataType.isValidType(new Timestamp(new Date().getTime())));
        assertTrue(DataType.isValidType(Calendar.getInstance()));
        assertTrue(DataType.isValidType(TestType.productList));
    }

    @Test
    public void testGetByName() throws Exception {
        assertEquals(DataType.STRING,  DataType.getByName("string"));
        assertEquals(DataType.STRING,  DataType.valueOf("STRING"));
        assertEquals(DataType.BOOLEAN,  DataType.getByName("boolean"));
        assertEquals(DataType.INTEGER,  DataType.getByName("int"));
        assertEquals(DataType.LONG,  DataType.getByName("long"));
        assertEquals(DataType.FLOAT,  DataType.getByName("float"));
        assertEquals(DataType.DOUBLE,  DataType.getByName("double"));
        assertEquals(DataType.BYTE_ARRAY,  DataType.getByName("byte-array"));
        assertEquals(DataType.STRING_ARRAY,  DataType.getByName("array"));
        assertEquals(DataType.LIST,  DataType.getByName("list"));
        assertEquals(DataType.PROPERTIES,  DataType.getByName("properties"));
        assertEquals(DataType.CDATA,  DataType.getByName("cdata"));
        assertEquals(DataType.XML_DATA,  DataType.getByName("xml"));
        assertEquals(DataType.CALENDAR, DataType.getByName("calendar"));
        assertEquals(DataType.ENUM, DataType.getByName("enum"));
    }

    @Test
    public void testGetByClass() throws Exception {
        assertEquals(DataType.STRING,  DataType.getByClass(String.class));
        assertEquals(DataType.BOOLEAN,  DataType.getByClass(Boolean.class));
        assertEquals(DataType.BOOLEAN,  DataType.getByClass(boolean.class));
        assertEquals(DataType.INTEGER,  DataType.getByClass(int.class));
        assertEquals(DataType.INTEGER,  DataType.getByClass(Integer.class));
        assertEquals(DataType.LONG,  DataType.getByClass(long.class));
        assertEquals(DataType.FLOAT,  DataType.getByClass(float.class));
        assertEquals(DataType.DOUBLE,  DataType.getByClass(double.class));
        assertEquals(DataType.DATE,  DataType.getByClass(java.util.Date.class));
        assertEquals(DataType.CALENDAR,  DataType.getByClass(java.util.Calendar.class));
        assertEquals(DataType.CALENDAR,  DataType.getByClass(java.util.GregorianCalendar.class));
        assertEquals(DataType.DATETIME,  DataType.getByClass(java.sql.Date.class));
        assertEquals(DataType.BYTE_ARRAY,  DataType.getByClass(byte[].class));
        assertEquals(DataType.STRING_ARRAY,  DataType.getByClass(String[].class));
        assertEquals(DataType.LIST,  DataType.getByClass(List.class));
        assertEquals(DataType.LIST,  DataType.getByClass(ArrayList.class));
        assertEquals(DataType.LIST,  DataType.getByClass(LinkedList.class));
        assertEquals(DataType.PROPERTIES,  DataType.getByClass(Properties.class));
        assertEquals(DataType.ENUM,  DataType.getByClass(TestType.class));
        assertEquals(DataType.MAP,  DataType.getByClass(HashMap.class));
        assertEquals(DataType.XML_DATA,  DataType.getByClass(SampleEntity.class));
        assertNull(DataType.getByClass(TestEntity.class));
    }

    @Test
    public void testGuessType() throws Exception {
        assertEquals(DataType.CDATA,  DataType.guessTypeByValue("<![CDATA[rO0ABX===]]>"));
    }


    public static class TestEntity {
        private static AtomicLong sequenceNumber;

        static long getNewId() {
            synchronized (TestEntity.class) {
                if(sequenceNumber == null) {
                    sequenceNumber = new AtomicLong(1L);
                }
            }
            return sequenceNumber.getAndIncrement();
        }


        private long storageId;
        private String test1;
        private String test2;
        private Date test3;

        public TestEntity() {
            this.storageId = getNewId();
        }

        public TestEntity(String test1, String test2) {
            this.storageId = getNewId();
            this.test1 = test1;
            this.test2 = test2;
            this.test3 = new Date();
        }

        public long getStorageId() {
            return storageId;
        }

        public void setStorageId(long id) {
            this.storageId = id;
        }

        public String getTest1() {
            return test1;
        }

        public void setTest1(String test1) {
            this.test1 = test1;
        }

        public String getTest2() {
            return test2;
        }

        public void setTest2(String test2) {
            this.test2 = test2;
        }

        public Date getTest3() {
            return test3;
        }

        public void setTest3(Date test3) {
            this.test3 = test3;
        }

        @Override
        public String toString() {
            final SimpleDateFormat FORMAT = new SimpleDateFormat(
                    "yyyy.MM.dd HH:mm:ss");
            return getClass().getSimpleName() + " [storageId=" + storageId
                    + ", test1="
                    + test1 + ", test2=" + test2 + ", test3=" + FORMAT.format(test3) + "]";
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name="entity")
    public static class SampleEntity {


        @XmlElement(name = "prop")
        private Properties prop;


        public void setProp(Properties prop) {
            this.prop = prop;
        }

        public Properties getProp() {
            return this.prop;
        }
    }

}
