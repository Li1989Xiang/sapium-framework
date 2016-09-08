package com.sapium.reader.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.sapium.configuration.GlobalConfiguration;
import com.sapium.utils.PropertiesFile;

public class ExcelReaderConfig {
	//全局控制列索引
	//负数代表倒数第几列
	private int wbControlColumn;
	//全局排除列索引
	private int[] wbExcludeColumns;
	//sheet控制列索引
	private Map<String, Integer> controlColumn;
	//sheet排除列索引
	private Map<String, int[]> excludeColumns;
	//是否配置了控制列
	private boolean controlColumnEnabled;
	
	public ExcelReaderConfig() {
		readConfig();
	}
	
	public boolean isControlColumnEnabled() {
		return controlColumnEnabled;
	}
	
	public int getControlColumn(String sheetName) {
		Integer sheetControl = controlColumn.get(sheetName + ".control");
		
		if(sheetControl != null) {
			return sheetControl;
		}
		
		return wbControlColumn;
	}
	
	public int[] getExcludeColumn(String sheetName) {
		int[] sheetExclude = excludeColumns.get(sheetName + ".exclude");
		
		if(sheetExclude != null) {
			return sheetExclude;
		}
		
		return wbExcludeColumns;
	}

	private void readConfig() {
		String folder = GlobalConfiguration.get("config.folder");
		PropertiesFile pf = new PropertiesFile(folder + "excelreader.properties", false);
		Properties p = pf.getProperties();
		
		controlColumn = new HashMap<>();
		excludeColumns = new HashMap<>();
		
		String name, value;
		
		for(Map.Entry<Object, Object> e : p.entrySet()) {
			name = (String)e.getKey();
			value = (String)e.getValue();
			
			//值为空则跳过
			if(StringUtils.isEmpty(value)) {
				continue;
			}
			
			if(name.indexOf("workbook") >= 0) {
				//global
				if(name.indexOf("control") >= 0) {
					wbControlColumn = Integer.valueOf(value);
					controlColumnEnabled = true;
				} else {
					wbExcludeColumns = parseArray(value);
				}
			} else {
				//local
				if(name.indexOf("control") >= 0) {
					controlColumn.put(name, Integer.valueOf(value));
					controlColumnEnabled = true;
				} else {
					excludeColumns.put(name, parseArray(value));
				}
			}
		}
	}
	
	private int[] parseArray(String str) {
		String[] arr = str.split(" *, *");
		int[] ret = new int[arr.length];
		
		for(int i = 0; i < arr.length; ++i) {
			ret[i] = Integer.valueOf(arr[i]);
		}
		
		return ret;
	}
}
