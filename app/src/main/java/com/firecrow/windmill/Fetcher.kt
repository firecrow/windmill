package com.firecrow.windmill

import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.palette.graphics.Palette

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
        var defaultBackground = ColorDrawable(Color.RED)

        if(icon !is AdaptiveIconDrawable){
            Log.i("fcrow", "NOT ADAPTIVE:"+icon.toString())
            Log.i("fcrow", "NOT ADAPTIVE BOUNDS:"+icon.getBounds().toString())
            return AdaptiveIconDrawable(defaultBackground, icon)
        }
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
