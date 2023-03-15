package com.evdayapps.jsonviewer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import org.json.JSONArray
import org.json.JSONObject
import java.util.regex.Pattern
import kotlin.math.max

val colorKey = Color(0xFF272829)
val colorBoolean = Color(0xFFD163DD)
val colorInt = Color(0xFF1E88E5)
val colorFloat = Color(0xff8250c4)
val colorString = Color(0xFF43A047)
val colorStringUrl = Color(0xFF00ACC1)
val colorNull = Color(0xFFDD031C)
val colorUnknown = Color(0xFF4D4C4C)

private val regexUrl = Pattern.compile(
    "^((https|http|ftp|rtsp|mms)?://)"
        + "?(([\\da-z_!~*'().&=+$%-]+: )?[\\da-z_!~*'().&=+$%-]+@)?" //ftp
        + "((\\d{1,3}\\.){3}\\d{1,3}" // IP
        + "|" // 允许IP和DOMAIN（域名）
        + "([\\da-z_!~*'()-]+\\.)*" // www.
        + "([\\da-z][\\da-z-]{0,61})?[\\da-z]\\."
        + "[a-z]{2,6})" // first level domain- .com or .museum
        + "(:\\d{1,4})?" // :80
        + "((/?)|" // a slash isn't required if there is no file name
        + "(/[\\da-z_!~*'().;?:@&=+$,%#-]+)+/?)$",
    Pattern.CASE_INSENSITIVE
)

data class JsonItem(
    val key: String? = null,
    val element: Any?,
    val closingTag: Boolean = false,
    var expanded: Boolean = true,
    val ancestry: List<JsonItem>
) {

    private val depth: Int = ancestry.size
    private val prefixSpace = " ".repeat((if (closingTag) max(0, depth - 1) else depth) * 8)

    val showButton = element is JSONObject || element is JSONArray

    var leftMatchString: AnnotatedString? = null
    var rightMatchString: AnnotatedString? = null

    // Text before the button, if any. Space + key
    private val textLeftRaw = AnnotatedString(
        when (closingTag) {
            true -> "$prefixSpace$element,"
            else -> "$prefixSpace${if (key?.isNotBlank() == true) "\"$key\": " else ""}"
        }
    )

    val textLeft: AnnotatedString
        get() = leftMatchString ?: textLeftRaw

    private fun isUrl(string: String): Boolean {
        return regexUrl.matcher(string).matches()
    }

    private val textRightExpanded: AnnotatedString = when (element) {
        is JSONObject -> AnnotatedString("{", SpanStyle(color = colorUnknown))
        is JSONArray -> AnnotatedString("[", SpanStyle(color = colorUnknown))
        is String -> AnnotatedString(
            "\"$element\"",
            SpanStyle(color = if (isUrl(element)) colorStringUrl else colorString)
        )
        is Int -> AnnotatedString(element.toString(), SpanStyle(color = colorInt))
        is Float -> AnnotatedString(element.toString(), SpanStyle(color = colorFloat))
        is Boolean -> AnnotatedString(element.toString(), SpanStyle(color = colorBoolean))
        // TODO Check for null ( == null ain't working!)

        else -> AnnotatedString(element.toString(), SpanStyle(color = colorUnknown))
    }

    private val textRightCollapsed: AnnotatedString = when (element) {
        is JSONArray -> AnnotatedString.Builder().apply {
            append("[... ")
            append(AnnotatedString(element.length().toString(), SpanStyle(color = colorString)))
            append(" ...]")
        }.toAnnotatedString()
        is JSONObject -> AnnotatedString.Builder().apply {
            append("{... ")

            val placeholder: String = try {
                when {
                    element.has("id") -> "id: ${element.getString("id")}"
                    element.has("name") -> "name: ${element.getString("name")}"
                    else -> (element.names()?.length() ?: 0).toString()
                }
            } catch (ex: Exception) {
                (element.names()?.length() ?: 0).toString()
            }

            append(AnnotatedString(placeholder, SpanStyle(color = colorString)))

            append(" ...}")
        }.toAnnotatedString()
        else -> textRightExpanded
    }

    val textRight: AnnotatedString
        get() = when (closingTag) {
            true -> AnnotatedString("")
            else -> if (expanded) (rightMatchString ?: textRightExpanded) else textRightCollapsed
        }

    // region Search
    fun query(query: String) {
        if (query.isNotEmpty()) {
            leftMatchString = getMatchString(textLeftRaw, query)
            rightMatchString = getMatchString(textRightExpanded, query)
            if (leftMatchString != null || rightMatchString != null) {
                ancestry.forEach { it.expanded = true }
            }
        } else {
            leftMatchString = null
            rightMatchString = null
        }
    }

    private fun getMatchString(text: AnnotatedString, query: String): AnnotatedString? {
        if (text.contains(query)) {
            // Walk through the text and highlight every instance of [string]
            val highlights = mutableListOf<AnnotatedString.Range<SpanStyle>>()
            var startIndex = 0
            while (startIndex != -1) {
                startIndex = text.indexOf(query, startIndex)
                if (startIndex != -1) {
                    highlights.add(
                        AnnotatedString.Range(
                            SpanStyle(background = Color.Yellow),
                            startIndex,
                            startIndex + query.length
                        )
                    )
                    startIndex += query.length
                }
            }

            return AnnotatedString(
                text.text,
                spanStyles = text.spanStyles.plus(highlights)
            )
        }

        return null
    }
    // endregion Search

}