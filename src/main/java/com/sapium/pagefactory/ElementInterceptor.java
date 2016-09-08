package com.sapium.pagefactory;

import java.lang.reflect.Method;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsDriver;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class ElementInterceptor implements MethodInterceptor {
	private final ElementLocator locator;
	private final WebDriver driver;
    
	ElementInterceptor(ElementLocator locator, WebDriver driver) {
		this.locator = locator;
		this.driver = driver;
	}

	protected Object getObject(WebElement element, Method method, Object[] args) throws Throwable {
		try {
			return method.invoke(element, args);
		}
		catch (Throwable t){
			throw ThrowableUtil.extractReadableException(t);
		}
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if(Object.class.equals(method.getDeclaringClass())) {
			return proxy.invokeSuper(obj, args);
		}
		
		if (WrapsDriver.class.isAssignableFrom(method.getDeclaringClass()) &&
				method.getName().equals("getWrappedDriver")) {
			return driver;
		}
		
		if("toString".equals(method.getName())) {
			return this.toString();
		}
		
		WebElement realElement = locator.findElement();
		return getObject(realElement, method, args);
	}

	@Override
	public String toString() {
		return String.format("{[%s] -> %s}", driver, locator);
	}
}
