package com.evdayapps.jsonviewer

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class JsonItemTest {

    private val mainList = JsonParser().parse(JSONArray().apply {
        for (i in 0 until 3) {
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
    }.toString())

    @Test
    fun `Collapsed element should not be visible if a query doesn't match it`() {
        // Collapse the array
        val collapsed = mainList.map {
            if (it.key == "array") {
                it.expanded = false
            }
            it
        }.filter { it.visible }
        collapsed.forEach { println("${it.textLeft}${it.textRight}") }

        // Perform a search
        mainList.forEach { it.query("integer") }
        val filtered = mainList.filter { it.visible }
        // Check that the children of the array are not visible
        assert(filtered.none { it.key == "string0" })

        println("Filtered")
        filtered.forEach {
            println("${it.textLeft}${it.textRight}")
        }

    }

}