package edf.project.coronainfo

import android.R.id.home
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import edf.project.coronainfo.commons.BaseActivity
import edf.project.coronainfo.commons.RxBus
import edf.project.coronainfo.commons.SharedPrefManager
import edf.project.coronainfo.commons.Utils
import edf.project.coronainfo.views.DashboardFragment
import edf.project.coronainfo.views.LoginFragment


class MainActivity : BaseActivity() {

    private var sharedPrefManager: SharedPrefManager? = null
    private val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                finish()
                dialog.dismiss()
            }

            DialogInterface.BUTTON_NEGATIVE -> {
                dialog.dismiss()
            }
        }
    }
    private val dialogLogin = DialogInterface.OnClickListener { dialog, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                sharedPrefManager?.saveSPBoolean(SharedPrefManager.LOGIN, false)
                changeFragment(LoginFragment(), false, Utils.LOGIN)
                dialog.dismiss()
            }

            DialogInterface.BUTTON_NEGATIVE -> {
                dialog.dismiss()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPrefManager = SharedPrefManager(this)



        if (savedInstanceState == null){
            manageSubscription()
            Utils.mActivity = this
            Utils.isLogin = sharedPrefManager?.spLogin!!

            if(Utils.isLogin){
                changeFragment(DashboardFragment(), false, Utils.DASHBOARD)
                Utils.username = sharedPrefManager?.spUsername.toString()
            }
            else{
                changeFragment(LoginFragment(), false, Utils.LOGIN)
            }
        }
    }

    private fun manageSubscription(){
        subscriptions.add(RxBus.get().toObservable().subscribe({
            event -> manageBus(event)
        },{ Toast.makeText(this, "Timeout! koneksi buruk", Toast.LENGTH_SHORT).show()}))
    }

    private fun manageBus(event:Any) {
        when (event) {
            Utils.LOGIN -> changeFragment(LoginFragment(), false, Utils.LOGIN)
            Utils.DASHBOARD -> changeFragment(DashboardFragment(), false, Utils.DASHBOARD)
        }
    }

    override fun onBackPressed() {
        when (supportFragmentManager.fragments.last()){
            is DashboardFragment ->{
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Logout ? ")
                        .setPositiveButton("YES", dialogLogin)
                        .setNegativeButton("NO", dialogLogin)
                        .setCancelable(false).show()
            }
            is LoginFragment -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Exit ? ")
                        .setPositiveButton("YES", dialogClickListener)
                        .setNegativeButton("NO", dialogClickListener)
                        .setCancelable(false).show()
            }
        }
    }
}