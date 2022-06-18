package com.firecrow.windmill

import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.ColorDrawable
import android.util.Log
import androidx.palette.graphics.Palette

class Fetcher(val ctx: Context) {
    val cache = hashMapOf<String, AppData>()
    var apps = arrayListOf<AppData>()

    fun fetchFromSystem(): ArrayList<AppData>{
        val pm = ctx.getPackageManager();
        val items: ArrayList<AppData> = arrayListOf<AppData>()
        pm.getInstalledApplications(PackageManager.GET_META_DATA).filter { app ->
            pm.getLaunchIntentForPackage(app.packageName) != null
        }.forEach { app ->
            var icon = pm.getApplicationIcon(app)
            var color = Color.TRANSPARENT

            if(icon is AdaptiveIconDrawable){
                val background = icon.getBackground()
                if(background is ColorDrawable){
                    color = background.getColor()
                }else{
                    val iconb: Bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
                    val canvas: Canvas = Canvas(iconb)
                    background.setBounds(0, 0, 10, 10)
                    background.draw(canvas)
                    val p: Palette = Palette.from(iconb).generate()
                    color = p.getDominantColor(0xffffffff.toInt())
                }
            }else{
                val background = ColorDrawable()
                background.color = Color.TRANSPARENT
                icon = AdaptiveIconDrawable(icon, background)
            }

            items.add(AppData(
                app.loadLabel(pm).toString(),
                app.packageName,
                icon as AdaptiveIconDrawable,
                color,
            ))
        }
        items.sortBy { app -> app.name }
        return items
    }

    fun fetch(query: String): ArrayList<AppData> {
        if(apps.isEmpty()){
            apps = fetchFromSystem()
        }
        return ArrayList(apps.filter { app ->
            app.name.toLowerCase().indexOf(query.toLowerCase()) >= 0
        })
    }
}
