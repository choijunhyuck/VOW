package com.vowcompany.vow.entry

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.vowcompany.vow.R
import com.vowcompany.vow.rest.API
import com.vowcompany.vow.rest.Global
import com.vowcompany.vow.see.SeeDetail
import com.vowcompany.vow.see.SeeTerms
import kotlinx.android.synthetic.main.activity_information.*
import kotlinx.android.synthetic.main.activity_universal_titlebar.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.*

class InformationActivity : AppCompatActivity() {

    /* Variables */

    lateinit var progressDialog: ProgressDialog
    lateinit var signature: String

    /* Override Functions */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)

        //Issue signature
        signature = issueSignature()

        setWidgets()

    }

    /* Rest Functions */

    private fun setWidgets() {

        //Status bar
        Global.statusBarBlackTheme(this)

        //Set custom title bar
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.activity_universal_titlebar)
        universal_title.text = resources.getString(R.string.information)

        //Set Progress bar
        progressDialog = ProgressDialog(this)

        //Set on clicks
        information_see_ex_btn.setOnClickListener {
            startActivity(Intent(this, SeeDetail::class.java))
        }
        information_see_terms_btn.setOnClickListener {
            startActivity(Intent(this, SeeTerms::class.java))
        }
        information_ok_btn.setOnClickListener {
            validation()
        }

    }

    private fun validation() {

        //Hide all keyboard
        Global.hideKeyboard(this, information_input_name)
        Global.hideKeyboard(this, information_input_imei)

        //Validate
        if (information_input_name.text.isEmpty()) {
            Global.briefToast(this, resources.getString(R.string.information_name_empty_toast))
        } else if (information_input_imei.text.length < 14 || information_input_imei.text.length > 16) {
            Global.briefToast(this, resources.getString(R.string.information_imei_wrong_toast))
        } else if (!information_policy_agree_box.isChecked) {
            Global.briefToast(this, resources.getString(R.string.information_pp_not_agree_toast))
        } else {
            //Checked
            registerUser()
        }

    }

    private fun registerUser() {

        //Show dialog
        Global.briefProgressDialog(this, progressDialog, "Register...", true)

        val client = OkHttpClient()
        val request = Global.briefRequest(API.REGISTER_USER_URL, makeFormBody())

        /* Save signature in web database */

        //Call with request
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                //Hide dialog
                Global.briefProgressDialog(this@InformationActivity, progressDialog, "", false)
                //Show toast
                Global.briefToast(this@InformationActivity, "Server error. please retry.")
                //Log
                Log.d("vowlog", e.localizedMessage!!)
            }

            override fun onResponse(call: Call, response: Response) {

                //Brief context
                var ctx = this@InformationActivity

                //Get data
                val json = JSONObject(response.body()!!.string())

                //Check error condition
                if (json.get("error").toString() == "false") {

                    //Hide dialog
                    Global.briefProgressDialog(ctx, progressDialog, "", false)
                    //Save signature temporary
                    Global.getPreference(ctx).edit().putString("signature", signature).apply()
                    //Set safety bar
                    Global.getPreference(ctx).edit().putBoolean("safetybar", true).apply()
                    //Go to initial lock key page
                    var intent = Intent(ctx, InitialLockKeyActivity::class.java)
                    startActivity(intent)
                    //Finish
                    finishAffinity()

                } else {

                    Global.briefProgressDialog(ctx, progressDialog, "", false)
                    when (json.get("error_msg").toString()) {
                        //Param leaked
                        "101" -> Global.briefToast(ctx, "Server error. please retry.")
                        //User existed
                        "102" -> Global.briefToast(ctx, "User already registered.")
                        //Server error
                        "103" -> Global.briefToast(ctx, "Server error. please retry.")
                    }

                }

            }

        })

    }

    private fun makeFormBody(): FormBody {

        //Form body => Add variables

        return FormBody.Builder()
            .add("name", information_input_name.text.toString())
            .add("imei", information_input_imei.text.toString())
            .add("signature", signature)
            .build()

    }

    fun issueSignature(): String {

        val allowedCharacter = "0123456789QWERTYUIOPASDFGHJKLZXCVBNM"
        var sizeOfRandomString = 14

        val random = Random()
        val sb = StringBuilder(sizeOfRandomString)
        for (i in 0 until sizeOfRandomString)
            sb.append(allowedCharacter[random.nextInt(allowedCharacter.length)])

        return sb.toString()

    }

}