package com.firecrow.windmill

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer

open class ListFragment: Fragment(R.layout.grid_fragment_layout) {
    lateinit var adapter: WMListAdapter
    private val model: AppsObservables by activityViewModels<AppsObservables>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val absList = view.findViewById<AbsListView>(R.id.apps_list) as AbsListView

        val rowBuilder = RowBuilder(activity as WMActivity)

        adapter = getAdapter(rowBuilder)
        val ctx = activity as WMActivity?
        ctx?.let {
            absList.setAdapter(adapter)
            absList.setOnItemClickListener { parent, view, idx, id ->
                val app: AppData = adapter.getItem(idx)
                it.getPackageManager().getLaunchIntentForPackage(app.packageName)
                    ?.let { ctx?.startActivity(it) }
            }
        }

        load()
    }

    fun getAdapter(rowBuilder: RowBuilder): WMListAdapter {
        return WMListAdapter(activity as WMActivity, arrayListOf<AppData>(), rowBuilder)
    }

    fun load() {
        val apps = (activity as WMActivity)?.fetcher.fetch("")
        apps?.let {
            adapter.clear()
            adapter.addAll(it)
            adapter.notifyDataSetChanged()
        }
    }
}
