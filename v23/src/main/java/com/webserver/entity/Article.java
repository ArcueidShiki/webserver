package com.webserver.entity;

import java.io.Serializable;

public class Article implements Serializable {
    public static final long serialVersionUID = 1L;
    private String tile;
    private String author;
    private String body;

    @Override
    public String toString() {
        return "Article{" +
                "tile='" + tile + '\'' +
                ", author='" + author + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

    public Article(String tile, String author, String body) {
        this.tile = tile;
        this.author = author;
        this.body = body;
    }

    public Article() {
    }

    public String getTile() {
        return tile;
    }

    public String getBody() {
        return body;
    }

    public String getAuthor() {
        return author;
    }
}
