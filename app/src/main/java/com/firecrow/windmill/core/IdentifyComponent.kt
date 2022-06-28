package com.firecrow.windmill

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import com.firecrow.windmill.R

data class NotifyEvent(
    val identifier: String,
    val state: String,
    val valueString: String?,
    val valueInt: Int?,
)

data class ComponentEventRecord(
    val identifier: String,
    var component: IdentifyComponent?,
    var curentValue: NotifyEvent?,
    val listening: MutableList<IdentifyComponent>
)

open class Bus(val busIdentier: String) {
    val subscribers: MutableMap<String, ComponentEventRecord> = mutableMapOf()

    companion object {
        val getBus: (busIdentifier: String) -> Bus? = ::getDefault
        var busMap: MutableMap<String, Bus> = mutableMapOf<String, Bus>("default" to Bus("default"))
        fun getDefault(busIdentifier: String): Bus? {
            return Bus.busMap.get(busIdentifier) ?: Bus.busMap.get("default")
        }
    }

    fun subscribe(component: IdentifyComponent) {
        val record = initSubscriber(component.identifier)
        record.component = component
        setListenOnIdentifier(component.identifier, component)
        for(targetIdentifier in component.listenTo){
            setListenOnIdentifier(targetIdentifier, component)
        }
    }

    fun setListenOnIdentifier(identifier: String, component:IdentifyComponent){
        val target = initSubscriber(identifier)
        target?.listening?.add(component)
    }

    /** Make a record of the component identifier without clobbering it
     * to preserve existing callbacks that may be present
     *
     * this is also used to init records of components that dont' exist
     * yet so that they can be listened to set as listen targets befor ethey
     * are set up
     */
    fun initSubscriber(identifier: String): ComponentEventRecord {
        val existingRecord = this.subscribers.get(identifier)
        existingRecord?.let {
            return it
        } ?: run {
            val record = ComponentEventRecord(
                identifier,
                null,
                null,
                mutableListOf<IdentifyComponent>()
            )
            this.subscribers.put(identifier, record)
            return record
        }
    }

    fun dispatch(event: NotifyEvent) {
        val source = subscribers.get(event.identifier)
        source?.let {
            for (target in it.listening) {
                target.onEventRecieved(event)
            }
        }
        // notify self as well
        source?.component?.onEventRecieved(event)
    }
}

open class IdentifyComponent(val ctx: Context, val attrs: AttributeSet) : LinearLayout(ctx, attrs) {
    var identifier: String = ""
    var listenTo: List<String> = listOf()
    var bus: Bus? = null
    var onEventRecievedCallback: ((component: IdentifyComponent, event: NotifyEvent) -> Unit)? = null

    init {
        val attsArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.IdentifyComponent,
            0, 0
        )
        identifier = attsArray.getString(R.styleable.IdentifyComponent_identifier) ?: ""
        val busIdentifier = attsArray.getString(R.styleable.IdentifyComponent_busIdentifier) ?: ""
        bus = Bus.getBus(busIdentifier)
        val listensToString = attsArray.getString(R.styleable.IdentifyComponent_listenTo) ?: ""
        listensToString?.let {
            listenTo = listensToString.split(',')
        }

        bus?.subscribe(this)
    }

    fun onEventRecieved(event:NotifyEvent){
        onEventRecievedCallback?.invoke(this, event)
    }
    fun setOnEventRecieved(callback: (IdentifyComponent, NotifyEvent) -> Unit){
        onEventRecievedCallback = callback
    }
}