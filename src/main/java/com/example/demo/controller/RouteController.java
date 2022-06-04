package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RouteController {
    @RequestMapping(value = "/FileUpload/Index")
    public ModelAndView index() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/FileUpload/Index");
        return mv;
    }

    @RequestMapping(value = "/ImageUpload/Upload")
    public ModelAndView ImageUpload() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/ImageUpload/Upload");
        return mv;
    }

    @RequestMapping(value = "/BigFileUpload/Index")
    public ModelAndView BigFileUpload() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/BigFileUpload/Index");
        return mv;
    }

    @RequestMapping(value = "/MultiPicker/Index")
    public ModelAndView MultiPicker() {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/MultiPicker/Index");
        return mv;
    }
}
