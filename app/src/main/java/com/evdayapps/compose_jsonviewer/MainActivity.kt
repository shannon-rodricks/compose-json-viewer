package com.evdayapps.compose_jsonviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.evdayapps.compose_jsonviewer.ui.theme.ComposeJsonviewerTheme
import com.evdayapps.jsonviewer.JsonItem
import com.evdayapps.jsonviewer.JsonParser
import com.evdayapps.jsonviewer.JsonViewerWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeJsonviewerTheme {
                PageWidget(loadData = {
                    getJson()
                })
            }
        }
    }

    private fun getJson(): String {
        var inputStream: InputStream? = null
        try {
            inputStream = assets.open("demo.json")
            val lenght = inputStream.available()
            val buffer = ByteArray(lenght)
            inputStream.read(buffer)
            return String(buffer, Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close();
            } catch (_: Exception) {

            }
        }

        return ""
    }
}

@Composable
fun PageWidget(
    loadData: () -> String
) {
    var list by remember {
        mutableStateOf<List<JsonItem>>(listOf())
    }
    var filteredList by remember {
        mutableStateOf<List<JsonItem>>(listOf())
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            list = JsonParser().parse(loadData())
            filteredList = list
        }
    }

    JsonViewerWidget(list = filteredList, onToggleExpand = {
        it.expanded = !it.expanded
        filteredList = list.filter { it.ancestry.all { it.expanded } }
    })
}