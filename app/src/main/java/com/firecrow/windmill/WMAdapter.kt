package com.firecrow.windmill

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

class WMAdapter(
    val ctx: Context,
    resource: Int,
    var apps: ArrayList<AppData>,
    val rowBuilder: RowBuilder
) :
    ArrayAdapter<AppData>(ctx, resource, apps) {
    override fun getCount(): Int {
        return apps.count()
    }

    override fun getItem(idx: Int): AppData {
        return apps.get(idx)
    }

    override fun getItemId(idx: Int): Long {
        return idx.toLong()
    }

    override fun getView(idx: Int, view: View?, parent: ViewGroup): View {
        val priorColor: Int = if (idx > 0) getItem(idx - 1).color else Color.parseColor("#000000")
        val item = getItem(idx)
        return rowBuilder.updateRow(rowBuilder.buildRow(item), idx, item, priorColor)
    }
}

