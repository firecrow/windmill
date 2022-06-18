package com.firecrow.windmill

import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Canvas
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
            val icon = pm.getApplicationIcon(app)
            var color = 0x000000
            try {
                var background = icon
                if(background is AdaptiveIconDrawable){
                    background = background.getBackground()
                }
                if(background is ColorDrawable){
                    color = background.getColor()
                    Log.i("fcrow", "get color: ${app.packageName}")
                }else{
                    val iconb: Bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
                    val canvas: Canvas = Canvas(iconb)
                    background.setBounds(0, 0, 10, 10)
                    background.draw(canvas)
                    val p: Palette = Palette.from(iconb).generate()
                    color = p.getDominantColor(0xffffffff.toInt())
                    Log.i("fcrow", "get nuclear: ${app.packageName}")
                }
                Log.i("fcrow", "background ended as drawableis: ${background.toString()}")
            }catch (e: ClassCastException){
                Log.e("fcrow", "unable to parse icon: ${icon.toString()} for package: ${app.packageName}, ${e.toString()}")
            }
            Log.i("fcrow", "color ${color.toString()}")
            items.add(AppData(
                app.loadLabel(pm).toString(),
                app.packageName,
                icon,
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
