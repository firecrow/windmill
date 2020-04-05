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

class Global(val listadapter:WMAdapter, val allarray:ArrayList<ApplicationInfo>, val layout:ListView)
var dbh: DBHelper? = null;
var global:Global? = null
var orderData:HashMap<String, Int> = hashMapOf<String, Int>()

class Activity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(instance: Bundle? ) {
        super.onCreate(instance)
        setContentView(R.layout.main)

        val packageManager = getPackageManager();
        val allapps =
            packageManager.getInstalledApplications(PackageManager.GET_META_DATA).filter { app ->
                packageManager.getLaunchIntentForPackage(app.packageName) != null
            }.toMutableList()

        fun cmp(a: ApplicationInfo): String = a.loadLabel(packageManager).toString()
        allapps.sortBy({ cmp(it) });

        val layout = findViewById(R.id.apps) as ListView
        val allarray = ArrayList(allapps)
        val listadapter = WMAdapter(this, R.layout.row, allarray)
        global = Global(listadapter, allarray, layout)
        fetchOrder(this)
        layout.setAdapter(listadapter)
        layout.setDivider(null)
        layout.setOnItemClickListener { parent, view, idx, id ->
            val app: ApplicationInfo = allarray.get(idx)
            packageManager.getLaunchIntentForPackage(app.packageName)
                ?.let { startActivity(it) }
        }

        if(this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            this.requestPermissions(
                arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                0)
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

class WMAdapter(val ctx: Context, resource: Int, val alist: ArrayList<ApplicationInfo>):
        ArrayAdapter<ApplicationInfo>(ctx, resource, alist) {
    override fun getCount(): Int { return alist.count() }
    override fun getItem(idx: Int): ApplicationInfo { return alist.get(idx) }
    override fun getItemId(idx: Int): Long { return idx.toLong() }

    override fun getView(idx: Int, view: View?, parent: ViewGroup): View {
        val app = alist.get(idx)

        val inflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        //val v = view?.let{ it } ?: run { inflater.inflate(R.layout.row, null)}
        val v =  inflater.inflate(R.layout.row, null)

        val namev = v.findViewById(R.id.appname) as TextView
        val iconv = v.findViewById(R.id.icon) as ImageView

        val pm = ctx.getPackageManager()
        val icon = app.loadIcon(pm)
        val color = getRowColor(app.loadIcon(pm))

        val r: Int = color and 0x00ff0000 shr 16
        val g: Int = color and 0x0000ff00 shr 8
        val b: Int = color and 0x000000ff
        val isDark = r+g+b > (255 * 3) / 2;
        val namecolor = if (isDark) Color.BLACK else Color.WHITE
        val name = app.loadLabel(pm).toString()

        val pin_button = v.findViewById(R.id.pin_button) as ImageView
        val is_pinned: Boolean = orderData[name]?.let { it > 0 } ?: run { false }
        var pin_image = R.drawable.not_pinned_white
        if (is_pinned) {
            if (isDark) {
                Log.d("fcrow", "$name dark pinned")
                pin_image = R.drawable.pinned_black
            } else {
                Log.d("fcrow","$name light pinned")
                pin_image = R.drawable.pinned_white
            }
        } else {
            if (isDark) {
                Log.d("fcrow","$name dark not pinned")
                pin_image = R.drawable.not_pinned_black
            } else {
                Log.d("fcrow","$name light not pinned")
                pin_image = R.drawable.not_pinned_white
            }
        }
        pin_button.setImageResource(pin_image)
        Log.d("fcrow", "setting... pinned:$is_pinned dark:$isDark name:$name")
        pin_button.setOnClickListener { v ->
            val is_pinned: Boolean = orderData[name]?.let { it > 0 } ?: run { false }
            if(!is_pinned) {
                setOrder(ctx, name, 1)
            }else {
                clearOrder(ctx, name)
            }
            fetchOrder(ctx)
            global?.let {
                it.listadapter.notifyDataSetChanged()
            }
            Log.d("fcrow", "clicked...$name")
        }
        namev.setText(name)
        namev.setTextColor(namecolor)
        iconv.setImageDrawable(icon)
        v.setBackgroundColor(color)
        return v
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

fun fetchOrder(ctx:Context){
    orderData = hashMapOf<String, Int>()
    val db = getDb(ctx)
    val c:Cursor? = db.rawQuery("select id, name, pin_order from windmill", null)
    c?.let {
        val id_idx =  c.getColumnIndex("id")
        val name_idx =  c.getColumnIndex("name")
        val pin_order_idx =  c.getColumnIndex("pin_order")
        if(c.moveToFirst()){
            do {
                val id=c.getInt(id_idx)
                val name=c.getString(name_idx)
                val pin_order=c.getInt(pin_order_idx)
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
    Log.d("fcrow", "........................... deleting 'delete from windmill where name = ?' % '$name'....................")
}

fun getDb(ctx:Context):SQLiteDatabase {
    return dbh?.let{
        it.getWritableDatabase()
    } ?: run {
        dbh = DBHelper(ctx)
        return DBHelper(ctx).getWritableDatabase()
    }
}
