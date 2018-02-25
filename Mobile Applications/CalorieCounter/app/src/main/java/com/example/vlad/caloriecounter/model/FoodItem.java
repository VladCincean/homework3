package com.example.vlad.caloriecounter.model;

import java.io.Serializable;

/**
 * Created by vlad on 06.11.2017.
 */

public class FoodItem implements Serializable {
    private String id;

    private String name;

    private int carbohydrates;

    private int proteins;

    private int fats;

    public FoodItem() {
    }

    public FoodItem(String id, String name, int carbohydrates, int proteins, int fats) {
        this.id = id;
        this.name = name;
        this.carbohydrates = carbohydrates;
        this.proteins = proteins;
        this.fats = fats;
    }

    public String getCalories() {
        double kcal = 0.0;
        kcal += 4.1 * this.carbohydrates;
        kcal += 4.1 * this.proteins;
        kcal += 9.3 * this.fats;

        kcal = kcal * Math.pow(10, 3);
        kcal = Math.round(kcal);
        kcal = kcal / Math.pow(10, 3);

        return Double.toString(kcal) + " kcal";
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCarbohydrates() {
        return carbohydrates;
    }

    public int getProteins() {
        return proteins;
    }

    public int getFats() {
        return fats;
    }

    public void setFats(int fats) {
        this.fats = fats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FoodItem foodItem = (FoodItem) o;

        if (carbohydrates != foodItem.carbohydrates) return false;
        if (proteins != foodItem.proteins) return false;
        if (fats != foodItem.fats) return false;
        if (!id.equals(foodItem.id)) return false;
        return name.equals(foodItem.name);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + carbohydrates;
        result = 31 * result + proteins;
        result = 31 * result + fats;
        return result;
    }

    @Override
    public String toString() {
        return name;
//        return "FoodItem{" +
//                "id='" + id + '\'' +
//                ", name='" + name + '\'' +
//                ", carbohydrates=" + carbohydrates +
//                ", proteins=" + proteins +
//                ", fats=" + fats +
//                '}';
    }
}