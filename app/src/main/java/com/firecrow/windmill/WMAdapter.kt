package com.firecrow.windmill

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.ListView

class WMListAdapter(
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

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = apps.get(position)
        if(cellHeight == 0)
            cellHeight = (ctx as WMActivity).layout.height /15
        return rowBuilder.buildRow(item, cellHeight+1)
    }

    fun resetHeight(){
        cellHeight = 0
    }
}

class WMGridAdapter(
    val ctx: Context,
    var apps: ArrayList<AppData>,
    val rowBuilder: RowBuilder,
) :
    ArrayAdapter<AppData>(ctx, R.layout.cell, R.id.icon, apps) {
    var cellHeight:Int = 0;
    override fun getCount(): Int {
        return apps.count()
    }

    override fun getItem(idx: Int): AppData {
        return apps.get(idx)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = apps.get(position)
        if(cellHeight == 0)
            cellHeight = (ctx as WMActivity).layout.height / 10
        return rowBuilder.buildCell(item, cellHeight+1, position % 2 != 0)
    }

    fun resetHeight(){
        cellHeight = 0
    }
}

