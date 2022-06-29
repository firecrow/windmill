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
        controller.setState(ScreenToken.GRID)
        controller.update("")
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
        controller.searchObj.setNavIconState(screen)
    }

    override fun onResume() {
        controller.reset()
        super.onResume()
    }
}