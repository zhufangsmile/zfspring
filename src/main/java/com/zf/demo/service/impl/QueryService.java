package com.zf.demo.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.zf.demo.service.IQueryService;
import com.zf.spring.framework.annotation.Service;

@Service
public class QueryService implements IQueryService {

	public String query(String name) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		String json = "{name:\"" + name + "\",time:\"" + time + "\"}";
		System.out.println("这是在业务方法中打印的：" + json);
		return json;
	}

}
