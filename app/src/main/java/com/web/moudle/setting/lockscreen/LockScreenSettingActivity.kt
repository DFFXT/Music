package com.web.moudle.setting.lockscreen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.request.transition.Transition
import com.web.common.base.BaseActivity
import com.web.common.base.BaseAdapter
import com.web.common.base.BaseGlideTarget
import com.web.common.base.BaseViewHolder
import com.web.common.constant.Constant
import com.web.common.imageLoader.glide.ImageLoad
import com.web.common.tool.ColorPickerDialog
import com.web.common.util.ResUtil
import com.web.common.util.ViewUtil
import com.web.misc.GapItemDecoration
import com.web.moudle.lockScreen.ui.LockScreenActivity
import com.web.moudle.music.player.MusicPlay
import com.web.moudle.preference.SP
import com.web.web.R
import kotlinx.android.synthetic.main.activity_setting_lockscreen.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class LockScreenSettingActivity :BaseActivity() {
    private val colorList= arrayListOf<Int>()

    private var mColor=SP.getInt(Constant.spName,Constant.SpKey.lockScreenBgColor)
    init {
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
        rv_s_lock_colorList.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rv_s_lock_colorList.addItemDecoration(GapItemDecoration(0,0,10,0))
        view_s_lock_colorSelected.setOnClickListener{colorPick()}
        sw_s_lock_switch.setOnCheckedChangeListener{_,res->
            SP.putValue(Constant.spName,Constant.SpKey.noLockScreen,!res)
            val intent=Intent(this@LockScreenSettingActivity,MusicPlay::class.java)
            intent.action = MusicPlay.ACTION_LOCKSCREEN
            startService(intent)
        }
        sw_s_lock_switch.isChecked=!SP.getBoolean(Constant.spName,Constant.SpKey.noLockScreen)
        rv_s_lock_colorList.adapter=object :BaseAdapter(colorList){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
                val v= ImageView(this@LockScreenSettingActivity)
                v.background=getDrawable(R.drawable.border_1dp)
                val lp=ViewGroup.LayoutParams(ViewUtil.dpToPx(40f),ViewUtil.dpToPx(40f))
                v.layoutParams=lp
                v.setPadding(10,10,10,10)
                return BaseViewHolder(v)
            }

            override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
                (holder.rootView as ImageView).setImageDrawable(ColorDrawable(colorList[position]))
                holder.rootView.setOnClickListener {
                    view_s_lock_colorSelected.setImageDrawable(ColorDrawable(colorList[position]))
                    SP.putValue(Constant.spName,Constant.SpKey.lockScreenBgColor,colorList[position])
                    SP.putValue(Constant.spName,Constant.SpKey.lockScreenBgMode,LockScreenActivity.BG_MODE_COLOR)
                }
            }
        }

        if(SP.getString(Constant.spName,Constant.SpKey.lockScreenBgMode)==LockScreenActivity.BG_MODE_IMAGE){
            setBackgroundImage(SP.getString(Constant.spName,Constant.SpKey.lockScreenBgImagePath))
        }


        tv_s_lock_setImageBackground.setOnClickListener {
            val intent=Intent(Intent.ACTION_GET_CONTENT,null)
            intent.type = "image/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)

            startActivityForResult(Intent.createChooser(intent,"图片选择"),2)
        }


    }
    private fun setBackgroundImage(path:String){
        ImageLoad.load(path).into(object :BaseGlideTarget(ViewUtil.screenWidth() shr 2,ViewUtil.screenHeight() shr 2){
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                val lp=iv_s_lock_imageShow.layoutParams
                lp.width=ViewUtil.screenWidth() shr 2
                lp.height=ViewUtil.screenHeight() shr 2
                iv_s_lock_imageShow.setImageDrawable(resource)
            }
        })
    }

    /**
     * 颜色选择
     */
    private fun colorPick(){
        val colorPickerDialog=ColorPickerDialog(this,SP.getInt(Constant.spName,Constant.SpKey.lockScreenBgColor))
        val array=IntArray(5)
        array[0]=Color.WHITE
        array[1]=Color.BLUE
        array[2]=Color.RED
        array[3]=Color.CYAN
        array[4]=Color.YELLOW
        colorPickerDialog.setColorArray(array)
        colorPickerDialog.negativeButtonListener= View.OnClickListener {
            colorPickerDialog.cancel()
        }
        colorPickerDialog.positiveButtonListener={
            colorPickerDialog.cancel()
            if(it!=null){
                SP.putValue(Constant.spName,Constant.SpKey.lockScreenBgColor,it)
                view_s_lock_colorSelected.setImageDrawable(ColorDrawable(it))
            }
        }
        colorPickerDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode!= Activity.RESULT_OK)return
        if(data==null)return
        val uri=data.data

        if(requestCode==2){
            val intent = Intent("com.android.camera.action.CROP")
            intent.setDataAndType(uri, "image/*")
            intent.putExtra("crop", "true")
            intent.putExtra("aspectX", ViewUtil.screenWidth())
            intent.putExtra("aspectY", ViewUtil.screenHeight())
            intent.putExtra("outputX", ViewUtil.screenWidth())
            intent.putExtra("outputY",ViewUtil.screenHeight())
            intent.putExtra("scale", false)
            intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(File(Environment.getExternalStorageDirectory().absolutePath+"/1.png")))
            intent.putExtra("return-data", false)
            intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString())
            intent.putExtra("noFaceDetection", true)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            //intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)//报错、无权限
            startActivityForResult(intent,3)
            return
        }


        if(requestCode==3) {
            val dir = File(filesDir.absolutePath + File.separator + "bg")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val path = dir.absolutePath + File.separator + "lockScreenImage.png"
            try {
                val inputStream = FileInputStream(Environment.getExternalStorageDirectory().absolutePath+"/1.png")
                inputStream.use { input ->
                    FileOutputStream(path).use { output ->
                        input.copyTo(output)
                    }
                }
                SP.putValue(Constant.spName, Constant.SpKey.lockScreenBgImagePath, path)
                SP.putValue(Constant.spName, Constant.SpKey.lockScreenBgMode, LockScreenActivity.BG_MODE_IMAGE)
                setBackgroundImage(path)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        fun actionStart(context: Context){
            context.startActivity(Intent(context,LockScreenSettingActivity::class.java))
        }
    }


}