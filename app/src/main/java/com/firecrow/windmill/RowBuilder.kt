package com.firecrow.windmill

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.AdaptiveIconDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class RowBuilder(val ctx: Context) {
    val inflater: LayoutInflater =
        ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var isOdd = false

    fun buildRow(app: AppData, height: Int): View {
        val row = inflater.inflate(R.layout.row, null)

        val iconView = row.findViewById<AppIconView>(R.id.icon)
        iconView.setIcon(app.icon)

        val label = row.findViewById(R.id.label) as TextView
        label.text = app.name

        row.layoutParams = LinearLayout.LayoutParams(row.getWidth(), height)

        return row
    }

    fun buildCell(app: AppData, height: Int): View {
        val cell = inflater.inflate(R.layout.cell, null)
        val iconView = cell.findViewById<AppIconView>(R.id.icon)

        iconView.setIcon(app.icon)

        cell.layoutParams = LinearLayout.LayoutParams(GridView.AUTO_FIT, height)

        if(isOdd){
            val icon = cell.findViewById<AppIconView>(R.id.icon) as AppIconView
            icon.tint = 0.03f
        }
        isOdd = !isOdd

        return cell
    }

    fun updateRow(row: View, idx: Int, app: AppData, priorColor: Int): View {
        return row
    }
}

