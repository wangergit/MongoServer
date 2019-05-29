package com.mongodb.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.dao.Point;
import com.mongodb.service.MongoDaoImpl;

import java.util.List;

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
    List<DBObject>   near(Model model,String ss)
    {
    	DBObject query = new BasicDBObject();
    	Point point = new Point();
    	point.setLng(-73.871150);
    	point.setLat(40.6730975);
    	int limit = 5;
    	Long maxDistance = 5000L; // รื
    	List<DBObject> list = mongoDao.geoNear("restaurants", query, point, limit, maxDistance);
    	for(DBObject obj : list)
    	   System.out.println(obj);

        //model.addAttribute("results",list);
        return list;
       
    }
    @RequestMapping("/count")
    public String count(Model model)
    {
    	DBObject query = new BasicDBObject();
    	Long count = mongoDao.count("restaurants", query);
    	
        model.addAttribute("msg",count);
        return "index";
    }
}