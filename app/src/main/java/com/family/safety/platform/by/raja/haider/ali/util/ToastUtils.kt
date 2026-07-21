package com.family.safety.platform.by.raja.haider.ali.util

import android.content.Context
import android.widget.Toast

object ToastUtils {
    private var currentToast: Toast? = null

    fun show(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        currentToast?.cancel()
        currentToast = Toast.makeText(context.applicationContext, message, duration)
        currentToast?.show()
    }

    fun showLong(context: Context, message: String) {
        show(context, message, Toast.LENGTH_LONG)
    }
}
