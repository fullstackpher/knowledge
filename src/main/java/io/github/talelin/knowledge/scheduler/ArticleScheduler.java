package io.github.talelin.knowledge.scheduler;

import io.github.talelin.knowledge.spider.ArticlePageProcessor;
import io.github.talelin.knowledge.spider.ArticlePipeline;
import io.github.talelin.knowledge.spider.LoginPageProcessor;
import io.github.talelin.knowledge.service.CourseService;
import io.github.talelin.knowledge.spider.WebDriverManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

@Component
public class ArticleScheduler {

    @Autowired
    private CourseService courseService;

    @Scheduled(fixedRate = 86400000) // 每天执行一次
    public void scheduleTaskUsingCronExpression() {
        // 先进行登录
//        Spider.create(new LoginPageProcessor())
//                .addUrl("https://svip.168sou.com")
//                .thread(1)
//                .run();

        // 使用登录后的会话信息抓取文章详情
        Spider.create(new ArticlePageProcessor())
                .addUrl("https://svip.168sou.com")
                .addPipeline(new ArticlePipeline(courseService))
                .thread(1)
                .run();
        WebDriverManager.close();
    }
}