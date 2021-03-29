package com.thangadurai.awignlocationtask.utils


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.thangadurai.awignlocationtask.BuildConfig
import com.thangadurai.awignlocationtask.R

object AskPermission {

    const val PERMISSION_CODE = 10001

    const val SCAN_PERMISSION_CODE = 1011

    const val LOCATION_FINE = Manifest.permission.ACCESS_FINE_LOCATION

    const val ALREADY_PERMISSION_DENY_TEXT =
        """You have previously declined this permission.You must approve this permission in the app settings on your device."""


    fun checkPermission(context: Context, permission: Array<String>): Boolean {
        var count = 0
        for (i in permission) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    i
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                count++
            }
        }

        return count == permission.size


    }

    fun requestPermission(context: Context, permission: Array<out String>) {

        ActivityCompat.requestPermissions(
            context as Activity,
            permission,
            PERMISSION_CODE
        )

    }


    fun handlePermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        return when (requestCode) {
            PERMISSION_CODE -> {

                var count = 0
                for (i in permissions.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED
                    ) {
                        count++
                    }
                }
                return count == permissions.size
            }
            SCAN_PERMISSION_CODE -> {
                var count = 0
                for (i in permissions.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED
                    ) {
                        count++
                    }
                }
                return count == permissions.size
            }

            else -> {
                handlePermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    fun shouldShowRequestPermissionRationale(
        context: Context,
        permissions: Array<out String>
    ): Boolean {
        var count = 0
        for (i in permissions.indices) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    permissions[i]
                )
            ) {
                count++
            }
        }
        return count == permissions.size
    }

    fun launchSettingsScreen(context: Context) {
        context.startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + BuildConfig.APPLICATION_ID)
            ),
        )
    }

    fun checkVersion(): Boolean {
        return VersionUtils.isGreaterThanM()
    }

    @SuppressLint("InflateParams")
    fun showGoToSettingsDialog(
        context: Context,
        body: String
    ) {
        context as Activity
        val builder = AlertDialog.Builder(context)
        val dialog = context.layoutInflater.inflate(R.layout.dialog_go_to_settings, null)
        val alertDialog = builder.create()

        dialog.findViewById<TextView>(R.id.tv_go_to_settings).text = body

        dialog.findViewById<Button>(R.id.btn_go_to_settings)
            .setOnClickListener {
                alertDialog.dismiss()
                launchSettingsScreen(context)
            }


        alertDialog.setView(dialog)
        alertDialog.show()
    }
}