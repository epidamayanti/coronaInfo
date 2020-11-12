package edf.project.coronainfo.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import edf.project.coronainfo.commons.Utils
import edf.project.coronainfo.models.Attributes
import edf.project.coronainfo.database.DBContract

class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES_COVID)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES_CATEGORY)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun insertData(data: Attributes): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(DBContract.UserEntry.COLUMN_FID, data.FID)
        values.put(DBContract.UserEntry.COLUMN_PROVINSI, data.Provinsi)
        values.put(DBContract.UserEntry.COLUMN_KODE_PROVINSI, data.Kode_Provi)
        values.put(DBContract.UserEntry.COLUMN_KASUS_POSITIF, data.Kasus_Posi)
        values.put(DBContract.UserEntry.COLUMN_KASUS_SEMBUH, data.Kasus_Semb)
        values.put(DBContract.UserEntry.COLUMN_KASUS_MENINGGAL, data.Kasus_Meni)

        // Insert the new row, returning the primary key value of the new row
        val row = db.insert(DBContract.UserEntry.TABLE_COVID, null, values)
        Log.d("error", ""+row)

        return true
    }

    fun readAllData(): MutableList<Attributes> {
        val data = ArrayList<Attributes>()
        val db = writableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery("SELECT * FROM " + DBContract.UserEntry.TABLE_COVID+" ORDER BY "+DBContract.UserEntry.COLUMN_FID +" ASC ", null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES_COVID)
            return ArrayList()
        }

        var fid: Int
        var provinsi: String
        var kode_provinsi: Int
        var positif: Int
        var sembuh: Int
        var meninggal : Int

        Utils.list_provinsi.clear()
        Utils.positif = 0
        Utils.meninggal = 0
        Utils.sembuh = 0

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                fid = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_FID))
                kode_provinsi = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_KODE_PROVINSI))
                provinsi = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_PROVINSI))
                positif = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_KASUS_POSITIF))
                sembuh = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_KASUS_SEMBUH))
                meninggal = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_KASUS_MENINGGAL))

                Utils.positif = Utils.positif + positif
                Utils.meninggal = Utils.meninggal + meninggal
                Utils.sembuh = Utils.sembuh + sembuh

                Utils.list_provinsi.add(provinsi)

                data.add(Attributes(fid, kode_provinsi, provinsi, positif, sembuh, meninggal))
                cursor.moveToNext()
            }
        }
        return data
    }

    fun sortByProvince(): ArrayList<Attributes> {
        val data = ArrayList<Attributes>()
        val db = writableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery("SELECT * FROM " + DBContract.UserEntry.TABLE_COVID+" ORDER BY "+DBContract.UserEntry.COLUMN_PROVINSI +" ASC ", null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES_COVID)
            return ArrayList()
        }

        var fid: Int
        var provinsi: String
        var kode_provinsi: Int
        var positif: Int
        var sembuh: Int
        var meninggal : Int

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                fid = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_FID))
                kode_provinsi = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_KODE_PROVINSI))
                provinsi = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_PROVINSI))
                positif = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_KASUS_POSITIF))
                sembuh = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_KASUS_SEMBUH))
                meninggal = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_KASUS_MENINGGAL))

                data.add(Attributes(fid, kode_provinsi, provinsi, positif, sembuh, meninggal))
                cursor.moveToNext()
            }
        }
        Log.d("sort-prov", ""+cursor)
        return data
    }

    fun sortByPositif(): ArrayList<Attributes> {
        val data = ArrayList<Attributes>()
        val db = writableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery("SELECT * FROM " + DBContract.UserEntry.TABLE_COVID+" ORDER BY "+DBContract.UserEntry.COLUMN_KASUS_POSITIF +" ASC ", null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES_COVID)
            return ArrayList()
        }

        var fid: Int
        var provinsi: String
        var kode_provinsi: Int
        var positif: Int
        var sembuh: Int
        var meninggal : Int

        if (cursor!!.moveToFirst()) {
            while (cursor.isAfterLast == false) {
                fid = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_FID))
                kode_provinsi = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_KODE_PROVINSI))
                provinsi = cursor.getString(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_PROVINSI))
                positif = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_KASUS_POSITIF))
                sembuh = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_KASUS_SEMBUH))
                meninggal = cursor.getInt(cursor.getColumnIndex(DBContract.UserEntry.COLUMN_KASUS_MENINGGAL))

                data.add(Attributes(fid, kode_provinsi, provinsi, positif, sembuh, meninggal))
                cursor.moveToNext()
            }
        }
        return data
    }

    fun deleteAllData(): Boolean {
        // Gets the data repository in write mode
        val db = writableDatabase

        // Issue SQL statement.
        db.execSQL("DELETE FROM "+DBContract.UserEntry.TABLE_COVID)

        return true
    }

    companion object {
        // If you change the database schema, you must increment the database version.
        val DATABASE_VERSION = 1
        val DATABASE_NAME = "CovidDb.db"

        private val SQL_CREATE_ENTRIES_COVID =
            "CREATE TABLE " + DBContract.UserEntry.TABLE_COVID + " (" +
                    DBContract.UserEntry.COLUMN_FID + " INTEGER PRIMARY KEY, " +
                    DBContract.UserEntry.COLUMN_KODE_PROVINSI + " INTEGER," +
                    DBContract.UserEntry.COLUMN_PROVINSI + " TEXT," +
                    DBContract.UserEntry.COLUMN_KASUS_POSITIF + " INTEGER," +
                    DBContract.UserEntry.COLUMN_KASUS_SEMBUH + " INTEGER," +
                    DBContract.UserEntry.COLUMN_KASUS_MENINGGAL + " INTEGER )"

        private val SQL_DELETE_ENTRIES_CATEGORY = "DROP TABLE IF EXISTS " + DBContract.UserEntry.TABLE_COVID

    }


}