package com.firecrow.windmill

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
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.palette.graphics.Palette
import kotlin.math.absoluteValue

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


class RowBuilder(val ctx:Context, val lifeCycle:LifeCycle) {

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
                return Color.rgb(maxOf(pr - 40, 0), maxOf(pg - 40, 0), maxOf(pb - 40, 0))
            } else {//Lighten
                return Color.rgb(minOf(pr + 40, 255), minOf(pg + 40, 255), minOf(pb + 40, 255))
            }
        }
        return color
    }

    fun buildClickHandler(app:AppData):(v:View?) -> Unit {
        return  { _:View? ->
            val db = getDb(ctx, true)
            val c: Cursor? = db.rawQuery("select id, name, pin_order from windmill where name = ?", arrayOf<String>(app.name))
            val count = c?.let {c.count} ?: run {0}
            c?.let{ it.close()}
            if(count == 0){
                val vals = ContentValues()
                vals.put("pin_order", 1)
                vals.put("name", app.name)
                db.insert( "windmill", null, vals)
            } else {
                db.delete("windmill","name = ?", arrayOf<String>(app.name))
            }
            db.close()
            lifeCycle.update("")
        }
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
        val pin_button = row.findViewById(R.id.pin_button) as ImageView
        app.v = row
    }

    fun updateRow(idx:Int, app: AppData, priorColor:Int) {
        val pin_button = app.v.findViewById(R.id.pin_button) as ImageView
        pin_button.setOnClickListener(buildClickHandler(app))
        val pin_image = if (app.is_pinned) R.drawable.pinned_graphic else R.drawable.not_pinned_graphic
        pin_button.setImageResource(pin_image)
        app.color = defineAlternateColor(app.color, priorColor)
        app.v.setBackgroundColor(app.color)
    }
}

class Fetcher(val ctx:Context, val blank:View) {
    val cache = hashMapOf<String, AppData>()

    fun refreshOrder(): HashMap<String, Int> {
        val orderData = hashMapOf<String, Int>()
        val db = getDb(ctx, false)
        val c: Cursor? = db.rawQuery("select id, name, pin_order from windmill", null)
        c?.let {
            if (c.moveToFirst()) {
                do {
                    val name = c.getString(c.getColumnIndex("name"))
                    val pin_order = c.getInt(c.getColumnIndex("pin_order"))
                    orderData.put(name, pin_order)
                } while (c.moveToNext())
            }
            c.close()
        }
        db.close()
        return orderData
    }

    fun fetch(query:String): ArrayList<AppData> {
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
        return items
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

class LifeCycle(var activity:Activity){
    var searchObj:SearchObj? = null
    var layout:ListView? = null
    var adapter:WMAdapter? = null
    var fetcher:Fetcher? = null
    var ctx:Context = activity as Context

    fun setupLifeCycle(layout:ListView, adapter:WMAdapter, fetcher:Fetcher, searchObj:SearchObj){
        this.layout = layout
        this.adapter = adapter
        this.fetcher = fetcher
        this.searchObj = searchObj
    }

    fun update(search:String) {
        //layout.invalidateViews()
        adapter?.clear()
        val apps = fetcher?.fetch(search)
        apps?.let {
            adapter?.addAll(it)
            adapter?.notifyDataSetChanged()
        }
    }
    fun scrollTop(){
        layout?.setSelection(0)
    }

    fun reset() {
        searchObj?.input?.setText("")
        searchObj?.input?.clearFocus()
        activity.hideKb(View(ctx))
        update("")
        scrollTop()
    }
}

class Activity : AppCompatActivity() {
    val lifeCycle:LifeCycle = LifeCycle(this)

    override fun onCreate(instance: Bundle? ) {
        super.onCreate(instance)
        setContentView(R.layout.main)

        val layout = findViewById<ListView>(R.id.apps) as ListView
        val blank = View(this)

        val rowBuilder = RowBuilder(this, lifeCycle)
        val adapter = WMAdapter(this, R.layout.row, arrayListOf<AppData>(), rowBuilder, blank)
        val fetcher = Fetcher(this, blank)
        val searchObj = setupSearchObj(findViewById<LinearLayout>(R.id.search_bar) as LinearLayout, lifeCycle)

        lifeCycle.setupLifeCycle(layout, adapter, fetcher, searchObj)
        setupLayout(this, layout, adapter, searchObj)
        lifeCycle.update("")
    }

    override fun onResume(){
        lifeCycle.reset()
        super.onResume()
    }

    fun hideKb(v:View){
        val imm =  getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
    }
}

fun getDb(ctx:Context, write: Boolean):SQLiteDatabase {
    return dbh?.let{
        return if(write)
            it.writableDatabase
        else
            it.readableDatabase
    } ?: run {
        val d = DBHelper(ctx)
        dbh = d
        return if(write)
            d.writableDatabase
        else
            d.readableDatabase
    }
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

fun setupSearchObj(bar:LinearLayout, lifeCycle:LifeCycle):SearchObj {
    val input = bar.findViewById<EditText>(R.id.search) as EditText;
    val button = bar.findViewById<ImageView>(R.id.search_button) as ImageView;

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
