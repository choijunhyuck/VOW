package com.vowcompany.vow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.vowcompany.vow.database.DatabaseHelper
import com.vowcompany.vow.feed.BriefFeedListAdapter
import com.vowcompany.vow.feed.BriefFeedListItem
import com.vowcompany.vow.rest.API
import com.vowcompany.vow.rest.Global
import com.vowcompany.vow.setting.SettingActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    /* Variables */

    //Feed count
    var count = 0

    //List variables
    var listModel = ArrayList<BriefFeedListItem>()
    lateinit var adapter: BriefFeedListAdapter

    //Information
    var signature = ""
    var p_count = 0

    /* Override Functions */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Set database
        net.sqlcipher.database.SQLiteDatabase.loadLibs(this)

        //Set widgets
        setWidgets()

    }

    override fun onRestart() {
        super.onRestart()

        //Clear create one text
        main_empty_layout.visibility = View.GONE

        //Clear all data
        clearList()

        //Clear count
        count = 0

        //Load new data
        getPLimit()

    }

    /* Rest Functions */

    private fun setWidgets() {

        //Status bar
        Global.statusBarBlackTheme(this)

        //Set list widgets
        adapter = BriefFeedListAdapter(this, listModel)
        main_feed_view.adapter = adapter

        //Set on clicks
        main_add_btn.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }
        main_setting_btn.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        main_create_one_btn.setOnClickListener {
            main_add_btn.callOnClick()
        }

        //Get user information
        getInformation()

    }

    private fun getInformation(){

        var database = DatabaseHelper(this, null)

        if(Global.briefGetSig(this, database) == "error"){
            Global.briefToast(this, "Please contact us.")
            finishAffinity()
        }else{
            signature = Global.briefGetSig(this, database)
            getPLimit()
        }

    }

    private fun getPLimit(){

        val client = OkHttpClient()
        val request = Global.briefRequest(API.GET_P_LIMIT_URL, makeFormBody())

        //Call with request
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                //Show toast
                Global.briefToast(this@MainActivity, resources.getString(R.string.server_error_please_retry))
                //Log
                Log.d("vowlog", e.localizedMessage!!)
            }

            override fun onResponse(call: Call, response: Response) {

                //Brief context
                var ctx = this@MainActivity

                //Get data
                val json = JSONObject(response.body()!!.string())

                val error = json.get("error").toString()
                val msg = json.get("error_msg").toString()

                //Check error condition
                if (error == "false" && msg != "null") {
                    p_count = msg.toInt()
                    getBriefFeed()
                    Log.d("vowlog", p_count.toString())
                } else if (error == "false" && msg == "null") {
                    Global.briefToast(ctx, resources.getString(R.string.something_wrong_please_contact_us))
                } else {
                    Global.briefToast(ctx, resources.getString(R.string.server_error_please_retry))
                }

            }

        })
        //Call End

    }

    //Get feed data from server
    private fun getBriefFeed() {

        val client = OkHttpClient()
        val request = Global.briefRequest(API.GET_BRIEF_FEED_URL, makeFormBody())

        //Call with request
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {

                //Show toast
                Global.briefToast(this@MainActivity, resources.getString(R.string.server_error_please_retry))
                //Log
                Log.d("vowlog", e.localizedMessage!!)

            }

            override fun onResponse(call: Call, response: Response) {

                //Brief context
                var ctx = this@MainActivity

                val json = JSONObject(response.body()!!.string())

                if (json.get("error").toString() == "false") {

                    //Get array data
                    val jsonArray = json.getJSONArray("data")

                    for (i in 0 until jsonArray.length()) {

                        count++

                        //Extract data
                        val jsonObject = jsonArray.getJSONObject(i)
                        val id = jsonObject.get("id").toString()
                        val created_at = jsonObject.get("created_at").toString()

                        //Add data to model
                        listModel.add(BriefFeedListItem(id, created_at))

                    }

                    //Reload list
                    runOnUiThread { adapter.notifyDataSetChanged() }

                    //Visible & Publish limit
                    runOnUiThread {

                        if(count == 0){
                            main_empty_layout.visibility = View.VISIBLE
                            main_add_btn.setOnClickListener {
                                startActivity(
                                    Intent(
                                        ctx,
                                        AddActivity::class.java
                                    )
                                )
                            }
                        }else if(count < p_count){
                            main_add_btn.setOnClickListener {
                                startActivity(
                                    Intent(
                                        ctx,
                                        AddActivity::class.java
                                    )
                                )
                            }
                        }else{
                            main_add_btn.setOnClickListener {
                                Global.briefToast(ctx, resources.getString(R.string.publish_limit_reached))
                            }
                        }

                    }

                } else {

                    when (json.get("error_msg").toString()) {
                        //Param leaked
                        "001" -> Global.briefToast(ctx, resources.getString(R.string.server_error_please_retry))
                        //Server error
                        else -> Global.briefToast(ctx, resources.getString(R.string.server_error_please_retry))
                    }

                }

            }

        })
        //Call End

    }

    private fun makeFormBody(): FormBody {

        //Form body => Add variables
        return FormBody.Builder()
            .add("signature", signature)
            .build()

    }

    private fun clearList() {

        listModel.clear()
        adapter.notifyDataSetChanged()

    }

}