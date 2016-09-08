package com.sapium.parser.impl;

import com.sapium.parser.IParser;
import com.sapium.resource.IDataResource;

public abstract class AbstractParser implements IParser {
	protected IDataResource res;

	public AbstractParser() {
		
	}
	
	public AbstractParser(IDataResource res) {
		this.res = res;
	}

	public IDataResource getResource() {
		return res;
	}

	public void setResource(IDataResource res) {
		this.res = res;
	}

	public abstract <T> T parse(Class<T> clazz);

}
