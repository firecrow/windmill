package com.firecrow.windmill

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

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


class WMAdapter(context: Context, resource: Int, list: ArrayList<ApplicationInfo>):ArrayAdapter<ApplicationInfo>(context, resource, list) {

}