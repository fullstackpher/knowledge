package io.github.talelin.knowledge.spider;

import io.github.talelin.knowledge.dto.course.CreateOrUpdateCourseDTO;
import io.github.talelin.knowledge.dto.course.CreateOrUpdateCourseDetailDTO;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import us.codecraft.webmagic.selector.Html;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticlePageProcessor implements PageProcessor {

    private static final String ARTICLE_TAG_XPATH = "//span[@class='meta-category-dot']/a[2]/text()";
    private static final String ARTICLE_URLS_XPATH = "//div[@class='placeholder']/a/@href";
    private static final String TITLE_XPATH = "//h1[@class='entry-title']/text()";
    private static final String DESCRIPTION_XPATH = "//article/div/div[2]/div[2]/p[4]/text()";
    private static final String COVER_IMAGE_XPATH = "article/div/div[2]/div[2]/p[2]/img/@src";
    private static final String HIDDEN_CONTENT_XPATH = "//div[@class='card-body']";
    private static final String CATEGORY_XPATH = "//span[@class='meta-category']/a/text()";
    private static final Logger logger = LoggerFactory.getLogger(ArticlePageProcessor.class);

    private final Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
    private final Map<String, String> articleTagMap = new HashMap<>();
    private final BloomFilter<CharSequence> bloomFilter = BloomFilter.create(
            Funnels.stringFunnel(StandardCharsets.UTF_8), 1000000, 0.01);
    private final WebDriver webDriver;

    public ArticlePageProcessor() {
        this.webDriver = WebDriverManager.getInstance();
    }

    @Override
    public void process(Page page) {
        try {
            if (isListPage(String.valueOf(page.getUrl()))) {
                processListPage(page);
            } else {
                processDetailPage(page);
            }
        } catch (Exception e) {
            logger.error("处理页面时发生错误: {}", page.getUrl(), e);
        }
    }

    private boolean isListPage(String url) {
        return url.matches("^https://svip\\.168sou\\.com(/page/\\d+/?)?$");
    }

    private void processListPage(Page page) {
        List<String> articleUrls = page.getHtml().xpath(ARTICLE_URLS_XPATH).all();
        processArticleUrls(page, articleUrls);
    }

    private void processArticleUrls(Page page, List<String> articleUrls) {
        for (String articleUrl : articleUrls) {
            String articleId = getArticleId(articleUrl);
            String articleTag = page.getHtml().xpath(ARTICLE_TAG_XPATH).get();
            logger.info("在列表页中抓取文章id={}, 标签={}", articleId, articleTag);
            articleTagMap.put(articleId, articleTag);

            if (!bloomFilter.mightContain(articleUrl)) {
                bloomFilter.put(articleUrl);
                page.addTargetRequest(articleUrl);
            }
        }
    }

    private void processDetailPage(Page page) {
        webDriver.get(page.getUrl().toString());
        page.setHtml(Html.create(webDriver.getPageSource()));

        logger.info("开始抓取文章详情页面: {}", page.getUrl());

        String articleId = getArticleId(page.getUrl().toString());
        String title = page.getHtml().xpath(TITLE_XPATH).get();
        String entryContent = String.join(" ", page.getHtml().xpath(DESCRIPTION_XPATH).all());
        String coverImage = page.getHtml().xpath(COVER_IMAGE_XPATH).get();
        String hiddenContent = page.getHtml().xpath(HIDDEN_CONTENT_XPATH).toString();
        String category = page.getHtml().xpath(CATEGORY_XPATH).get();
        String articleTag = articleTagMap.get(articleId);

        logger.info("抓取文章详情信息: 标题={}, 描述={}, 封面图片={}, 分类={}, 隐藏内容={}, 标签={}",
                title, entryContent, coverImage, category, hiddenContent, articleTag);

        CreateOrUpdateCourseDTO courseDTO = createCourseDTO(articleId, title, entryContent,
                coverImage, category, articleTag, hiddenContent);
        page.putField("courseDTO", courseDTO);
    }

    private static String getArticleId(String articleUrl) {
        logger.info("抓取文章URL: {}", articleUrl);
        return articleUrl.replaceAll(".*/(\\d+)/?$", "$1");
    }

    private CreateOrUpdateCourseDTO createCourseDTO(String articleId, String title, String entryContent, String coverImage, String category, String articleTag, String hiddenContent) {
        CreateOrUpdateCourseDTO courseDTO = new CreateOrUpdateCourseDTO();
        courseDTO.setId(Long.parseLong(articleId));
        courseDTO.setTitle(title);
        courseDTO.setDescription(entryContent);
        courseDTO.setCoverImage(coverImage);
        courseDTO.setCategory(category);
        courseDTO.setTag(articleTag);

        CreateOrUpdateCourseDetailDTO detailDTO = new CreateOrUpdateCourseDetailDTO();
        detailDTO.setContent(entryContent);
        detailDTO.setHiddenContent(hiddenContent);

        courseDTO.setCourseDetail(detailDTO);
        return courseDTO;
    }

    @Override
    public Site getSite() {
        return site;
    }
}