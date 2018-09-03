package com.github.sxw.model;

import io.searchbox.annotations.JestId;

public class Product {
    @JestId
    private Integer id;
    private String productId;
    private Integer price;

    public Product() {
    }

    public Product(Integer id, String productId, Integer price) {
        this.id = id;
        this.productId = productId;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
