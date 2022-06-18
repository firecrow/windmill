package com.firecrow.windmill

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.ListView

open class WMAdapter(
    val ctx: Context,
    resource: Int,
    var apps: ArrayList<AppData>,
    val rowBuilder: RowBuilder,
) :
    ArrayAdapter<AppData>(ctx, resource, apps) {
    var cellHeight:Int = 0;
    override fun getCount(): Int {
        return apps.count()
    }

    override fun getItem(idx: Int): AppData {
        return apps.get(idx)
    }


    override fun getItemId(idx: Int): Long {
        return idx.toLong()
    }

    open fun setupView(view: AbsListView){
        val listView = view as ListView;
        listView.divider = null
    }


    open fun buildItemContent(item: AppData, height: Int): View {
        return rowBuilder.buildRow(item, height)
    }

    fun resetHeight(){
        cellHeight = 0
    }
}

class WMGridAdapter(
    ctx: Context,
    resource: Int,
    apps: ArrayList<AppData>,
    rowBuilder: RowBuilder,
) : WMAdapter(ctx, resource, apps, rowBuilder){

    override fun setupView(view: AbsListView){
        return
    }

    override fun buildItemContent(item: AppData, height: Int): View {
        return rowBuilder.buildCell(item, height)
    }
}


