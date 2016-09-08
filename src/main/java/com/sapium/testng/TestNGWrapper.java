package com.sapium.testng;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.TestNG;

import com.sapium.configuration.GlobalConfiguration;
import com.sapium.utils.SapiumLogger;

public class TestNGWrapper {
	private static final Logger log = SapiumLogger.get();
	private static final String XML_FILE = "testng.xml";
	private static String CONFIG_DIR;
	private static TestNG runner;

	public static void init() {
		if(runner != null) {
			log.debug("runner was initialized before");
			return;
		}
		
		log.debug("begin");
		
		CONFIG_DIR = GlobalConfiguration.get("config.folder");
		runner = new TestNG(false);
		runner.setVerbose(2);
		List<String> suites = new ArrayList<>();
		suites.add(CONFIG_DIR + XML_FILE);
		
		log.debug("File: " + CONFIG_DIR + XML_FILE);
		
		runner.setTestSuites(suites);
		
		log.debug("end");
	}

	public static void run() {
		if(runner == null) {
			String msg = "runner is null";
			log.error(msg);
			throw new IllegalStateException(msg);
		}
		
		runner.run();
	}
	
	public static TestNG getRunner() {
		return runner;
	}
}
