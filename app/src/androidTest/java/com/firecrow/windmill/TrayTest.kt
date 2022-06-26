package com.firecrow.windmill

import android.content.Context
import android.view.LayoutInflater
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.android.synthetic.main.main.view.*
import kotlinx.android.synthetic.main.tray.view.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SeachBarTests {

    @Test
    fun testTray() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val mainLayout = inflater.inflate(R.layout.main, null)
        // val searchObj = SearchObj(mainLayout.tray, ctx as WMActivity)
    }
}
