package com.firecrow.windmill

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels

class SearchObj(val bar: LinearLayout, val ctx: WMActivity) {
    val input = bar.findViewById<EditText>(R.id.search) as EditText
    val button = bar.findViewById<ImageView>(R.id.search_button) as ImageView
    val gridNavButton = bar.findViewById<ImageView>(R.id.grid_nav_button) as ImageView
    val listNavButton = bar.findViewById<ImageView>(R.id.list_nav_button) as ImageView
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

        gridNavButton.setImageResource(R.drawable.grid_icon)
        gridNavButton.setOnClickListener(View.OnClickListener {
            ctx.setContent(ScreenToken.GRID)
        })
        listNavButton.setImageResource(R.drawable.list_icon)
        listNavButton.setOnClickListener(View.OnClickListener {
            ctx.setContent(ScreenToken.LIST)
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