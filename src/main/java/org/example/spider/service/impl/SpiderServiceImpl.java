package org.example.spider.service.impl;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.spider.service.SpiderService;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class SpiderServiceImpl implements SpiderService {
    int count = 0;
    @Override
    public void start() {
        String startUrl = "https://cn.bing.com/search?q=%E7%88%AC%E8%99%AB";
        String pageText = getPageText(startUrl);
        String title = getTitle(pageText);
        List<String> urls = getUrls(pageText);
        String content = getContent(pageText);
        log.info(title);
        log.info(urls.toString());
        log.info(content);
        for (String url : urls) {
            String pageText1 = getPageText(url);
            log.info("{}count: {}", getTitle(pageText1), count++);
        }
    }

    @Override
    public String getPageText(String url) {
        String res = "";
        try {
            res = HttpUtil.get(url);
        }catch (Exception e){
            log.info(e.getMessage());
        }
        return res;
    }

    @Override
    public List<String> getUrls(String pageText) {
        String regexUrl = "(http|https|ftp)://[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,3}(\\/\\S*)?";
        List<String> urls = new ArrayList<>();
        Pattern pattern = Pattern.compile(regexUrl);
        Matcher matcher = pattern.matcher(pageText);

        while (matcher.find()) {
            urls.add(matcher.group());
        }
        return urls;
    }

    @Override
    public String getTitle(String pageText) {
        String patternString = "<title>(.*?)</title>";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(pageText);
        if (matcher.find()) {
            // 获取匹配项的第一个捕获组（即标题内容）
            return matcher.group(1);
        } else {
            return "";
        }
    }

    @Override
    public String getContent(String pageText) {
        return pageText;
    }

}
