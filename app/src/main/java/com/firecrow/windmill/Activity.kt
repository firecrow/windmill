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

class WmCtx (val ctx:Context, val layout:ListView, val adapter:WMAdapter, val activity:Activity,
             val cache:HashMap<String, AppData>,
             val searchObj:SearchObj,
             val update:(search:String) -> Unit,
             val reset:() -> Unit
)

class SearchObj(val input:EditText, val button: ImageView, var getText:() -> String, var setButton:(itemPos:Int) -> Unit)

fun getRowColor(icon: Drawable): Int {
    val iconb: Bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
    val canvas: Canvas = Canvas(iconb)
    icon.setBounds(0, 0, 10, 10)
    icon.draw(canvas)

    val p: Palette = Palette.from(iconb).generate()
    return p.getVibrantColor(0xff000000.toInt())
}

fun setupSearchObj(bar:LinearLayout, reset: () -> Unit, updateSearch:(query:String)->Unit):SearchObj {
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
        reset()
        setButton(0)
        input.setText("")
        input.clearFocus()
    }

    input.addTextChangedListener(object: TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(editable: Editable?) {}
        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            reset()
            setButton(0)
            updateSearch(s.toString())
        }
    })
    val getText = { -> input.text.toString()}
    return  SearchObj( input, button,  getText, setButton )
}

fun setupLayout(ctx:Context, layout:ListView, adapter:WMAdapter, searchObj:SearchObj){
    layout.setDivider(null)
    layout.setAdapter(adapter)
    layout.setOnItemClickListener { parent, view, idx, id ->
        val app: AppData = adapter.getItem(idx)
        ctx.getPackageManager().getLaunchIntentForPackage(app.name)?.let { ctx.startActivity(it) }
    }
    layout.setOnScrollListener(object: AbsListView.OnScrollListener {
        override fun onScroll(view: AbsListView, first:Int, visible:Int, total:Int) {
            searchObj.setButton(first)
        }
        override fun onScrollStateChanged(view:AbsListView? , scrollState:Int) {

        }
    })
}

fun rowBuilder(ctx:Context, update:(query:String) -> Unit): (item:AppData)-> Unit {
    fun setupPin(app:AppData) {
        val pin_button = app.v.findViewById(R.id.pin_button) as ImageView
        pin_button.setOnClickListener { v ->
            val db = getDb(ctx)
            if (!app.is_pinned) {
                val vals = ContentValues()
                vals.put("pin_order", 1)
                vals.put("name", app.name)

                if(db.insert( "windmill", null, vals) == -1L)
                    db.rawQuery("update windmill set pin_order = ? where name = ?", arrayOf<String>(1.toString(), app.name))
            } else {
                db.delete("windmill","name = ?", arrayOf<String>(app.name))
            }
            update("")
        }
    }

    return {app:AppData ->
        val inflater: LayoutInflater =
            ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
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
        setupPin(app)
        app.v = row
    }
}

class Activity : AppCompatActivity() {
    var reset:() -> Unit = {->}

    override fun onCreate(instance: Bundle? ) {
        super.onCreate(instance)
        setContentView(R.layout.main)

        val cache = hashMapOf<String, AppData>()
        val layout = findViewById<ListView>(R.id.apps) as ListView
        val adapter = WMAdapter(this, R.layout.row, arrayListOf<AppData>())
        val fetchAppDataArray = buildFetcher(this, layout, cache)
        val update = {search:String ->
            adapter.clear()
            adapter.addAll(fetchAppDataArray(search))
            adapter.notifyDataSetChanged()
        }
        reset = { ->
            update("")
            // if cleared this value is always 0 because we have scrolled to the top
            layout.setSelection(0)
            hideTheFuckingKeyboard(layout)
        }

        val searchObj = setupSearchObj(findViewById<EditText>(R.id.search_bar) as LinearLayout, reset, update)
        setupLayout(this, layout,adapter, searchObj)
        adapter.buildRow = rowBuilder(this, update)


        val wmCtx = WmCtx( this, layout, adapter, this, searchObj, cache, rowBuilder(this, update), update, reset, fetchAppDataArray)
        update("")
    }

    override fun onResume(){
        reset()
        super.onResume()
    }

    override fun onPause(){
        super.onPause()
    }

    fun hideTheFuckingKeyboard(v:View){
        val imm =  getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
    }
}

fun buildFetcher(ctx:Context, layout:ListView, cache: HashMap<String, AppData>): (query:String) -> ArrayList<AppData> {
    return { query:String ->
        val orderData = fetchOrder(ctx)
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
                val item = AppData(it, name, 0x00000000, order, order != 0, layout, LayoutType.APP)
                cache.put(name, item)
                item
            }
        }.sortedWith(compareBy<AppData>({ !it.is_pinned }).thenBy({ it.name })).filter { a ->
            Regex(query.toLowerCase()).find(a.name.toLowerCase()) != null}
        items.addAll(apps)
        items
    }
}


class WMAdapter(val ctx:Context, resource: Int, var apps: ArrayList<AppData>, var buildRow: (item:AppData) -> Unit):
        ArrayAdapter<AppData>(wmCtx.ctx, resource, apps) {
    override fun getCount(): Int { return apps.count() }
    override fun getItem(idx: Int): AppData { return apps.get(idx) }
    override fun getItemId(idx: Int): Long { return idx.toLong() }

    override fun getView(idx: Int, view: View?, parent: ViewGroup): View {
        val priorColor:Int = if(idx > 0) getItem(idx-1).color else Color.parseColor("#000000")
        val item = getItem(idx)
        if(item.v == null) buildRow(item)
        updateRow(wmCtx, idx, item, priorColor)
        return item.v
    }

    fun defineAlternateColor(color:Int, priorColor:Int): Int{
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)

        val er: Int = r - Color.red(priorColor)
        val eg: Int = g - Color.green(priorColor)
        val eb: Int = b - Color.blue(priorColor)

        val delta = er.absoluteValue + eg.absoluteValue + eb.absoluteValue
        if (delta < 100) {
            if (r + g + b > 220 * 3) {
                return Color.rgb(200, 200, 200)
            } else if (r + g + b < 50) {
                return Color.rgb(80, 80, 80)
            } else {
                fun getAtleast(d:Int, x:Int):Int {
                    var sign = kotlin.math.sign(d.toDouble())
                    if(sign == 0.0) sign = 1.0;
                    val value = (maxOf(d.absoluteValue*2, 80) * sign).toInt()
                    //Log.d("fcrow", "sign: $sign value:$value")
                    val withOrig = value+x
                    val withinBounds = maxOf(minOf(withOrig, 255), 0)
                    return return withinBounds
                }
                val nr = getAtleast(er, r)
                val ng = getAtleast(eg, g)
                val nb = getAtleast(eb, b)
                return Color.rgb(nr, ng, nb)
            }
        }
        return color
    }

    fun updateRow(wmCtx: WmCtx, idx:Int, app: AppData, priorColor:Int): View {
        val pin_button = app.v.findViewById(R.id.pin_button) as ImageView
        val pin_image = if (app.is_pinned) R.drawable.pinned_graphic else R.drawable.not_pinned_graphic
        pin_button.setImageResource(pin_image)
        if(idx > 0) app.color = defineAlternateColor(app.color, priorColor)
        return app.v
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
    Log.d("fcrow", "getDb................................................")
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
