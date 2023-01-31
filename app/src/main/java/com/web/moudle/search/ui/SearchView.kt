package com.web.moudle.search.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import com.music.m.R
import kotlinx.android.synthetic.main.layout_search.view.*

/**
 * 搜索框
 */
class SearchView @JvmOverloads constructor(
        ctx: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(ctx, attrs, defStyleAttr) {

    private var editText: EditText
    private var cancel: TextView
    var hint: String? = null
        set(v) {
            field = v
            editText.hint = v
        }
    var cancelCallback: ((View) -> Unit)? = null
        set(v) {
            field = v
            cancel.setOnClickListener(v)
        }
    var searchCallback: ((String) -> Unit)? = null
    var textChangeCallback: ((String) -> Unit)? = null

    init {


        addView(LayoutInflater.from(ctx).inflate(R.layout.layout_search, this, false))
        editText = this.findViewById(R.id.et_searchView)
        cancel = this.findViewById(R.id.tv_cancel)
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                textChangeCallback?.invoke(s.toString())
            }
        })
        editText.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                searchCallback?.invoke(editText.text.toString())
            }
            return@setOnKeyListener false
        }
        iv_delete.setOnClickListener {
            editText.text = null
        }

        attrs?.let {
            val ta = ctx.obtainStyledAttributes(attrs, R.styleable.SearchView)
            hint = ta.getString(R.styleable.SearchView_searchView_hint)
            if (ta.getBoolean(R.styleable.SearchView_searchView_autoFocus, false)) {
                editText.post {
                    editText.requestFocus()
                }
            }
            editText.isEnabled = ta.getBoolean(R.styleable.SearchView_searchView_enable, true)
            ta.recycle()
        }
    }
}