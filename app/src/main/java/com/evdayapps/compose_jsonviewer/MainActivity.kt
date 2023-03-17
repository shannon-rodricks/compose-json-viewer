package com.evdayapps.compose_jsonviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.evdayapps.compose_jsonviewer.ui.theme.ComposeJsonviewerTheme
import com.evdayapps.jsonviewer.JsonItem
import com.evdayapps.jsonviewer.JsonParser
import com.evdayapps.jsonviewer.JsonViewerWidget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
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
            val length = inputStream.available()
            val buffer = ByteArray(length)
            inputStream.read(buffer)
            return String(buffer, Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
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
    val list = remember {
        mutableStateOf<List<JsonItem>>(listOf())
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            list.value = JsonParser().parse(loadData())
        }
    }

    JsonViewerWidget(mainList = list.value, modifier = Modifier.fillMaxSize())
}