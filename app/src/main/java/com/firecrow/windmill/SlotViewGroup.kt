package com.firecrow.windmill

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup

class SlotViewGroup(val ctx: Context, val attrs: AttributeSet): ViewGroup(ctx, attrs) {

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SlotViewGroup,
            0, 0).apply {

            try {
                //mShowText = getBoolean(R.styleable.PieChart_showText, false)
                //textPos = getInteger(R.styleable.PieChart_labelPosition, 0)
            } finally {
                recycle()
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        //
    }
}