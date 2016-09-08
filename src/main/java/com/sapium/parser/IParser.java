package com.sapium.parser;

public interface IParser {
	public <T> T parse(Class<T> clazz);
}
