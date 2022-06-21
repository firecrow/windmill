package com.firecrow.windmill

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
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
import kotlin.random.Random

class AppIconView(val ctx: Context, val attrs: AttributeSet) : LinearLayout(ctx, attrs) {
    var iconView: ImageView
    // var root: LinearLayout
    var size: Int = 100
    var mTint: Float = 0f

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
            val iconParams = LinearLayout.LayoutParams(size, size)
            iconView.layoutParams = iconParams

            // create a layout to hold and center the icon
            // add a layout to center the image
            val l = LinearLayout(ctx)
            l.layoutParams = TableLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            l.gravity = Gravity.CENTER

            orientation = LinearLayout.VERTICAL

            l.addView(iconView)
            addView(l)
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

    var tint: Float get() {
        return  mTint
    }
    set(amount:Float){
        val seed = (255 * amount).toInt()
        val bgColor = Color.argb(seed, seed, seed, seed)
        background.setTint(bgColor)
        background.setTintMode(PorterDuff.Mode.DARKEN)
        mTint = tint
    }
}