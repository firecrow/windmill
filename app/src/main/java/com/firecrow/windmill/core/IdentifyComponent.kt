package com.firecrow.windmill

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.firecrow.windmill.R

data class NotifyEvent(
    val identifier: String,
    val state: String,
    val valueString: String,
    val valueInt: Int,
)

data class CoponentEventRecord(
    val identifier: String,
    val component: IdentifyComponent,
    val value: NotifyEvent,
    val listeningCallbacks: MutableList<(IdentifyComponent, NotifyEvent) -> Unit>
)

open class Bus(val busIdentier: String){
    var subscribers: MutableMap<String, IdentifyComponent> = mutableMapOf()
    companion object {
        val getBus: (busIdentifier:String) -> Bus? = ::getDefault
        var busMap: MutableMap<String, Bus> = mutableMapOf<String, Bus>("default" to Bus("default"))
        fun getDefault(busIdentifier: String): Bus? {
            return Bus.busMap.get(busIdentifier) ?: Bus.busMap.get("default")
        }
    }
    fun subscribe(component: IdentifyComponent){
        this.subscribers.put(component.identifier, component)
    }
    fun dispatch(){

    }
}

open class IdentifyComponent(val ctx: Context, val attrs:AttributeSet): ViewGroup(ctx, attrs) {
    var identifier: String = ""
    var listenTo: List<String> = listOf()
    var bus: Bus? = null
    init {
        val attsArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.IdentifyComponent,
            0, 0
        )
        identifier = attsArray.getString(R.styleable.IdentifyComponent_identifier) ?: ""
        val busIdentifier = attsArray.getString(R.styleable.IdentifyComponent_busIdentifier) ?: ""
        bus = Bus.getBus(busIdentifier)
        bus?.subscribe(this)
        val listensToString = attsArray.getString(R.styleable.IdentifyComponent_listenTo) ?: ""
        listensToString?.let {
            listenTo = listensToString.split(',')
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        TODO("Not yet implemented")
    }
}