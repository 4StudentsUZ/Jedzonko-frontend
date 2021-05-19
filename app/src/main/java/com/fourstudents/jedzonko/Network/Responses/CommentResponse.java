package com.fourstudents.jedzonko.Network.Responses;

import com.fourstudents.jedzonko.Other.Author;

import java.util.Date;

public class CommentResponse {
    private final int id;
    private final String content;
    private final Date creationDate;
    private final Date modificationDate;
    private final Author author;

    public CommentResponse(int id, String content, Date creationDate, Date modificationDate, Author author) {
        this.id = id;
        this.content = content;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public Author getAuthor() {
        return author;
    }
}
