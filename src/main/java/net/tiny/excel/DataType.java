package net.tiny.excel;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Mapping from java data type.
 */
public enum DataType {

	@XmlEnumValue("boolean")
	BOOLEAN("boolean", boolean.class),

	@XmlEnumValue("int")
    INTEGER("int", int.class),

	@XmlEnumValue("short")
    SHORT("short", int.class),

    @XmlEnumValue("long")
    LONG("long", long.class),

    @XmlEnumValue("float")
    FLOAT("float", float.class),

    @XmlEnumValue("double")
    DOUBLE("double", double.class),

    @XmlEnumValue("number")
    NUMBER("number", double.class),

    @XmlEnumValue("decimal")
    DECIMAL("decimal", BigDecimal.class),

    @XmlEnumValue("bigint")
    BIGINTEGER("bigint", BigInteger.class),

    @XmlEnumValue("byte")
    BYTE("byte", byte.class),

    @XmlEnumValue("char")
    CHAR("char", char.class),

    @XmlEnumValue("string")
    STRING("string", String.class),

    @XmlEnumValue("enum")
    ENUM("enum", Enum.class),

    @XmlEnumValue("date")
    DATE("date", java.util.Date.class),

    @XmlEnumValue("datetime")
    DATETIME("datetime", java.sql.Date.class),

    @XmlEnumValue("timestamp")
    TIMESTAMP("time", Timestamp.class),

    @XmlEnumValue("calendar")
    CALENDAR("calendar", Calendar.class),

    @XmlEnumValue("ldate")
    LOCALDATE("ldate", java.time.LocalDate.class),

    @XmlEnumValue("ltime")
    LOCALDATETIME("ltime", java.time.LocalDateTime.class),

    @XmlEnumValue("list")
    LIST("list", List.class),

    @XmlEnumValue("map")
    MAP("map", Map.class),

    @XmlEnumValue("properties")
    PROPERTIES("properties", Properties.class),

    @XmlEnumValue("byte-array")
	BYTE_ARRAY("byte-array", byte[].class),

    @XmlEnumValue("char-array")
	CHAR_ARRAY("char-array", char[].class),

    @XmlEnumValue("boolean-array")
	BOOLEAN_ARRAY("boolean-array", boolean[].class),

    @XmlEnumValue("integer-array")
	INTEGER_ARRAY("integer-array", int[].class),

    @XmlEnumValue("long-array")
	LONG_ARRAY("long-array", long[].class),

    @XmlEnumValue("float-array")
	FLOAT_ARRAY("float-array", float[].class),

    @XmlEnumValue("double-array")
	DOUBLE_ARRAY("double-array", double[].class),

	@XmlEnumValue("array")
	STRING_ARRAY("array", String[].class),

	@XmlEnumValue("cdata")
	CDATA("cdata", Serializable.class),

	@XmlEnumValue("xml")
    XML_DATA("xml", XmlData.class);

	//@XmlEnumValue("bean")
	//BEAN("bean", Bean.class);

	private static Map<String, DataType> TYPE_MAP = new HashMap<String, DataType>();
	private static Map<Class<?>, DataType> CLASS_MAP = new HashMap<Class<?>, DataType>();
	static {
        for (DataType b : DataType.values()) {
            // Lowercase descriptor name!
            if (TYPE_MAP.containsKey(b.getName().toLowerCase()))
                continue; // Ignore double-declarations, take first one only
            TYPE_MAP.put(b.getName(), b);
        }
        CLASS_MAP.put(boolean.class, BOOLEAN);
        CLASS_MAP.put(Boolean.class, BOOLEAN);
        CLASS_MAP.put(byte.class, BYTE);
        CLASS_MAP.put(Byte.class, BYTE);
        CLASS_MAP.put(char.class, CHAR);
        CLASS_MAP.put(Character.class, CHAR);
        CLASS_MAP.put(int.class, INTEGER);
        CLASS_MAP.put(Integer.class, INTEGER);
        CLASS_MAP.put(short.class, SHORT);
        CLASS_MAP.put(Short.class, SHORT);
        CLASS_MAP.put(long.class, LONG);
        CLASS_MAP.put(Long.class, LONG);
        CLASS_MAP.put(float.class, FLOAT);
        CLASS_MAP.put(Float.class, FLOAT);
        CLASS_MAP.put(double.class, DOUBLE);
        CLASS_MAP.put(Double.class, DOUBLE);
        CLASS_MAP.put(BigDecimal.class, DECIMAL);
        CLASS_MAP.put(BigInteger.class, BIGINTEGER);
        CLASS_MAP.put(String.class, STRING);
        CLASS_MAP.put(Enum.class, ENUM);
        CLASS_MAP.put(java.util.Date.class, DATE);
        CLASS_MAP.put(java.sql.Date.class, DATETIME);
        CLASS_MAP.put(Timestamp.class, TIMESTAMP);
        CLASS_MAP.put(Calendar.class, CALENDAR);
        CLASS_MAP.put(GregorianCalendar.class, CALENDAR);
        CLASS_MAP.put(java.time.LocalDate.class, LOCALDATE);
        CLASS_MAP.put(java.time.LocalDateTime.class, LOCALDATETIME);
        CLASS_MAP.put(List.class, LIST);
        CLASS_MAP.put(ArrayList.class, LIST);
        CLASS_MAP.put(LinkedList.class, LIST);
        CLASS_MAP.put(Properties.class, PROPERTIES);
        CLASS_MAP.put(Map.class, MAP);
        CLASS_MAP.put(HashMap.class, MAP);
        CLASS_MAP.put(TreeMap.class, MAP);
        CLASS_MAP.put(LinkedHashMap.class, MAP);
        CLASS_MAP.put(byte[].class, BYTE_ARRAY);
        CLASS_MAP.put(char[].class, CHAR_ARRAY);
        CLASS_MAP.put(boolean[].class, BOOLEAN_ARRAY);
        CLASS_MAP.put(int[].class, INTEGER_ARRAY);
        CLASS_MAP.put(long[].class, LONG_ARRAY);
        CLASS_MAP.put(float[].class, FLOAT_ARRAY);
        CLASS_MAP.put(double[].class, DOUBLE_ARRAY);
        CLASS_MAP.put(String[].class, STRING_ARRAY);
        CLASS_MAP.put(Serializable.class, CDATA);
        CLASS_MAP.put(XmlData.class, XML_DATA);
        //CLASS_MAP.put(Bean.class, BEAN);
	}

    private final String name;
    private final Class<?> datatype;

    DataType(String name, Class<?> datatype) {
        this.name = name;
        this.datatype = datatype;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the Java class associated with the data type of this state variable.
     * Mapping between the data types and Java classes is performed according to the schema mentioned above.
     * @return the Java class
     */
    public Class<?> getJavaDatatype() {
        return datatype;
    }

    public static DataType getByName(String name) {
        if (name == null) return null;
        return TYPE_MAP.get(name.toLowerCase());
    }

    public static DataType getByClass(Class<?> classType) {
		if (CLASS_MAP.containsKey(classType)) {
			return CLASS_MAP.get(classType);
		} else {
	    	if(classType.isArray()) {
	    		return getByClass(classType.getComponentType());
	    	} else if(classType.isEnum()) {
	    		return DataType.ENUM;
	    	} else {
	    		// Check XmlRootElement Annotation class.
	    		if(null != classType.getAnnotation(XmlRootElement.class)) {
    				return DataType.XML_DATA;
	    		}
	    		// Check List interface class.
	    		if(hasInterface(classType, List.class)) {
	    			return DataType.LIST;
	    		}
	    		// Check Serializable interface class.
	    		if(hasInterface(classType, Serializable.class)) {
	    			return DataType.CDATA;
	    		}
	    		//return DataType.BEAN;
	    		return null;
	    	}
		}
    }

    private static boolean hasInterface(Class<?> classType, Class<?> interfaceClass) {
    	if(null == classType)
    		return false;
		Class<?>[] ifs = classType.getInterfaces();
		for(Class<?> c : ifs) {
			if(interfaceClass.equals(c)) {
				return true;
			}
		}
		return hasInterface(classType.getSuperclass(), interfaceClass);
    }

    /**
     * Returns the type of the given Object
     * @param value the Object
     * @return the type String or <code>null</code> if the given object is null
     */
    public static DataType getDataType(Object value) {
        if (value == null) {
            return null;
        }
        return getByClass(value.getClass());
    }

    /**
     * Tests if the given value is a valid type for conversions
     * @param value the object to be tested
     * @return true if the object is defined in the jms conversion table otherwise false
     * @throws NullPointerException if the given value is null.
     */
    public static boolean isValidType(Object value) {
        if (value == null) {
            throw new NullPointerException("null is not allowed.");
        }
        return isValidType(value.getClass());
    }

    public static boolean isValidType(Class<?> type) {
        if (type == null) {
            throw new NullPointerException("null is not allowed.");
        }
        return getByClass(type) != null;
    }

    public static boolean isNumeric(DataType builtin) {
        return builtin != null &&
                (builtin.equals(INTEGER) ||
                builtin.equals(SHORT) ||
                builtin.equals(LONG) ||
                builtin.equals(FLOAT) ||
                builtin.equals(DOUBLE) ||
                builtin.equals(DECIMAL) ||
                builtin.equals(BIGINTEGER) );
    }

    public static DataType guessTypeByValue(String value) {
    	if(value.startsWith("<![CDATA[") && value.endsWith("]]>")) {
    		return DataType.CDATA; // TODO
    	}
    	return null;
    }

    //
    private static class XmlData {
    }
}
