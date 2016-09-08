package com.sapium.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class ScreenShot {
	private static final Logger log = SapiumLogger.get();
	
	public static void take(WebDriver driver, String fileName) {
		TakesScreenshot ts = (TakesScreenshot)driver;
		File pic = ts.getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(pic, new File(fileName));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}
	}
}
