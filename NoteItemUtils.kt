package com.example.lab2_20

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object NoteItemUtils {
    private val gson = Gson()

    fun noteItemsToJson(noteItems: List<NoteItem>): String {
        return gson.toJson(noteItems)
    }

    fun jsonToNoteItems(json: String): List<NoteItem> {
        val type = object : TypeToken<List<NoteItem>>() {}.type
        return gson.fromJson(json, type)
    }
}