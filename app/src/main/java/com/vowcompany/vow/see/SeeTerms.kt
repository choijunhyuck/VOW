package com.vowcompany.vow.see

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.vowcompany.vow.R
import com.vowcompany.vow.rest.API
import com.vowcompany.vow.rest.Global
import kotlinx.android.synthetic.main.activity_see_terms.*
import kotlinx.android.synthetic.main.activity_universal_titlebar.*

class SeeTerms : AppCompatActivity() {

    /* Override Functions */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_see_terms)

        setWidgets()

    }

    /* Rest Functions */

    private fun setWidgets() {

        //Status bar
        Global.statusBarBlackTheme(this)

        //Set custom title bar
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.activity_universal_titlebar)
        universal_title.text = resources.getString(R.string.terms_of_pp)

        //Show see terms
        see_terms_webview.loadUrl(API.PRIVATE_POLICY_ENG_URL)

        //Visibility of left button
        universal_left_button.visibility = View.VISIBLE

        //Set on clicks
        universal_left_button.setOnClickListener {
            if (universal_left_button.text == "KOR"){
                see_terms_webview.clearHistory()
                universal_left_button.text = "ENG"
                see_terms_webview.loadUrl(API.PRIVATE_POLICY_KOR_URL)
            }else if(universal_left_button.text == "ENG"){
                see_terms_webview.clearHistory()
                universal_left_button.text = "KOR"
                see_terms_webview.loadUrl(API.PRIVATE_POLICY_ENG_URL)
            }
        }
        terms_back_btn.setOnClickListener {
            finish()
        }

    }

}