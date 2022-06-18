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

class AppIconView(val ctx: Context, val attrs: AttributeSet): ViewGroup(ctx, attrs) {
    lateinit var iconView: ImageView
    lateinit var root: LinearLayout

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SlotViewGroup,
            0, 0).apply {

            try {
                iconView = ImageView(ctx)
                iconView.foregroundGravity = Gravity.CENTER

                // get drawable for icon from configuration if it exists
                val drawableResource = getInt(R.styleable.AppIconView_iconDrawableResource, -1)
                if(drawableResource != -1) {
                    iconView.setImageResource(drawableResource)
                }

                // create a layout to hold and center the icon
                root = LinearLayout(ctx)
                root.orientation = LinearLayout.VERTICAL

                // set the background from the configuration if provided
                val backgroundColorAtt = getInt(R.styleable.AppIconView_backdropColor, -1)
                if(backgroundColorAtt != -1) {
                    root.setBackgroundColor(backgroundColorAtt)
                }

                val backgroundDrawableResource = getInt(R.styleable.AppIconView_backdropColor, -1)
                if(backgroundDrawableResource != -1) {
                    val backgroundDrawable = ResourcesCompat.getDrawable(ctx.resources, backgroundDrawableResource, null)
                    root.background = backgroundDrawable
                }

                // assign the views
                root.addView(iconView)
                addView(root)
            } finally {
                recycle()
            }
        }
    }

    var icon: AdaptiveIconDrawable get() {
        return iconView.drawable as AdaptiveIconDrawable
    }
    set(icon){
        iconView.setImageDrawable(icon)

        val background = icon.getBackground()
        val color = getDrawableColor(background)
        setBackdrop(color)

        invalidate()
        requestLayout()
    }

    val backdropColor: Int get() {
        val background = root.background
        return getDrawableColor(background)
    }

    private fun getDrawableColor(drawable: Drawable?): Int{
        if( drawable is ColorDrawable) {
            return (drawable as ColorDrawable).getColor()
        }
        return Color.TRANSPARENT
    }

    private fun setBackdrop(color:Int){
       root.background = ColorDrawable(color)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        root.layout(0,0,root.getMeasuredWidth(), root.getMeasuredHeight());
    }
}