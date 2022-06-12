package com.firecrow.windmill

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.GridView
import androidx.fragment.app.Fragment

class GridFragment: AppsFragment(R.layout.grid_fragment_layout) {
    override fun getAdapter(rowBuilder: RowBuilder): WMAdapter {
        return WMGridAdapter(activity as WMActivity, R.layout.cell, arrayListOf<AppData>(), rowBuilder)
    }
}
