package com.zf.demo.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zf.demo.service.IModifyService;
import com.zf.demo.service.IQueryService;
import com.zf.spring.framework.annotation.AutoWired;
import com.zf.spring.framework.annotation.Controller;
import com.zf.spring.framework.annotation.RequestMapping;
import com.zf.spring.framework.annotation.RequestParam;


@Controller
@RequestMapping("/web")
public class MyAction {

	@AutoWired
	IQueryService queryService;
	@AutoWired
	IModifyService modifyService;

	@RequestMapping("/query.json")
	public void query(HttpServletRequest request, HttpServletResponse response,
								@RequestParam("name") String name){
		String result = queryService.query(name);
		out(response,result);
	}
	
	@RequestMapping("/add*.json")
	public void add(HttpServletRequest request,HttpServletResponse response,
			   @RequestParam("name") String name,@RequestParam("addr") String addr){
		String result = modifyService.add(name,addr);
		out(response,result);
	}
	
	@RequestMapping("/remove.json")
	public void remove(HttpServletRequest request,HttpServletResponse response,
		   @RequestParam("id") Integer id){
		String result = modifyService.remove(id);
		out(response,result);
	}
	
	@RequestMapping("/edit.json")
	public void edit(HttpServletRequest request,HttpServletResponse response,
			@RequestParam("id") Integer id,
			@RequestParam("name") String name){
		String result = modifyService.edit(id,name);
		out(response,result);
	}
	
	
	
	private void out(HttpServletResponse resp,String str){
		try {
			resp.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
