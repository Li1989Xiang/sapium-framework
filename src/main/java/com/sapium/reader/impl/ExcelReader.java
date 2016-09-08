package com.sapium.reader.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.sapium.reader.IReader;
import com.sapium.resource.IDataResource;
import com.sapium.starter.IAfterEnd;
import com.sapium.utils.SapiumLogger;

public class ExcelReader implements IReader {
	private static final Logger log = SapiumLogger.get();
	//需关闭列表
	private static final Map<IDataResource, Workbook> listToClose = new HashMap<>();
	//配置参数
	private final ExcelReaderConfig config = new ExcelReaderConfig();;
	private boolean controlColumnEnabled;
	private int controlColumn;
	private int[] excludeColumns;
	//POI对象
	private Workbook workbook;
	private Sheet sheet;
	private String currentName;
	private Row firstRow;
	private int rowNum;
	private int columnNum;
	
	private int[] sequence;
	
	private List<Boolean> controlInfo = new ArrayList<>();
	private List<String> title = new ArrayList<>();
	
	public ExcelReader(IDataResource res) {
		workbook = listToClose.get(res);
		if(workbook == null) {
			try(InputStream ins = res.getInputStream()) {
				workbook = WorkbookFactory.create(ins);
				listToClose.put(res, workbook);
				log.debug("open file: " + res);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new RuntimeException(e.getMessage());
			}
		}
		
		log.debug("workbook: " + workbook);
	}
	
	@Override
	public void changeGroup(String name) {
		if(!name.equals(currentName)) {
			controlColumnEnabled = config.isControlColumnEnabled();
			
			sheet = workbook.getSheet(name);
			if(sheet == null) {
				throw new RuntimeException("can't find sheet with name: " + name);
			}
			
			currentName = name;
			firstRow = sheet.getRow(0);
			rowNum = sheet.getPhysicalNumberOfRows();
			columnNum = firstRow.getPhysicalNumberOfCells();
			
			controlColumn = getRealIndex(config.getControlColumn(name));
			int[] tmp = config.getExcludeColumn(name);
			
			if(tmp != null) {
				//拷贝数组，防止直接修改原数组
				excludeColumns = Arrays.copyOf(tmp, tmp.length);
				Arrays.sort(excludeColumns);
				
				for(int i = 0; i < excludeColumns.length; ++i) {
					excludeColumns[i] = getRealIndex(excludeColumns[i]);
				}
			}
			
			fixInfo();
			setControlInfo();
			
			log.debug("change to sheet: " + name);
		}
	}

	@Override
	public int getNextIndex(int index) {
		if(controlColumnEnabled) {
			int len = controlInfo.size();
			
			for(int i = index; i < len; ++i) {
				if(controlInfo.get(i)) {
					return i + 1;
				}
			}
			
			return -1;
		}
		
		if(index >= rowNum - 1)
			return -1;
		
		return index + 1;
	}

	@Override
	public Map<String, String> readLineWithTitle(int index) {
		Map<String, String> m = new LinkedHashMap<>();
		Row r = sheet.getRow(index);
		
		for(int i = 0; i < sequence.length; ++i) {
			m.put(
				title.get(i),
				r.getCell(sequence[i]).getStringCellValue()
			);
		}
		
		log.debug("data: " + m + " line: " + index);
		
		return m;
	}

	@Override
	public List<String> readLine(int index) {
		List<String> ls = new LinkedList<>();
		Row r = sheet.getRow(index);
		
		for(int i = 0; i < sequence.length; ++i) {
			ls.add(r.getCell(sequence[i]).getStringCellValue());
		}
		
		log.debug("data: " + ls + " line: " + index);
		
		return ls;
	}
	
	/*
	 * 将ExcelReaderConfig读取的索引值转换为POI对应的索引值 
	 */
	private int getRealIndex(int index) {
		if(index >= 0) {
			return index;
		}
		
		int columnCount = firstRow.getPhysicalNumberOfCells();
		return columnCount + index;
	}
	
	/*
	 * 根据控制列和排除列修正成员变量
	 */
	private void fixInfo() {
		//列数中减去控制列
		if(controlColumnEnabled) {
			--columnNum;
		}
		//列数中减去排除列
		if(excludeColumns != null) {
			columnNum -= excludeColumns.length;
			
			//排除列中包含控制列
			if(Arrays.binarySearch(excludeColumns, controlColumn) >= 0) {
				++columnNum;
			}
		}
		
		title.clear();
		
		generateIndexSequence();
		//获取标题
		for(int i = 0; i < sequence.length; ++i) {
			title.add(firstRow.getCell(sequence[i]).getStringCellValue());
		}
	}
	
	/*
	 * 获取控制列除第一行外的数据，用于标志此行是否读取
	 */
	private void setControlInfo() {
		if(!controlColumnEnabled)
			return;
		
		controlInfo.clear();
		
		//从第二行开始读取
		for(int i = 1; i < rowNum; ++i) {
			controlInfo.add(Boolean.parseBoolean(sheet.getRow(i).getCell(controlColumn).getStringCellValue()));
		}
	}
	
	/*
	 * 生成除去控制列、排除列的列索引序列
	 */
	private void generateIndexSequence() {
		sequence = new int[columnNum];
		
		int c = firstRow.getPhysicalNumberOfCells();
		int j = 0;
		
		for(int i = 0; i < c; ++i) {
			//跳过控制列
			if(controlColumnEnabled && i == controlColumn) {
				continue;
			}
			//跳过排除列
			if(excludeColumns != null && Arrays.binarySearch(excludeColumns, i) >= 0) {
				continue;
			}
			
			sequence[j++] = i;
		}
	}
	
	/**
	 * 用于关闭所有打开的Excel文件
	 */
	public static class ExcelReaderCloser implements IAfterEnd {
		@Override
		public void doAfter() {
			for(Workbook wb : listToClose.values()) {
				try {
					wb.close();
					log.debug("close workbook: " + wb);
				} catch (IOException e) {
					log.error(e.getMessage(), e);
					throw new RuntimeException(e.getMessage());
				}
			}
		}
	}
}
