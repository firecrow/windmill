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
import com.firecrow.windmill.model.AppData
import com.firecrow.windmill.model.DBHelper
import com.firecrow.windmill.model.MetaData
import java.lang.reflect.GenericDeclaration
import java.lang.reflect.TypeVariable
import kotlin.random.Random
import kotlin.collections.filter

fun filterByQuery (query: String): (app:AppData) -> Boolean = {
    app:AppData ->
        app.name.lowercase().indexOf(query.lowercase()) >= 0
}

fun filterByStarred (): (app:AppData) -> Boolean = {
    app:AppData -> app.meta.starred != true
}

class Fetcher(val ctx: Context, dbh: DBHelper) {
    var apps:List<AppData> = listOf<AppData>()

    fun fetchFromSystem(): ArrayList<AppData>{
        val pm = ctx.getPackageManager()
        val items: ArrayList<AppData> = arrayListOf<AppData>()
        pm.getInstalledApplications(PackageManager.GET_META_DATA).filter { app ->
            pm.getLaunchIntentForPackage(app.packageName) != null
        }.forEach { app ->
            Log.i("fcrow", "package name ----------- "+app.packageName)
            var icon = asAdaptive(pm.getApplicationIcon(app))

            items.add(
                AppData(
                    app.loadLabel(pm).toString(),
                    app.packageName,
                    icon,
                    MetaData(true)
            ))
        }
        items.sortBy{ app -> app.name }
        return items
    }

    fun asAdaptive(icon: Drawable): AdaptiveIconDrawable{
        var defaultBackground = ColorDrawable(Color.WHITE)

        if(icon !is AdaptiveIconDrawable){
            return AdaptiveIconDrawable(defaultBackground, icon)
        }
        return icon
    }

    fun fetch(filters: List<(app: AppData) -> Boolean>): List<AppData> {
        if(apps.isEmpty()){
            apps = fetchFromSystem()
        }
        var filteredApps = apps;
        for(f in filters){
            filteredApps = apps.filter(f)
        }
        return filteredApps
    }
}