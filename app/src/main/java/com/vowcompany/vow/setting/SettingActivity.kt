package com.vowcompany.vow.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.vowcompany.vow.R
import com.vowcompany.vow.rest.Global
import kotlinx.android.synthetic.main.activity_setting.*
import kotlinx.android.synthetic.main.activity_universal_titlebar_2.*

class SettingActivity : AppCompatActivity() {

    /* Override Functions */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        setWidgets()

    }

    /* Rest Functions */

    private fun setWidgets() {

        //Status bar
        Global.statusBarBlackTheme(this)

        //Set custom title bar
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.activity_universal_titlebar_2)
        universal_2_title.text = resources.getString(R.string.setting)

        //Set on clicks
        setting_lock_key_btn.setOnClickListener {
            startActivity(Intent(this, SettingLockKeyActivity::class.java))
        }
        setting_information_btn.setOnClickListener {
            startActivity(Intent(this, SettingInformationActivity::class.java))
        }
        setting_sar_btn.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.vow-official.com")))
        }
        universal_2_back_btn.setOnClickListener {
            finish()
        }

    }

}