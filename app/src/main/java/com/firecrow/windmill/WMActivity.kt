package com.firecrow.windmill

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.*
import androidx.lifecycle.Observer


val NAV_SELECTED = "nav:selected"
val DEFAULT = "default"
val GRID = "grid"
val LIST = "list"
val ANONYMOUS = "anonymous"

open class WMActivity : AppCompatActivity() {
    lateinit var controller: WMController
    lateinit var layout: FragmentContainerView
    val model: AppsObservables by viewModels()
    lateinit var fetcher: Fetcher

    override fun onCreate(instance: Bundle?) {
        super.onCreate(instance)
        setContentView(R.layout.main)
        fetcher = Fetcher(this)

        layout = findViewById<FragmentContainerView>(R.id.apps_fragment) as FragmentContainerView
        val searchBar = findViewById<LinearLayout>(R.id.tray) as SlotViewGroup
        controller = WMController(this, layout, searchBar)

        if (instance == null) {
            setupNavigation()
        }

        val bus = NotifyBus.busMap.get(DEFAULT)
        bus?.subscribe(NotifyEventCallback { event ->
                setContent(event.valueString ?: "")
            }, listOf<String>(NAV_SELECTED))

        controller.update("")
    }

    fun setupNavigation(){
        setContent(GRID)
    }

    fun setContent(screen: String){
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            if (screen == GRID) {
                replace<GridFragment>(R.id.apps_fragment)
            }else if (screen == LIST) {
                replace<ListFragment>(R.id.apps_fragment)
            }
        }
    }

    override fun onResume() {
        controller.reset()
        super.onResume()
    }
}