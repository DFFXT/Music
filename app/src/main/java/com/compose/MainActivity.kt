package com.compose

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.compose.page.MusicDetailComponent
import com.compose.page.MusicMainComponent
import com.compose.page.SettingsComponent
import com.compose.ui.theme.DeleteTheme
import com.compose.util.LocalNavi
import com.compose.util.Navi

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Page()
        }
    }

    @Preview
    @Composable
    fun Page() {
        DeleteTheme {
            MaterialTheme.colorScheme.primary
            val controller = rememberNavController()
            CompositionLocalProvider(LocalNavi provides controller) {
                Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                    Column {
                        Box(modifier = Modifier.fillMaxWidth()
                            .height(padding.calculateTopPadding())
                            .background(MaterialTheme.colorScheme.primaryContainer)
                        )
                        NavHost(navController = controller, startDestination = Navi.MUSIC_HOME) {
                            composable(Navi.MUSIC_SETTING) {
                                SettingsComponent()
                            }
                            composable(Navi.MUSIC_HOME) {
                                MusicMainComponent()
                            }
                            composable(Navi.MUSIC_DETAIL) {
                                MusicDetailComponent()
//                                Text(MaterialTheme.colorScheme.primary.toString(), modifier = Modifier.clickable {
//                                    controller.navigate("home")
//                                })
                            }
                        }
                    }

                }
            }

        }


    }

    companion object {
        fun actionStart(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }
}
