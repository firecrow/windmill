package com.firecrow.windmill

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.*
import androidx.lifecycle.Observer


class WMActivity : AppCompatActivity() {
    lateinit var layout: FragmentContainerView
    lateinit var fetcher: Fetcher
    lateinit var searchObj:SearchObj
    val model: AppsObservables by viewModels()

    override fun onCreate(instance: Bundle?) {
        super.onCreate(instance)
        setContentView(R.layout.main)

        layout = findViewById<FragmentContainerView>(R.id.apps_fragment) as FragmentContainerView
        fetcher = Fetcher(this)
        searchObj =
            SearchObj(findViewById<LinearLayout>(R.id.search_bar) as LinearLayout, this)

        if (instance == null) {
            setupNavigation()
        }
        update("")
    }

    fun setupNavigation(){
        setContent(ScreenToken.GRID)
    }

    fun setContent(screen: ScreenToken){
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            if (screen == ScreenToken.GRID) {
                replace<GridFragment>(R.id.apps_fragment)
            }else if(screen == ScreenToken.LIST){
                replace<ListFragment>(R.id.apps_fragment)
            }
        }
        searchObj.setNavIconState(screen)
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

    fun update(search: String?) {
        model.searchCriteria.value = search ?: ""
    }

    fun scrollTop() {
        //layout.setSelection(0)
    }

    fun reset() {
        hideKb()
        searchObj.input?.setText("")
        searchObj.input?.clearFocus()
        update("")
        scrollTop()
    }
}