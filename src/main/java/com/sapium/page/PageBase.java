package com.sapium.page;

import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.sapium.configuration.GlobalConfiguration;
import com.sapium.configuration.TestConfiguration;
import com.sapium.configuration.TestPlatform;
import com.sapium.driver.DriverManager;
import com.sapium.page.internal.ObjectRepository;
import com.sapium.pagefactory.RepositoryByBuilder;
import com.sapium.pagefactory.SapiumElementLocatorFactory;
import com.sapium.pagefactory.SapiumFieldDecorator;
import com.sapium.parser.ParserFactory;
import com.sapium.parser.impl.AbstractParser;
import com.sapium.resource.impl.FileResouce;
import com.sapium.utils.SapiumLogger;

public abstract class PageBase<T extends WebDriver> {
	private static final Logger log = SapiumLogger.get();
	//对象库文件夹路径
	private static String folderPath;
	//是否已经解析过全局对象库
	private static boolean globalParsed;
	//共用Parser
	private static AbstractParser parser;
	//全局对象库
	protected static ObjectRepository globalRepository;
	//本地对象库
	protected ObjectRepository localRepository;
	//driver对象
	protected T driver;
	
	public PageBase() {
		log.debug("begin: " + this.getClass().getSimpleName());
		
		//获取对象库文件夹
		getFolderPath();
		
		//解析全局对象库
		parseGlobalObject();
		
		//解析本地对象库
		parseLocalObject();
		
		//获取Driver
		driver = DriverManager.getDriver();
		
		//初始化成员变量
		RepositoryByBuilder builder = new RepositoryByBuilder(localRepository, globalRepository);
		ElementLocatorFactory factory = new SapiumElementLocatorFactory(driver, builder);
		PageFactory.initElements(new SapiumFieldDecorator(driver, factory), this);
		
		localRepository = null;
		
		log.debug("end");
	}
	
	/**
	 * 判断指定控件是否存在并可见
	 * @param e 要判断的控件
	 * @param seconds 查找时间
	 * @return 在规定时间内找到控件返回true，否则返回false
	 */
	protected boolean exists(final WebElement e, int seconds) {
		log.debug("begin");
		
		boolean ret = false;
		
		try {
			ret = new WebDriverWait(driver, seconds).until(
					new ExpectedCondition<Boolean>() {
						@Override
						public Boolean apply(WebDriver input) {
							return e.isDisplayed();
						}
					}
				);
		} catch(TimeoutException ex) {}
		
		log.debug("return: " + ret);
		
		return ret;
	}

	/**
	 * 等待指定元素出现，超时则抛出TimeoutException
	 * @param e 要等待的控件
	 * @param seconds 等待时间
	 */
	protected void waitFor(final WebElement e, int seconds) {
		log.debug("begin");
		
		new WebDriverWait(driver, seconds).until(
				new ExpectedCondition<Boolean>() {
					@Override
					public Boolean apply(WebDriver input) {
						return e.isDisplayed();
					}
					
					@Override
					public String toString() {
						return "visibility of element " + e;
					}
				}
			);
		
		log.debug("end");
	}
	
	/**
	 * 等待指定元素消失，超时则抛出TimeoutException
	 * @param e 要等待的控件
	 * @param seconds 等待时间
	 */
	protected void untilGone(final WebElement e, int seconds) {
		log.debug("begin");
		
		new WebDriverWait(driver, seconds).until(
				new ExpectedCondition<Boolean>() {
					@Override
					public Boolean apply(WebDriver input) {
						Boolean ret = Boolean.FALSE;
						try {
							ret = (e.isDisplayed() == false);
						} catch(NoSuchElementException | StaleElementReferenceException e) {
							ret = Boolean.TRUE;
						}
						return ret;
					}
					
					@Override
					public String toString() {
						return "invisibility of element " + e;
					}
				}
			);
		
		log.debug("end");
	}
	
	/**
	 * 等待指定时间
	 * @param seconds 等待秒数
	 */
	protected void waitTime(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	//----------------------------------------------------------------------------------
	
	/*
	 * 获取对象库文件夹
	 */
	private void getFolderPath() {
		log.debug("begin: old folderPath - " + folderPath);
		
		//只获取一次
		if(folderPath != null) {
			log.debug("folderPath has been setted, needn't to do it again");
			return;
		}
		
		//根据当前平台生成路径
		String folder = GlobalConfiguration.get("object.folder");
		String platform = TestPlatform.getName(TestConfiguration.getPlatform());
		folderPath = folder + platform + "/";
		
		log.debug("end: new folderPath - " + folderPath);
	}
	
	/*
	 * 解析全局对象库
	 */
	private void parseGlobalObject() {
		//全局对象库只解析一次
		if(globalParsed) {
			log.debug("globalRepository was parsed before - " + globalRepository);
			return;
		}
		
		log.debug("begin: old globalRepository - " + globalRepository);
		
		String file = folderPath + "global.json";
		FileResouce res = new FileResouce(file);
		
		//存在 global.json则进行解析
		if(res.getFile().exists()) {
			log.debug(file + " exists");
			parser = ParserFactory.getParser(res);
			globalRepository = parser.parse(ObjectRepository.class);
		}
		
		globalParsed = true;
		
		log.debug("end: new globalRepository - " + globalRepository);
	}
	
	/*
	 * 解析本地对象库
	 */
	private void parseLocalObject() {
		log.debug("begin");
		
		//根据类名获取资源文件名
		String file = folderPath + this.getClass().getSimpleName() + ".json";
		FileResouce res = new FileResouce(file);
		
		//若没有parser则生成一个，有则直接要解析的资源
		if(parser == null) {
			parser = ParserFactory.getParser(res);
			log.debug("create localParser - " + parser);
		} else {
			parser.setResource(res);
			log.debug("localParser is not null - " + parser);
		}
		
		//解析本地对象库
		localRepository = parser.parse(ObjectRepository.class);
		
		log.debug("end: new localRepository - " + localRepository);
	}
}
