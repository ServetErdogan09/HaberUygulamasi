package com.example.haberuygulamasi.converts

import androidx.room.TypeConverter
import com.example.haberuygulamasi.model.Source
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromSource(source: Source?): String? {
        return Gson().toJson(source)
    }

    @TypeConverter
    fun toSource(source: String?): Source? {
        val type = object : TypeToken<Source>() {}.type
        return Gson().fromJson(source, type)
    }
}
