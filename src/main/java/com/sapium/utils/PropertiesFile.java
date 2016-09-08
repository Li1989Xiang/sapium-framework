package com.sapium.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesFile {
	private Properties p;
	
	public PropertiesFile(String file) {
		this(file, true);
	}
	
	public PropertiesFile(String file, boolean throwException) {
		p = new Properties();
		
		try(FileInputStream fin = new FileInputStream(file)) {
			p.load(fin);
		} catch (IOException e) {
			if(throwException) {
				throw new RuntimeException("read file '" + file + "' failed", e);
			}
		}
	}
	
	public Properties getProperties() {
		return p;
	}
}
