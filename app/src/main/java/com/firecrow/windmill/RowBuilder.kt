package com.firecrow.windmill

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.AdaptiveIconDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class RowBuilder(val ctx: Context) {

    fun getRowColor(icon: AdaptiveIconDrawable): Int {
        // return (icon.background as ColorDrawable).getColor()
        return 0x00000

    }

    fun getWidth(): Int{
        val total = (ctx as WMActivity).getWindow().decorView.width
        val cols = 5;
        return total/cols;
    }

    fun buildRow(app: AppData, height: Int): View {
        val inflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val row = inflater.inflate(R.layout.row, null)
        val container = row.findViewById(R.id.list_icon_container) as LinearLayout
        val iconv = row.findViewById(R.id.icon) as ImageView
        val label = row.findViewById(R.id.label) as TextView

        label.text = app.name

        iconv.setImageDrawable(app.icon)

        container.setBackgroundColor(app.color)
        container.layoutParams = LinearLayout.LayoutParams(getWidth(), height)

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
        return row
    }
}

