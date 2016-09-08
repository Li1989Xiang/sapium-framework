package com.sapium.configuration;

import java.util.Properties;

import com.sapium.utils.PropertiesFile;

public class TestConfiguration {
	private static final String CONFIG_FILE = "test.properties";
	//private static final Logger log = SapiumLogger.get();
	private static Properties p;
	
	//禁止实例化
	private TestConfiguration() {}
	
	static {
		PropertiesFile pf = new PropertiesFile(CONFIG_FILE);
		p = pf.getProperties();
		//log.debug("TestConfigration initialized");
	}
	
	public static TestPlatform getPlatform() {
		//log.debug("begin");
		
		String platform = p.getProperty("platform");
		TestPlatform tp = TestPlatform.toTestPaltform(platform);
		
		if(tp == null) {
			String msg = "can't get platform in " + CONFIG_FILE;
			//log.error(msg);
			throw new RuntimeException(msg);
		}
		
		//log.debug("end: return " + tp);
		
		return tp;
	}
}
