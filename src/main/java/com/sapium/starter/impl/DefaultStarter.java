package com.sapium.starter.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.sapium.starter.IAfterEnd;
import com.sapium.starter.IBeforeStart;
import com.sapium.starter.IStarter;
import com.sapium.testng.TestNGWrapper;
import com.sapium.utils.SapiumLogger;

public class DefaultStarter implements IStarter {
	private static final Logger log = SapiumLogger.get();

	@Override
	public void beforeStart(List<IBeforeStart> before) {
		log.debug("begin");
		
		for(IBeforeStart b : before) {
			b.doBeofre();
		}
		
		log.debug("end");
	}

	@Override
	public void start() {
		log.debug("begin");
		
		TestNGWrapper.run();
		
		log.debug("end");
	}

	@Override
	public void afterStart(List<IAfterEnd> after) {
		log.debug("begin");
		
		for(IAfterEnd a : after) {
			a.doAfter();
		}
		
		log.debug("end");
	}
}
