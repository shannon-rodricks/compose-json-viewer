package com.evdayapps.jsonviewer

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject


@Preview(device = Devices.PIXEL_4_XL)
@Composable
private fun JsonViewerWidgetPreview() {
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
    Box(
        //modifier = Modifier.background(color = Color.White)
    ) {
        JsonViewerWidget(list = list, modifier = Modifier.fillMaxSize())
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun JsonViewerWidget(
    list: List<JsonItem>,
    modifier: Modifier = Modifier
) {
    var search by remember { mutableStateOf(TextFieldValue("")) }
    val horizontalScrollState = rememberScrollState()
    var filteredList by mutableStateOf(list)

    Column {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = search,
            label = { Text("Search") },
            placeholder = { Text("Search by key or value")},
            onValueChange = {
                if (it.text != search.text) {
                    search = it
                    list.forEach { item -> item.query(it.text) }
                    filteredList = list.filter {
                        it.ancestry.all { it.expanded }
                    }
                }
            }
        )
        LazyColumn(
            modifier = modifier
                .horizontalScroll(horizontalScrollState)
                .fillMaxWidth()
        ) {
            items(filteredList) {
                JsonItemWidget(
                    item = it,
                    onToggleExpand = {
                        it.expanded = !it.expanded
                        filteredList = list.filter {
                            it.ancestry.all { it.expanded }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun JsonItemWidget(item: JsonItem, onToggleExpand: (item: JsonItem) -> Unit) {
    Row(
        modifier = Modifier.defaultMinSize(minHeight = 32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            item.textLeft, style = MaterialTheme.typography.body2.copy(
                fontWeight = FontWeight.Medium
            )
        )
        if (item.showButton)
            Icon(
                imageVector = Icons.Outlined.ArrowDropDown,
                contentDescription = "expand",
                tint = Color(0xFF00B0FF),
                modifier = Modifier
                    .rotate(if (item.expanded) 0f else -90f)
                    .size(32.dp)
                    .clickable {
                        onToggleExpand(item)
                    }
            )
        Text(
            item.textRight, style = MaterialTheme.typography.body2.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }
}