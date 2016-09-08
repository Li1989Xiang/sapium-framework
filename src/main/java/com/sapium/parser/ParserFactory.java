package com.sapium.parser;

import com.sapium.parser.impl.AbstractParser;
import com.sapium.parser.impl.JsonParser;
import com.sapium.resource.IDataResource;

public class ParserFactory {
	/*
	 * 禁止实例化
	 */
	private ParserFactory() {}
	
	/**
	 * 根据资源文件返回对应的解析器
	 * @param res 资源文件
	 * @return 支持解析该类型返回对应的解析器，不支持返回null
	 */
	public static AbstractParser getParser(IDataResource res) {
		String type = res.getType().toLowerCase();
		
		switch (type) {
		case "json":
			return new JsonParser(res);
		default:
			return null;
		}
	}
}
