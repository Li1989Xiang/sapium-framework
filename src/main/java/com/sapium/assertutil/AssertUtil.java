package com.sapium.assertutil;

import java.lang.reflect.Array;

public class AssertUtil {
	
	public static void assertEquals(Object expect, Object actual) {
		//array
		if(expect != null && expect.getClass().isArray()) {
			assertArrayEquals(actual, expect);
			return;
		}
		assertEqualsImpl(actual, expect);
	}

	public static void assertNotEquals(Object expect, Object actual) {
		boolean fail = false;
		try {
			assertEquals(expect, actual);
			fail = true;
		} catch (AssertFailException e) {
		}

		if (fail) {
			throw new AssertFailException(expect, actual,
					"expect not equal, but equal");
		}
	}
	
	public static void assertTrue(boolean actual) {
		assertEquals(Boolean.TRUE, Boolean.valueOf(actual));
	}
	
	public static void assertFalse(boolean actual) {
		assertEquals(Boolean.FALSE, Boolean.valueOf(actual));
	}
	
	public static void assertCondition(boolean actual, boolean condition) {
		assertEquals(condition, Boolean.valueOf(actual));
	}
	
	public static void assertNull(Object actual) {
		assertEquals(null, actual);
	}
	
	public static void assertNotNull(Object actual) {
		if(actual == null) {
			throw new AssertFailException("not null", actual);
		}
	}
	
	public static void assertContains(String actual, String expect) {
		if(!actual.contains(expect)) {
			throw new AssertFailException(expect, actual);
		}
	}
	
	private static void assertArrayEquals(Object actual, Object expect) {
		if (expect == actual) {
			return;
		}
		if (null == expect) {
			throw new AssertFailException(expect, actual,
					"expect a null array, but not null found");
		}
		if (null == actual) {
			throw new AssertFailException(expect, actual,
					"expect not null array, but null found");
		}
		//is called only when expected is an array
		if (actual.getClass().isArray()) {
			int expectedLength = Array.getLength(expect);
			if (expectedLength == Array.getLength(actual)) {
				for (int i = 0 ; i < expectedLength ; i++) {
					Object _actual = Array.get(actual, i);
					Object _expected = Array.get(expect, i);
					try {
						assertEquals(_actual, _expected);
					} catch (AssertionError ae) {
						throw new AssertFailException(expect, actual,
								"values at index " + i + " are not the same");
					}
				}
				//array values matched
				return;
			} else {
				throw new AssertFailException(expect, actual,
						"Array lengths are not the same");
			}
		}
		throw new AssertFailException(expect, actual);
	}
	

	private static void assertEqualsImpl(Object actual, Object expect) {
		//both are null
		if((expect == null) && (actual == null)) {
			return;
		}
		//one is null
		if(expect == null ^ actual == null) {
			throw new AssertFailException(expect, actual);
		}
		//both are not null
		if (expect.equals(actual) && actual.equals(expect)) {
			return;
		}
		
		throw new AssertFailException(expect, actual);
	}
}
