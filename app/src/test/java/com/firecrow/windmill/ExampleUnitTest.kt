package com.firecrow.windmill

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.FragmentContainerView
import org.junit.Test

import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testSetup() {

        val context = mock(Context::class.java)

        val activity = mock(WMActivity::class.java)

        val layout =  FragmentContainerView(context)

        val searchBar = mock(LinearLayout::class.java)
        Mockito.`when`(searchBar.findViewById<EditText>(R.id.search)).thenReturn(EditText(context))
        Mockito.`when`(searchBar.findViewById<ImageView>(R.id.search_button)).thenReturn(ImageView(context))
        Mockito.`when`(searchBar.findViewById<ImageView>(R.id.grid_nav_button)).thenReturn(ImageView(context))
        Mockito.`when`(searchBar.findViewById<ImageView>(R.id.list_nav_button)).thenReturn(ImageView(context))

        val controller = WMController(activity, layout, searchBar)
    }
}
