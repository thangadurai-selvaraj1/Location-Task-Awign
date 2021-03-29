package com.thangadurai.awignlocationtask.utils

import android.os.Build

object VersionUtils {

    fun isGreaterThanM(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

}