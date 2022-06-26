package com.firecrow.windmill.core

import com.firecrow.windmill.R
import android.content.Context
import android.view.LayoutInflater
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.firecrow.windmill.Bus
import com.firecrow.windmill.IdentifyComponent
import kotlinx.android.synthetic.main.main.view.*
import kotlinx.android.synthetic.main.tray.view.*
import kotlinx.android.synthetic.main.test.view.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class IdendifyTest {

    @Test
    fun testTray() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val testLayout = inflater.inflate(R.layout.test, null)
        var idtest = testLayout.identify_test as IdentifyComponent
        assertEquals(idtest.identifier,"test:one")
        assert(idtest.listenTo.size == 3)
        assertEquals(idtest.bus, Bus.getBus("default"))
    }
}
