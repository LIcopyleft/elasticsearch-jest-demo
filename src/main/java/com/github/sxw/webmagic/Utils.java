package com.github.sxw.webmagic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sxw.client.elasticsearch.TransportClient;
import com.github.sxw.model.Article;

/**
 * Created by Administrator on 2015/9/11.
 */
public class Utils {

    /**
     * 创建Article
     * @return
     */
    public static Article createArticle(String title,String content,String source,String author,String url){
        Article article = new Article();
        article.setTitle(title);
        article.setContent(content);
        article.setSource(source);
        article.setAuthor(author);
        article.setUrl(url);
        return article;
    }

    /**
     * 创建索引
     * @param article
     */
    public static void index(Article article){
        // 创建索引
        ObjectMapper mapper = new ObjectMapper();
        try {
            TransportClient.createIndex("news", "article", mapper.writeValueAsString(article));
        }catch (Exception e){
        }
    }

    /**
     * html字符过滤
     * @param str
     * @return
     */
    public static String replaceHTML(String str){
        return str!=null?str.replaceAll("\\<.*?>","").replaceAll("&nbsp;",""):"";
    }
}
