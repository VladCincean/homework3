package com.example.vlad.exam.room;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

/**
 * Created by vlad on 29.01.2018.
 */

public class Converter {
    @TypeConverter
    public Date timestampToDate(Long timestamp) {
        if (timestamp == null) {
            return null;
        }

        return new Date(timestamp);
    }

    @TypeConverter
    public Long dateToTimestamp(Date date) {
        if (date == null) {
            return null;
        }

        return date.getTime();
    }
}
