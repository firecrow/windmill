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
    val ctx: WMActivity,
    var apps: ArrayList<AppData>,
    val rowBuilder: RowBuilder,
) :
    ArrayAdapter<AppData>(ctx, R.layout.row, R.id.icon, apps) {
    override fun getCount(): Int {
        return apps.count()
    }

    override fun getItem(idx: Int): AppData {
        return apps.get(idx)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = apps.get(position)
        val cellSize = ctx.controller.cellSize
        return rowBuilder.buildRow(item, cellSize.y+1, cellSize.x+1, (ctx as WMActivity).controller.query)
    }
}

class WMGridAdapter(
    val ctx: WMActivity,
    var apps: ArrayList<AppData>,
    val rowBuilder: RowBuilder,
) :
    ArrayAdapter<AppData>(ctx, R.layout.cell, R.id.icon, apps) {
    override fun getCount(): Int {
        return apps.count()
    }

    override fun getItem(idx: Int): AppData {
        return apps.get(idx)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = apps.get(position)
        val cellSize = ctx.controller.cellSize
        return rowBuilder.buildCell(item, cellSize.y+1, position % 2 != 0)
    }
}

