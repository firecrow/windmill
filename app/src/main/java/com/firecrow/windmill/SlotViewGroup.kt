package com.firecrow.windmill

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout

class SlotViewGroup(val ctx: Context, val attrs: AttributeSet): LinearLayout(ctx, attrs) {
    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SlotViewGroup,
            0, 0)
    }
}