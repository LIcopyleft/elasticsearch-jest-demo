package com.github.sxw.webmagic;

import com.github.sxw.model.Article;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class NeteaseNewsPageProcesser implements PageProcessor {

    private Site site = Site.me().setDomain("news.163.com")
            .setRetryTimes(3)
            .setSleepTime(1000)
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.155 Safari/537.36");

    public static final String URL_LIST = "http://news\\.163\\.com/special/\\w+/\\w+\\.html";

    public static final String URL_POST = "http://news\\.163\\.com/.+\\.html";

    @Override
    public void process(Page page) {
        //列表页
        if (page.getUrl().regex(URL_LIST).match()||page.getUrl().regex("http://news\\.163\\.com/domestic").match()
                ||page.getUrl().regex("http://news\\.163\\.com/shehui").match()) {
            page.addTargetRequests(page.getHtml().links().regex(URL_POST).all());
            page.addTargetRequests(page.getHtml().links().regex(URL_LIST).all());
        }else{
            //photoview 新闻和普通列表格式的新闻页面元素不一样
            String url = page.getUrl().get();
            if(url.contains("photoview")){
                page.putField("title", Utils.replaceHTML(page.getHtml().xpath("//div[@class='headline']").toString()));
                page.putField("content", Utils.replaceHTML(page.getHtml().xpath("//div[@class='overview']").toString()));
                page.putField("source", "empty");
                page.putField("url", page.getUrl().get());
            }else{
                page.putField("title", Utils.replaceHTML(page.getHtml().xpath("//div[@id='epContentLeft']//h1").toString()));
                page.putField("content", Utils.replaceHTML(page.getHtml().xpath("//div[@id='endText']").toString()));
                String create = Utils.replaceHTML(page.getHtml().xpath("//div[@class=\"post_time_source\"]").toString());
                page.putField("source", Utils.replaceHTML(page.getHtml().xpath("//a[@id=\"ne_article_source\"]/text()").toString()));
                page.putField("url", page.getUrl().get());
            }


            String title = (String)page.getResultItems().get("title");
            String content = (String)page.getResultItems().get("content");
            String source = (String)page.getResultItems().get("source");
            String author = "苏雄伟";

            if(StringUtils.isNotEmpty(title) && StringUtils.isNotEmpty(content)
                     && StringUtils.isNotEmpty(source)){
                // 创建article
                Article article = Utils.createArticle(title, content, source, author, url);
                // 索引
                Utils.index(article);
            }
//            System.out.println("title="+title);
//            System.out.println("content="+content);
//            System.out.println("create="+create);
//            System.out.println("source="+source);
//            System.out.println("url="+url);
//            System.out.println("author="+author);
//            System.out.println("--------------------------------------------------");
        }
    }

    @Override
    public Site getSite() {
        return site;
    }



    public static void main(String[] args) {
        long startTime, endTime;
        System.out.println("开始爬取...");
        startTime = System.currentTimeMillis();


        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        JdbcPipeline jdbcPipeline = (JdbcPipeline)applicationContext.getBean("jdbcPipeline");
        Spider.create(new NeteaseNewsPageProcesser())
                .addUrl("http://news.163.com/domestic")
                .addUrl("http://news.163.com/shehui")
                .addPipeline(jdbcPipeline)
                .thread(5)
                .run();

        endTime = System.currentTimeMillis();
        System.out.println("爬取结束，耗时约" + ((endTime - startTime) / 1000) + "秒");
    }
}
