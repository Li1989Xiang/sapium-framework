package com.sapium.pagefactory;

import static io.appium.java_client.pagefactory.utils.ProxyFactory.getEnhancedProxy;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchableElement;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.ios.IOSElement;
import io.appium.java_client.pagefactory.utils.WebDriverUnpackUtility;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.FieldDecorator;

public class SapiumFieldDecorator implements FieldDecorator {
	private static final List<Class<? extends WebElement>> availableElementClasses =
			new ArrayList<Class<? extends WebElement>>() {
				private static final long serialVersionUID = 1L;
				{
					add(WebElement.class);
					add(RemoteWebElement.class);
					add(MobileElement.class);
					add(TouchableElement.class);
					add(AndroidElement.class);
					add(IOSElement.class);
				}
			};
			
	private final static Map<Class<? extends SearchContext>, Class<? extends WebElement>> elementRuleMap =
			new HashMap<Class<? extends SearchContext>, Class<? extends WebElement>>() {
				private static final long serialVersionUID = 1L;
				{
					put(AndroidDriver.class, AndroidElement.class);
					put(IOSDriver.class, IOSElement.class);
				}
			};
			
	private ElementLocatorFactory factory;
	private final WebDriver originalDriver;

	public SapiumFieldDecorator(SearchContext context, ElementLocatorFactory factory) {
		this.factory = factory;
		this.originalDriver = WebDriverUnpackUtility.unpackWebDriverFromSearchContext(context);
	}
	
	@Override
	public Object decorate(ClassLoader loader, Field field) {
		if (!(WebElement.class.isAssignableFrom(field.getType())
				|| isDecoratableList(field))) {
			return null;
		}
			
		ElementLocator locator = factory.createLocator(field);
		
		if (locator == null) {
			return null;
		}
		
		if (WebElement.class.isAssignableFrom(field.getType())) {
			return proxyForLocator(loader, locator);
		} else if (List.class.isAssignableFrom(field.getType())) {
			return proxyForListLocator(loader, locator);
		} else {
			return null;
		}
	}

	private boolean isDecoratableList(Field field) {
		if (!List.class.isAssignableFrom(field.getType())) {
			return false;
		}

		// Type erasure in Java isn't complete. Attempt to discover the generic
		// type of the list.
		Type genericType = field.getGenericType();
		
		if (!(genericType instanceof ParameterizedType)) {
			return false;
		}

		Type listType = ((ParameterizedType) genericType).getActualTypeArguments()[0];

		boolean result = false;
		
		for (Class<? extends WebElement> webElementClass : availableElementClasses) {
			if (!webElementClass.equals(listType)) {
				continue;
			}
			
			result = true;
			break;
		}
		
		return result;
	}

	private Object proxyForLocator(ClassLoader loader, ElementLocator locator) {
		ElementInterceptor elementInterceptor = new ElementInterceptor(locator, originalDriver);
        return (WebElement) getEnhancedProxy(getTypeForProxy(), elementInterceptor);
	}
	
	private Object proxyForListLocator(ClassLoader loader,
			ElementLocator locator) {
		ElementListInterceptor elementInterceptor = new ElementListInterceptor(locator);
		return  getEnhancedProxy(ArrayList.class,
				elementInterceptor);
	}
	
	private Class<?> getTypeForProxy() {
		Class<? extends SearchContext> driverClass = originalDriver.getClass();
		Iterable<Map.Entry<Class<? extends SearchContext>, Class<? extends WebElement>>> rules = elementRuleMap.entrySet();
		//it will return MobileElement subclass when here is something
		for (Map.Entry<Class<? extends SearchContext>, Class<? extends WebElement>> e : rules) {
			//that extends AppiumDriver or MobileElement
			if (e.getKey().isAssignableFrom(driverClass)) {
				return e.getValue();
			}
		} //it is compatible with desktop browser. So at this case it returns RemoteWebElement.class
		return RemoteWebElement.class;
    }
}
