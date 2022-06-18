package com.firecrow.windmill

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ShapeDrawable
import android.view.LayoutInflater
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.util.stream.Stream


private class MockBitmapDrawable(val id: Int) {
    val bitmap = BitmapFactory.decodeByteArray("bytes".toByteArray(), 0, 0)
    val drawable = BitmapDrawable(bitmap)
}

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
    /*
    @Test fun testEvent() {
        launchActivity<WMActivity>().onActivity {
            val barLayout = it.findViewById<LinearLayout>(R.id.search_bar) as LinearLayout
            val searchObj = SearchObj(barLayout,it)
        }
    }
    */
    @Test fun testAppData() {
        val logo = MockBitmapDrawable(1).drawable
        val iconDrawable = AdaptiveIconDrawable(logo, ShapeDrawable())
        val name = "Test App"
        val packageName = "com.firecrow.TestApp"
        val color = Color.RED

        val app = AppData(
            name,
            packageName,
            logo,
            color,
        )

        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val inflater =  ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val cell = inflater.inflate(R.layout.cell, null)
        val iconView = cell.findViewById<AppIconView>(R.id.icon)
        iconView.setIcon(app.icon)
        iconView.setBackdropColor(app.color)

        assertEquals(iconView.backdropColor, app.color)

    }
}