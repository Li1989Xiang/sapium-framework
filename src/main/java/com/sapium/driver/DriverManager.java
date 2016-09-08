package com.sapium.driver;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import com.sapium.configuration.TestConfiguration;
import com.sapium.configuration.TestPlatform;
import com.sapium.driver.impl.MobileDriverGenerator;
import com.sapium.driver.impl.WebDriverGenerator;
import com.sapium.starter.IAfterEnd;
import com.sapium.utils.SapiumLogger;

public class DriverManager {
	private static final Logger log = SapiumLogger.get();
	private static WebDriver driver;
	
	//禁止实例化
	private DriverManager() {}
	
	/**
	 * 获取对应平台的Driver对象
	 * @return Driver对象
	 */
	@SuppressWarnings("unchecked")
	public static <T extends WebDriver> T getDriver() {
		if(driver != null) {
			log.debug("driver is not null, return - " + driver);
			return (T)driver;
		}
		
		log.debug("begin: create driver");
		
		TestPlatform p = TestConfiguration.getPlatform();
		IDriverGenerator generator = null;
		
		switch (p) {
		case ANDROID:
		case IOS:
			generator = new MobileDriverGenerator(p);
			break;
		case WEB:
			generator = new WebDriverGenerator(p);
			break;
		default:
			break;
		}
		
		T ret = generator.generate();
		driver = ret;
		
		log.debug("end: return - " + driver);
		
		return ret;
	}
	
	/**
	 * 负责测试结束后退出Driver
	 */
	public static class DriverCloser implements IAfterEnd {
		@Override
		public void doAfter() {
			if(driver != null) {
				driver.quit();
				driver = null;
				log.debug("driver quitted");
			}
		}
	}
}
