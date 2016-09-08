package com.sapium.utils;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.sapium.configuration.GlobalConfiguration;

/**
 * <p>日志记录，可在配置文件夹下创建sapiumlogger.properties来定制日志输出格式，若无此文件则按默认格式输出</p>
 * <p>sapiumlogger.properties可包含内容（默认日志输出格式）：</p>
 * 
 * <div>log.pattern=[%d{yyyy-mm-dd HH:mm:ss,SSS}][%p] %C{1}:%L %M() - %m%n</div>
 * <div>filename.pattern='log.'yyyy.MM.dd-HH.mm.ss'.txt'</div>
 * <div>appenders=file, console</div>
 * <div>level=debug</div>
 */
public class SapiumLogger {
	private static final String CONFIG_FILE = "sapiumlogger.properties";
	private static final String DEFAULT_LOG_PATTERN = "[%d{yyyy-mm-dd HH:mm:ss,SSS}][%p] %C{1}:%L %M() - %m%n";
	private static final String DEFAULT_FILENAME_PATTERN = "'log.'yyyy.MM.dd-HH.mm.ss'.txt'";
	private static String log_folder;
	private static String config_folder;
	private static Logger log;
	private static String log_pattern;
	private static String filename_pattern;
	private static String appenders;
	private static Level level;
	
	//禁止实例化
	private SapiumLogger() {}
	
	static {
		init();
	}
	
	/**
	 * 获取日志记录器
	 * @return Logger对象
	 */
	public static Logger get() {
		return log;
	}

	private static void init() {
		//因classpath中引入common logging会提示log4j:WARN No appenders could be found for logger
		//设置此属性屏蔽警告
		System.setProperty("org.apache.commons.logging.Log","org.apache.commons.logging.impl.NoOpLog");
		
		log = Logger.getLogger("SapiumLogger");
		
		readConfig();
		addAppenders(appenders);
		log.setLevel(level);
		
		log.debug("SapiumLogger initialized");
	}
	
	private static void readConfig() {
		//获取日志生成目录路径
		log_folder = GlobalConfiguration.get("log.folder", "log/");
		
		//获取配置文件夹路径
		config_folder = GlobalConfiguration.get("config.folder");
		String filename = config_folder != null ? config_folder + CONFIG_FILE : CONFIG_FILE;
		PropertiesFile pf = new PropertiesFile(filename, false);
		Properties p = pf.getProperties();
		
		//获取appenders
		appenders = p.getProperty("appenders", "console,file");
		
		//根据sapiumlogger.properties生成配置
		log_pattern = p.getProperty("log.pattern", DEFAULT_LOG_PATTERN);
		filename_pattern = p.getProperty("filename.pattern", DEFAULT_FILENAME_PATTERN);
		String levelStr = p.getProperty("level");
		
		level = Level.toLevel(levelStr);
	}
		
	private static void addAppenders(String str) {
		for(String s : str.split(" *, *")) {
			AppenderType a = AppenderType.parse(s);
			
			if(a != null) {
				log.addAppender(a.getAppender());
			}
		}
	}
	
	private static enum AppenderType {
		CONSOLE {
			@Override
			public Appender getAppender() {
				Layout lay = new PatternLayout(log_pattern);
				Appender appender = new ConsoleAppender(lay);
				
				return appender;
			}
		},
		FILE {
			@Override
			public Appender getAppender() {
				Layout lay = new PatternLayout(log_pattern);
				Appender appender = null;
				
				try {
					appender = new FileAppender(lay, log_folder + DateFormator.getDate(filename_pattern));
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				return appender;
			}
		};
		
		public abstract Appender getAppender();
		
		public static AppenderType parse(String str) {
			AppenderType ret = null;
			
			for(AppenderType t : AppenderType.values()) {
				if(t.toString().equalsIgnoreCase(str)) {
					ret = t;
					break;
				}
			}
			
			return ret;
		}
	}
}
