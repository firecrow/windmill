package com.firecrow.windmill

import android.content.Context
import android.graphics.Point
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentContainerView
import com.firecrow.windmill.core.NotifyEvent


open class WMController (val ctx: WMActivity, val layout:FragmentContainerView, val searchBar:SlotViewGroup) {
    val searchObj = SearchObj(searchBar, ctx)
    var query: String = ""
    var layoutHeight = 0
    var layoutWidth = 0
    val columns = 5
    val rows = 10
    var totals: Point = Point(0,0)
    val bus = searchObj.idendifyGridNav.bus

    fun setState(state:ScreenToken){
        var targetValue = "grid"
        if(state == ScreenToken.LIST) {
            targetValue = "list"
        }
        bus?.dispatch(
            NotifyEvent(searchObj.idendifyGridNav.identifier, searchObj.idendifyGridNav.providesState, targetValue, null)
        )
    }

    fun hideKb() {
        val view: View? = ctx.getCurrentFocus()
        val imm = ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val v = view?.let { it } ?: run { View(ctx) }
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    private fun getHeightRect(): Point {
        val metrics = DisplayMetrics()
        ctx.getWindowManager().getDefaultDisplay().getMetrics(metrics)

        val height = metrics.heightPixels - ctx.resources.getDimension(R.dimen.search_bar_height).toInt()
        return Point(metrics.widthPixels, height )
    }

    val cellSize:Point get(){
        if(totals.x == 0)
            totals = getHeightRect()
        return Point(totals.x / columns, totals.y / rows)
    }

    fun reset() {
        hideKb()
        searchObj.input?.setText("")
        searchObj.input?.clearFocus()
        update("")
        scrollTop()
    }

    fun update(search: String) {
        query = search
        var screen = ScreenToken.GRID
        if(query?.length > 0){
            screen = ScreenToken.LIST
        }
        ctx.setContent(screen)
        ctx.model.searchCriteria.value = query
    }

    fun scrollTop() {
        // layout.setSelection(0)
    }
}