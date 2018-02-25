package com.example.vlad.exam.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

/**
 * Created by vlad on 29.01.2018.
 */
@Entity(tableName = "cars")
public class PurchasedCar {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "carId")
    private int carId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "buyDate")
    private Date buyDate;

    public PurchasedCar(int carId, String name, String type, Date buyDate) {
        this.carId = carId;
        this.name = name;
        this.type = type;
        this.buyDate = buyDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCarId() {
        return carId;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Date getBuyDate() {
        return buyDate;
    }

    @Override
    public String toString() {
        return "PurchasedCar{" +
                "id=" + id +
                ", carId=" + carId +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", buyDate=" + buyDate +
                '}';
    }
}
