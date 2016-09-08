package com.sapium.driver;

import org.openqa.selenium.WebDriver;

/**
 * 生成指定类型的Driver
 */
public interface IDriverGenerator {
	public <T extends WebDriver> T generate();
}
