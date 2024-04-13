package org.example.spider.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.spider.entity.Content;
import org.example.spider.mapper.ContentMapper;
import org.example.spider.service.SpiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class SpiderServiceImpl implements SpiderService {
    @Autowired
    ContentMapper contentMapper;
    ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(50, 500, 10, TimeUnit.HOURS, new LinkedBlockingDeque<>());
    @Autowired
    private ThreadPoolExecutor messageConsumeDynamicExecutor;
    @Autowired
private ThreadPoolExecutor messageProduceDynamicExecutor;

    @Override
    public void start() {
        // https://cn.bing.com/search?q=%E7%88%AC%E8%99%AB
        // 获取数据库中status为0的数据
        List<Content> contents = contentMapper.selectList(new QueryWrapper<Content>().eq("status", 0).orderByAsc("id").last("limit 500"));
        if (CollectionUtil.isNotEmpty(contents)) {
            CountDownLatch countDownLatch = new CountDownLatch(contents.size());
            for (Content content : contents) {
                messageProduceDynamicExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        startOneContent(content);
                        countDownLatch.countDown();
                    }
                });
            }
            try {
                countDownLatch.await();
                log.info("执行完成："+contents.size());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void startOneContent(Content content) {
        // 获取该页面信息
        String url = content.getUrl();
        String pageText = getPageText(url);
        content.setTitle(getTitle(pageText));
        content.setContent(getContent(pageText));
        content.setStatus(1);
        contentMapper.updateById(content);
        // 保存该页面下urls
        List<String> urls = getUrls(pageText);
//        log.info("当前第{}个页面，内容大小{}，获取到url数量{}", content.getId(), getContent(pageText).length(), urls.size());
        if (CollectionUtil.isNotEmpty(urls)) {
            for (String urlOne : urls) {
                Content contentEntity = new Content();
                contentEntity.setUrl(urlOne);
                contentEntity.setParentId(content.getId());
                contentMapper.insert(contentEntity);
            }
        }
    }

    @Override
    public String getPageText(String url) {
        String res = "";
        try {
            res = HttpUtil.get(url);
        } catch (Exception e) {
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
