package com.firecrow.windmill

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.blue
import androidx.palette.graphics.Palette
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.absoluteValue
import kotlin.collections.List

var dbh:DBHelper? = null

enum class LayoutType {
    APP,
    SEARCH
}

class AppData (
    val appInfo: ApplicationInfo,
    var name:String,
    var color:Int,
    var order: Int,
    var is_pinned:Boolean,
    var v:View?,
    val type: LayoutType);

fun fetchAppDataArray(ctx:Context):ArrayList<AppData> {
    val orderData = fetchOrder(ctx)
    val pm = ctx.getPackageManager();
    val searchApp = AppData(ApplicationInfo(), "search", 0x00000000,0, true, null, LayoutType.SEARCH)
    val items:ArrayList<AppData> = arrayListOf<AppData>()
    val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA).filter { app ->
        pm.getLaunchIntentForPackage(app.packageName) != null
    }.toMutableList<ApplicationInfo>().map {
        val name = it.loadLabel(pm).toString()
        val order = orderData[name]?.let {it} ?: run {0}
        AppData( it, name, 0x00000000, order, order != 0, null, LayoutType.APP)
    }.sortedWith(compareBy<AppData>({ !it.is_pinned }).thenBy({ it.name }))
    items.add(searchApp)
    items.addAll(apps)
    return items
}

fun fetchOrder(ctx:Context): HashMap<String, Int>{
    val data = hashMapOf<String, Int>()
    val db = getDb(ctx)
    val c:Cursor? = db.rawQuery("select id, name, pin_order from windmill", null)
    c?.let {
        if(c.moveToFirst()){
            do {
                val name=c.getString(c.getColumnIndex("name"))
                val pin_order=c.getInt(c.getColumnIndex("pin_order"))
                data.put(name, pin_order)
            } while(c.moveToNext())
        }
    }
    return data
}

fun setOrder(ctx:Context, name:String, pin_order:Int) {
    val db = getDb(ctx)
    val vals = ContentValues()
    vals.put("pin_order", pin_order)
    vals.put("name", name)

    if(db.insert( "windmill", null, vals) == -1L)
        db.rawQuery("update windmill set pin_order = ? where name = ?", arrayOf<String>(pin_order.toString(), name))
}

fun clearOrder(ctx:Context, name:String) {
    val db = getDb(ctx)
    db.delete("windmill","name = ?", arrayOf<String>(name))
}

fun getDb(ctx:Context):SQLiteDatabase {
    return dbh?.let{
        it.getWritableDatabase()
    } ?: run {
        dbh = DBHelper(ctx)
        return DBHelper(ctx).getWritableDatabase()
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

class Activity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(instance: Bundle? ) {
        super.onCreate(instance)
        setContentView(R.layout.main)

        val layout = findViewById<ListView>(R.id.apps) as ListView
        layout.setDivider(null)
        val apps = fetchAppDataArray(this)
        val listadapter = WMAdapter(this, layout, R.layout.row, apps)
        layout.setAdapter(listadapter)
        layout.setOnItemClickListener { parent, view, idx, id ->
            val app: AppData = listadapter.getItem(idx)
            getPackageManager().getLaunchIntentForPackage(app.appInfo.packageName)?.let { startActivity(it) }
        }
    }

    override fun onResume(){
        val layout = findViewById<ListView>(R.id.apps) as ListView
        layout.setSelection(0)
        super.onResume()
    }

    override fun onPause(){
        /* */
        super.onPause()
    }
}

class WMAdapter(val ctx: Context, val layout:ListView, resource: Int, var apps: ArrayList<AppData>):
        ArrayAdapter<AppData>(ctx, resource, apps) {
    override fun getCount(): Int { return apps.count() }
    override fun getItem(idx: Int): AppData { return apps.get(idx) }
    override fun getItemId(idx: Int): Long { return idx.toLong() }
    fun update(apps: ArrayList<AppData>) {
        this.apps = apps
        this.clear()
        this.addAll(apps)
        this.notifyDataSetChanged()
    }

    override fun getView(idx: Int, view: View?, parent: ViewGroup): View {
        val priorColor:Int = if(idx > 0) getItem(idx-1).color else Color.parseColor("#000000")
        val item = getItem(idx)
        if(item.type == LayoutType.SEARCH) {
            return buildSearchRow(ctx, idx, item)
        }
        return buildRow(ctx, idx, item, priorColor)
    }

    fun buildSearchRow(ctx:Context, idx:Int, app: AppData): View {
        val inflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (app.v == null) {
            app.v = inflater.inflate(R.layout.row_search, null)
            app.v?.let {
                val inputv = it.findViewById(R.id.search) as EditText
                return it
            }
        }
        return  inflater.inflate(R.layout.row_search, null)
    }

    fun buildRow(ctx:Context, idx:Int, app: AppData, priorColor: Int): View {
        val inflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if(app.v == null) {
            app.v = inflater.inflate(R.layout.row, null)
            app.v?.let {
                val namev = it.findViewById(R.id.appname) as TextView
                val iconv = it.findViewById(R.id.icon) as ImageView

                val pm = ctx.getPackageManager()
                val icon = app.appInfo.loadIcon(pm)
                app.color = getRowColor(app.appInfo.loadIcon(pm))

                namev.setText(app.name)
                namev.setTextColor(Color.WHITE)
                iconv.setImageDrawable(icon)
                it.setBackgroundColor(app.color)
            }
        }
        app.v?.let {
            val pin_button = it.findViewById(R.id.pin_button) as ImageView
            val pin_image = if (app.is_pinned) R.drawable.pinned_white else R.drawable.not_pinned_white
            pin_button.setImageResource(pin_image)
            pin_button.setOnClickListener { v ->
                if (!app.is_pinned) {
                    setOrder(ctx, app.name, 1)
                } else {
                    clearOrder(ctx, app.name)
                }
                this.update(fetchAppDataArray(ctx))
            }

            if(idx > 0) {

                val r = Color.red(app.color)
                val g = Color.green(app.color)
                val b = Color.blue(app.color)

                val er: Int = r - Color.red(priorColor)
                val eg: Int = g - Color.green(priorColor)
                val eb: Int = b - Color.blue(priorColor)

                Log.d("fcrow", "${app.name} rbg<$r, $g, $b> deltas<$er,$eg,$eb>...................")

                val delta = er.absoluteValue + eg.absoluteValue + eb.absoluteValue
                Log.d("fcrow", "delta:$delta.......")
                if (delta < 100) {
                    Log.d("fcrow", "delta:$delta below 100================.........")
                    if (r + g + b > 220 * 3) {
                        Log.d("fcrow", "$delta damn white.......")
                        app.color = Color.rgb(200, 200, 200)
                    } else if (r + g + b < 50) {
                        app.color = Color.rgb(80, 80, 80)
                        Log.d("fcrow", "$delta damn black.......")
                    } else {
                        Log.d("fcrow", "$delta in main.......")
                        fun getAtleast(d:Int, x:Int):Int {
                            var sign = kotlin.math.sign(d.toDouble())
                            if(sign == 0.0) sign = 1.0;
                            val value = (maxOf(d.absoluteValue*2, 80) * sign).toInt()
                            Log.d("fcrow", "sign: $sign value:$value")
                            val withOrig = value+x
                            val withinBounds = maxOf(minOf(withOrig, 255), 0)
                            return return withinBounds
                        }
                        val nr = getAtleast(er, r)
                        val ng = getAtleast(eg, g)
                        val nb = getAtleast(eb, b)
                        Log.d("fcrow", "${app.name} final color<$nr,$ng,$nb>...................")
                        app.color = Color.rgb(nr, ng, nb)
                    }
                    it.setBackgroundColor(app.color)
                }
                Log.d("fcrow", "............................................................................................................")
            }
            return it
        } ?: run {
            return inflater.inflate(R.layout.row, null)
        }
    }
}

class DBHelper(ctx:Context) : SQLiteOpenHelper(ctx, "windmill.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE windmill (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, pin_order INTEGER)");
    }

    override fun onUpgrade(db:SQLiteDatabase, old:Int, version:Int) {}

    override fun onDowngrade(db:SQLiteDatabase, oldVersion:Int, newVersion:Int) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
