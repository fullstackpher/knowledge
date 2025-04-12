package io.github.talelin.knowledge.controller.v1;

import io.github.talelin.knowledge.scheduler.ArticleScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spider")
public class SpiderController {

    private static final Logger logger = LoggerFactory.getLogger(SpiderController.class);

    private final ArticleScheduler articleScheduler;

    public SpiderController(ArticleScheduler articleScheduler) {
        this.articleScheduler = articleScheduler;
    }

    @GetMapping("/schedule")
    public void spider() {
        logger.info("开始执行爬虫任务...");
        articleScheduler.scheduleTaskUsingCronExpression();
        logger.info("爬虫任务执行完成。");
    }
}