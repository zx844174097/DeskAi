package com.mugui.sqlite.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import com.alibaba.fastjson.JSONObject;
import com.mugui.base.base.Component;
import com.mugui.sql.SqlModel;
import com.mugui.sql.TableMode;

@Component
public class Sqlite extends SqlModel {
	{
		selectSql("PRAGMA journal_mode = WAL;");
	}
	/**
	 * 查询所有表
	 * @auther 木鬼
	 * @return
	 */
	public List<String> selectTableNames() {
		TableMode selectBy = selectBy("table_names");
		ArrayList<String> list=new ArrayList<>();
		Iterator<JSONObject> iterator = selectBy.iterator();
		while(iterator.hasNext()) {
			JSONObject next = iterator.next();
			list.add(next.getString("name"));
		}
		return list;
	}
	/**
	 * 查询表所有字段
	 * @auther 木鬼
	 * @param table_name
	 * @return
	 */
	public List<String> selectTableFields(String table_name) {
		TableMode selectBy = selectSql("select * from `"+table_name+"` limit 1");
		return selectBy.getColumnNames();
	
	}
}
