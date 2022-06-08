package com.firecrow.windmill

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout

class SearchObj(val bar: LinearLayout, val ctx: WMActivity) {
    val input = bar.findViewById<EditText>(R.id.search) as EditText
    val button = bar.findViewById<ImageView>(R.id.search_button) as ImageView
    var state = SearchState.BLANK

    init {

        button.setOnClickListener { v ->
            if (state == SearchState.SEARCH) {
                ctx.reset()
            } else if (state == SearchState.SCROLL) {
                ctx.scrollTop()
            }
        }

        input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(editable: Editable?) {}
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                ctx.update(s.toString())
                setButton(0)
                ctx.scrollTop()
            }
        })
    }

    fun setButton(itemPos: Int) {
        if (input.text.length > 0) {
            state = SearchState.SEARCH
            button.setImageResource(R.drawable.x_search)
        } else if (itemPos > 0) {
            state = SearchState.SCROLL
            button.setImageResource(R.drawable.up_arrow)
        } else {
            state = SearchState.BLANK
            button.setImageResource(R.drawable.blank)
        }
    }

    fun getText(): String {
        return input.text.toString()
    }
}