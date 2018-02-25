package com.example.vlad.exam.model;

/**
 * Created by vlad on 29.01.2018.
 */

public class Car {

    private int id;

    private String name;

    private int quantity;

    private String type;

    private String status;

    public Car() {
    }

    public Car(String name, int quantity, String type, String status) {
        this.name = name;
        this.quantity = quantity;
        this.type = type;
        this.status = status;
    }

    public Car(int id, String name, int quantity, String type, String status) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.type = type;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
