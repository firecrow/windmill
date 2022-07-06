package com.firecrow.windmill.model

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


data class AppData(
    var name: String,
    var packageName: String,
    var icon: AdaptiveIconDrawable,
    var meta: MetaData,
);

data class MetaData(
    val starred: Boolean? = null,
    var position: Int? = null,
    val iconStyle: String? = null,
)
