package com.posada.santiago.alphapostsandcomments.domain.commands;

import co.com.sofka.domain.generic.Command;

public class ChangeTitle extends Command {

    private String postId;

    private String title;

    public ChangeTitle() {
    }

    public ChangeTitle(String postId, String title) {
        this.postId = postId;
        this.title = title;
    }

    public String getPostId() {
        return postId;
    }

    public String getTitle() {
        return title;
    }
}
