package com.sapium.pagefactory;

import java.lang.reflect.InvocationTargetException;

public class ThrowableUtil {

	public static Throwable extractReadableException(Throwable e) {
		if (!RuntimeException.class.equals(e.getClass()) && !InvocationTargetException.class.equals(e.getClass())) {
			return e;
		}

		return extractReadableException(e.getCause());
	}
}
