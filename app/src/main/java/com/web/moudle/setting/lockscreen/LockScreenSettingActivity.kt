package com.web.moudle.setting.lockscreen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.request.transition.Transition
import com.web.common.base.BaseActivity
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseGlideTarget
import com.web.common.base.BaseViewHolder
import com.web.common.constant.AppConfig
import com.web.common.imageLoader.glide.ImageLoad
import com.web.common.tool.ColorPickerDialog
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.misc.GapItemDecoration
import com.web.moudle.music.player.NewPlayer
import com.web.moudle.music.player.plug.LockScreenPlug
import com.music.m.R
import kotlinx.android.synthetic.main.activity_setting_lockscreen.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class LockScreenSettingActivity : BaseActivity() {
    private val colorList = arrayListOf<Int>()

    private var mColor = AppConfig.lockScreenBgColor

    init {
        colorList.add(ResUtil.getColor(R.color.themeColor))
        colorList.add(Color.RED)
        colorList.add(Color.GREEN)
        colorList.add(Color.BLACK)
        colorList.add(Color.BLUE)
        colorList.add(Color.CYAN)
        colorList.add(Color.DKGRAY)

    }

    override fun getLayoutId(): Int {
        return R.layout.activity_setting_lockscreen
    }

    override fun initView() {
        view_s_lock_colorSelected.setImageDrawable(ColorDrawable(mColor))
        rv_s_lock_colorList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rv_s_lock_colorList.addItemDecoration(GapItemDecoration(right = 10))
        view_s_lock_colorSelected.setOnClickListener { colorPick() }
        sw_lockScreenMode.setOnClickListener {
            if (getMode() == BG_MODE_COLOR&&switchLockScreenMode(BG_MODE_IMAGE)) {
                setMode(BG_MODE_IMAGE)
            } else if(switchLockScreenMode(BG_MODE_COLOR)) {
                setMode(BG_MODE_COLOR)
            }
        }
        switchLockScreenMode(getMode())
        sw_s_lock_switch.setOnCheckedChangeListener { _, res ->
            setNoLockScreen(!res)
            LockScreenPlug.lockScreen(this)
        }
        sw_s_lock_switch.isChecked = !getNoLockScreen()
        rv_s_lock_colorList.adapter = object : BaseAdapter<Int>(colorList) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
                val v = ImageView(this@LockScreenSettingActivity)
                v.background = getDrawable(R.drawable.border_1dp)
                v.elevation=ViewUtil.dpToPx(3f).toFloat()
                val lp = ViewGroup.LayoutParams(ViewUtil.dpToPx(40f), ViewUtil.dpToPx(40f))
                v.layoutParams = lp
                v.setPadding(10, 10, 10, 10)
                return BaseViewHolder(v)
            }

            override fun onBindViewHolder(holder: BaseViewHolder, position: Int, item: Int) {
                (holder.itemView as ImageView).setImageDrawable(ColorDrawable(item!!))
                holder.itemView.setOnClickListener {
                    view_s_lock_colorSelected.setImageDrawable(ColorDrawable(colorList[position]))
                    setBgColor(colorList[position])
                }
            }
        }

        setBackgroundImage(getBgImagePath())


        iv_s_lock_imageShow.setOnClickListener {
            tv_s_lock_setImageBackground.performClick()
        }
        tv_s_lock_setImageBackground.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT, null)
            intent.type = "image/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)

            startActivityForResult(Intent.createChooser(intent, "图片选择"), 2)
        }


        val lp = iv_s_lock_imageShow.layoutParams
        lp.width = ViewUtil.screenWidth() shr 2
        lp.height = ViewUtil.screenHeight() shr 2
    }

    private fun switchLockScreenMode(mode: String) :Boolean{
        var res=true
        sw_lockScreenMode.text =
                if (mode == BG_MODE_COLOR) getString(R.string.setting_lockScreen_mode_color)
                else if (!File(getBgImagePath()).exists()) {
                    tv_s_lock_setImageBackground.performClick()
                    res=false
                    getString(R.string.setting_lockScreen_mode_color)
                } else {
                    getString(R.string.setting_lockScreen_mode_image)
                }

        return res
    }

    private fun setBackgroundImage(path: String) {
        ImageLoad.loadAsBitmap(path).into(object : BaseGlideTarget(ViewUtil.screenWidth() shr 2, ViewUtil.screenHeight() shr 2) {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                iv_s_lock_imageShow.setImageBitmap(resource)
            }
        })
    }

    /**
     * 颜色选择
     */
    private fun colorPick() {
        val colorPickerDialog = ColorPickerDialog(this, AppConfig.lockScreenBgColor)
        val array = IntArray(5)
        array[0] = Color.WHITE
        array[1] = Color.BLUE
        array[2] = Color.RED
        array[3] = Color.CYAN
        array[4] = Color.YELLOW
        colorPickerDialog.setColorArray(array)
        colorPickerDialog.negativeButtonListener = View.OnClickListener {
            colorPickerDialog.dismiss()
        }
        colorPickerDialog.positiveButtonListener = {
            setBgColor(it)
            setMode(BG_MODE_COLOR)
            switchLockScreenMode(BG_MODE_COLOR)
            view_s_lock_colorSelected.setImageDrawable(ColorDrawable(it))
        }
        colorPickerDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        if (data == null) return
        val uri = data.data

        if (requestCode == 2) {
            val intent = Intent("com.android.camera.action.CROP")
            intent.setDataAndType(uri, "image/*")
            intent.putExtra("crop", "true")
            intent.putExtra("aspectX", ViewUtil.screenWidth())
            intent.putExtra("aspectY", ViewUtil.screenHeight())
            intent.putExtra("outputX", ViewUtil.screenWidth())
            intent.putExtra("outputY", ViewUtil.screenHeight())
            intent.putExtra("scale", false)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(Environment.getExternalStorageDirectory().absolutePath + "/1.png")))
            intent.putExtra("return-data", false)
            intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString())
            intent.putExtra("noFaceDetection", true)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            //intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)//报错、无权限
            startActivityForResult(intent, 3)
            return
        }


        if (requestCode == 3) {
            val dir = File(filesDir.absolutePath + File.separator + "bg")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val path = dir.absolutePath + File.separator + "lockScreenImage.png"
            try {
                val tmpFile = File(Environment.getExternalStorageDirectory().absolutePath + "/1.png")
                val inputStream = FileInputStream(tmpFile)
                inputStream.use { input ->
                    FileOutputStream(path).use { output ->
                        input.copyTo(output)
                    }
                }
                setBgImagePath(path)
                if(switchLockScreenMode(BG_MODE_IMAGE)){
                    setMode(BG_MODE_IMAGE)
                }
                setBackgroundImage(path)
                tmpFile.delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val BG_MODE_COLOR = "color"
        const val BG_MODE_IMAGE = "image"
        fun actionStart(context: Context) {
            context.startActivity(Intent(context, LockScreenSettingActivity::class.java))
        }

        @JvmStatic
        fun getNoLockScreen(): Boolean = AppConfig.noLockScreen
        @JvmStatic
        fun setNoLockScreen(noLock: Boolean) {
            AppConfig.noLockScreen = noLock
        }
        @JvmStatic
        fun getMode(): String = AppConfig.lockScreenBgMode
        @JvmStatic
        fun setMode(mode: String) {
            AppConfig.lockScreenBgMode = mode
        }
        @JvmStatic
        fun setBgColor(color: Int) {
            AppConfig.lockScreenBgColor = color
        }
        @JvmStatic
        fun getBgColor(): Int = AppConfig.lockScreenBgColor
        @JvmStatic
        fun setBgImagePath(path: String) {
            AppConfig.lockScreenBgImagePath = path
        }
        @JvmStatic
        fun getBgImagePath(): String = AppConfig.lockScreenBgImagePath?:""
    }


}