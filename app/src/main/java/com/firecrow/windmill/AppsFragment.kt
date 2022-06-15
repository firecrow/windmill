package com.firecrow.windmill

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer

open abstract class AppsFragment(resource:Int): Fragment(resource) {
    lateinit var adapter: WMAdapter
    private val model: AppsObservables by activityViewModels<AppsObservables>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val absList = view.findViewById<AbsListView>(R.id.apps_grid) as AbsListView

        val rowBuilder = RowBuilder(activity as WMActivity)

        adapter = getAdapter(rowBuilder)
        adapter.setupView(absList)
        val ctx = activity as WMActivity?
        ctx?.let {
            absList.setAdapter(adapter)
            absList.setOnItemClickListener { parent, view, idx, id ->
                val app: AppData = adapter.getItem(idx)
                it.getPackageManager().getLaunchIntentForPackage(app.appInfo.packageName)
                    ?.let { ctx?.startActivity(it) }
            }
        }

        update("")

        model.searchCriteria.observe(viewLifecycleOwner, Observer<String> { query ->
            update(query)
        })
    }

    fun update(query: String) {
        val apps = (activity as WMActivity)?.fetcher.fetch(query)
        apps?.let {
            adapter.clear()
            adapter.addAll(it)
            adapter.notifyDataSetChanged()
        }
    }

    abstract fun getAdapter(rowBuilder:RowBuilder): WMAdapter
}
