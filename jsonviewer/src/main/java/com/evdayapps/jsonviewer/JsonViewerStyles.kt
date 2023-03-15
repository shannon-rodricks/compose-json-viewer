package com.evdayapps.jsonviewer

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle

data class JsonViewerStyles(
    val KEY: SpanStyle = SpanStyle(color = Color(0xFF272829)),
    val INT: SpanStyle = SpanStyle(color = Color(0xFF1E88E5)),
    val BOOLEAN: SpanStyle = SpanStyle(color = Color(0xFFD163DD)),
    val FLOAT: SpanStyle = SpanStyle(color = Color(0xff8250c4)),
    val STRING: SpanStyle = SpanStyle(color = Color(0xFF43A047)),
    val URL: SpanStyle = SpanStyle(color = Color(0xFF0020C1)),
    val NULL: SpanStyle = SpanStyle(color = Color(0xFFDD031C)),
    val UNKNOWN: SpanStyle = SpanStyle(color = Color(0xFF4D4C4C)),
    val HIGHLIGHT: SpanStyle = SpanStyle(background = Color.Yellow)
)