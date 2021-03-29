package com.thangadurai.awignlocationtask.ui.base

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat


open class BaseActivity : AppCompatActivity() {

    var mToast: Toast? = null

    fun showMessage(msg: String) {
        if (mToast != null) {
            mToast?.cancel()
        }
        mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        mToast?.show()
    }

    fun changeIcon(view: AppCompatImageView, icon: Int) {
        view.setImageDrawable(ContextCompat.getDrawable(this, icon))
    }
}