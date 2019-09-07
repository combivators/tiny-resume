package net.tiny.excel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ParameterReference {
	
	public static interface Listener {
		void macro(String msg);
		void define(String msg);
	}
	
	private Listener listener = null;
	private Map<String, Cell> defines = new HashMap<String, Cell>();
	private Map<String, String> macros = new HashMap<String, String>();
	private Map<String, String> relations = new HashMap<String, String>();
	private final Sheet sheet;
	
	public ParameterReference(Sheet sheet) {
		this(sheet, null);
	}

	public ParameterReference(Sheet sheet, Listener listener) {
		this.sheet = sheet;
		setListener(listener);
		parse();
	}
	
	public Listener getListener() {
		return this.listener;
	}
	
	public void setListener(Listener listener) {
		this.listener = listener;
	}

	protected void parse()  {
		this.defines.clear();
		this.macros.clear();
		int firstRow = this.sheet.getFirstRowNum();
		int lastRow = this.sheet.getLastRowNum();
		
		for(int y=firstRow; y<=lastRow; y++) {
			Row row = this.sheet.getRow(y);
			if(null == row) {
				break;
			}
			int firstCol  = row.getFirstCellNum();
			int lastCol  = row.getLastCellNum();
			for(int x=firstCol; x<=lastCol; x++) {
				Cell c = row.getCell(x);
				if (null != c) {
					String value = CellRange.getCellValue(c);
					if(isTemplateNamePattern(value)) {
						String name = value.substring(2, (value.length() - 1));
						if(isExpressionNamePattern(value)) {
							int pos = name.indexOf("=");
							String expression  = name.substring(pos+1);
							name = name.substring(0,  pos);
							//Is Expression macro
							this.macros.put(name, expression);
							if(listener != null) {
								listener.macro(String.format("(%1$d, %2$d) %3$s = '%4$s'", 
										c.getColumnIndex(), c.getRowIndex(), name, expression));
							}
							//TODO
							this.relations.put("body.total", name);
						}
						this.defines.put(name, c);
						if(listener != null) {
							listener.define(String.format("(%1$d, %2$d) %3$s", 
									c.getColumnIndex(), c.getRowIndex(), name));
						}
					}
				}
			}
		}
    }
	
	public String getRelationKey(String key) {
		return this.relations.get(key);
	}
	
	public String getExpression(String key) {
		String name = this.relations.get(key);
		if(name != null) {
			return this.macros.get(name);
		}
		return null;
	}
	
	public boolean hasCell(String key) {
		return this.defines.containsKey(key);
	}
	
	public Cell getCell(String key) {
		return this.defines.get(key);
	}
	
	public List<String> getBodyKeys(String name) {
		Set<String> keys = this.defines.keySet();
		String prefix = name + ".";
		List<String> rowKeys = new ArrayList<String>();
		for(String key : keys) {
			if(key.startsWith(prefix)) {
				rowKeys.add(key);
			}
		}
		return rowKeys;
	}
	
	public static boolean isTemplateNamePattern(String name) {
		return (null != name && name.length() > 3 && name.startsWith("${") && name.endsWith("}") );
	}
	
	public static boolean isExpressionNamePattern(String name) {
		return (null != name && 
				name.length() > 5 && 
				name.indexOf("=") > 4 && 
				name.startsWith("${") && 
				name.endsWith("}") );
	}
}
