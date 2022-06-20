package com.firecrow.windmill

import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Color.*
import android.graphics.PorterDuff
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.palette.graphics.Palette
import kotlin.random.Random

class Fetcher(val ctx: Context) {
    var apps = arrayListOf<AppData>()

    fun fetchFromSystem(): ArrayList<AppData>{
        val pm = ctx.getPackageManager()
        val items: ArrayList<AppData> = arrayListOf<AppData>()
        pm.getInstalledApplications(PackageManager.GET_META_DATA).filter { app ->
            pm.getLaunchIntentForPackage(app.packageName) != null
        }.forEach { app ->
            Log.i("fcrow", "package name ----------- "+app.packageName)
            var icon = asAdaptive(pm.getApplicationIcon(app))

            items.add(AppData(
                app.loadLabel(pm).toString(),
                app.packageName,
                icon,
            ))
        }
        items.sortBy { app -> app.name }
        return items
    }

    fun asAdaptive(icon: Drawable): AdaptiveIconDrawable{
        val seed = (20 * Random.nextFloat()).toInt()
        val bgColor = argb(seed, seed, seed, seed)

        var defaultBackground = ColorDrawable(Color.WHITE)

        if(icon !is AdaptiveIconDrawable){
            return AdaptiveIconDrawable(defaultBackground, icon)
        }
        icon.background.setTint(bgColor)
        icon.background.setTintMode(PorterDuff.Mode.DARKEN)
        return icon
    }

    fun fetch(query: String): ArrayList<AppData> {
        if(apps.isEmpty()){
            apps = fetchFromSystem()
        }
        return ArrayList(apps.filter { app ->
            app.name.lowercase().indexOf(query.lowercase()) >= 0
        })
    }
}
