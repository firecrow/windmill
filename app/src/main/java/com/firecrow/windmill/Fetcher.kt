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
    val cache = hashMapOf<String, AppData>()
    var apps = arrayListOf<AppData>()

    private var mDefaultBackground:Drawable = ColorDrawable(Color.WHITE)
    var defaultBackground: Drawable get(){
        return mDefaultBackground
    }
    set(drawable: Drawable){
        mDefaultBackground = drawable
    }

    fun fetchFromSystem(): ArrayList<AppData>{
        val pm = ctx.getPackageManager();
        val items: ArrayList<AppData> = arrayListOf<AppData>()
        pm.getInstalledApplications(PackageManager.GET_META_DATA).filter { app ->
            pm.getLaunchIntentForPackage(app.packageName) != null
        }.forEach { app ->
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
