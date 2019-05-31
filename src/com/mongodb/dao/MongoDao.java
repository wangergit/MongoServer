package com.mongodb.dao;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.DBObject;
public interface  MongoDao {
	public DBObject findOne(String collection, DBObject query, DBObject fields);
	public List<DBObject> find(String collection, DBObject query, DBObject fields, DBObject orderBy, int pageNum, int pageSize);
	public List<DBObject> find(String collection, DBObject query, DBObject fields, DBObject orderBy, int limit);
	public int delete(String collection,String key, String value);
	public String save(String collection, JSONObject saveItems);
	public String update(String collection, String key, String value, JSONArray editItems, boolean upsert, boolean multi);
	public Long count(String collection, DBObject query);
	public List<?> distinct(String collection, String key, DBObject query);
	JSONArray DecimalToDouble(JSONArray editItems);

}
