package org.example.spider.controller;

import org.example.spider.service.SpiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpiderController {
    @Autowired
    SpiderService spiderService;
    @GetMapping("/start")
    public void start(){
        spiderService.start();
    }
}
