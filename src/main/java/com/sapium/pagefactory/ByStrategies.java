package com.sapium.pagefactory;

import org.openqa.selenium.By;

import com.sapium.configuration.TestConfiguration;
import com.sapium.configuration.TestPlatform;

import io.appium.java_client.MobileBy;

public enum ByStrategies {
	UIAUTOMATOR {
		@Override
		public By getBy(String using) {
			if(p == null)
				p = TestConfiguration.getPlatform();
			
			switch (p) {
			case ANDROID:
				return MobileBy.AndroidUIAutomator(using);
			case IOS:
				return MobileBy.IosUIAutomation(using);
			default:
				break;
			}
			return null;
		}
	},
	ACCESSIBILITY {
		@Override
		public By getBy(String using) {
			return MobileBy.AccessibilityId(using);
		}
	},
	CLASSNAME {
		@Override
		public By getBy(String using) {
			return By.className(using);
		}
	},
	ID {
		@Override
		public By getBy(String using) {
			return By.id(using);
		}
	},
	TAG {
		@Override
		public By getBy(String using) {
			return By.tagName(using);
		}
	},
	NAME {
		@Override
		public By getBy(String using) {
			return By.name(using);
		}
	},
	XPATH {
		@Override
		public By getBy(String using) {
			return By.xpath(using);
		}
	},
	LINKTEXT {
		@Override
		public By getBy(String using) {
			return By.linkText(using);
		}
	},
	PARTIALLINKTEXT {
		@Override
		public By getBy(String using) {
			return By.partialLinkText(using);
		}
	},
	CSSSELECTOR {
		@Override
		public By getBy(String using) {
			return By.cssSelector(using);
		}
	};
	
	private static TestPlatform p;
	
	public static ByStrategies toByStrategies(String name) {
		name = name.toUpperCase();
		ByStrategies ret = null;
		
		for(ByStrategies by : ByStrategies.values()) {
			if(by.toString().equals(name)) {
				ret = by;
				break;
			}
		}
		
		if(ret == null) {
			throw new RuntimeException("unsupport strategies: " + name);
		}
		
		return ret;
	}
	
	public abstract By getBy(String using);
}
