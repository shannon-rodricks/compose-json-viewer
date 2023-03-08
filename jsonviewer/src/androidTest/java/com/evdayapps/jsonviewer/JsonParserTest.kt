package com.evdayapps.jsonviewer

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JsonParserTest {
    @Test
    fun convert_simple_json() {
        val json = JSONArray().apply {
            for (i in 0 until 10) {
                put(JSONObject().apply {
                    put("string", "string_$i")
                    put("boolean", true)
                    put("float", i)
                    put("integer", i.toFloat())
                    put("array", JSONArray().apply {
                        put(JSONObject().apply {
                            put("string$i", "test")
                        })
                    })
                })
            }
        }

        val list = JsonParser().parse(json.toString())
        Log.i("Test", "results: ${list.size}")
    }
}