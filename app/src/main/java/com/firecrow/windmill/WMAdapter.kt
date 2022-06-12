package com.firecrow.windmill

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

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

    override fun getView(idx: Int, view: View?, parent: ViewGroup): View {
        val priorColor: Int = if (idx > 0) getItem(idx - 1).color else Color.parseColor("#000000")
        val item = getItem(idx)
        if(cellHeight == 0){
            cellHeight = parent.getHeight()/10
        }
        // odd bug with adding one to cellHeight fix is to increment it down here
        return rowBuilder.updateRow(buildItemContent(item, cellHeight+1), idx, item, priorColor)
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

    override fun buildItemContent(item: AppData, height: Int): View {
        return rowBuilder.buildCell(item, height)
    }
}


