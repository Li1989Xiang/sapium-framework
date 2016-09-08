package com.sapium.reader;

import com.sapium.reader.impl.ExcelReader;
import com.sapium.resource.IDataResource;

public class ReaderFactory {
	public static IReader getReader(IDataResource res) {
		String type = res.getType();
		IReader ret = null;
		
		switch (type) {
		case "xls":
		case "xlsx":
		case "xlsm":
			ret = new ExcelReader(res);
			break;
		default:
			break;
		}
		
		return ret;
	}
}
