package com.firecrow.windmill

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.ShapeDrawable
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.children
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

    fun makeMockApp(color: Int = Color.TRANSPARENT): AppData {
        val logo = MockBitmapDrawable(1).drawable
        val colorDrawable = ColorDrawable(color)
        val icon = AdaptiveIconDrawable(colorDrawable, logo)
        val name = "Test App"
        val packageName = "com.firecrow.TestApp"

        return AppData(
            name,
            packageName,
            icon,
            Color.TRANSPARENT,
        )
    }

    @Test fun testAppData() {
        val color = Color.RED
        val app = makeMockApp(color)

        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val inflater =  ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val cell = inflater.inflate(R.layout.cell, null)
        val iconView = cell.findViewById<AppIconView>(R.id.icon)
        iconView.icon = app.icon

        assertEquals(iconView.backdropColor, color)

        val containerLayout = iconView.children.first()
        assertEquals(containerLayout is LinearLayout, true)

        val logoView = (containerLayout as LinearLayout).children.first()
        assertEquals(logoView is ImageView, true)
        assertEquals((logoView as ImageView).drawable, app.icon)
    }

    @Test fun testRowBuilder_buildCell() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val builder = RowBuilder(ctx)

        val color = Color.BLACK
        val height = 100
        val app1 = makeMockApp(color)
        val cell = builder.buildCell(app1, height)
        Log.i("fcrow", "color for arg is"+color.toString())
        val iconView = cell.findViewById<AppIconView>(R.id.icon)

        Log.i("fcrow", "-> color for arg is"+color.toString())
        assertEquals(iconView.backdropColor, Color.BLACK)
        assertEquals(cell.layoutParams.height, height)
    }

    @Test fun testRowBuilder_buildRow() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val builder = RowBuilder(ctx)

        val height = 100
        val app1 = makeMockApp()
        val row = builder.buildRow(app1, 100)
        val iconView = row.findViewById<AppIconView>(R.id.icon)

        assertEquals(iconView.backdropColor, Color.TRANSPARENT)
        assertEquals(row.layoutParams.height, height)
    }
}