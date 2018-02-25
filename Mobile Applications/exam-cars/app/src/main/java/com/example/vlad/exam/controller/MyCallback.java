package com.example.vlad.exam.controller;

/**
 * Created by vlad on 29.01.2018.
 */

public interface MyCallback {

    void showError(String message);

    void clear();

    void onRequestSuccess();
}
