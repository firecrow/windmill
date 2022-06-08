package com.firecrow.windmill

import android.content.Context
import android.graphics.drawable.AdaptiveIconDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView

class RowBuilder(val ctx: Context) {

    fun getRowColor(icon: AdaptiveIconDrawable): Int {
        // return (icon.background as ColorDrawable).getColor()
        return 0x00000
    }

    fun buildRow(app: AppData): View {
        val inflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row = inflater.inflate(R.layout.row, null)
        val pm = ctx.getPackageManager()

        val iconv = row.findViewById(R.id.icon) as ImageView
        iconv.setImageDrawable(app.icon)
        row.setBackgroundColor(app.color)
        return row
    }

    fun updateRow(row: View, idx: Int, app: AppData, priorColor: Int): View {
        row.setBackgroundColor(app.color)
        return row
    }
}

