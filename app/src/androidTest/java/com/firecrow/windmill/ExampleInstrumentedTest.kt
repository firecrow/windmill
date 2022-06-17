package com.firecrow.windmill

import android.util.Log
import android.widget.LinearLayout
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.firecrow.windmill", appContext.packageName)
    }
    @Test fun testEvent() {
        launchActivity<WMActivity>().onActivity {
            val barLayout = it.findViewById<LinearLayout>(R.id.search_bar) as LinearLayout
            val searchObj = SearchObj(barLayout,it)
        }
    }
}