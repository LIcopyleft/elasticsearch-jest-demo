package com.github.sxw.model;

import io.searchbox.annotations.JestId;

import java.util.Date;

public class Article {

    @JestId
    private int id;
    private String title;
    private String content;
    private String url;
    private String source;
    private String author;

    public Article() {
    }

    public Article(int id, String title, String content, String url,
                   String source, String author) {
        super();
        this.id = id;
        this.title = title;
        this.content = content;
        this.url = url;
        this.source = source;
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

}
