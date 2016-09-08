package com.sapium.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormator {
	/*
	 * 禁止实例化
	 */
	private DateFormator() {
		
	}
	
	/**
	 * 以yyyyMMdd_HHmmss格式化当前时间
	 * @return 格式化字符串
	 */
	public static String getDate() {
		SimpleDateFormat d = new SimpleDateFormat("yyyyMMdd_HHmmss");
		return d.format(new Date());
	}
	
	/**
	 * 以指定格式化当前时间
	 * @param pattern 指定格式
	 * @return 格式化字符串
	 */
	public static String getDate(String pattern) {
		SimpleDateFormat d = new SimpleDateFormat(pattern);
		return d.format(new Date());
	}
}
