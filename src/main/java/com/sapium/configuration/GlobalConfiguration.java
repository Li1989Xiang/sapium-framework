package com.sapium.configuration;

import java.util.Properties;

import com.sapium.utils.PropertiesFile;

public class GlobalConfiguration {
	private static final String CONFIG_FILE = "sapium.properties";
	private static Properties p;

	//禁止实例化
	private GlobalConfiguration() {}
	
	static {
		PropertiesFile pf = null;
		
		try {
			pf = new PropertiesFile(CONFIG_FILE);
			p = pf.getProperties();
		} catch(RuntimeException e) {
			setDefault();
		}
		
		System.out.println("GlobalConfigration initialized");
	}
	
	/**
	 * 根据 sapium.properties 文件中的配置，获取key对应的值
	 * @param key 要查找的键
	 * @param defaultValue 默认值
	 * @return 找到则返回键对应的字符串，找不到返回 null
	 */
	public static String get(String key, String defaultValue) {
		return p.getProperty(key, defaultValue);
	}
	
	/**
	 * 根据 sapium.properties 文件中的配置，获取key对应的值
	 * @param key 要查找的键
	 * @return 找到则返回键对应的字符串，找不到返回 null
	 */
	public static String get(String key) {
		return p.getProperty(key);
	}
	
	private static void setDefault() {
		//===========================================================
		String path = TestConfiguration.getPlatform().getName() + "/";
		//===========================================================
		p = new Properties();
		p.setProperty("config.folder", path + "config/");
		p.setProperty("object.folder", path + "object/");
		p.setProperty("data.folder", path + "data/");
		p.setProperty("report.folder", path + "report/");
		p.setProperty("log.folder", path + "log/");
		p.setProperty("starter", "com.sapium.starter.impl.DefaultStarter");
		
		System.out.println("GlobalConfigration: " + CONFIG_FILE + " not found");
		System.out.println("GlobalConfigration: initialized with default values");
	}
}
