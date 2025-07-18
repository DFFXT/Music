package com.compose.util

import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlin.reflect.KProperty

fun <T> MutableState<T>.update() {
    value = value
}

@Composable
fun getColor(): Long {
    return if (isSystemInDarkTheme()) {
        0xFF000000
    } else {
        0xFFFFFFFF
    }
}

@Composable
fun isPreview(): Boolean {
    return LocalInspectionMode.current
}

@Composable
inline fun <reified T : ViewModel> getViewModel(): T? {
    return if (isPreview()) return null else viewModel()
}

inline operator fun <T> State<T>?.getValue(thisObj: Any?, property: KProperty<*>): T? = this?.value



val LocalNavi =
    compositionLocalOf<NavHostController> { NavHostController(ContextWrapper(null)) }
