package com.sapium.resource.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.sapium.resource.IDataResource;
import com.sapium.utils.SapiumLogger;

public class FileResouce implements IDataResource {
	private static final Logger log = SapiumLogger.get();
	private String file;
	
	public FileResouce(String file) {
		this.file = file;
	}
	
	@Override
	public String getType() {
		log.debug("begin: file - " + file);
		
		if(file == null || StringUtils.isBlank(file)) {
			return null;
		}
		
		String ret = StringUtils.substringAfterLast(file, ".");
		
		log.debug("end: return - " + ret);
		
		return ret;
	}
	
	@Override
	public InputStream getInputStream() {
		log.debug("begin: file - " + file);
		
		InputStream ret = null;
		
		try {
			ret = FileUtils.openInputStream(this.getFile());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage(), e);
		}
		
		log.debug("end: return - " + ret);
		
		return ret;
	}

	@Override
	public Reader getReader() {
		log.debug("begin: file - " + file);
		
		Reader ret = null;
		
		try {
			ret = new FileReader(file);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException("找不到文件: " + file, e);
		}
		
		log.debug("end: return - " + ret);
		
		return ret;
	}

	public File getFile() {
		if(file == null)
			return null;
		
		return new File(file);
	}

	@Override
	public String toString() {
		return "FileResouce[" + file + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof FileResouce) {
			FileResouce fes = (FileResouce)obj;
			File f = fes.getFile();
			return f.equals(this.getFile());
		}
		
		return false;
	}

	@Override
	public int hashCode() {
		return this.getFile().hashCode();
	}
}
