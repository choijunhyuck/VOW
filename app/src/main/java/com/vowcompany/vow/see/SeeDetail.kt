package com.vowcompany.vow.see

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.vowcompany.vow.R
import com.vowcompany.vow.rest.Global
import kotlinx.android.synthetic.main.activity_see_detail.*
import kotlinx.android.synthetic.main.activity_universal_titlebar.*

class SeeDetail : AppCompatActivity() {

    /* Override Functions */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_detail)

        setWidgets()

    }

    /* Rest Functions */

    private fun setWidgets() {

        //Status bar
        Global.statusBarBlackTheme(this)

        //Set custom title bar
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.activity_universal_titlebar)
        universal_title.text = resources.getString(R.string.find_imei)

        //Set on clicks
        find_imei_back_btn.setOnClickListener {
            finish()
        }

    }

}