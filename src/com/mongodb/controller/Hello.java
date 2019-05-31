package com.mongodb.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.dao.Point;
import com.mongodb.service.MongoDaoImpl;
import com.sun.xml.internal.txw2.Document;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

@Controller
public class Hello 
{
	@Autowired
	private MongoDaoImpl mongoDao;	
    @RequestMapping("/hello")
    public String hello(Model model)
    {
        model.addAttribute("msg","hello springmvc!");
        return "index";
    }
    
    @ResponseBody  
    @RequestMapping("/near")
    /**
     * 查找附近点
     * @param model
     * @param table  表名
     * @param x 经度
     * @param y  纬度
     * @param distance  距离  单位：米
     * @param limit 条数
     * @return
     */
    public Map<String,List<DBObject>> near(Model model,String table,double x,double y,int distance,int limit)
    {
    	DBObject query = new BasicDBObject();
    	Point point = new Point();
    	point.setLng(x);
    	point.setLat(y);
    	List<DBObject> list = mongoDao.geoNear(table, query, point, limit, (long) distance);
    	Map<String,List<DBObject>> result = new HashMap();
    	result.put("data", list);
        return result;
    }
    
    /**
     * 查询数据表中数据条目
     * @param model
     * @param table  表名
     * @return
     */
    @ResponseBody
    @RequestMapping("/count")
    public Map<String,Long> count(Model model,String table)
    {
    	DBObject query = new BasicDBObject();
    	Long count = mongoDao.count(table, query);
    	Map<String,Long> result = new HashMap();
    	result.put("count", count);
        return result;
    }
    
    /**
     * 查询表中所有数据
     * @param model
     * @param table  表名
     * @param orderFiled
     * @return
     */
    @ResponseBody
    @RequestMapping("/find")
    public Map<String,List<DBObject>> find(Model model,String table,String orderFiled)
    {
    	DBObject query = new BasicDBObject();
    	DBObject order = new BasicDBObject();
    	if(!orderFiled.equals("")) {
    		order.put(orderFiled, 0);
    	}
    	DBObject fields = new BasicDBObject();
    	List<DBObject> results = mongoDao.find(table, query, fields, order, 1000);
    	Map<String,List<DBObject>> result = new HashMap();
    	result.put("data", results);
        return result;
    }
    
    @ResponseBody
    @RequestMapping(value="/update",method = RequestMethod.POST)
    public Map<String,Object> update(Model model,HttpServletRequest request)
    {
    	String status = "success";
    	//JSONObject editItems = (JSONObject) JSONObject.parse((String) request.getParameter("update"));
    	JSONArray editItems = JSONArray.parseArray((String) request.getParameter("update"));
    	String table = (String) request.getParameter("table");
    	
    	DBObject query = new BasicDBObject();
    	query.put((String) request.getParameter("key"), (String) request.getParameter("value"));
    	
    	DBObject update = new BasicDBObject();
    	JSONObject items = new JSONObject();
    	
    	//List<List<double[]>> polygons = new LinkedList<>();
//    	for(int i = 0 ; i < editItems.size() ; i ++) {
//    		JSONArray job = editItems.getJSONArray(i);
//    		if(job.size() > 0) {
//    			for(int j = 0 ; j < job.size() ; j ++) {
//    	    		JSONArray job1 = job.getJSONArray(j);
//    	    		if(job1.size() > 0) {
//    	    			for(int k = 0 ; k < job1.size() ; k ++) {
//    	    				job1.get(k) = new Decimal128(job1.getBigDecimal(k));
//    	    	    	}
//    	    		}
//    	    	}
//    		}
//    	}
        //polygons.add((List<double[]>) editItems.get(0));
        items.put("geometry.coordinates", editItems);
        update.put("$set",items);
    	//update.put("$set", new BasicDBObject("geometry",
                //new BasicDBObject("type","Polygon")
                //.append("coordinates",polygons)));
    	try {
    		mongoDao.update(table, query, update, false, true);
    	}catch(Exception e) {
    		status = "error";
    		e.printStackTrace();
    	}
    	Map<String,Object> result = new HashMap();
    	result.put("data", status);
        return result;
    }
}