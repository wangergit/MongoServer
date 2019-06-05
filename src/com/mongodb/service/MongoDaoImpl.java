package com.mongodb.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.AggregationOptions;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.dao.*;

import org.bson.types.Decimal128;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
 
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
 
/**
 * mongodb 服务实现类
 *
 * @author babylon
 * @version 1.1
 * @date 2016年7月12日-下午1:36:50
 */
@Repository
public  class MongoDaoImpl implements MongoGeoDao {
	
    private static Logger logger = LoggerFactory.getLogger(MongoDaoImpl.class);
    
    @Autowired
    private MongoTemplate mongoTemplate;
 
    @Override
    public DBObject findOne(String collection, DBObject query, DBObject fields) {
        return mongoTemplate.getCollection(collection).findOne(query, fields);
    }
 
    @Override
    public List<DBObject> find(String collection, DBObject query, DBObject fields, DBObject orderBy, int pageNum, int pageSize) {
        List<DBObject> list = new ArrayList<>();
        Cursor cursor = mongoTemplate.getCollection(collection).find(query, fields).skip((pageNum - 1) * pageSize).limit(pageSize).sort(orderBy);
        while (cursor.hasNext()) {
            list.add(cursor.next());
        }
        return list.size() > 0 ? list : null;
    }
 
    @Override
    public List<DBObject> find(String collection, DBObject query, DBObject fields, DBObject orderBy, int limit) {
        List<DBObject> list = new ArrayList<>();
        Cursor cursor = mongoTemplate.getCollection(collection).find(query, fields).sort(orderBy).limit(limit);
        while (cursor.hasNext()) {
            list.add(cursor.next());
        }
        return list.size() > 0 ? list : null;
    }
 
    @Override
    public int delete(String collection,String[] key, String[] value) {
    	int status = 0;
    	DBObject delete = new BasicDBObject();
    	if(key != null && value != null) {
    		for (int i = 0; i < key.length; i++) {
            	delete.put(key[i], value[i]);
    		}
    	}
    	try {
    		mongoTemplate.getCollection(collection).remove(delete);
    		status = 1;
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
        return status;
    }
 
    @Override
    public String save(String collection, JSONObject saveItems) {
    	String status = "success";
    	DBObject save = new BasicDBObject();
    	
    	JSONObject geom = (JSONObject) saveItems.get("geometry");
    	JSONArray coordinates = (JSONArray) geom.get("coordinates");
    	geom.put("coordinates", DecimalToDouble(coordinates));
    	
    	save.putAll(saveItems);
    	save.put("geometry", geom);
    	try {
            mongoTemplate.getCollection(collection).save(save);
    	}catch(Exception e) {
    		status = "error";
    		e.printStackTrace();
    	}
        return status;
    }
 
    @Override
    public Long count(String collection, DBObject query) {
        return mongoTemplate.getCollection(collection).count(query);
    }
 
	@Override
    public List<?> distinct(String collection, String key, DBObject query) {
        return mongoTemplate.getCollection(collection).distinct(key, query);
    }
 
    @Override
    public List<DBObject> geoNear(String collection, DBObject query, Point point, int limit, long maxDistance) {
        if(query==null)
            query = new BasicDBObject();
 
        List<DBObject> pipeLine = new ArrayList<>();
        BasicDBObject aggregate = new BasicDBObject("$geoNear",
	    		new BasicDBObject("near",new BasicDBObject("type","Point").append("coordinates",new double[]{point.getLng(), point.getLat()}))
				        .append("distanceField","properties.distance")
				        .append("query", new BasicDBObject())
				        .append("num", 5)
				        .append("maxDistance", maxDistance)
				        .append("spherical",true)
	    		);
        pipeLine.add(aggregate);
        Cursor cursor=mongoTemplate.getCollection(collection).aggregate(pipeLine, AggregationOptions.builder().outputMode(AggregationOptions.OutputMode.CURSOR).build());
        List<DBObject> list = new LinkedList<>();
        while (cursor.hasNext()) {
            list.add(cursor.next());
        }
        return list;
    }
 
    @Override
    public List<DBObject> withinCircle(String collection,String locationField, Point center,
                                       long radius, DBObject fields, DBObject query, int limit) {
        LinkedList<Object> circle = new LinkedList<>();
        //Set the center coordinate
        circle.addLast(new double[]{center.getLng(),center.getLat()});
        //Set the radius. unit:meter
        circle.addLast(radius/6378137.0);
 
        if(query==null)
            query = new BasicDBObject();
        query.put(locationField, new BasicDBObject("$geoWithin", new BasicDBObject("$centerSphere", circle)));
        logger.info("withinCircle:{}",query.toString());
        return mongoTemplate.getCollection(collection).find(query, fields).limit(limit).toArray();
    }
 
    @Override
    public List<DBObject> nearSphere(String collection, String locationField, Point center,
                                     long minDistance, long maxDistance, DBObject query, DBObject fields, int limit) {
        if(query==null)
            query = new BasicDBObject();
 
        query.put(locationField,
                new BasicDBObject("$nearSphere",
                    new BasicDBObject("$geometry",
                            new BasicDBObject("type","Point")
                                    .append("coordinates",new double[]{center.getLng(),center.getLat()}))
                            .append("$minDistance",minDistance)
                            .append("$maxDistance",maxDistance)
        ));
        logger.info("nearSphere:{}",query.toString());
        return mongoTemplate.getCollection(collection).find(query, fields).limit(limit).toArray();
    }
 
    @Override
    public List<DBObject> withinPolygon(String collection, String locationField,
                                        List<double[]> polygon, DBObject fields, DBObject query, int limit) {
        if(query==null)
            query = new BasicDBObject();
 
        List<List<double[]>> polygons = new LinkedList<>();
        polygons.add(polygon);
        query.put(locationField, new BasicDBObject("$geoWithin",
                new BasicDBObject("$geometry",
                        new BasicDBObject("type","Polygon")
                        .append("coordinates",polygons))));
        logger.info("withinPolygon:{}",query.toString());
        return mongoTemplate.getCollection(collection).find(query, fields).limit(limit).toArray();
    }
 
    @Override
    public List<DBObject> withinMultiPolygon(String collection, String locationField, List<List<double[]>> polygons, DBObject fields, DBObject query, int limit) {
        if(query==null)
            query = new BasicDBObject();
 
        List<List<List<double[]>>> list = new LinkedList<>();
        for (List<double[]> polygon : polygons) {
            List<List<double[]>> temp = new LinkedList<>();
            temp.add(polygon);
            list.add(temp);
        }
        query.put(locationField, new BasicDBObject("$geoWithin",
                new BasicDBObject("$geometry",
                        new BasicDBObject("type","MultiPolygon")
                                .append("coordinates",list))));
        logger.info("withinMultiPolygon:{}",query.toString());
        return mongoTemplate.getCollection(collection).find(query, fields).limit(limit).toArray();
    }
 
    @Override
    public List<DBObject> withinBox(String collection, String locationField, Point bottomLeft, Point upperRight, DBObject fields, DBObject query, int limit) {
        if(query==null)
            query = new BasicDBObject();
 
        LinkedList<double[]> box = new LinkedList<>();
        box.add(new double[]{bottomLeft.getLng(), bottomLeft.getLat()});
        box.add(new double[]{upperRight.getLng(), upperRight.getLat()});
 
        query.put(locationField, new BasicDBObject("$geoWithin", new BasicDBObject("$box", box)));
        logger.info("withinBox:{}",query.toString());
        return mongoTemplate.getCollection(collection).find(query, fields).limit(limit).toArray();
    }

    //更新空间范围geometry
    @Override
	public String update(String collection, String key, String value, JSONArray editItems, boolean upsert, boolean multi) {
		String status = "success";
		//查询条件
    	DBObject query = new BasicDBObject();
    	query.put(key,value);
    	//更新内容
    	DBObject update = new BasicDBObject();
    	JSONObject items = new JSONObject();
    	items.put("geometry.coordinates", DecimalToDouble(editItems));
    	update.put("$set",items);
    	try {
    		mongoTemplate.getCollection(collection).update(query, update, upsert, multi);
    	}catch(Exception e) {
    		status = "error";
    		e.printStackTrace();
    	}
        return status;
	}
    
    //更新属性prototype
    @Override
	public String updatePrototype(String table, String[] key, String[] value, JSONObject prototype, boolean b,boolean c) {
		String status = "success";
		//查询条件
    	DBObject query = new BasicDBObject();
    	if(key != null && value != null) {
    		for (int i = 0; i < key.length; i++) {
    			query.put(key[i],value[i]);
			}
    	}
    	//更新内容
    	DBObject update = new BasicDBObject();
    	JSONObject items = new JSONObject();
    	items.put("properties",prototype);
    	update.put("$set",items);//只更新prototype字段
    	try {
    		mongoTemplate.getCollection(table).update(query, update, b, c);
    	}catch(Exception e) {
    		status = "error";
    		e.printStackTrace();
    	}
		return status;
	}
    
    //将Decimal128转换成double来存储
    @Override
    public JSONArray DecimalToDouble(JSONArray editItems) {
    	for(int i = 0 ; i < editItems.size() ; i ++) {
    		try {
    			JSONArray job = editItems.getJSONArray(i);
        		if(job.size() > 0) {
        			for(int j = 0 ; j < job.size() ; j ++) {
        	    		JSONArray job1 = job.getJSONArray(j);
        	    		if(job1.size() > 0) {
        	    			for(int k = 0 ; k < job1.size() ; k ++) {
        	    				Decimal128 s = new Decimal128(job1.getBigDecimal(k));
        	    				job1.set(k, Double.valueOf(s.toString()));
        	    	    	}
        	    		}
        	    	}
        		}
    		}catch(Exception e) {
    			Decimal128 s = new Decimal128(editItems.getBigDecimal(i));
    			editItems.set(i, Double.valueOf(s.toString()));
    		}
    	}
    	return editItems;
    }
    
    /**
     * 字符串转数组
     */
    @Override
    public String[] stringToArr(String str){
    	String[] arr = str.split(","); // 用,分割
    	return arr;
    }
 
}
