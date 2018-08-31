package com.github.sxw.webmagic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.sxw.client.elasticsearch.MyTransportClient;
import com.github.sxw.model.Article;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

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
     * 创建mapping
     * @throws Exception
     */
    public static void createArticleMapping() throws Exception {
        MyTransportClient.createArticleMapping("news","news","ik");
    }
    /**
     * 创建索引
     * @param article
     */
    public static void index(Article article){
        // 创建索引
        ObjectMapper mapper = new ObjectMapper();
        try {
            MyTransportClient.createIndex("news", "article", mapper.writeValueAsString(article));
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
