package com.firecrow.windmill

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.fragment.app.FragmentContainerView

open class WMController (val ctx: WMActivity, val layout:FragmentContainerView, val searchBar:SlotViewGroup) {
    val searchObj = SearchObj(searchBar, ctx)

    fun hideKb() {
        val view: View? = ctx.getCurrentFocus()
        val imm = ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val v = view?.let { it } ?: run { View(ctx) }
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    fun reset() {
        hideKb()
        searchObj.input?.setText("")
        searchObj.input?.clearFocus()
        update("")
        scrollTop()
    }
    fun update(search: String?) {
        ctx.model.searchCriteria.value = search ?: ""
    }

    fun scrollTop() {
        // layout.setSelection(0)
    }
}