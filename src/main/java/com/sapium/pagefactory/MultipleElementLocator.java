package com.sapium.pagefactory;

import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import com.sapium.utils.SapiumLogger;

public class MultipleElementLocator implements ElementLocator {
	private static final Logger log = SapiumLogger.get();
	private final SearchContext searchContext;
	private final boolean shouldCache;
	private final List<By> bys;
	private WebElement cachedElement;
	private List<WebElement> cachedElementList;

	public MultipleElementLocator(SearchContext searchContext, RepositoryByBuilder builder) {
		this.searchContext = searchContext;
		this.shouldCache = builder.isLookupCached();
		this.bys = builder.getBys();
	}

	@Override
	public WebElement findElement() {
		log.trace("begin");
		
		if(cachedElement != null && shouldCache) {
			log.trace("return with cachedElement: " + cachedElement);
			return cachedElement;
		}
		
		WebElement element = null;
		
		for(By by : bys) {
			try {
				element = searchContext.findElement(by);
				break;
			} catch (NoSuchElementException e) {
				log.trace("can't find element with locator: " + by);
			}
		}
		
		if(element == null) {
			String msg = "there is nothing found with all locators: " + bys;
			log.error(msg);
			throw new NoSuchElementException(msg);
		}
		
		if(shouldCache) {
			cachedElement = element;
			log.trace("cache element: " + element);
		}
		
		log.trace("end: return - " + element);
		
		return element;
	}

	@Override
	public List<WebElement> findElements() {
		log.debug("begin");
		
		if(cachedElementList != null && shouldCache) {
			log.debug("return with cachedElement: " + cachedElement);
			return cachedElementList;
		}
		
		List<WebElement> elements = null;
		
		for(By by : bys) {
			try {
				elements = searchContext.findElements(by);
				break;
			} catch (NoSuchElementException e) {
				log.debug("can't find element with locator: " + by);
			}
		}
		
		if(elements == null) {
			String msg = "can't find element with all locators";
			log.error(msg);
			throw new NoSuchElementException(msg);
		}
		
		if(shouldCache) {
			cachedElementList = elements;
			log.debug("cache element: " + elements);
		}
		
		log.debug("end: return - " + elements);
		
		return elements;
	}

	@Override
	public String toString() {
		return "locators: " + bys;
	}
}
