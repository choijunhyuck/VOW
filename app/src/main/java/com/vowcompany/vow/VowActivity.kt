package com.vowcompany.vow

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.vowcompany.vow.database.DatabaseHelper
import com.vowcompany.vow.rest.API
import com.vowcompany.vow.rest.Global
import kotlinx.android.synthetic.main.activity_editing.*
import kotlinx.android.synthetic.main.activity_error.*
import kotlinx.android.synthetic.main.activity_published.*
import kotlinx.android.synthetic.main.activity_view.*
import kotlinx.android.synthetic.main.activity_vow.*
import net.sqlcipher.database.SQLiteDatabase
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class VowActivity : AppCompatActivity() {

    /* Variables */

    var id = ""

    companion object {

        var verify = false

        var open = ""
        var name = ""
        var birth = ""

    }

    var splashTime: Long = 3000

    lateinit var progressDialog: ProgressDialog

    /* Override Functions */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vow)

        //Get id
        id = intent.getStringExtra("id")!!

        //Set database
        SQLiteDatabase.loadLibs(this)

        //Set widgets
        setWidgets()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.viewmenu, menu)
        return true

    }

    /* Options Menu */

    fun onItemClick(item: MenuItem?): Boolean {

        when (item!!.itemId) {

            R.id.menu_edit -> {

                editing_input.setText(view_input.text.toString())
                vow_editing_layout.visibility = View.VISIBLE
                vow_view_layout.visibility = View.GONE
                return true

            }

            R.id.menu_option -> {

                Log.d("vowlog", open)

                if (open != "") {

                    var intent = Intent(this, OptionActivity::class.java)
                    intent.putExtra("id", id)
                    intent.putExtra("open", open)
                    intent.putExtra("name", name)
                    intent.putExtra("birth", birth)
                    startActivity(intent)

                }

                return true

            }

            R.id.menu_delete -> {

                //Set reminder builder
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Reminder")
                builder.setMessage(resources.getString(R.string.vow_reminder))

                builder.setPositiveButton(android.R.string.yes) { _, _ ->
                    delete()
                }

                builder.setNegativeButton(android.R.string.no) { _, _ ->
                    return@setNegativeButton
                }

                builder.show()

                return true

            }

        }

        return true

    }

    /* Locks */

    override fun onResume() {
        super.onResume()

        //If not verified in pattern activity
        if(!verify){
            var intent = Intent(this, LockKeyActivity::class.java)
            intent.putExtra("type", 0)
            startActivity(intent)
        }

    }

    override fun onStop() {
        super.onStop()
        verify = false
    }

    /* Rest Functions */

    private fun setWidgets() {

        //Status bar
        Global.statusBarBlackTheme(this)

        //Set Progress bar
        progressDialog = ProgressDialog(this)

        //Show title bar menu popup
        view_action_btn.setOnClickListener {

            val popup = PopupMenu(this, view_action_btn)

            val inf: MenuInflater = popup.menuInflater
            inf.inflate(R.menu.viewmenu, popup.menu)
            popup.show()

        }

        //Set on clicks
        view_back_btn.setOnClickListener {
            finish()
        }

        //Activity's widget setting
        editingActivity()

        //Get data
        getData()

    }

    private fun getData() {

        val client = OkHttpClient()
        val request = Global.briefRequest(API.GET_DATA_URL, makeFormBody(0))

        //Call with request
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                //Show toast
                Global.briefToast(this@VowActivity, resources.getString(R.string.server_error_please_retry))
                //Log
                Log.d("vowlog", e.localizedMessage!!)
            }

            override fun onResponse(call: Call, response: Response) {

                //Brief context
                var ctx = this@VowActivity

                //Get data
                val json = JSONObject(response.body()!!.string())

                //Check error condition
                if (json.get("error").toString() == "false") {

                    //Extract data
                    val jsonArray = json.getJSONArray("data")
                    val jsonObject = jsonArray.getJSONObject(0)

                    //Get data
                    val content = jsonObject.get("content").toString()
                    val created_at = jsonObject.get("created_at").toString()
                    open = jsonObject.get("open").toString()
                    name = jsonObject.get("name").toString()
                    birth = jsonObject.get("birth").toString()

                    runOnUiThread {
                        view_created_at.text = "Created at " + created_at
                        view_input.text = content
                    }

                } else {

                    when (json.get("error_msg").toString()) {
                        //Param leaked
                        "401" -> Global.briefToast(ctx, resources.getString(R.string.server_error_please_retry))
                        //Server error
                        "402" -> Global.briefToast(ctx, resources.getString(R.string.server_error_please_retry))
                        //Server error
                        "403" -> Global.briefToast(ctx, resources.getString(R.string.server_error_please_retry))
                        //Server error
                        "404" -> Global.briefToast(ctx, resources.getString(R.string.server_error_please_retry))
                    }

                }

            }

        })

    }

    /* Actions */

    private fun publish() {

        //Show dialog
        Global.briefProgressDialog(this, progressDialog, "Publishing...", true)

        val client = OkHttpClient()
        val request = Global.briefRequest(API.EDIT_URL, makeFormBody(1))

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
                var ctx = this@VowActivity

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
                        "501" -> Global.briefToast(ctx, resources.getString(R.string.server_error_please_retry))
                        //User existed
                        "502" -> Global.briefToast(ctx, resources.getString(R.string.server_error_please_retry))
                    }

                }

            }

        })

    }

    private fun delete() {

        //Show dialog
        Global.briefProgressDialog(this, progressDialog, "Deleting...", true)

        val client = OkHttpClient()
        val request = Global.briefRequest(API.DELETE_URL, makeFormBody(2))

        //Call with request
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {

                Global.briefToast(this@VowActivity, resources.getString(R.string.server_error_please_retry))
                //Log
                Log.d("vowlog", e.localizedMessage!!)

            }

            override fun onResponse(call: Call, response: Response) {

                //Brief context
                var ctx = this@VowActivity

                //Get data
                val json = JSONObject(response.body()!!.string())

                //Check error condition
                if (json.get("error").toString() == "false") {

                    finish()

                } else {

                    Global.briefProgressDialog(ctx, progressDialog, "", false)
                    when (json.get("error_msg").toString()) {
                        //Param leaked
                        "601" -> Global.briefToast(ctx, resources.getString(R.string.server_error_please_retry))
                        //User existed
                        "602" -> Global.briefToast(ctx, resources.getString(R.string.server_error_please_retry))
                    }

                }

            }

        })

    }

    private fun makeFormBody(type: Int): FormBody {

        var database = DatabaseHelper(this, null)

        when (type) {
            0 -> {
                return FormBody.Builder()
                    .add("id", id)
                    .add("hash", API.HASH)
                    .build()
            }
            1 -> {
                return FormBody.Builder()
                    .add("signature", Global.briefGetSig(this, database))
                    .add("id", id)
                    .add("content", editing_input.text.toString())
                    .build()
            }
            else -> {
                return FormBody.Builder()
                    .add("signature", Global.briefGetSig(this, database))
                    .add("id", id)
                    .build()
            }
        }

    }

    /* Activities */

    private fun editingActivity() {

        //Set on clicks
        editing_discard_btn.setOnClickListener {
            vow_editing_layout.visibility = View.GONE
            vow_view_layout.visibility = View.VISIBLE
        }
        editing_publish_btn.setOnClickListener {
            //Hide keyboard
            Global.hideKeyboard(this, editing_input)
            //Publish
            publish()
        }

    }

    private fun publishedActivity() {

        if (vow_error_layout.visibility == View.VISIBLE) {
            vow_error_layout.visibility = View.GONE
        }

        //Hide dialog
        Global.briefProgressDialog(this@VowActivity, progressDialog, "", false)

        //Set content
        published_input.text = editing_input.text.toString()

        //Show layout
        vow_published_layout.visibility = View.VISIBLE
        Handler().postDelayed({

            //Set content (View)
            view_input.text = editing_input.text.toString()

            //Clear text
            editing_input.setText("")

            vow_editing_layout.visibility = View.GONE
            vow_published_layout.visibility = View.GONE
            vow_view_layout.visibility = View.VISIBLE

        }, splashTime)

    }

    private fun errorActivity() {

        //Hide dialog
        Global.briefProgressDialog(this@VowActivity, progressDialog, "", false)

        //Set content
        error_input.text = editing_input.text.toString()

        //Show layout
        vow_error_layout.visibility = View.VISIBLE

        //Retry
        error_retry_btn.setOnClickListener {
            publish()
        }

    }

}