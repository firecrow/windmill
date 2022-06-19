package com.firecrow.windmill

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.ListView

open class WMAdapter(
    val ctx: Context,
    var apps: ArrayList<AppData>,
    val rowBuilder: RowBuilder,
) :
    ArrayAdapter<AppData>(ctx, R.layout.row, R.id.icon, apps) {
    var cellHeight:Int = 0;
    override fun getCount(): Int {
        return apps.count()
    }

    override fun getItem(idx: Int): AppData {
        return apps.get(idx)
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

open class WMGridAdapter(
    val ctx: Context,
    var apps: ArrayList<AppData>,
    val rowBuilder: RowBuilder,
) :
    ArrayAdapter<AppData>(ctx, R.layout.cell, R.id.icon, apps) {
    var cellHeight:Int = 100;
    override fun getCount(): Int {
        return apps.count()
    }

    override fun getItem(idx: Int): AppData {
        return apps.get(idx)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = apps.get(position)
        return rowBuilder.buildCell(item, cellHeight)
    }

    fun resetHeight(){
        cellHeight = 0
    }
}

