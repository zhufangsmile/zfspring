package com.zf.demo.action;

import com.zf.demo.service.IQueryService;
import com.zf.spring.framework.annotation.AutoWired;
import com.zf.spring.framework.annotation.Controller;
import com.zf.spring.framework.annotation.RequestMapping;
import com.zf.spring.framework.annotation.RequestParam;
import com.zf.spring.framework.webmvc.servlet.ModelAndView;
//import com.gupaoedu.vip.spring.framework.webmvc.servlet.GPModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * 公布接口url
 * @author Tom
 *
 */
@Controller
@RequestMapping("/")
public class PageAction {

    @AutoWired
    IQueryService queryService;

    @RequestMapping("/first.html")
    public ModelAndView query(@RequestParam("teacher") String teacher){
        String result = queryService.query(teacher);
        Map<String,Object> model = new HashMap<String,Object>();
        model.put("teacher", teacher);
        model.put("data", result);
        model.put("token", "123456");
        return new ModelAndView("first.html",model);
    }

}
