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

    private var mDefaultBackground:Drawable = ColorDrawable(Color.WHITE)
    var defaultBackground: Drawable get(){
        return mDefaultBackground
    }
    set(drawable: Drawable){
        mDefaultBackground = drawable
    }

    fun fetchFromSystem(): ArrayList<AppData>{
        val pm = ctx.getPackageManager()
        val items: ArrayList<AppData> = arrayListOf<AppData>()
        Log.i("fcrow", "querying")
        pm.getInstalledApplications(PackageManager.GET_META_DATA).filter { app ->
            Log.i("fcrow", "package returned: -> "+app.toString())
            Log.i("fcrow", "package returned: -> "+app.packageName)
            Log.i("fcrow", "package intent returned: -> "+pm.getLaunchIntentForPackage(app.packageName).toString())
            pm.getLaunchIntentForPackage(app.packageName) != null
        }.forEach { app ->
            Log.i("fcrow", "in loop app: " + app.toString())
            var icon = asAdaptive(pm.getApplicationIcon(app))
            Log.i("fcrow", "icon retrieved: " + icon.toString())

            items.add(AppData(
                app.loadLabel(pm).toString(),
                app.packageName,
                icon,
            ))
        }
        Log.i("fcrow", "about to sort")
        items.sortBy { app -> app.name }
        return items
    }

    fun asAdaptive(icon: Drawable): AdaptiveIconDrawable{
        Log.i("fcrow", "asAdaptive called")
        if(icon !is AdaptiveIconDrawable){
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
