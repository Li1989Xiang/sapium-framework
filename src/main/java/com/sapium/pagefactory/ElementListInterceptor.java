package com.sapium.pagefactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class ElementListInterceptor implements MethodInterceptor {
	private final ElementLocator locator;
	
	ElementListInterceptor(ElementLocator locator){
		this.locator = locator;
	}

	protected Object getObject(List<WebElement> elements, Method method, Object[] args) throws Throwable {
		try {
			return method.invoke(elements, args);
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
		
		ArrayList<WebElement> realElements = new ArrayList<WebElement>();
		realElements.addAll(locator.findElements());
		return getObject(realElements, method, args);
	}
}
