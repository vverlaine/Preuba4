package com.example.demo.domain;

public final class Quote {

    private String id;
    private String book;
    private String content;

    public Quote(String id, String book, String content) {
        this.id = id;
        this.book = book;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getBook() {
        return book;
    }

    public String getContent() {
        return content;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
