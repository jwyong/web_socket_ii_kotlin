package com.example.websocket_ii.util

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

object GsonUtil {

    private val gson: Gson = Gson()

    // Convert object to JSON string
    fun <T> toJson(obj: T): String {
        return gson.toJson(obj)
    }

    // Convert JSON string to object
    fun <T> fromJson(json: String, clazz: Class<T>): T? {
        return try {
            gson.fromJson(json, clazz)
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            null
        }
    }
}
