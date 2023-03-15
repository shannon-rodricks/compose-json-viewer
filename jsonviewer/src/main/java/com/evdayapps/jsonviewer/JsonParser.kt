package com.evdayapps.jsonviewer

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener

/**
 * Main parser class
 * Input: Json String
 * Output: List of [JsonItem] which can be fed into a [JsonViewerWidget]
 *
 * @property [styles] Instance of [JsonViewerStyles]
 */
class JsonParser(
    private val styles: JsonViewerStyles = JsonViewerStyles()
) {

    /**
     * Main parse method
     * @param strJson The json object/array, as a string
     * @return List of [JsonItem]
     * @throws IllegalArgumentException if string doesnt represent a json object/array
     */
    @Throws(JSONException::class, java.lang.IllegalArgumentException::class)
    fun parse(strJson: String): List<JsonItem> {
        val data: Any?
        try {
            data = JSONTokener(strJson).nextValue()
        } catch (e: JSONException) {
            throw e
        }

        if (data != null) {
            when (data) {
                is JSONObject,
                is JSONArray -> return parseElement(element = data)
            }
        }

        throw IllegalArgumentException("json string is illegal.")
    }

    private fun parseElement(
        key: String? = null,
        element: Any?,
        ancestry: List<JsonItem> = listOf()
    ): List<JsonItem> {
        return when (element) {
            is JSONObject -> parseJsonObject(key = key, obj = element, ancestry = ancestry)
            is JSONArray -> parseJsonArray(key = key, array = element, ancestry = ancestry)
            else -> parsePrimitive(key = key ?: "", value = element, ancestry = ancestry)
        }
    }

    private fun parseJsonObject(
        key: String? = null,
        obj: JSONObject,
        ancestry: List<JsonItem> = listOf()
    ): List<JsonItem> {
        val result = mutableListOf<JsonItem>()
        val item = JsonItem(key = key, element = obj, ancestry = ancestry, styles = styles)
        result.add(item)

        val newAncestry = ancestry.plus(item)
        obj.names()?.let {
            for (i in 0 until it.length()) {
                val childKey = it.getString(i)
                result.addAll(
                    parseElement(
                        key = childKey,
                        element = obj.get(childKey),
                        ancestry = newAncestry
                    )
                )
            }
        }

        // Closing Tag
        result.add(
            JsonItem(closingTag = true, element = "}", ancestry = newAncestry, styles = styles)
        )

        return result
    }

    private fun parseJsonArray(
        key: String? = null,
        array: JSONArray,
        ancestry: List<JsonItem> = listOf()
    ): List<JsonItem> {
        val result = mutableListOf<JsonItem>()
        val item = JsonItem(key = key, element = array, ancestry = ancestry, styles = styles)
        result.add(item)

        val newAncestry = ancestry.plus(item)
        val len = array.length()
        for (i in 0 until len) {
            result.addAll(
                parseElement(
                    element = array.get(i),
                    ancestry = newAncestry
                )
            )
        }

        // Closing Tag
        result.add(
            JsonItem(closingTag = true, element = "]", ancestry = newAncestry, styles = styles)
        )

        return result
    }

    private fun parsePrimitive(
        key: String,
        value: Any?,
        ancestry: List<JsonItem> = listOf()
    ): List<JsonItem> {
        return listOf(
            JsonItem(key = key, element = value, ancestry = ancestry, styles = styles)
        )
    }


}