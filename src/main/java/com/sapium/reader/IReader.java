package com.sapium.reader;

import java.util.List;
import java.util.Map;

public interface IReader {
	/**
	 * 切换数据分组，如Excel中的Sheet
	 * @param name 分组名
	 */
	void changeGroup(String name);
	/**
	 * 获取index行的下一条索引
	 * @param index 行索引
	 * @return 成功返回行索引，失败返回-1
	 */
	int getNextIndex(int index);
	/**
	 * 按照行索引，以<列标题，值>的形式获取数据
	 * @param index 行索引
	 * @return 返回该行数据
	 */
	Map<String, String> readLineWithTitle(int index);
	/**
	 * 获取整行数据
	 * @param index 行索引
	 * @return 返回该行数据
	 */
	List<String> readLine(int index);
}
