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

class AppIconView(val ctx: Context, val attrs: AttributeSet) : LinearLayout(ctx, attrs) {
    var iconView: ImageView
    var root: LinearLayout

    init {
        val attsArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.AppIconView,
            0, 0
        )
        try {

            val size = attsArray.getDimension(R.styleable.AppIconView_logoSize, 100.0f).toInt()

            // create the image
            iconView = ImageView(ctx)
            val iconParams = LinearLayout.LayoutParams(size, size)
            iconParams.gravity = Gravity.CENTER
            iconView.layoutParams = iconParams

            // create a layout to hold and center the icon
            root = LinearLayout(ctx)
            root.layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            root.orientation = LinearLayout.VERTICAL

            // assign the views
            root.addView(iconView)
            addView(root)
        } finally {
            attsArray.recycle()
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