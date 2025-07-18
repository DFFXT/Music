package com.compose.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.compose.common.TopBarComponent
import com.compose.util.LocalNavi
import com.web.common.constant.AppConfigCore
import com.web.moudle.setting.suffix.sp.IgnorePath

@Preview(showBackground = true, backgroundColor = 0xffffffff)
@Composable
fun SettingsComponent() {
    Column(modifier = Modifier.fillMaxHeight()) {


        val controller = LocalNavi.current
        val ctx = LocalContext.current
        val appConfigCore = remember { AppConfigCore(ctx) }
        val ignorePathObject = remember { IgnorePath() }
        val ignorePath = remember {
            val list = ignorePathObject.ignorePathList
            val res = mutableStateListOf<IgnorePath.IgnoreItem>()
            res.addAll(list)
            res
        }
        val ignoreSystem = remember { mutableStateOf(appConfigCore.enableSystemMusic) }
        if (LocalInspectionMode.current) {
            repeat(100) {
                ignorePath.add(IgnorePath.IgnoreItem("xxx", true))
            }
        }
        TopBarComponent(title = "音乐类型", endTitle = "保存", endClick = {
            ignorePathObject.ignorePathList = ignorePath
        }, startClick = {
            controller.popBackStack()
        })

        Row(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "包含系99999999999999999统音乐包含系99999999999999999统音乐",
                modifier = Modifier.weight(1f, fill = false).padding(vertical = 10.dp)
            )
            Switch(
                checked = ignoreSystem.value,
                modifier = Modifier.padding(start = 30.dp),
                onCheckedChange = { checked ->
                    appConfigCore.enableSystemMusic = checked
                    ignoreSystem.value = checked
                })
        }
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .wrapContentWidth(Alignment.CenterHorizontally),
            ) {
                itemsIndexed(ignorePath, key = { index:Int, item: Any -> item.hashCode() } ) { index, item ->
                    val text = remember { mutableStateOf(item.path) }
                    val enable = remember { mutableStateOf(item.enable) }
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()) {
                        TextField(
                            value = text.value,
                            onValueChange = {
                                item.path = it
                                text.value = it
                            })
                        Switch(
                            checked = enable.value,
                            modifier = Modifier.padding(start = 30.dp),
                            onCheckedChange = { checked ->
                                item.enable = checked
                                enable.value = checked
                            })
                    }
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 10.dp)
                    .background(Color.LightGray)
                    .clickable {
                        ignorePath.add(IgnorePath.IgnoreItem("", true))
                    }
                    .padding(vertical = 10.dp, horizontal = 40.dp)) {
                Text("新增", color = MaterialTheme.colorScheme.primary)
            }
        }
    }


}


