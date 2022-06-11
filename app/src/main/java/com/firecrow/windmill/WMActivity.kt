package com.firecrow.windmill

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class WMActivity : AppCompatActivity() {
    lateinit var adapter: WMAdapter
    lateinit var layout: GridView
    lateinit var fetcher: Fetcher
    lateinit var searchObj:SearchObj

    override fun onCreate(instance: Bundle?) {
        super.onCreate(instance)
        setContentView(R.layout.main)

        val rowBuilder = RowBuilder(this)

        layout = findViewById<GridView>(R.id.apps) as GridView
        adapter = WMAdapter(this, R.layout.row, arrayListOf<AppData>(), rowBuilder, layout)
        fetcher = Fetcher(this)
        searchObj =
            SearchObj(findViewById<LinearLayout>(R.id.search_bar) as LinearLayout, this)

        setupLayout(this, layout, adapter, searchObj)
        update("")
    }

    override fun onResume() {
        reset()
        super.onResume()
    }

    fun hideKb() {
        val view: View? = this.getCurrentFocus()
        val v = view?.let { it } ?: run { View(this) }
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.windowToken, 0)
    }

    fun setupLayout(ctx: Context, layout: GridView, adapter: WMAdapter, searchObj: SearchObj) {
        layout.setAdapter(adapter)
        layout.setOnItemClickListener { parent, view, idx, id ->
            val app: AppData = adapter.getItem(idx)
            ctx.getPackageManager().getLaunchIntentForPackage(app.appInfo.packageName)
                ?.let { ctx.startActivity(it) }
        }
    }

    fun update(search: String?) {
        adapter.clear()
        val apps = fetcher.fetch(search ?: "")
        apps?.let {
            adapter.addAll(it)
            adapter.notifyDataSetChanged()
        }
    }

    fun scrollTop() {
        layout.setSelection(0)
    }

    fun reset() {
        hideKb()
        searchObj.input?.setText("")
        searchObj.input?.clearFocus()
        update("")
        scrollTop()
    }
}