package com.sapium.configuration;

public enum TestPlatform {
	ANDROID,
	IOS,
	WEB;
	
	public String getName() {
		return this.toString().toLowerCase();
	}
	
	public static TestPlatform toTestPaltform(String name) {
		name = name.toUpperCase();
		TestPlatform ret = null;
		
		for(TestPlatform by : TestPlatform.values()) {
			if(by.toString().equals(name)) {
				ret = by;
				break;
			}
		}
		
		return ret;
	}
	
	public static String getName(TestPlatform t) {
		return t.toString().toLowerCase();
	}
}
