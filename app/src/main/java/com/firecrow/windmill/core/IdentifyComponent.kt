package com.firecrow.windmill

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.firecrow.windmill.R

open class IdentifyComponent(val ctx: Context, attrs: AttributeSet) : LinearLayout(ctx, attrs) {
    var identifier: String = ""
    var listenTo: List<String> = listOf()
    var bus: NotifyBus? = null
    var onEventRecievedCallback: ((component: IdentifyComponent, event: NotifyEvent) -> Unit)? = null
    var targetValue: String = ""
    var targetState: String = ""
    var providesState: String = ""

    init {
        val attsArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.IdentifyComponent,
            0, 0
        )
        identifier = attsArray.getString(R.styleable.IdentifyComponent_identifier) ?: ""

        val busIdentifier = attsArray.getString(R.styleable.IdentifyComponent_busIdentifier) ?: ""
        bus = NotifyBus.getBus(busIdentifier)

        val listensToString = attsArray.getString(R.styleable.IdentifyComponent_listenTo) ?: ""
        listensToString?.let {
            listenTo = listensToString.split(',')
        }

        targetValue = attsArray.getString(R.styleable.IdentifyComponent_targetValue) ?: ""
        targetState = attsArray.getString(R.styleable.IdentifyComponent_targetState) ?: ""
        providesState = attsArray.getString(R.styleable.IdentifyComponent_providesState) ?: ""

        bus?.subscribe(this, this.listenTo)
    }

    fun onEventRecieved(event:NotifyEvent){
        onEventRecievedCallback?.invoke(this, event)
    }
    fun setOnEventRecieved(callback: (IdentifyComponent, NotifyEvent) -> Unit){
        onEventRecievedCallback = callback
    }
}

class ClickableIdentifyComponent(ctx: Context, attrs: AttributeSet): IdentifyComponent(ctx, attrs){

    init {
        this.targetValue?.let {
            this.setOnClickListener(View.OnClickListener {
                this.bus?.dispatch(
                    NotifyEvent(
                        this.identifier,
                        this.providesState,
                        this.targetValue,
                        null
                    )
                )
            })
        }
    }
}