package com.example.vlad.exam.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.vlad.exam.model.PurchasedCar;

import java.util.List;

/**
 * Created by vlad on 29.01.2018.
 */
@Dao
public interface PurchasedCarDao {
    @Query("SELECT * FROM cars")
    LiveData<List<PurchasedCar>> selectAll();

    @Query("SELECT * FROM cars")
    List<PurchasedCar> selectAllSync();

    @Query("SELECT * FROM cars WHERE id=:id ORDER BY buyDate DESC LIMIT 1")
    PurchasedCar selectOne(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PurchasedCar... cars);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<PurchasedCar> cars);

    @Delete
    void delete(PurchasedCar... cars);

    @Delete
    void delete(List<PurchasedCar> cars);

    @Query("DELETE FROM cars")
    void deleteAll();

    @Update
    void update(PurchasedCar... cars);

    @Update
    void update(List<PurchasedCar> cars);
}
