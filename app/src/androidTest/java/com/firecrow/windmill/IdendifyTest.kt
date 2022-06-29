package com.firecrow.windmill

import com.firecrow.windmill.R
import android.content.Context
import android.view.LayoutInflater
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.firecrow.windmill.core.*
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

        val IDENTIFIER = "test:one"
        val IDENTIFIER_TWO = "test:two"
        val testLayout = inflater.inflate(R.layout.test, null)

        var idtest = testLayout.identify_test as IdentifyComponent
        assertEquals(idtest.identifier,IDENTIFIER)
        assert(idtest.listenTo.size == 3)
        val bus = idtest.bus
        assertEquals(bus, NotifyBus.getBus("default"))

        val record = bus?.subscribers?.get(idtest.identifier)
        assertEquals(record?.identifier, IDENTIFIER)
        assertEquals(record?.component, idtest)

        //  ? how many does i have
        //assert(bus?.subscribers?.size == 5)
        for(target in idtest.listenTo){
           assertNotNull(bus?.subscribers?.get(target))
        }

        var currentEvent:NotifyEvent? = null
        var currentSource:IdentifyComponent? = null

        idtest.onEventRecievedCallback = { source, event ->
            currentSource = source
            currentEvent = event
        }

        var event = NotifyEvent(IDENTIFIER_TWO, "ON", null, null)
        bus?.dispatch(event)
        assertEquals(currentEvent, event)

        event = NotifyEvent(IDENTIFIER, "ON2", null, null)
        bus?.dispatch(event)
        assertEquals(currentEvent, event)

        val idthree = testLayout.identify_test_three
        var eventFromThree: NotifyEvent? = null
        idthree.onEventRecievedCallback = { _, event ->
            eventFromThree = event
        }
        val IDENTIFIER_THREE = "test:three"
        event = NotifyEvent(IDENTIFIER_THREE, "ON3", null, null)
        bus?.dispatch(event)
        assertEquals(event, currentEvent)
        assertEquals(event, eventFromThree)
    }
}
