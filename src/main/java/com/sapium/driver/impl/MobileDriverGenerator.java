package com.sapium.driver.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.sapium.configuration.GlobalConfiguration;
import com.sapium.configuration.TestPlatform;
import com.sapium.utils.PropertiesFile;
import com.sapium.utils.SapiumLogger;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;

public class MobileDriverGenerator extends AbstractDriverGenerator {
	private static final Logger log = SapiumLogger.get();
	private static String file;
	
	public MobileDriverGenerator(TestPlatform platform) {
		super(platform);
		
		if(file == null) {
			file = GlobalConfiguration.get("config.folder") + "dc_" + platform.getName() + ".properties";
			log.debug("get file's name from configration: " + file);
		}
	}
	
	@Override
	public WebDriver getDriverInstance(TestPlatform platform) {
		log.debug("begin: param - " + platform);
		
		WebDriver ret = null;
		
		try {
			switch (platform) {
			case ANDROID:
				ret = new AndroidDriver<AndroidElement>(getURL(), getDC());
				break;
			case IOS:
				ret = new IOSDriver<IOSElement>(getURL(), getDC());
				break;
			default:
				throw new IllegalArgumentException("unsupported paltform for MobileDriverGenerator: " + platform);
			}
		} catch (Exception e) {
			String msg = "create driver instance failed";
			log.error(msg, e);
			throw new RuntimeException(msg, e);
		}
		
		ret.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		
		log.debug("end: return - " + ret);
		
		return ret;
	}

	private URL getURL() throws MalformedURLException {
		// TODO if need to read url from file ?
		URL u = new URL("http://127.0.0.1:4723/wd/hub");
		return u;
	}
	
	private Capabilities getDC() {
		DesiredCapabilities dc = new DesiredCapabilities();
		PropertiesFile pf = new PropertiesFile(file);
		Properties p = pf.getProperties();
		
		for(Map.Entry<Object, Object> e : p.entrySet()) {
			String key = (String)e.getKey();
			String value = (String)e.getValue();
			dc.setCapability(key, value);
			
			log.trace("DesiredCapabilities: " + key + " - " + value);
		}
		
		return dc;
	}
}
