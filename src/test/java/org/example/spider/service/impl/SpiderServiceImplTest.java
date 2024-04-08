package org.example.spider.service.impl;


import org.example.spider.SpiderApplication;
import org.example.spider.service.SpiderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SpiderApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SpiderServiceImplTest {

    @Autowired
    SpiderService service;

    @Test
    public void start() {
        service.start();
    }

    @Test
    public void getPageText() {
    }

    @Test
    public void getUrls() {
    }

    @Test
    public void getTitle() {
    }

    @Test
    public void getContent() {
    }
}