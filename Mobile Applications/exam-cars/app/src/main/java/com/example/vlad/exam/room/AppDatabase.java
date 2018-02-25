package com.example.vlad.exam.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.example.vlad.exam.dao.PurchasedCarDao;
import com.example.vlad.exam.model.PurchasedCar;

/**
 * Created by vlad on 29.01.2018.
 */
@Database(
        entities = {PurchasedCar.class},
        version = 1
)
@TypeConverters({
        Converter.class
})
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase INSTANCE;
    private static final String DB_NAME = "app.db";

    public static AppDatabase getInstance(final Context context) {
        if (null == INSTANCE) {
            synchronized (AppDatabase.class) {
                if (null == INSTANCE) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DB_NAME
                    )
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    public abstract PurchasedCarDao getPurchasedCarDao();
}
