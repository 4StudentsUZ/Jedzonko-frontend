package com.fourstudents.jedzonko.Network.Responses;

import com.fourstudents.jedzonko.Other.Author;


public class CommentResponse {
    private final int id;
    private final String content;
    private final String creationDate;
    private final String modificationDate;
    private final Author author;

    public CommentResponse(int id, String content, String creationDate, String modificationDate, Author author) {
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

    public String getCreationDate() {
        return creationDate;
    }

    public String getModificationDate() {
        return modificationDate;
    }

    public Author getAuthor() {
        return author;
    }
}
