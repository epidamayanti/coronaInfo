package edf.project.coronainfo.commons

import android.content.Context
import android.content.SharedPreferences

class SharedPrefManager(context: Context) {

    internal var sp: SharedPreferences
    internal var spEditor: SharedPreferences.Editor

    val spUsername: String?
        get() = sp.getString(USERNAME, "")

    val spLogin: Boolean?
        get() = sp.getBoolean(LOGIN, false)


    init {
        sp = context.getSharedPreferences(APP, Context.MODE_PRIVATE)
        spEditor = sp.edit()
    }

    fun saveSPString(keySP: String, value: String) {
        spEditor.putString(keySP, value)
        spEditor.commit()
    }

    fun saveSPInt(keySP: String, value: Int) {
        spEditor.putInt(keySP, value)
        spEditor.commit()
    }

    fun saveSPBoolean(keySP: String, value: Boolean) {
        spEditor.putBoolean(keySP, value)
        spEditor.commit()
    }


    companion object {
        val APP = "App"
        val USERNAME = "Username"
        val LOGIN = "Login"
    }
}
