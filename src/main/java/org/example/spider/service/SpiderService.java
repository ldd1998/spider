package org.example.spider.service;

import java.util.List;

public interface SpiderService {
    void start();
    String getPageText(String url);
    String getContent(String pageText);
    List<String> getUrls(String pageText);
    String getTitle(String pageText);
}
