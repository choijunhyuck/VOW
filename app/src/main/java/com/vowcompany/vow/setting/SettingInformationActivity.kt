package com.vowcompany.vow.setting

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import com.vowcompany.vow.LockKeyActivity
import com.vowcompany.vow.R
import com.vowcompany.vow.VowActivity
import com.vowcompany.vow.database.DatabaseHelper
import com.vowcompany.vow.rest.API
import com.vowcompany.vow.rest.Global
import kotlinx.android.synthetic.main.activity_setting_information.*
import kotlinx.android.synthetic.main.activity_universal_titlebar_2.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class SettingInformationActivity : AppCompatActivity() {

    /* Variables */

    companion object {

        var verify = false

    }

    lateinit var progressDialog: ProgressDialog
    lateinit var signature: String

    /* Override functions */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_information)

        setWidgets()

    }

    /* Locks */

    override fun onResume() {
        super.onResume()

        //If not verified in pattern activity
        if(!verify){
            var intent = Intent(this, LockKeyActivity::class.java)
            intent.putExtra("type", 2)
            startActivity(intent)
        }

    }

    override fun onStop() {
        super.onStop()
        verify = false
    }

    /* Rest functions */

    private fun setWidgets() {

        //Status bar
        Global.statusBarBlackTheme(this)

        //Set custom title bar
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.activity_universal_titlebar_2)
        universal_2_title.text = resources.getString(R.string.setting_information)

        //Set Progress bar
        progressDialog = ProgressDialog(this)

        //Set on clicks
        universal_2_back_btn.setOnClickListener {
            finish()
        }
        setting_information_signature.setOnClickListener {

            //Set reminder builder
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Reminder")
            builder.setMessage(resources.getString(R.string.setting_information_reminder))

            builder.setPositiveButton(android.R.string.yes) { _, _ ->

            }

            builder.show()

        }

        //Get & Set signature
        val database = DatabaseHelper(this, null)
        signature = Global.briefGetSig(this, database)
        setting_information_signature.text = signature

        /*
        //Get & Set license status
        getActivated()
        */

    }

    private fun makeFormBody(): FormBody {

        //Form body => Add variables
        return FormBody.Builder()
            .add("signature", signature)
            .build()

    }

    /* Deprecated */

    private fun getActivated() {

        //Show dialog
        Global.briefProgressDialog(this, progressDialog, "Checking...", true)

        val client = OkHttpClient()
        val request = Global.briefRequest(API.GET_ACTIVATED_URL, makeFormBody())

        /* Save signature in web database */

        //Call with request
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                //Hide dialog
                Global.briefProgressDialog(
                    this@SettingInformationActivity,
                    progressDialog,
                    "",
                    false
                )
                //Show toast
                Global.briefToast(this@SettingInformationActivity, resources.getString(R.string.server_error_please_retry))
                //Log
                Log.d("vowlog", e.localizedMessage!!)
            }

            override fun onResponse(call: Call, response: Response) {

                //Hide dialog
                Global.briefProgressDialog(
                    this@SettingInformationActivity,
                    progressDialog,
                    "",
                    false
                )

                //Brief context
                var ctx = this@SettingInformationActivity

                //Get data
                val json = JSONObject(response.body()!!.string())

                val error = json.get("error").toString()
                val msg = json.get("error_msg").toString()

                //Check error condition
                if (error == "false" && msg != "null") {
                    updateActiveWidget(msg)
                } else if (error == "false" && msg == "null") {
                    Global.briefToast(ctx, resources.getString(R.string.something_wrong_please_contact_us))
                } else {
                    Global.briefToast(ctx, resources.getString(R.string.server_error_please_retry))
                }


            }

        })

    }

    private fun updateActiveWidget(status: String) {

        if (status == "activated") {
            runOnUiThread {
                //setting_information_activated.visibility = View.VISIBLE
            }
        } else {
            runOnUiThread {
                //setting_information_deactivated.visibility = View.VISIBLE
            }
        }

    }

}