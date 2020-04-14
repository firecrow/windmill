package com.firecrow.windmill

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
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
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
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
    var v:View,
    val type: LayoutType);

class SearchObj(val input:EditText, val button: ImageView, var getText:() -> String, var setButton:(itemPos:Int) -> Unit)

fun setupSearchObj(bar:LinearLayout, lifeCycle:LifeCycle):SearchObj {
    val input = bar.findViewById<EditText>(R.id.search) as EditText;
    val button = bar.findViewById<EditText>(R.id.search_button) as ImageView;

    val setButton  = { itemPos:Int ->
        if (input.text.length > 0) {
            button.setImageResource(R.drawable.x_search)
        } else if (itemPos > 0) {
            button.setImageResource(R.drawable.up_arrow)
        } else {
            button.setImageResource(R.drawable.blank)
        }
    }

    button.setOnClickListener{v ->
        lifeCycle.reset()
    }

    input.addTextChangedListener(object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(editable: Editable?) {}
        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if(s.toString() != "") {
                lifeCycle.update(s.toString())
                setButton(0)
                lifeCycle.scrollTop()
            }
        }
    })
    val getText = { -> input.text.toString()}
    val obj =   SearchObj( input, button,  getText, setButton )
    lifeCycle.searchObj = obj
    return obj
}

fun setupLayout(ctx:Context, layout:ListView, adapter:WMAdapter, searchObj:SearchObj){
    layout.setDivider(null)
    layout.setAdapter(adapter)
    layout.setOnItemClickListener { parent, view, idx, id ->
        val app: AppData = adapter.getItem(idx)
        ctx.getPackageManager().getLaunchIntentForPackage(app.appInfo.packageName)?.let { ctx.startActivity(it) }
    }
    layout.setOnScrollListener(object: AbsListView.OnScrollListener {
        override fun onScroll(view: AbsListView, first:Int, visible:Int, total:Int) {
            searchObj.setButton(first)
        }
        override fun onScrollStateChanged(view:AbsListView? , scrollState:Int) {

        }
    })
}

class LifeCycle(ctx:Context, val layout:ListView, val adapter:WMAdapter, val fetchAppDataArray:(String) -> ArrayList<AppData>, val hideKb:(View)->Unit, rowBuilder:RowBuilder){
    var searchObj:SearchObj? = null
    init {
        rowBuilder.lifeCycle = this
    }
    fun update(search:String) {
        adapter.clear()
        adapter.addAll(fetchAppDataArray(search))
        adapter.notifyDataSetChanged()
    }
    fun scrollTop(){
        layout.setSelection(0)
    }
    fun reset() {
        searchObj?.let {
            it.input.setText("")
            it.input.clearFocus()
        }
        hideKb(layout)
        update("")
        scrollTop()
    }
}

class RowBuilder(val ctx:Context) {
    var lifeCycle:LifeCycle? = null

    fun setupPin(app:AppData) {
        val pin_button = app.v.findViewById(R.id.pin_button) as ImageView
        pin_button.setOnClickListener { v ->
            val db = getDb(ctx)
            if (!app.is_pinned) {
                val vals = ContentValues()
                vals.put("pin_order", 1)
                vals.put("name", app.name)

                Log.d("fcrow","${app.name}: insert setting pin to 1 ${app.is_pinned}")
                if(db.insert( "windmill", null, vals) == -1L) {
                    Log.d("fcrow","${app.name}: update setting pin to 1 ${app.is_pinned}")
                    db.rawQuery(
                        "update windmill set pin_order = ? where name = ?",
                        arrayOf<String>(1.toString(), app.name)
                    )
                }
            } else {
                Log.d("fcrow","${app.name}: delete setting pin to 1 ${app.is_pinned}")
                db.delete("windmill","name = ?", arrayOf<String>(app.name))
            }
            lifeCycle?.let{it.update("")}
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

    fun defineAlternateColor(color:Int, priorColor:Int): Int{
        var r = Color.red(color)
        var g = Color.green(color)
        var b = Color.blue(color)

        var pr = Color.red(priorColor)
        var pg = Color.green(priorColor)
        var pb = Color.blue(priorColor)
        if(((r-pr).absoluteValue + (g-pg).absoluteValue + (b-pb).absoluteValue) < 100) {
            if (r > 220 || g > 220 || b > 220 || r + g + b > 140 * 3) {//Darken
                return Color.rgb(maxOf(r - 40, 0), maxOf(g - 40, 0), maxOf(b - 40, 0))
            } else {//Lighten
                return Color.rgb(minOf(r + 40, 255), minOf(g + 40, 255), minOf(b + 40, 255))
            }
        }
        return color
    }

    fun buildRow (app:AppData) {
        val inflater: LayoutInflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row = inflater.inflate(R.layout.row, null)
        val pm = ctx.getPackageManager()

        val namev = row.findViewById(R.id.appname) as TextView
        namev.setText(app.name)
        namev.setTextColor(Color.WHITE)

        val icon = app.appInfo.loadIcon(pm)
        val iconv = row.findViewById(R.id.icon) as ImageView
        iconv.setImageDrawable(icon)
        app.color = getRowColor(icon)
        row.setBackgroundColor(app.color)
        val isN = row == null
        app.v = row
        setupPin(app)
    }

    fun updateRow(idx:Int, app: AppData, priorColor:Int): View {
        fun colorToString(color:Int):String {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            return "rgb($red,$green,$blue)"
        }
        val pin_button = app.v.findViewById(R.id.pin_button) as ImageView
        val pin_image = if (app.is_pinned) R.drawable.pinned_graphic else R.drawable.not_pinned_graphic
        Log.d("fcrow","${app.name}: update pin image to ${app.is_pinned}")
        pin_button.setImageResource(pin_image)
        val newColor = defineAlternateColor(app.color, priorColor)

        val colorStr = colorToString(app.color)
        val priorColorStr = colorToString(priorColor)
        val newColorStr = colorToString(newColor)
        val same = colorStr == newColorStr;
        Log.d("fcrow","${app.name} | same:$same priorColor:$priorColorStr color: $colorStr alternateColor: $newColorStr")

        if(idx > 0 && app.color != newColor){
            app.color = newColor
            app.v.setBackgroundColor(newColor)
        }
        return app.v
    }
}

class Activity : AppCompatActivity() {
    var life:LifeCycle? = null

    override fun onCreate(instance: Bundle? ) {
        super.onCreate(instance)
        setContentView(R.layout.main)

        val cache = hashMapOf<String, AppData>()
        val layout = findViewById<ListView>(R.id.apps) as ListView
        val blank = View(this)

        val rowBuilder = RowBuilder(this)
        val adapter = WMAdapter(this, R.layout.row, arrayListOf<AppData>(), rowBuilder,blank)
        val fetchAppDataArray = buildFetcher(this, layout, cache, blank)
        val lifeCycle = LifeCycle(this, layout, adapter, fetchAppDataArray, ::hideKb, rowBuilder)
        val searchObj = setupSearchObj(findViewById<EditText>(R.id.search_bar) as LinearLayout, lifeCycle)
        setupLayout(this, layout,adapter, searchObj)
        lifeCycle.update("")
        life = lifeCycle
    }

    override fun onResume(){
        life?.let{it.reset()}
        super.onResume()
    }

    override fun onPause(){
        super.onPause()
    }

    fun hideKb(v:View){
        val imm =  getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
    }
}

fun buildFetcher(ctx:Context, layout:ListView, cache: HashMap<String, AppData>, blank:View): (query:String) -> ArrayList<AppData> {
    fun refreshOrder(): HashMap<String, Int> {
        Log.d("fcrow", "refreshing order...........")
        val orderData = hashMapOf<String, Int>()
        val db = getDb(ctx)
        val c: Cursor? = db.rawQuery("select id, name, pin_order from windmill", null)
        c?.let {
            if (c.moveToFirst()) {
                do {
                    val name = c.getString(c.getColumnIndex("name"))
                    val pin_order = c.getInt(c.getColumnIndex("pin_order"))
                    orderData.put(name, pin_order)
                } while (c.moveToNext())
            }
        }
        Log.d("fcrow", "refreshing order........... count: ${orderData.count()}")
        return orderData
    }
    return { query:String ->
        val orderData = refreshOrder()
        val pm = ctx.getPackageManager();
        val items:ArrayList<AppData> = arrayListOf<AppData>()
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA).filter { app ->
            pm.getLaunchIntentForPackage(app.packageName) != null
        }.toMutableList<ApplicationInfo>().map {
            val name = it.loadLabel(pm).toString()
            val order = orderData[name]?.let {it} ?: run {0}
            cache.get(name)?.let{
                it.order = order
                it.is_pinned = order != 0
                it
            } ?:run {
                val item = AppData(it, name, 0x00000000, order, order != 0, blank, LayoutType.APP)
                cache.put(name, item)
                item
            }
        }.sortedWith(compareBy<AppData>({ !it.is_pinned }).thenBy({ it.name })).filter { a ->
            Regex(query.toLowerCase()).find(a.name.toLowerCase()) != null}
        items.addAll(apps)
        items
    }
}


class WMAdapter(val ctx:Context, resource: Int, var apps: ArrayList<AppData>, val rowBuilder:RowBuilder, val blank:View):
        ArrayAdapter<AppData>(ctx, resource, apps) {
    override fun getCount(): Int { return apps.count() }
    override fun getItem(idx: Int): AppData { return apps.get(idx) }
    override fun getItemId(idx: Int): Long { return idx.toLong() }
    override fun getView(idx: Int, view: View?, parent: ViewGroup): View {
        val priorColor:Int = if(idx > 0) getItem(idx-1).color else Color.parseColor("#000000")
        val item = getItem(idx)
        if(item.v == blank){
            rowBuilder.buildRow(item)
        }
        rowBuilder.updateRow(idx, item, priorColor)
        return item.v
    }
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

fun getDb(ctx:Context):SQLiteDatabase {
    return dbh?.let{
        it.getWritableDatabase()
    } ?: run {
        dbh = DBHelper(ctx)
        return DBHelper(ctx).getWritableDatabase()
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
