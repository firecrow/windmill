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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.palette.graphics.Palette
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

var listadapter:WMAdapter? = null;
var dbh:DBHelper? = null

class AppData (val appInfo: ApplicationInfo, var name:String, var color:Int, var order: Int, var is_pinned:Boolean, var v:View?);

fun fetchAppDataArray(ctx:Context):ArrayList<AppData> {
    val orderData = hashMapOf<String, Int>()
    fetchOrder(ctx, orderData)
    val pm = ctx.getPackageManager();
    val apps =  pm.getInstalledApplications(PackageManager.GET_META_DATA).filter { app ->
        pm.getLaunchIntentForPackage(app.packageName) != null
    }.toMutableList().map {
        val name = it.loadLabel(pm).toString()
        val order = orderData[name]?.let {it} ?: run {0}
        AppData( it, name, 0x00000000, order, order != 0, null )
    }
    return ArrayList<AppData>(apps.sortedWith(compareBy<AppData>({ !it.is_pinned }).thenBy({ it.name })))
}

fun fetchOrder(ctx:Context, orderData:HashMap<String, Int>){
    val db = getDb(ctx)
    val c:Cursor? = db.rawQuery("select id, name, pin_order from windmill", null)
    c?.let {
        if(c.moveToFirst()){
            do {
                val name=c.getString(c.getColumnIndex("name"))
                val pin_order=c.getInt(c.getColumnIndex("pin_order"))
                orderData.put(name, pin_order)
            } while(c.moveToNext())
        }
    }
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

fun buildRow(ctx:Context, app: AppData): View {
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

            val r: Int = app.color and 0x00ff0000 shr 16
            val g: Int = app.color and 0x0000ff00 shr 8
            val b: Int = app.color and 0x000000ff

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
            listadapter?.let {it.update(fetchAppDataArray(ctx))}
        }
        return it
    } ?: run {
        return inflater.inflate(R.layout.row, null)
    }
}

class Activity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(instance: Bundle? ) {
        super.onCreate(instance)
        setContentView(R.layout.main)

        val layout = findViewById<ListView>(R.id.apps) as ListView
        val apps = fetchAppDataArray(this)
        listadapter = WMAdapter(this, R.layout.row, apps)
        listadapter?.let {
            it.notifyDataSetChanged()
            val li = it
            layout.setAdapter(listadapter)
            layout.setOnItemClickListener { parent, view, idx, id ->
                val app: AppData = li.getItem(idx)
                Log.d("fcrow", "....................................${app.appInfo.packageName}................................................................")
                getPackageManager().getLaunchIntentForPackage(app.appInfo.packageName)?.let { startActivity(it) }
            }
        }
    }
}

class WMAdapter(val ctx: Context, resource: Int, var apps: ArrayList<AppData>):
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
        return buildRow(ctx, getItem(idx))
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
