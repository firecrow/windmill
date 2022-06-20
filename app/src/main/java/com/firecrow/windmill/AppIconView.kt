package com.firecrow.windmill

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
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
import android.widget.TableLayout
import androidx.core.content.res.ResourcesCompat
import kotlinx.android.synthetic.main.cell.view.*

class AppIconView(val ctx: Context, val attrs: AttributeSet) : LinearLayout(ctx, attrs) {
    var iconView: ImageView
    // var root: LinearLayout
    var size: Int = 100

    init {
        val attsArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.AppIconView,
            0, 0
        )
        try {

            size = attsArray.getDimension(R.styleable.AppIconView_logoSize, 100.0f).toInt()

            // create the image
            iconView = ImageView(ctx)
            //val iconParams = LinearLayout.LayoutParams(size, size)
            val iconParams = TableLayout.LayoutParams(size, size, 1f)
            iconParams.gravity = Gravity.CENTER
            iconView.layoutParams = iconParams

            // create a layout to hold and center the icon
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
            orientation = LinearLayout.VERTICAL
            gravity=Gravity.CENTER

            addView(iconView)
        } finally {
            attsArray.recycle()
        }
    }

    fun setIcon(icon: AdaptiveIconDrawable){
        iconView.setImageDrawable(icon.foreground)
        background = icon.background

        invalidate()
        requestLayout()
    }

    val logo: Drawable get() {
        return iconView.drawable as Drawable
    }

    val backdrop: Drawable
        get() {
            return background
        }
}