package com.firecrow.windmill

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
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
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock


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
        )
    }

    @Test
    fun testAppData() {
        val color = Color.RED
        val app = makeMockApp(color)

        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val cell = inflater.inflate(R.layout.cell, null)
        val iconView = cell.findViewById<AppIconView>(R.id.icon)
        iconView.setIcon(app.icon)

        assertEquals(iconView.backdrop, app.icon.background)
        assertEquals(iconView.logo, app.icon.foreground)

        val containerLayout = iconView.children.first()
        assertEquals(containerLayout is LinearLayout, true)
        assertEquals((containerLayout as LinearLayout).background, app.icon.background)

        val logoView = (containerLayout as LinearLayout).children.first()
        assertEquals(logoView is ImageView, true)
        assertEquals((logoView as ImageView).drawable, app.icon.foreground)
    }

    @Test
    fun testRowBuilder_buildCell() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val builder = RowBuilder(ctx)

        val color = Color.BLACK
        val height = 100
        val app1 = makeMockApp(color)
        val cell = builder.buildCell(app1, height)
        val iconView = cell.findViewById<AppIconView>(R.id.icon)

        assertEquals(iconView.backdrop, app1.icon.background)
        assertEquals(iconView.logo, app1.icon.foreground)
        assertEquals(cell.layoutParams.height, height)
    }

    @Test
    fun testRowBuilder_buildRow() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val builder = RowBuilder(ctx)

        val height = 100
        val app1 = makeMockApp()
        val row = builder.buildRow(app1, 100)
        val iconView = row.findViewById<AppIconView>(R.id.icon)

        assertEquals(iconView.backdrop, app1.icon.background)
        assertEquals(iconView.logo, app1.icon.foreground)
        assertEquals(row.layoutParams.height, height)
    }

    @Test
    fun testFetcher_fetch() {
        val ctx = mock(WMActivity::class.java)
        val fetcher = Fetcher(ctx)
        val mockPackageManager = mock(PackageManager::class.java)

        val drawableRed = ColorDrawable(Color.RED)
        val drawableBlue = ColorDrawable(Color.BLUE)
        val labelOne = "Alpha"
        val labelTwo = "Bravo"
        val labelThree = "Chalie"
        val packageOne = "com.example.alpha"
        val packageTwo = "com.example.bravo"
        val packageThree = "com.example.charlie"

        val app1 = mock(ApplicationInfo::class.java)
        app1.packageName = packageOne
        `when`(mockPackageManager.getApplicationIcon(app1)).thenReturn(AdaptiveIconDrawable(drawableRed, ColorDrawable()) as Drawable)
        `when`(app1.loadLabel(mockPackageManager)).thenReturn(labelOne)

        val app2 = mock(ApplicationInfo::class.java)
        app2.packageName = packageTwo
        `when`(mockPackageManager.getApplicationIcon(app2)).thenReturn(AdaptiveIconDrawable(drawableRed, ColorDrawable()) as Drawable)
        `when`(app2.loadLabel(mockPackageManager)).thenReturn(labelTwo)

        val app3 = mock(ApplicationInfo::class.java)
        app3.packageName = packageThree
        `when`(mockPackageManager.getApplicationIcon(app3)).thenReturn(AdaptiveIconDrawable(drawableRed, ColorDrawable()) as Drawable)
        `when`(app3.loadLabel(mockPackageManager)).thenReturn(labelThree)

        // test that they are sorted as well
        val fakeApps = arrayListOf<ApplicationInfo>(
            app1,
            app3,
            app2
        )

        `when`(
            mockPackageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        ).thenReturn(fakeApps)

        `when`(
            mockPackageManager.getLaunchIntentForPackage(packageOne)
        ).thenReturn(Intent())
        `when`(
            mockPackageManager.getLaunchIntentForPackage(packageTwo)
        ).thenReturn(Intent())
        `when`(
            mockPackageManager.getLaunchIntentForPackage(packageThree)
        ).thenReturn(Intent())

        `when`(ctx.getPackageManager()).thenReturn(mockPackageManager)

        var apps = fetcher.fetchFromSystem()
        assertEquals(apps.size, 3)

        var appOne = apps.get(0)
        assertEquals(appOne.name, labelOne)

        var appTwo = apps.get(1)
        Log.i("fcrow", "app two is.................."+appTwo.packageName)
        assertEquals(appTwo.name, labelTwo)

        var appThree = apps.get(2)
        Log.i("fcrow", "app thrre is.................."+appThree.packageName)
        assertEquals(appThree.name, labelThree)

        // limit those with null launch intents
        `when`(
            mockPackageManager.getLaunchIntentForPackage(packageOne)
        ).thenReturn(null)

        apps = fetcher.fetchFromSystem()
        assertEquals(apps.size, 2)

        appTwo = apps.get(0)
        assertEquals(appTwo.name, labelTwo)

        appThree = apps.get(1)
        assertEquals(appThree.name, labelThree)

        assertEquals(fetcher.apps.size, 0)


        var filterString = "Ch"
        apps = fetcher.fetch(filterString)
        assertEquals(apps.size, 1)

        appThree = apps.get(0)
        assertEquals(appThree.name, labelThree)

        assertNotEquals(fetcher.apps.size, apps.size)

        filterString = ""
        apps = fetcher.fetch(filterString)
        assertEquals(apps.size, 2)

        assertEquals(fetcher.apps.size, apps.size)
    }

    @Test
    fun testSearchObj() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val inflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val main = inflater.inflate(R.layout.main, null)
        val slot = main.findViewById<SlotViewGroup>(R.id.tray) as SlotViewGroup
        assert(slot is SlotViewGroup)
    }
}