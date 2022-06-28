package com.firecrow.windmill

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.children
import kotlinx.android.synthetic.main.tray.view.*

class SearchObj(val slot: SlotViewGroup, val ctx: WMActivity) {
    val bar = ctx.layoutInflater.inflate(R.layout.tray, slot)
    val search = bar.search

    val input = bar.search
    val button = bar.search_button
    val gridNavButton = bar.grid_nav_button
    val idendifyGridNav = bar.identify_grid_nav
    val listNavButton = bar.list_nav_button
    val idendifyListNav = bar.identify_list_nav
    var state = SearchState.BLANK

    init {

        button.setOnClickListener { v ->
            if (state == SearchState.SEARCH) {
                ctx.controller.reset()
            } else if (state == SearchState.SCROLL) {
                ctx.controller.scrollTop()
            }
        }

        input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(editable: Editable?) {}
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                ctx.controller.update(s.toString())
                setButton(0)
                ctx.controller.scrollTop()
            }
        })

        gridNavButton.setImageResource(R.drawable.grid_icon)
        idendifyGridNav.setOnEventRecieved { component, event ->
            if (event.identifier == idendifyGridNav.identifier) {
                ctx.setContent(ScreenToken.GRID)
                setNavIconState(ScreenToken.GRID)
            }
        }
        idendifyGridNav.setOnClickListener(View.OnClickListener {
            idendifyGridNav.bus?.dispatch(
                NotifyEvent(
                    idendifyGridNav.identifier,
                    "SELECTED",
                    null,
                    null
                )
            )
        })
        listNavButton.setImageResource(R.drawable.list_icon)
        idendifyListNav.setOnClickListener(View.OnClickListener {
            ctx.setContent(ScreenToken.LIST)
            setNavIconState(ScreenToken.LIST)
        })
    }

    fun resetNav() {
        listNavButton.setColorFilter(R.color.nav_icon_tint)
        gridNavButton.setColorFilter(R.color.nav_icon_tint)
    }

    fun setNavIconState(activeNavState: ScreenToken) {
        resetNav()
        if (activeNavState == ScreenToken.GRID) {
            gridNavButton.clearColorFilter()
        }
        if (activeNavState == ScreenToken.LIST) {
            listNavButton.clearColorFilter()
        }
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