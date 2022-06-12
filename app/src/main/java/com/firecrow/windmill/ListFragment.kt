package com.firecrow.windmill

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

class ListFragment: AppsFragment(R.layout.list_fragment_layout) {
    override fun getAdapter(rowBuilder: RowBuilder): WMAdapter {
        return WMGridAdapter(activity as WMActivity, R.layout.row, arrayListOf<AppData>(), rowBuilder)
    }
}