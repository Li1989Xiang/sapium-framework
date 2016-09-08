package com.sapium.pagefactory;

import java.lang.reflect.Field;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.support.pagefactory.AbstractAnnotations;

import com.sapium.page.internal.ObjectRepository;

public class RepositoryByBuilder extends AbstractAnnotations {
	private static ObjectRepository globalRepo;
	private ObjectRepository localRepo;
	private Field field;
	
	public RepositoryByBuilder(ObjectRepository localRepo, ObjectRepository globalRepo) {
		this.localRepo = localRepo;
		RepositoryByBuilder.globalRepo = globalRepo;
	}

	public void setField(Field field) {
		this.field = field;
	}

	@Override
	public By buildBy() {
		By by = null;
		List<By> bys = getBys();
		
		if(bys != null && bys.size() >= 0)
			by = bys.get(0);
		
		return by;
	}

	@Override
	public boolean isLookupCached() {
		return false;
	}

	public List<By> getBys() {
		String name = field.getName();
		List<By> ls = localRepo.getBys(name);
		
		if(ls == null) {
			//本地对象库中不存在则读取全局对象库
			if(globalRepo != null) {
				ls = globalRepo.getBys(name);
			}
		}
		
		if(ls == null) {
			throw new IllegalArgumentException("can't find object in repository: " + name);
		}
		
		return ls;
	}
}
