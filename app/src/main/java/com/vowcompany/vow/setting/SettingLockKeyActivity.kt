package com.vowcompany.vow.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBar
import com.andrognito.pinlockview.PinLockListener
import com.vowcompany.vow.LockKeyActivity
import com.vowcompany.vow.MainActivity
import com.vowcompany.vow.R
import com.vowcompany.vow.database.DatabaseHelper
import com.vowcompany.vow.rest.API
import com.vowcompany.vow.rest.Global
import kotlinx.android.synthetic.main.activity_setting_lock_key.*
import kotlinx.android.synthetic.main.activity_universal_titlebar_2.*

class SettingLockKeyActivity : AppCompatActivity() {

    /* Variables */

    companion object {

        var verify = false

    }

    var tempPin = ""

    /* Override functions */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_lock_key)

        setWidgets()

    }

    /* Locks */

    override fun onResume() {
        super.onResume()

        //If not verified in pattern activity
        if(!verify){
            var intent = Intent(this, LockKeyActivity::class.java)
            intent.putExtra("type", 1)
            startActivity(intent)
        }

    }

    override fun onStop() {
        super.onStop()
        verify = false
    }

    /* Rest functions */

    private fun setWidgets() {

        //Set custom title bar
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.activity_universal_titlebar_2)
        universal_2_title.text = resources.getString(R.string.setting_lock_key)

        //Set on clicks
        universal_2_back_btn.setOnClickListener {
            finish()
        }

        //Initializing database
        var database = DatabaseHelper(this, null)

        //Set indicator
        pin_indicator.text = getString(R.string.pin_indicator_relay_1)

        //Initializing pin view listener
        var mPinLockViewListener: PinLockListener? = object : PinLockListener {

            override fun onComplete(pin: String) {

                var ctx = this@SettingLockKeyActivity

                //Case:(1)there is no registered pin and needs to be newly registered
                //     (2)incorrect pin
                if (pin_indicator.text == getString(R.string.pin_indicator_relay_1) ||
                    pin_indicator.text == getString(R.string.pin_indicator_relay_3)
                ) {

                    //Set temp pin
                    tempPin = pin

                    //Update indicator
                    pin_indicator.text = getString(R.string.pin_indicator_relay_2)

                    //Clear pin
                    pin_pin.resetPinLockView()

                } else if (pin_indicator.text == getString(R.string.pin_indicator_relay_2)) {

                    //If previous pin matches the current pin
                    if (pin == tempPin) {

                        //Update pin
                        database.updatePin(ctx, pin)
                        //Finish
                        finish()

                    } else {

                        //Update indicator
                        pin_indicator.text = getString(R.string.pin_indicator_relay_3)
                        //Clear pin & tempPin
                        pin_pin.resetPinLockView()
                        tempPin = ""

                    }

                }

            }

            override fun onEmpty() {
                Log.d("vowlog", "Pin empty")
            }

            override fun onPinChange(pinLength: Int, intermediatePin: String?) {
            }

        }

        //Set pin view listener
        pin_pin.setPinLockListener(mPinLockViewListener)

        //Attach indicator dots
        pin_pin.attachIndicatorDots(pin_indicator_dot)

    }

}