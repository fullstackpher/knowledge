package io.github.talelin.knowledge.spider;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.HashMap;
import java.util.Map;

public class LoginPageProcessor implements PageProcessor {

    private Site site = Site.me()
            .setRetryTimes(3)
            .setSleepTime(1000)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");

    @Override
    public void process(Page page) {
        // 模拟弹窗登录，假设弹窗登录通过表单提交
        String loginUrl = page.getUrl().get();

        // 设置登录表单参数
        Map<String, Object> params = new HashMap<>();
        params.put("username", "Jovian");
        params.put("password", "zl241680");

        // 创建表单请求
        Request formRequest = new Request(loginUrl);
        formRequest.setMethod(HttpConstant.Method.POST);
        formRequest.setRequestBody(HttpRequestBody.form(params, "UTF-8"));
        // 逐个添加请求头
        site.getHeaders().forEach(formRequest::addHeader);

        // 添加登录请求到目标请求列表
        page.addTargetRequest(formRequest);
    }

    @Override
    public Site getSite() {
        return site;
    }
}
