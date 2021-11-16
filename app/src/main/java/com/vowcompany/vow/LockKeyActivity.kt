package com.vowcompany.vow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import com.andrognito.pinlockview.PinLockListener
import com.vowcompany.vow.database.DatabaseHelper
import com.vowcompany.vow.rest.Global
import com.vowcompany.vow.setting.SettingInformationActivity
import com.vowcompany.vow.setting.SettingLockKeyActivity
import kotlinx.android.synthetic.main.activity_setting_lock_key.*
import kotlinx.android.synthetic.main.activity_universal_titlebar.*

class LockKeyActivity : AppCompatActivity() {

    /* Variables */

    var type = 0

    /* Override functions */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_key)

        //Get type
        type = intent.getIntExtra("type", 0)

        setWidgets()

    }

    /* Rest functions */

    private fun setWidgets() {

        //Status bar
        Global.statusBarBlackTheme(this)

        //Set custom title bar
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.activity_universal_titlebar)
        universal_title.text = resources.getString(R.string.lock_key)

        //Initializing database
        var database = DatabaseHelper(this, null)

        //Get registered pin
        var registeredPin = Global.briefGetPin(this, database)

        //Set indicator
        pin_indicator.text = getString(R.string.pin_indicator_relay_4)

        //Initializing pin view listener
        var mPinLockViewListener: PinLockListener? = object : PinLockListener {

            override fun onComplete(pin: String) {

                /* Check relay */

                //Case:(1)there is registered pin
                //     (2)incorrect pin
                if (pin_indicator.text == getString(R.string.pin_indicator_relay_4) ||
                    pin_indicator.text == getString(R.string.pin_indicator_relay_5)
                ) {

                    //If registered pin matches the current pin
                    if (pin == registeredPin) {

                        //Set verified, and Go
                        when (type) {
                            0 -> {
                                VowActivity.verify = true
                                finish()
                            }
                            1 -> {
                                SettingLockKeyActivity.verify = true
                                finish()
                            }
                            2 -> {
                                SettingInformationActivity.verify = true
                                finish()
                            }
                            3-> {
                                AddActivity.verify = true
                                finish()
                            }
                        }

                    } else {

                        //Update indicator
                        pin_indicator.text = getString(R.string.pin_indicator_relay_5)

                        //Clear pin
                        pin_pin.resetPinLockView()

                    }

                }

            }

            override fun onEmpty() {
                Log.d("vowlog", "Pin empty")
            }

            override fun onPinChange(pinLength: Int, intermediatePin: String?) {
                //Log.d("vowlog", "Pin changed")
            }

        }

        //Set pin view listener
        pin_pin.setPinLockListener(mPinLockViewListener)

        //Attach indicator dots
        pin_pin.attachIndicatorDots(pin_indicator_dot)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

}