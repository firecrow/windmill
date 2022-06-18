package com.firecrow.windmill

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.icu.lang.UCharacter
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.cell.view.*

class AppIconView(val ctx: Context, val attrs: AttributeSet) : ViewGroup(ctx, attrs) {
    var iconView: ImageView
    var root: LinearLayout

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SlotViewGroup,
            0, 0
        )
            .apply {

                try {
                    // create the image
                    iconView = ImageView(ctx)
                    iconView.foregroundGravity = Gravity.CENTER

                    // create a layout to hold and center the icon
                    root = LinearLayout(ctx)
                    root.orientation = LinearLayout.VERTICAL

                    // assign the views
                    root.addView(iconView)
                    addView(root)
                } finally {
                    recycle()
                }
            }
    }

    fun setIcon(icon: AdaptiveIconDrawable){
        iconView.setImageDrawable(icon.foreground)
        root.background = icon.background

        invalidate()
        requestLayout()
    }

    val logo: Drawable get() {
        return iconView.drawable as Drawable
    }

    val backdrop: Drawable
        get() {
            return root.background
        }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        root.layout(0, 0, root.getMeasuredWidth(), root.getMeasuredHeight());
    }
}