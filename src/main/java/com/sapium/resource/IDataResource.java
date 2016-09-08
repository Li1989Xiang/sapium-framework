package com.sapium.resource;

import java.io.InputStream;
import java.io.Reader;

public interface IDataResource {
	/**
	 * 获取文件类型
	 * @return 扩展名字符串，如xml
	 */
	public String getType();
	
	/**
	 * 获取文件输入流
	 * @return 输入流对象
	 */
	public InputStream getInputStream();
	
	/**
	 * 获取文件Reader
	 * @return Reader对象
	 */
	public Reader getReader();
}
