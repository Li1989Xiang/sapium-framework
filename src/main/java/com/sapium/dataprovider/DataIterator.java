package com.sapium.dataprovider;

import java.util.Iterator;

import com.sapium.reader.IReader;
import com.sapium.reader.ReaderFactory;
import com.sapium.resource.IDataResource;

public class DataIterator implements Iterator<Object[]> {
	private IReader reader;
	private int currentIndex;
	
	public DataIterator(IDataResource res, String name) {
		reader = ReaderFactory.getReader(res);
		setGroup(name);
	}
	
	public void setGroup(String name) {
		reader.changeGroup(name);
	}

	@Override
	public boolean hasNext() {
		return reader.getNextIndex(currentIndex) > 0;
	}

	@Override
	public Object[] next() {
		currentIndex = reader.getNextIndex(currentIndex);
		return new Object[]{
				reader.readLineWithTitle(currentIndex)
		};
	}

}
