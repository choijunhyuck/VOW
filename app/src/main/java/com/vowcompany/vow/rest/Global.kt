package com.vowcompany.vow.rest

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.vowcompany.vow.database.DatabaseHelper
import okhttp3.FormBody
import okhttp3.Request
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.*

class Global {

    companion object {

        fun statusBarBlackTheme(activity: Activity) {
            activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        fun hideKeyboard(activity: Activity, input: EditText) {
            val imm: InputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(input.windowToken, 0)
        }

        fun getPreference(ctx: Context): SharedPreferences {
            return ctx.getSharedPreferences("app", 0)
        }

        fun briefProgressDialog(
            activity: Activity,
            progressDialog: ProgressDialog,
            msg: String,
            show: Boolean
        ) {
            if (show) {
                activity.runOnUiThread {
                    progressDialog.setMessage(msg)
                    progressDialog.show()
                }
            } else {
                activity.runOnUiThread {
                    progressDialog.hide()
                }
            }
        }

        fun briefRequest(url: String, body: FormBody): Request {
            return Request.Builder().url(url).post(body).build()
        }

        fun briefToast(activity: Activity, msg: String) {
            activity.runOnUiThread {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
            }
        }

        fun briefGetSig(activity: Activity, database: DatabaseHelper): String {

            //Set database cursor & Get registered pin
            val cursor = database.getSig(activity)
            cursor!!.moveToLast()
            var signature = ""

            if (cursor.moveToLast() && cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SIG)) != null) {
                signature = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_SIG))
            }

            cursor.close()

            return signature

        }

        fun briefGetPin(activity: Activity, database: DatabaseHelper): String {

            //Set database cursor & Get registered pin
            val cursor = database.getPin(activity)
            cursor!!.moveToLast()
            var registeredPin = ""

            if (cursor.moveToLast() && cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PIN)) != null) {
                registeredPin = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PIN))
            }

            cursor.close()

            return registeredPin

        }

    }

}