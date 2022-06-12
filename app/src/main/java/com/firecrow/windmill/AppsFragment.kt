package com.firecrow.windmill

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.GridView
import androidx.fragment.app.Fragment

open abstract class AppsFragment(resource:Int): Fragment(resource) {
    lateinit var adapter: WMAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val absList = view.findViewById<AbsListView>(R.id.apps_grid) as AbsListView

        val rowBuilder = RowBuilder(activity as WMActivity)

        adapter = getAdapter(rowBuilder)
        val ctx = activity as WMActivity?
        ctx?.let {
            absList.setAdapter(adapter)
            absList.setOnItemClickListener { parent, view, idx, id ->
                val app: AppData = adapter.getItem(idx)
                it.getPackageManager().getLaunchIntentForPackage(app.appInfo.packageName)
                    ?.let { ctx?.startActivity(it) }
            }
        }
        update()
    }

    fun update() {
        val apps = (activity as WMActivity)?.fetcher.fetch("")
        apps?.let {
            adapter.clear()
            adapter.addAll(it)
            adapter.notifyDataSetChanged()
        }
    }

    abstract fun getAdapter(rowBuilder:RowBuilder): WMAdapter
}
