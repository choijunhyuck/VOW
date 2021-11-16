package com.vowcompany.vow.entry

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.vowcompany.vow.MainActivity
import com.vowcompany.vow.R
import com.vowcompany.vow.database.DatabaseHelper
import com.vowcompany.vow.rest.API
import com.vowcompany.vow.rest.Global
import com.vowcompany.vow.setting.SettingLockKeyActivity
import net.sqlcipher.database.SQLiteDatabase

class SplashActivity : AppCompatActivity() {

    /* Variables */

    var splashTime: Long = 3000
    var relay = 0

    /* Override Functions */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        setWidgets()

    }

    /* Rest Functions */

    private fun setWidgets() {

        //Status bar
        Global.statusBarBlackTheme(this)

        //Make database if not exist
        makeDatabase()

        //Initializing database
        var database = DatabaseHelper(this, null)

        //Set database cursor & Get registered pin
        var registeredPin = Global.briefGetPin(this, database)

        if (registeredPin == "" && !Global.getPreference(this).getBoolean("safetybar", false)) {
            relay = 0
        } else if (registeredPin == "" && Global.getPreference(this)
                .getBoolean("safetybar", false)
        ) {
            relay = 1
        } else {
            relay = 2
        }

        //Initiate
        Handler().postDelayed({

            database.close()

            when (relay) {
                0 -> startActivity(Intent(this, InformationActivity::class.java))
                1 -> {
                    var intent = Intent(this, InitialLockKeyActivity::class.java)
                    intent.putExtra("type", 0)
                    startActivity(intent)
                }
                2 -> startActivity(Intent(this, MainActivity::class.java))
            }
            finish()

        }, splashTime)

    }

    fun makeDatabase() {

        //Create database if not exist
        val CREATE_DATA_TABLE = ("CREATE TABLE IF NOT EXISTS " +
                DatabaseHelper.TABLE_NAME + "("
                + DatabaseHelper.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DatabaseHelper.COLUMN_SIG + " TEXT," +
                DatabaseHelper.COLUMN_PIN + " TEXT" + ")")

        //Set rest variables
        SQLiteDatabase.loadLibs(this)
        val databaseFile = getDatabasePath("lock.db")
        databaseFile.parentFile.mkdirs()

        //Set database
        val database = SQLiteDatabase.openOrCreateDatabase(databaseFile, API.PASSWORD, null)

        //Make table if not exists
        database.execSQL(CREATE_DATA_TABLE)
        database.close()

    }

}