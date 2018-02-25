package com.example.vlad.caloriecounter.model;

import java.io.Serializable;

/**
 * Created by vlad on 04.12.2017.
 */

public class User implements Serializable {
    private String id;

    private String email;

    private boolean isAdmin;

    public User() {
    }

    public User(String email, boolean isAdmin) {
        this.email = email;
        this.isAdmin = isAdmin;
    }

    public User(String id, String email, boolean isAdmin) {
        this.id = id;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (isAdmin != user.isAdmin) return false;
        if (!id.equals(user.id)) return false;
        return email.equals(user.email);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + (isAdmin ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}

