package edf.project.coronainfo.commons

import android.app.Activity
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class Utils {
    companion object {

        //endpoint API
        val ENDPOINT = "https://services5.arcgis.com/VS6HdKS0VfIhv8Ct/arcgis/rest/services/COVID19_Indonesia_per_Provinsi/FeatureServer/"
        const val PATH = "0/query?where=1%3D1"

        //fragment
        var mActivity: Activity? = null
        val DASHBOARD = "dashboard"
        val LOGIN = "login"

        //data login
        var username = ""
        var isLogin = false

        //data dashboard
        var positif = 0
        var sembuh = 0
        var meninggal = 0
        var list_provinsi: MutableList<String> = mutableListOf()

        //retrofit
        fun buildClient(): OkHttpClient.Builder {
            val clientBuilder = OkHttpClient.Builder()
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            clientBuilder.addInterceptor(loggingInterceptor)
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)

            return clientBuilder
        }



    }
}