package com.sapium.testng;

import com.sapium.starter.IBeforeStart;

public class TestNGInitiator implements IBeforeStart {

	@Override
	public void doBeofre() {
		TestNGWrapper.init();
	}

}
