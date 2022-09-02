package com.posada.santiago.alphapostsandcomments.domain.events;

import co.com.sofka.domain.generic.DomainEvent;

public class TitleChanged extends DomainEvent {


    private String title;

    public TitleChanged(String title) {
        super("posada.santiago.TitleChanged");
        this.title = title;
    }


    public String getTitle() {
        return title;
    }

}
