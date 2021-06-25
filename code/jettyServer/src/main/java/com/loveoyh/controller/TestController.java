package com.loveoyh.controller;

import com.loveoyh.annotation.Controller;
import com.loveoyh.annotation.RequestMapping;

@Controller
public class TestController {

    @RequestMapping("/")
    public String hello(){
        return "Hello Word!";
    }

}
