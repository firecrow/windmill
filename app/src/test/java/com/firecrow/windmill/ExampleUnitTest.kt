package com.firecrow.windmill

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.FragmentContainerView
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

lateinit var searchBar: SearchObj
var input:EditText? = null

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Before
    fun testSetup() {
        val context = mock(Context::class.java)
        val activity = mock(WMActivity::class.java)
        val layout =  FragmentContainerView(context)

        input = EditText(context)
        val searchLayout = mock(LinearLayout::class.java)
        Mockito.`when`(searchLayout.findViewById<EditText>(R.id.search)).thenReturn(input)
        Mockito.`when`(searchLayout.findViewById<ImageView>(R.id.search_button)).thenReturn(ImageView(context))
        Mockito.`when`(searchLayout.findViewById<ImageView>(R.id.grid_nav_button)).thenReturn(ImageView(context))
        Mockito.`when`(searchLayout.findViewById<ImageView>(R.id.list_nav_button)).thenReturn(ImageView(context))

        searchBar = SearchObj(searchLayout, activity)
    }
}
