package com.sapium.starter;

import java.util.List;

public interface IStarter {
	public void beforeStart(List<IBeforeStart> before);
	public void start();
	public void afterStart(List<IAfterEnd> after);
}
