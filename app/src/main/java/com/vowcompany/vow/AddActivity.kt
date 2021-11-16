package com.vowcompany.vow

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.vowcompany.vow.database.DatabaseHelper
import com.vowcompany.vow.rest.API
import com.vowcompany.vow.rest.Global
import com.vowcompany.vow.setting.SettingInformationActivity
import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_add_main.*
import kotlinx.android.synthetic.main.activity_error.*
import kotlinx.android.synthetic.main.activity_published.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class AddActivity : AppCompatActivity() {

    /* Variables */

    companion object {

        var verify = false

    }

    var splashTime: Long = 3000

    lateinit var progressDialog: ProgressDialog

    /* Override Functions */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_main)

        /* Clear all preference (signature, safetybar) */
        Global.getPreference(this).edit().clear().apply()

        //Set database
        net.sqlcipher.database.SQLiteDatabase.loadLibs(this)

        //Set widgest
        setWidgets()

    }

    /* Locks */

    override fun onResume() {
        super.onResume()

        //If not verified in pattern activity
        if(!AddActivity.verify){
            var intent = Intent(this, LockKeyActivity::class.java)
            intent.putExtra("type", 3)
            startActivity(intent)
        }

    }

    override fun onStop() {
        super.onStop()
        SettingInformationActivity.verify = false
    }

    /* Rest Functions */

    private fun setWidgets() {

        //Status bar
        Global.statusBarBlackTheme(this)

        //Set Progress bar
        progressDialog = ProgressDialog(this)

        //Set on click
        add_discard_btn.setOnClickListener {
            finish()
        }
        add_publish_btn.setOnClickListener {
            //Empty check
            if (!add_input.text.isEmpty()) {
                //Hide keyboard
                Global.hideKeyboard(this, add_input)
                //Publish
                publish()
                //Diable button
                add_publish_btn.isClickable = false
            }
        }

    }

    private fun publish() {

        //Show dialog
        Global.briefProgressDialog(this, progressDialog, "Publishing...", true)

        val client = OkHttpClient()
        val request = Global.briefRequest(API.PUBLISH_URL, makeFormBody())

        //Call with request
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {

                runOnUiThread {
                    errorActivity()
                }
                //Log
                Log.d("vowlog", e.localizedMessage!!)
            }

            override fun onResponse(call: Call, response: Response) {

                //Brief context
                var ctx = this@AddActivity

                //Get data
                val json = JSONObject(response.body()!!.string())

                //Check error condition
                if (json.get("error").toString() == "false") {

                    runOnUiThread {
                        publishedActivity()
                    }

                } else {

                    Global.briefProgressDialog(ctx, progressDialog, "", false)
                    when (json.get("error_msg").toString()) {
                        //Param leaked
                        "301" -> Global.briefToast(ctx, resources.getString(R.string.server_error_please_retry))
                        //User existed
                        "302" -> Global.briefToast(ctx, resources.getString(R.string.server_error_please_retry))
                    }

                }

            }

        })

    }

    private fun makeFormBody(): FormBody {

        var database = DatabaseHelper(this, null)

        //Form body => Add variables
        return FormBody.Builder()
            .add("signature", Global.briefGetSig(this, database))
            .add("content", add_input.text.toString())
            .build()

    }

    private fun publishedActivity() {

        if (add_error_layout.visibility == View.VISIBLE) {
            add_error_layout.visibility = View.GONE
        }

        //Hide dialog
        Global.briefProgressDialog(this@AddActivity, progressDialog, "", false)

        //Set content
        published_input.text = add_input.text.toString()

        //Show layout
        add_published_layout.visibility = View.VISIBLE
        Handler().postDelayed({

            finish()

        }, splashTime)

    }

    private fun errorActivity() {

        //Hide dialog
        Global.briefProgressDialog(this@AddActivity, progressDialog, "", false)

        //Set content
        error_input.text = add_input.text.toString()

        //Show layout
        add_error_layout.visibility = View.VISIBLE

        //Retry
        error_retry_btn.setOnClickListener {
            publish()
        }

    }

}