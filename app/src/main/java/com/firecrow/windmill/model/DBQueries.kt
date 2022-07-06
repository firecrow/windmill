package com.firecrow.windmill.model

import android.provider.BaseColumns

class ExanomeDBQueries {
    companion object {
        public val appsCreateTableSql = "" +
                "CREATE TABLE ${DBContract.AppEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${DBContract.AppEntry.COLUMN_NAME_PACKAGE_NAME} TEXT UNIQUE" +
                "${DBContract.AppEntry.COLUMN_NAME_NAME} TEXT" +
                "${DBContract.AppEntry.COLUMN_NAME_STARRED} NUMBER" +
                "${DBContract.AppEntry.COLUMN_NAME_POSITION} NUMBER" +
                "${DBContract.AppEntry.COLUMN_NAME_ICON_STYLE} TEXT" +
                ")"

    }
}
