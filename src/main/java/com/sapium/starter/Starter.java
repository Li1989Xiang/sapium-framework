package com.sapium.starter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sapium.configuration.GlobalConfiguration;
import com.sapium.configuration.TestConfiguration;
import com.sapium.testng.TestNGInitiator;
import com.sapium.utils.SapiumLogger;
import com.sapium.writer.impl.ExcelWriter.ExcelWriterCloser;

import up.light.Setting;
import up.light.driver.DriverFactory;

public class Starter {
	private static final Logger log = SapiumLogger.get();
	private static List<IBeforeStart> before = new ArrayList<>(5);
	private static List<IAfterEnd> after = new ArrayList<>(5);

	private Starter() {}
	
	public static void run() {
		log.debug("begin");
		
		addDefault();
		
		IStarter s = getStarter();
		//====================================
		Map<String, String> m = new HashMap<String, String>();
		String p = TestConfiguration.getPlatform().getName();
		m.put("run.platform", p);
		Setting.setProperties(m);
		//====================================
		try {
			s.beforeStart(before);
			s.start();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			s.afterStart(after);
		}
		
		log.debug("end");
	}
	
	public static void addBeforeObjects(IBeforeStart obj) {
		log.debug("begin");
		
		checkNull(obj);
		
		before.add(obj);
		
		log.debug("end: add - " + obj);
	}
	
	public static void addAfterObjects(IAfterEnd obj) {
		log.debug("begin");
		
		checkNull(obj);
		
		after.add(obj);
		
		log.debug("end: add - " + obj);
	}
	
	private static IStarter getStarter() {
		String className = GlobalConfiguration.get("starter");
		IStarter ret = null;
		
		log.debug("begin: className - " + className);
		
		try {
			Class<?> clazz = Class.forName(className);
			Object ins = clazz.newInstance();
			
			if(ins instanceof IStarter) {
				ret = (IStarter)ins;
			} else {
				throw new IllegalArgumentException("not a IStarter: " + className);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException("can't instance class with name " + className);
		}
		
		log.debug("end: return - " + ret);
		
		return ret;
	}
	
	private static void checkNull(Object obj) {
		if(obj == null) {
			String msg = "parameter is null";
			log.error(msg);
			throw new IllegalArgumentException(msg);
		}
	}
	
	private static void addDefault() {
		// Before
		// 添加TestNG初始化
		addBeforeObjects(new TestNGInitiator());
		
		// After
    	// 关闭Excel报告
    	addAfterObjects(new ExcelWriterCloser());
    	// 添加关闭Driver操作
    	addAfterObjects(new IAfterEnd() {
			@Override
			public void doAfter() {
				DriverFactory.close();
			}
		});
	}
}
