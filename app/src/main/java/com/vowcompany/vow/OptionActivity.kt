package com.vowcompany.vow

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.vowcompany.vow.database.DatabaseHelper
import com.vowcompany.vow.rest.API
import com.vowcompany.vow.rest.Global
import kotlinx.android.synthetic.main.activity_editing.*
import kotlinx.android.synthetic.main.activity_option.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class OptionActivity : AppCompatActivity() {

    /* Variables */

    var id = ""
    var open = ""
    var name = "null"
    var birth = "null"
    var type = 0

    lateinit var progressDialog: ProgressDialog

    /* Override Functions */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option)

        //Get id
        id = intent.getStringExtra("id")!!
        open = intent.getStringExtra("open")!!
        name = intent.getStringExtra("name")!!
        birth = intent.getStringExtra("birth")!!

        //Set widgets
        setWidgets()

    }

    /* Rest Functions */

    private fun setWidgets() {

        //Status bar
        Global.statusBarBlackTheme(this)

        //Set Progress bar
        progressDialog = ProgressDialog(this)

        //Pre-type setting
        when (open) {
            "family" -> {
                type = 0
                option_family_button.isChecked = true
            }
            "friend" -> {
                type = 1
                option_friend_button.isChecked = true
                option_name_input.setText(name)
                option_birth_input.setText(birth)
            }
            "cancellation" -> {
                type = 2
                option_cancellation_button.isChecked = true
            }
        }

        //Set on click
        option_back_btn.setOnClickListener {
            finish()
        }
        option_apply_btn.setOnClickListener {
            update()
        }
        //Radio buttons
        option_family_button.setOnClickListener {
            type = 0
            option_family_button.isChecked = true
            option_friend_button.isChecked = false
            option_cancellation_button.isChecked = false
        }
        option_friend_button.setOnClickListener {

            if (option_name_input.text.isNotEmpty() && option_birth_input.text.isNotEmpty()) {
                type = 1
                option_friend_button.isChecked = true
                option_family_button.isChecked = false
                option_cancellation_button.isChecked = false
            } else {
                option_friend_button.isChecked = false
                Global.briefToast(this, resources.getString(R.string.please_enter_friend_information_first))
            }

        }
        option_cancellation_button.setOnClickListener {
            type = 2
            option_cancellation_button.isChecked = true
            option_family_button.isChecked = false
            option_friend_button.isChecked = false
        }

    }

    private fun update() {

        //Show dialog
        Global.briefProgressDialog(this, progressDialog, "Updating...", true)

        val client = OkHttpClient()
        val request = Global.briefRequest(API.OPTION_UPDATE_URL, makeFormBody())

        //Call with request
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {

                Global.briefToast(this@OptionActivity, resources.getString(R.string.server_error_please_retry))
                //Log
                Log.d("vowlog", e.localizedMessage!!)

            }

            override fun onResponse(call: Call, response: Response) {

                //Brief context
                var ctx = this@OptionActivity

                //Get data
                val json = JSONObject(response.body()!!.string())

                //Check error condition
                if (json.get("error").toString() == "false") {

                    VowActivity.open = open
                    VowActivity.name = name
                    VowActivity.birth = birth
                    finish()

                } else {

                    Global.briefProgressDialog(ctx, progressDialog, "", false)
                    when (json.get("error_msg").toString()) {
                        //Param leaked
                        "701" -> Global.briefToast(ctx, resources.getString(R.string.server_error_please_retry))
                        //User existed
                        "702" -> Global.briefToast(ctx, resources.getString(R.string.server_error_please_retry))
                    }

                }

            }

        })

    }

    private fun makeFormBody(): FormBody {

        when (type) {
            0 -> {
                open = "family"
                name = "null"
                birth = "null"
            }
            1 -> {
                open = "friend"
                name = option_name_input.text.toString()
                birth = option_birth_input.text.toString()
            }
            else -> {
                open = "cancellation"
                name = "null"
                birth = "null"
            }
        }

        var database = DatabaseHelper(this, null)

        return FormBody.Builder()
            .add("signature", Global.briefGetSig(this, database))
            .add("id", id)
            .add("open", open)
            .add("name", name)
            .add("birth", birth)
            .build()

    }

}