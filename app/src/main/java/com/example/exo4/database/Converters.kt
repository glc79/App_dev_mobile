package com.example.exo4.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.exo4.model.GpsPoint

class Converters {
    @TypeConverter
    fun fromLongList(value: List<Long>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toLongList(value: String): List<Long> {
        val listType = object : TypeToken<List<Long>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromGpsPointList(value: List<GpsPoint>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toGpsPointList(value: String): List<GpsPoint>? {
        val listType = object : TypeToken<List<GpsPoint>>() {}.type
        return Gson().fromJson(value, listType)
    }
} 