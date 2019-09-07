package net.tiny.excel;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.Format;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class TemplateSheet {

	private static Logger LOGGER = Logger.getLogger(TemplateSheet.class.getName());
	private static String INDEX_KEY = "#index";

	protected final Workbook workbook;
	protected final Sheet sheet;
	protected final String sheetName;
	protected final int sheetIndex;
	protected LinkedList<String> workNames = new LinkedList<>();
	protected Sheet workSheet;
	private CellRange body;

	private Map<String, Format> formats = new HashMap<String, Format>();
	private ParameterReference reference;
	private Properties headerProperties =  new Properties();
	private Properties footerProperties  =  new Properties();
	private Hashtable<String, List<String>> bodies =  new Hashtable<String, List<String>>();
	private String workSheetName;
	private short bodyRowHeight = 10;
	private int bodySize = 0;
	private int bodyRow;
	private int bodyCol;
	private int workSheetNum;
	private boolean finish = false;
	private boolean debug = false;

	static class DefaultListener implements ParameterReference.Listener {
		@Override
		public void macro(String msg) {
			LOGGER.info(msg);
		}

		@Override
		public void define(String msg) {
			LOGGER.info(msg);
		}
	}

	public TemplateSheet(String bookName, String sheetName, int bodyRow, int bodyCol) throws Exception {
		this.sheetName = sheetName;
		this.bodyRow = bodyRow;
		this.bodyCol = bodyCol;
		URL url;
		File file = new File(bookName);
		if(file.exists()) {
			url = file.toURI().toURL();
		} else {
			url = getClass().getResource(bookName);
		}
		if(url == null) {
			throw new FileNotFoundException(bookName );
		}
		InputStream stream = url.openStream();
		this.workbook = WorkbookFactory.create(stream);
		stream.close();
		this.sheetIndex = getSheetIndex(this.sheetName);
		this.sheet = this.workbook.getSheetAt(this.sheetIndex);

		prepareWorkSheet();
	}

	public TemplateSheet(InputStream stream, String sheetName, int bodyRow, int bodyCol) throws Exception {
		this.sheetName = sheetName;
		this.bodyRow = bodyRow;
		this.bodyCol = bodyCol;
		this.workbook = WorkbookFactory.create(stream);
		stream.close();
		this.sheetIndex = getSheetIndex(this.sheetName);
		this.sheet = this.workbook.getSheetAt(this.sheetIndex);

		prepareWorkSheet();
	}

	public String getTempSheetName() {
		return this.sheetName;
	}

	public Sheet getTempSheet() {
		return this.sheet;
	}


    public void setHeaders(Properties  properties) {
    	this.finish = false;
		this.headerProperties.putAll(properties);
	}
	public void setHeader(String name, Object value) {
		this.finish = false;
		this.headerProperties.put(name, value);
	}

	public void setFooter(String name, Object value) {
		this.finish = false;
		this.footerProperties.put(name, value);
	}
    public void setFooters(Properties  properties) {
    	this.finish = false;
		this.footerProperties.putAll(properties);
	}

	public  void setBodies(String name, Collection<?> list, Class<?> type) {
		this.finish = false;
		List<String> rowKeys = this.reference.getBodyKeys(name);
		if(rowKeys.isEmpty()) {
			throw new IllegalArgumentException("Not found prefix '" + name + ".' argument");
		}
		int prefixSize = name.length() + 1;
		int index = 1;
		for(Object bean : list) {
			Map<String, Object> attributes = getAttributes(bean, type);
			for(String key : rowKeys) {
				String propertyName =  key.substring(prefixSize);
				String value = getPropertyValue(name, propertyName, attributes);
				setBody(key, value);
			}
			setBody(INDEX_KEY, String.valueOf(index++));
		}
		this.bodySize = list.size();
	}

	<T> String getPropertyValue(String prefix, String propertyName,  Map<String, Object> attributes) {
		String ret = "";
		String key = propertyName;
		int pos  = propertyName.indexOf(".");
		if(pos > 0) {
			key = propertyName.substring(0, pos);
		}
		Object value = attributes.get(key);
		if(null != value) {
			//TODO
			String name = prefix + "." + propertyName;
			/*
			String expression = this.reference.getExpression(name);
			if(null != expression) {
				//TODO
				String expName = this.reference.getRelationKey(name);

				BigDecimal dec = (BigDecimal)headerProperties.get(expName);
				if(null == dec) {
					dec = new BigDecimal(value.toString());
					headerProperties.put(expName, dec);
				} else {
					//TODO
					dec = dec.add((BigDecimal)value);
					headerProperties.put(expName, dec);
				}
				System.out.println(expName + " ## " + expression + " " + dec.toString());
			}
			*/

			Format format = this.formats.get(name);
			if(null != format) {
				ret = format.format(value);
			} else {
				ret = String.valueOf(value);
			}
		}
		return ret;
	}

	private Map<String, Object> getAttributes(Object target, Class<?>classType) {
    	try {
    		Map<String, Object> attributes = new LinkedHashMap<String, Object>();
			BeanInfo javaBean = Introspector.getBeanInfo(classType);
			PropertyDescriptor[] descriptors = javaBean.getPropertyDescriptors();
			for(PropertyDescriptor property : descriptors) {
				Method getter = property.getReadMethod();
				Method setter = property.getWriteMethod();
				if (null == getter || null == setter)
					continue;
				Class<?> type = getter.getReturnType();
				Object value = getter.invoke(target);
				if(DataType.isValidType(type) || type.isEnum()) {
					if (value != null) {
						attributes.put(property.getName(), value);
					} else {
						attributes.put(property.getName(), null);
					}
				}
			}
			return attributes;
    	} catch (RuntimeException ex) {
    		throw ex;
    	} catch (Exception ex) {
			throw new IllegalArgumentException("Can not support property class type '"
					+ classType.getName() +"' - " + target.toString(), ex);
    	}
	}

	public void setFormaters(Map<String, Format>  formaters) {
		this.finish = false;
		this.formats.putAll(formaters);
	}

	public void setFormater(String name, Format format) {
		this.finish = false;
		this.formats.put(name, format);
	}

	public void setBody(String name, String value) {
		this.finish = false;
		List<String> values;
		if(this.bodies.containsKey(name)) {
			values = this.bodies.get(name);
		} else {
			values = new ArrayList<String>();
			this.bodies.put(name, values);
		}
		values.add(value);
		this.bodySize = Math.max(this.bodySize,  values.size());
	}

	void prepareWorkSheet() {
		//Clear data parameters
		//-------------------------------
		this.workSheetName = sheetName;
		this.workSheet = this.workbook.cloneSheet(this.sheetIndex);
		this.workSheetNum = getSheetIndex(this.workSheet.getSheetName());
		this.workNames.add(this.workSheet.getSheetName());
    	this.body = new CellRange(this.workSheet, this.bodyRow, this.bodyRow, 0, this.bodyCol);
    	this.bodyRowHeight = this.workSheet.getRow(this.bodyRow).getHeight();

		ParameterReference.Listener listener = null;
		if(debug) {
			listener = new DefaultListener();
		}
		this.reference = new ParameterReference(this.workSheet, listener);

    	//-------------------------------
		this.formats.clear();
		this.headerProperties.clear();
		this.footerProperties.clear();
		this.bodies.clear();
	}

	public void generatorSheet()  {
		generatorSheet(null);
	}

	public void generatorSheet(String newSheetName)  {

		//Set body values
		generatorBodies();

		//Set generator values
		generator(this.headerProperties);

		//Set footer values
		generator(this.footerProperties);



		if(null != newSheetName && !newSheetName.equals(this.workSheetName)) {
			//Rename
			this.workSheetName = newSheetName;
			this.workbook.setSheetName(this.workSheetNum, this.workSheetName);
			this.workNames.removeLast();
			this.workNames.addLast(this.workSheetName);
		}

		prepareWorkSheet();

		this.finish = true;
	}

	/**
	 * Generator header, footer
	 * @param properties
	 */
	private void generator(Properties properties)  {

		Enumeration<?> keys = properties.propertyNames();
		while(keys.hasMoreElements()) {
			String key = (String)keys.nextElement();
			Cell cell = this.reference.getCell(key);
			if(null != cell) {
				Object value = properties.get(key);
				if(null != value) {
					Format format =this.formats.get(key);
					if(null != format) {
						cell.setCellValue(format.format(value));
					} else {
						cell.setCellValue(value.toString());
						//String link = CellRange.getHyperlink(cell); //TODO
					}
				}
			} else {
				//Not found
			}
		}
	}

	String getBodyValue(String name, int index) {
		if(this.bodies.containsKey(name)) {
			return this.bodies.get(name).get(index);
		}
		return "";
	}

	void generatorBodies()  {
		for(int i=0; i<this.bodySize; i++) {
			// Create one record row
			CellRange parts = this.body.clone();
			int width = parts.getWidth();
			int height = parts.getHeight();
			for(int y=0; y<height; y++) {
				for(int x=0; x<width; x++) {
					String value = parts.getValue(x, y);
					if(ParameterReference.isTemplateNamePattern(value)) {
						String name = value.substring(2, (value.length() - 1));
						value = getBodyValue(name, i);
						parts.setValue(x, y, value);
					}
				}
			}
			Row row = this.workSheet.createRow(this.workSheet.getLastRowNum());
			row.setHeight(this.bodyRowHeight);
			parts.writeTo(row, false);
		}
	}

    public void writeTo(OutputStream out, String newSheetName) throws IOException {
    	if(!this.finish) {
    		generatorSheet(newSheetName);
    	}
    	removeSheet(newSheetName);
	    //Write a excel stream
	    this.workbook.write(out);
    }

    private int getSheetIndex(String name) {
       	int sheetCount = this.workbook.getNumberOfSheets();
        for (int i = 0; i < sheetCount; i++) {
        	if(this.workbook.getSheetAt(i).getSheetName().equals(name)) {
        		return i;
        	}
        }
        return -1;
    }

    /**
     * Before write excel, remove excess sheet
     * @param newSheetName
     */
    void removeSheet(String newSheetName) {

    	boolean found = true;
    	while(found) {
    		found = false;
	       	int sheetCount = this.workbook.getNumberOfSheets();
	        for (int i = 0; i < sheetCount; i++) {
	        	if(!this.workNames.contains(this.workbook.getSheetAt(i).getSheetName())) {
	        		this.workbook.removeSheetAt(i);
	        		found = true;
	        		break;
	        	}
	        }
    	}

    	if(this.workNames.size() == 2 && this.workNames.contains(this.sheetName +" (2)") && this.workNames.contains(this.sheetName +" (3)")) {

    		//Only one copy sheet, rename it.
    		int idx = this.getSheetIndex(this.sheetName +" (3)");
    		if(idx > 0) {
    			this.workbook.removeSheetAt(idx);
    		}
    		if(null != newSheetName) {
    			this.workbook.setSheetName(0, newSheetName);
    		} else {
    			this.workbook.setSheetName(0, this.sheetName);
    		}
    	} else if(this.workNames.size() > 1) {
	       	int sheetCount = this.workbook.getNumberOfSheets();
	        for (int i = 0; i < sheetCount; i++) {
	        	if(this.workbook.getSheetAt(i).getSheetName().equals(this.sheetName +" (2)")) {
	        		this.workbook.removeSheetAt(i);
	        		break;
	        	}
	        }
    	}

    }

    public void writeTo(File excel, String newSheetName) throws Exception {
    	FileOutputStream out = new FileOutputStream(excel);
    	writeTo(out, newSheetName);
	    out.close();
    }

    public void writeTo(File excel) throws Exception {
    	writeTo(excel, null);
    }

    protected void finalize() throws Throwable {
    	if(null != this.workbook)
    		this.workbook.close();
    }

}
