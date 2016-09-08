package com.sapium.writer;

import java.util.Map;

public interface IWriter {
	public void writeLine(Map<String, String> param);
	public String getFolder();
}
