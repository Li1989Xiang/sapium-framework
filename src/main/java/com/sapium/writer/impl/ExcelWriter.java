package com.sapium.writer.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFHyperlink;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.sapium.configuration.GlobalConfiguration;
import com.sapium.starter.IAfterEnd;
import com.sapium.utils.DateFormator;
import com.sapium.utils.SapiumLogger;
import com.sapium.writer.IWriter;

public class ExcelWriter implements IWriter {
	//TODO private static final String TEMPLATE_FILE = "../../../../ReportTemplate.xls";
	private static final String TEMPLATE_FILE = "/ReportTemplate.xls";
	private static final Logger log = SapiumLogger.get();
	private static final String folder = GlobalConfiguration.get("report.folder");
	private static String folderName;
	private static Workbook workbook;
	private static ExcelWriter ins;
	
	private Sheet sheet1;
	private Sheet sheet2;
	private int rowNum1;
	private int rowNum2;
	private String oldTestName;
	private int nullNameIndex = 1;
	
	private static CellStyle linkStyle;
	private static CellStyle normalStyle;
	private static CellStyle titleStyle;
	
	public synchronized static ExcelWriter getInstance() {
		if(ins == null) {
			ins = new ExcelWriter();
		}
		
		return ins;
	}
	
	private ExcelWriter() {
		try(InputStream ins = ExcelWriter.class.getResourceAsStream(TEMPLATE_FILE);) {
			workbook = WorkbookFactory.create(ins);
			sheet1 = workbook.getSheet("测试用例");
			rowNum1 = sheet1.getLastRowNum();
			sheet2 = workbook.getSheet("测试详情");
			rowNum2 = sheet2.getLastRowNum();
			oldTestName = "";
			folderName = DateFormator.getDate();
			
			initFolder();
			initStyle();
		} catch (Exception e) {
			log.error("initialize failed", e);
			throw new RuntimeException("ExcelReporter initialize failed", e);
		}
	}

	@Override
	public void writeLine(Map<String, String> param) {
		if(param == null)
			return;
		
		String testName = param.get("testName");
		String caseName = param.get("caseName");
		String status = param.get("status");
		String parameter = param.get("parameter");
		String expect = param.get("expect");
		String actual = param.get("actual");
		String screenShot = param.get("screenShot");
		
		writeLine(testName, caseName, status, parameter, expect, actual, screenShot);
	}
	
	@Override
	public String getFolder() {
		return folder + folderName + "/";
	}

	private void writeLine(String testName, String caseName, String status, String param,
			String expect, String actual, String screenShot) {
		if(!oldTestName.equals(testName)) {
			if(testName == null)
				testName = "NullName_" + nullNameIndex++;
			addTest(testName);
			this.oldTestName = testName;
		}
		addCase(screenShot, caseName, status, param, expect, actual);
	}

	private void addTest(String testName) {
		//sheet1
		Row r = sheet1.createRow(++rowNum1);
		Cell c = r.createCell(0);
		c.setCellValue(testName);
		Hyperlink link = new HSSFHyperlink(Hyperlink.LINK_DOCUMENT);
		link.setAddress("#测试详情!B" + (rowNum2 + 2));
		c.setHyperlink(link);
		c.setCellStyle(linkStyle);
		
		//隐藏列填充公式
		int row = rowNum1 + 1;
		c = r.createCell(4);
		String formula = "MATCH(A" + row + ",测试详情!B:B,0)+1";
		c.setCellType(HSSFCell.CELL_TYPE_FORMULA);
		c.setCellFormula(formula);
		
		//隐藏列填充公式
		c = r.createCell(5);
		formula = "IF(A%<>\"\",MATCH(A%,测试详情!B:B,0)-1,COUNTA(测试详情!B:B))";
		int row1 = row + 1;
		formula = formula.replace("%", String.valueOf(row1));
		c.setCellType(HSSFCell.CELL_TYPE_FORMULA);
		c.setCellFormula(formula);
		
		//测试结果
		c = r.createCell(1);
		formula = "IF(COUNTIF(INDIRECT(\"测试详情!C\"&E%&\":C\"&F%),\"pass\")=C%,\"Pass\",\"Fail\")";
		formula = formula.replace("%", String.valueOf(row));
		c.setCellType(HSSFCell.CELL_TYPE_FORMULA);
		c.setCellFormula(formula);
		c.setCellStyle(normalStyle);
		
		//测试数量
		c = r.createCell(2);
		formula = "F%-E%+1";
		formula = formula.replace("%", String.valueOf(row));
		c.setCellType(HSSFCell.CELL_TYPE_FORMULA);
		c.setCellFormula(formula);
		c.setCellStyle(normalStyle);
		
		//备注
		c = r.createCell(3);
		c.setCellStyle(normalStyle);
		
		//sheet2
		++rowNum2;
		r = sheet2.createRow(rowNum2);
		
		c = r.createCell(0);
		c.setCellStyle(titleStyle);
		
		c = r.createCell(1);
		c.setCellValue(testName);
		c.setCellStyle(titleStyle);
		
		for(int i = 2; i < 6; ++i) {
			c = r.createCell(i);
			c.setCellStyle(titleStyle);
		}
	}

	private void addCase(String...param) {
		Row r = sheet2.createRow(++rowNum2);
		Cell c = null;
		CellStyle alignLeftStyle = workbook.createCellStyle();
		alignLeftStyle.cloneStyleFrom(normalStyle);
		alignLeftStyle.setAlignment(CellStyle.ALIGN_LEFT);
				
		c = r.createCell(0);
		c.setCellStyle(linkStyle);
		
		if(param[0] != null && !"".equals(param[0])) {
			c.setCellFormula("HYPERLINK(\""+param[0]+"\",\"截图\")"); 
		}
		
		if(param.length > 1) {
			for(int i = 1; i < param.length; ++i) {
				c = r.createCell(i);
				
				if(i >= 3) {
					c.setCellStyle(alignLeftStyle);
				} else {
					c.setCellStyle(normalStyle);
				}
				
				if(param[i] != null) {
					c.setCellValue(param[i]);
				}
			}
		}
	}
	
	private void initStyle() {
		Sheet s = workbook.getSheet("hide");
		Cell c = s.getRow(1).getCell(1);
		linkStyle = c.getCellStyle();
		
		c = s.getRow(3).getCell(1);
		normalStyle = c.getCellStyle();
		
		c = s.getRow(5).getCell(1);
		titleStyle = c.getCellStyle();
	}
	
	private void initFolder() {
		File base = new File(folder);
		if(!base.exists()) {
			if(!base.mkdir()) {
				String msg = "can't create folder: " + folder;
				log.error(msg);
				throw new RuntimeException(msg);
			}
		}
		
		File report = new File(folder + folderName);
		if(!report.mkdir()) {
			String msg = "can't create folder: " + folderName;
			log.error(msg);
			throw new RuntimeException(msg);
		}
	}
	
	public static class ExcelWriterCloser implements IAfterEnd {
		@Override
		public void doAfter() {
			if(workbook != null) {
				try(FileOutputStream fos = new FileOutputStream(folder + folderName + "/report.xls")) {
					workbook.write(fos);
					workbook.close();
					log.debug("report.xls closed");
				} catch (IOException e) {
					log.error(e.getMessage(), e);
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}
	}
}
