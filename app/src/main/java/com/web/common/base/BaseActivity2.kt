package com.web.common.base

import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.music.m.R
import com.music.m.databinding.ActivityHomePageBinding
import com.web.common.constant.Constant
import com.web.misc.SwipeFrameLayout
import java.lang.reflect.ParameterizedType


abstract class BaseActivity2<T : ViewBinding> : BaseViewBindingActivity<T>()
