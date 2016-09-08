package com.sapium.page.internal;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;

public class ObjectRepository {
	Map<String, List<By>> repo;
	
	
	public Map<String, List<By>> getRepoMap() {
		return repo;
	}
	
	public List<By> getBys(String elementName) {
		if(repo == null)
			return null;
		
		return repo.get(elementName);
	}
}
