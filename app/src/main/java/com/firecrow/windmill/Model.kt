package com.firecrow.windmill

import android.content.pm.ApplicationInfo
import android.graphics.drawable.AdaptiveIconDrawable
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
    var name: String,
    var packageName: String,
    var icon: AdaptiveIconDrawable,
    var color: Int,
);
