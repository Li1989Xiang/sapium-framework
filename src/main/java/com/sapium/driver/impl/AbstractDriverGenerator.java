package com.sapium.driver.impl;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import com.sapium.configuration.TestPlatform;
import com.sapium.driver.IDriverGenerator;
import com.sapium.utils.SapiumLogger;

public abstract class AbstractDriverGenerator implements IDriverGenerator {
	private static final Logger log = SapiumLogger.get();
	private TestPlatform platform;

	public AbstractDriverGenerator(TestPlatform platform) {
		this.platform = platform;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends WebDriver> T generate() {
		log.debug("begin: platform - " + platform);
		
		T ret = null;
		ret = (T)getDriverInstance(platform);
		
		log.debug("end: return - " + ret);
		
		return ret;
	}
	
	/**
	 * 根据当前平台创建Driver实例
	 * @param platform 当前平台
	 * @return Driver实例
	 */
	public abstract WebDriver getDriverInstance(TestPlatform platform);
}
