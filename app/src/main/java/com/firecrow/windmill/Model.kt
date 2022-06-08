package com.firecrow.windmill

import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable

enum class LayoutType {
    APP,
    SEARCH
}

enum class SearchState {
    BLANK,
    SEARCH,
    SCROLL
}


class AppData(
    val appInfo: ApplicationInfo,
    var name: String,
    var icon: Drawable,
    var color: Int,
    val type: LayoutType
);
