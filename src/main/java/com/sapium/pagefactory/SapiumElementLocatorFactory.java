package com.sapium.pagefactory;

import java.lang.reflect.Field;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

public class SapiumElementLocatorFactory implements ElementLocatorFactory {
	private SearchContext searchContext;
	private RepositoryByBuilder builder;

	public SapiumElementLocatorFactory(SearchContext searchContext, RepositoryByBuilder builder) {
		this.searchContext = searchContext;
		this.builder = builder;
	}
	
	@Override
	public ElementLocator createLocator(Field field) {
		builder.setField(field);
		return new MultipleElementLocator(searchContext, builder);
	}
}
