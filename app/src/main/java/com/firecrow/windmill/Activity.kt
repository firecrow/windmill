package com.firecrow.windmill

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
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

        val layout  = findViewById(R.id.apps) as ListView;
        val listadaptor = WMAdapter(Activity.this, R.layout.row, allapps);
        layout.setAdapter(listadaptor);
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

fun getIconFilter(icon: Drawable): ColorFilter {
    val matrix = ColorMatrix()
    matrix.setSaturation(0.0f)

    // https://stackoverflow.com/questions/30340591/changing-an-imageview-to-black-and-white
    return ColorMatrixColorFilter(matrix);
}

class WMAdapter(context: Context, resource: Int, val alist: ArrayList<ApplicationInfo>):ArrayAdapter<ApplicationInfo>(context, resource, alist) {
    override fun getCount(): Int { return alist.count() }
    override fun getItem(idx: Int): ApplicationInfo { return alist.get(idx) }
    override fun getItemId(idx: Int): Long { return idx.toLong() }


    override fun getView(idx: Int, view: View, parent: ViewGroup) {
        return if view == null

        inflator:LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflator
        val v = layoutInflater.inflate(R.layout.grid_item, null);

        ApplicationInfo data = list.get(idx);
        return if data == null

        val namev = (TextView) view.findViewById(R.id.name);
        val iconv = (ImageView) view.findViewById(R.id.icon);

        val icon = data.loadIcon(packageManager)

        val color = getRowColor(data.loadIcon(packageManager));

        namev.setText(data.loadLabel(packageManager));
        iconv.setImageDrawable(icon);
        iconv.setColorFilter(getIconFilter();
        view.setBackgroundColor(color);

    }
    return view;

}
