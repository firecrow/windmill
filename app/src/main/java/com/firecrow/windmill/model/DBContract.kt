package com.firecrow.windmill.model

class IconTypes {
    companion object {
        val ADAPTIVE = "adaptive"
        val SHRUNK = "shrunk"
        val COLOR_SAMPLE = "color_sample"
    }
}

object DBContract {
    object AppEntry {
        const val TABLE_NAME ="apps"
        const val COLUMN_NAME_PACKAGE_NAME ="package_name"
        const val COLUMN_NAME_NAME ="name"
        const val COLUMN_NAME_STARRED ="is_starred"
        const val COLUMN_NAME_POSITION ="position"
        const val COLUMN_NAME_ICON_STYLE ="icon_style"
    }
}

