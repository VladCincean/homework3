package com.example.vlad.caloriecounter.model;

import java.io.Serializable;

/**
 * Created by vlad on 31.12.2017.
 */

public class FoodRecord implements Serializable {
    private String id;

    private String userId;

    private String foodItemId;

    private String foodItemName;

    private int quantity;

//    private String date;


    public FoodRecord() {
    }

    public FoodRecord(String id, String userId, String foodItemId, String foodItemName, int quantity) {
        this.id = id;
        this.userId = userId;
        this.foodItemId = foodItemId;
        this.foodItemName = foodItemName;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getFoodItemId() {
        return foodItemId;
    }

    public String getFoodItemName() {
        return foodItemName;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FoodRecord that = (FoodRecord) o;

        if (quantity != that.quantity) return false;
        if (!id.equals(that.id)) return false;
        if (!userId.equals(that.userId)) return false;
        if (!foodItemId.equals(that.foodItemId)) return false;
        return foodItemName.equals(that.foodItemName);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + userId.hashCode();
        result = 31 * result + foodItemId.hashCode();
        result = 31 * result + foodItemName.hashCode();
        result = 31 * result + quantity;
        return result;
    }

    @Override
    public String toString() {
        return foodItemName + " | " + Integer.toString(quantity) + " g";
//        return "FoodRecord{" +
//                "id='" + id + '\'' +
//                ", userId='" + userId + '\'' +
//                ", foodItemId='" + foodItemId + '\'' +
//                ", foodItemName='" + foodItemName + '\'' +
//                ", quantity=" + quantity +
//                '}';
    }
}
