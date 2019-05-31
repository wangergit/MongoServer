package com.mongodb.controller;
import org.bson.types.Decimal128;
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

import java.lang.reflect.Array;
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
    	Map<String,List<DBObject>> result = new HashMap<>();
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
    	Map<String,Long> result = new HashMap<>();
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
    	Map<String,List<DBObject>> result = new HashMap<>();
    	result.put("data", results);
        return result;
    }
    
    /**
     * 更新数据
     * @param model
     * @param request
     * table 表名称
     * key 查询字段
     * value 查询字段值
     * update 要更新的coordinates坐标集合
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/update",method = RequestMethod.POST)
    public Map<String,Object> update(Model model,HttpServletRequest request)
    {
    	JSONArray editItems = JSONArray.parseArray((String) request.getParameter("update"));
    	String table = (String) request.getParameter("table");
    	String status = mongoDao.update(table, (String) request.getParameter("key"),(String) request.getParameter("value"), editItems, false, true);
    	Map<String,Object> result = new HashMap<>();
    	result.put("data", status);
        return result;
    }
    
    /**
     * 保存数据
     * @param model
     * @param request
     * table ： 表名称
     * save ： geojson字符串
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/save",method = RequestMethod.POST)
    public Map<String,Object> save(Model model,HttpServletRequest request)
    {
    	JSONObject saveItems = JSONObject.parseObject((String) request.getParameter("save"));
    	String table = (String) request.getParameter("table");
    	String status = mongoDao.save(table, saveItems);
    	Map<String,Object> result = new HashMap<>();
    	result.put("data", status);
        return result;
    }
    
    /**
     * 删除数据
     * @param model
     * @param request
     * table : 表名称
     * key : 属性字段
     * value ： 属性值
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/delete",method = RequestMethod.POST)
    public Map<String,Object> delete(Model model,HttpServletRequest request)
    {
    	String table = (String) request.getParameter("table");
    	String key = (String) request.getParameter("key");
    	String value = (String) request.getParameter("value");
    	int status = mongoDao.delete(table, key , value);
    	Map<String,Object> result = new HashMap<>();
    	result.put("data", status);
        return result;
    }
}