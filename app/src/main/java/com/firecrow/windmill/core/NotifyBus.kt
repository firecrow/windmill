package com.firecrow.windmill

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
    val listening: MutableList<NotifyCallable>
)

interface NotifyCallable {
    fun onEventReceived(event:NotifyEvent)
}

open class NotifyBus(val busIdentier: String) {
    val subscribers: MutableMap<String, ComponentEventRecord> = mutableMapOf()

    companion object {
        val getBus: (busIdentifier: String) -> NotifyBus? = ::getDefault
        var busMap: MutableMap<String, NotifyBus> = mutableMapOf<String, NotifyBus>("default" to NotifyBus("default"))
        fun getDefault(busIdentifier: String): NotifyBus? {
            return NotifyBus.busMap.get(busIdentifier) ?: NotifyBus.busMap.get("default")
        }
    }

    fun subscribe(component: NotifyCallable, subscribeTo:List<String>) {
        for(targetIdentifier in subscribeTo){
            setListenOnIdentifier(targetIdentifier, component)
        }
    }

    fun setListenOnIdentifier(identifier: String, component: NotifyCallable){
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
                mutableListOf<NotifyCallable>()
            )
            this.subscribers.put(identifier, record)
            return record
        }
    }

    fun dispatch(event: NotifyEvent) {
        val source = subscribers.get(event.state)
        source?.let {
            for (target in it.listening) {
                target.onEventReceived(event)
            }
        }
        // notify self as well
        source?.component?.onEventReceived(event)
    }
}

fun interface NotifyEventCallback: NotifyCallable {
    override fun onEventReceived(event: NotifyEvent)
}
