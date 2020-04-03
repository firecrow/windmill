package com.firecrow.windmill

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.palette.graphics.Palette


class Activity : AppCompatActivity() {
    override fun onCreate(instance: Bundle? ) {
        super.onCreate(instance)
        setContentView(R.layout.main)

        val packageManager = getPackageManager();
        val allapps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA).filter{
            app -> packageManager.getLaunchIntentForPackage(app.packageName) != null
        }.toMutableList()
        fun cmp(a: ApplicationInfo): String = a.loadLabel(packageManager).toString()
        allapps.sortBy({cmp(it)});

        val layout  = findViewById(R.id.apps) as ListView
        val allarray = ArrayList(allapps)
        val listadaptor = WMAdapter(this, R.layout.row, allarray)
        layout.setAdapter(listadaptor)
        layout.setDivider(null)
        layout.setOnItemClickListener { parent, view, idx, id ->
            val app: ApplicationInfo = allarray.get(idx)
            packageManager.getLaunchIntentForPackage(app.packageName)?.let { startActivity(it)}
        }
    }
}

fun getRowColor(icon: Drawable): Int {
    val iconb: Bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
    val canvas: Canvas = Canvas(iconb)
    icon.setBounds(0, 0, 10, 10)
    icon.draw(canvas)

    val p: Palette = Palette.from(iconb).generate()
    return p.getVibrantColor(0xff000000.toInt())
}

@RequiresApi(Build.VERSION_CODES.O)
class RowData(app: ApplicationInfo, ctx: Context) {
    val inflater: LayoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val v = inflater.inflate(R.layout.row, null)

    val namev = v.findViewById(R.id.appname) as TextView
    val iconv = v.findViewById(R.id.icon) as ImageView

    val pm = ctx.getPackageManager()
    val icon = app.loadIcon(pm)
    val color = getRowColor(app.loadIcon(pm))

    init {
        val r:Int = color and 0x00ff0000 shr 16
        val g:Int = color and 0x0000ff00 shr 8
        val b:Int = color and 0x000000ff
        val namecolor:Int = if(r+g+b > (255*3)/2) Color.BLACK else Color.WHITE
        namev.setText(app.loadLabel(pm).toString())
        namev.setTextColor(namecolor)
        iconv.setImageDrawable(icon)
        v.setBackgroundColor(color)
    }
}

val cache: HashMap<Int, RowData> = HashMap<Int, RowData>();

fun fromCache(idx:Int, app:ApplicationInfo, ctx:Context): View {
    cache[idx]?.let {
        return it.v
    } ?: run {
        val rd = RowData(app, ctx)
        cache.put(idx, rd)
        return rd.v
    }
}

class WMAdapter(context: Context, resource: Int, val alist: ArrayList<ApplicationInfo>):ArrayAdapter<ApplicationInfo>(context, resource, alist) {
    override fun getCount(): Int { return alist.count() }
    override fun getItem(idx: Int): ApplicationInfo { return alist.get(idx) }
    override fun getItemId(idx: Int): Long { return idx.toLong() }

    override fun getView(idx: Int, view: View?, parent: ViewGroup): View {
        val app = alist.get(idx)
        return fromCache(idx, app, context);
    }
}
