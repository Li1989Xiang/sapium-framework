package com.sapium.assertutil;

public class AssertFailException extends RuntimeException {
	private static final long serialVersionUID = 3029259308057170271L;
	private Object expect;
	private Object actual;

	public AssertFailException(Object expect, Object actual) {
		super("expect " + expect + " but actual is " + actual);
		
		this.expect = expect;
		this.actual = actual;
	}
	
	public AssertFailException(Object expect, Object actual, String message) {
		super(message);
		
		this.expect = expect;
		this.actual = actual;
	}
	
	public Object getExpect() {
		return expect;
	}
	
	public Object getActual() {
		return actual;
	}
}
