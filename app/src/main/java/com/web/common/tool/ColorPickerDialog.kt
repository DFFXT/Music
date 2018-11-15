package com.web.common.tool

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.support.v4.graphics.ColorUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.web.common.util.ResUtil
import com.web.misc.ColorPicker
import com.web.web.R

class ColorPickerDialog(context:Context,currentColor:Int) {
    private var dialog:AlertDialog?=null
    private var rootView: View = LayoutInflater.from(context).inflate(R.layout.dialog_color_picker,null,false)
    private var builder:AlertDialog.Builder = AlertDialog.Builder(context)

    private var colorPicker:ColorPicker
    private var alphaSeekBar:SeekBar
    private var negativeButton:TextView
    private var positiveButton:TextView
    private var colorShowView:View
    //**外部的监听器
    var colorChange:((Int?)->Unit)?=null
    var negativeButtonListener:View.OnClickListener?=null
    var positiveButtonListener:((Int?)->Unit)?=null
    private var color:Int=currentColor
    private var alpha:Int=Color.alpha(currentColor)
    init {
        builder.setView(rootView)
        colorPicker=rootView.findViewById(R.id.colorPicker)
        alphaSeekBar=rootView.findViewById(R.id.seekBar_alpha)
        negativeButton=rootView.findViewById(R.id.tv_negative)
        positiveButton=rootView.findViewById(R.id.tv_positive)
        colorShowView=rootView.findViewById(R.id.view_colorShow)
        negativeButton.setOnClickListener{
            negativeButtonListener?.onClick(it)
        }
        positiveButton.setOnClickListener {
            positiveButtonListener?.invoke(color)
        }
        colorPicker.listener= {mColor->
            mColor?.let {
                color= ColorUtils.setAlphaComponent(it,alpha)
                colorShowView.setBackgroundColor(color)
                colorChange?.invoke(color)
            }
        }
        alphaSeekBar.max=255
        alphaSeekBar.progress=alpha
        alphaSeekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                alpha=progress
                color= ColorUtils.setAlphaComponent(color,alpha)
                colorChange?.invoke(color)
                colorShowView.setBackgroundColor(color)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        negativeButton.text=ResUtil.getString(R.string.no)
        positiveButton.text=ResUtil.getString(R.string.yes)
        colorShowView.setBackgroundColor(color)



    }
    fun setColorArray(array:IntArray){
        colorPicker.colorArray=array
        val pa=FloatArray(array.size)
        val gap=1f/(pa.size-1)
        for(i in 0 until pa.size){
            pa[i]=i*gap
        }
        setPositionArray(pa)
    }
    fun setPositionArray(array:FloatArray){
        colorPicker.positionArray=array
    }
    fun cancel(){
        dialog?.cancel()
    }
    fun show(){
        dialog=builder.create()
        dialog?.show()
    }

}