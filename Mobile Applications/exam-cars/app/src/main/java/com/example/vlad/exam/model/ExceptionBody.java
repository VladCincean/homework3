package com.example.vlad.exam.model;

/**
 * Created by vlad on 31.01.2018.
 */

public class ExceptionBody {
    private String text;

    public ExceptionBody() {
    }

    public ExceptionBody(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
