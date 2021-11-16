package com.vowcompany.vow.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.vowcompany.vow.rest.API

import net.sqlcipher.database.*

class DatabaseHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    //Initializing variables
    companion object {

        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "lock.db"
        const val TABLE_NAME = "user"
        const val COLUMN_ID = "_id"
        const val COLUMN_SIG = "signature"
        const val COLUMN_PIN = "pin"

    }

    /* Override functions */

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    override fun onCreate(db: SQLiteDatabase) {

        //Create database if not exist
        val CREATE_DATA_TABLE = ("CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_SIG + " TEXT," +
                COLUMN_PIN + " TEXT" + ")")
        db.execSQL(CREATE_DATA_TABLE)

    }

    /* Rest functions */

    fun loadDB(context: Context, password: String): SQLiteDatabase {

        val databaseFile = context.getDatabasePath("lock.db")

        val database = SQLiteDatabase.openOrCreateDatabase(databaseFile, password, null)

        return database

    }

    fun setInformation(context: Context, sig: String, pin: String) {

        val values = ContentValues()
        values.put(COLUMN_SIG, sig)
        values.put(COLUMN_PIN, pin)

        val db = loadDB(context, API.PASSWORD)
        db.insert(
            TABLE_NAME,
            null,
            values
        )

        db.close()

    }

    fun updatePin(context: Context, pin: String) {

        val values = ContentValues()
        values.put(COLUMN_PIN, pin)

        val db = loadDB(context, API.PASSWORD)
        db.update(
            TABLE_NAME,
            values,
            "_id = 1",
            null
        )

        db.close()

    }

    fun getPin(context: Context): Cursor? {
        val db = loadDB(context, API.PASSWORD)
        return db.rawQuery("SELECT $COLUMN_PIN FROM $TABLE_NAME", null)
    }

    fun getSig(context: Context): Cursor? {
        val db = loadDB(context, API.PASSWORD)
        return db.rawQuery("SELECT $COLUMN_SIG FROM $TABLE_NAME", null)
    }

}