package com.firecrow.windmill

import android.content.Context
import android.graphics.drawable.AdaptiveIconDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView

class RowBuilder(val ctx: Context) {

    fun getRowColor(icon: AdaptiveIconDrawable): Int {
        // return (icon.background as ColorDrawable).getColor()
        return 0x00000
    }

    fun buildRow(app: AppData, height: Int): View {
        val inflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row = inflater.inflate(R.layout.row, null)
        val iconv = row.findViewById(R.id.icon) as ImageView
        iconv.setImageDrawable(app.icon)
        row.setBackgroundColor(app.color)
        row.layoutParams.height = height
        return row
    }

    fun buildCell(app: AppData, height: Int): View {
        val inflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val cell = inflater.inflate(R.layout.cell, null)
        val iconv = cell.findViewById(R.id.icon) as ImageView
        iconv.setImageDrawable(app.icon)
        cell.setBackgroundColor(app.color)
        cell.layoutParams = LinearLayout.LayoutParams(GridView.AUTO_FIT, height)
        return cell
    }

    fun updateRow(row: View, idx: Int, app: AppData, priorColor: Int): View {
        row.setBackgroundColor(app.color)
        return row
    }
}

