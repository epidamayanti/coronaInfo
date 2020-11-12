package edf.project.coronainfo.views

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edf.project.coronainfo.R
import edf.project.coronainfo.commons.*
import kotlinx.android.synthetic.main.dialog_warning.view.*
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.*
import kotlin.concurrent.schedule


class LoginFragment : RxBaseFragment() {

    private var sharedPrefManager: SharedPrefManager? = null
    private var username = ""
    private var pass = ""
    private var loading: Dialog? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPrefManager = SharedPrefManager(this.requireContext())
        loading = LoadingAlert.loginDialog(this.context!!, this.activity!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onResume() {
        super.onResume()

        btn_login.setOnClickListener {
            username = et_uname.text.toString()
            pass = et_pass.text.toString()
            when {
                TextUtils.isEmpty(et_uname.text.toString()) -> {
                    et_uname.error = "username tidak boleh kosong"
                    et_uname.requestFocus()
                    return@setOnClickListener
                }
                TextUtils.isEmpty(et_pass.text.toString()) -> {
                    et_pass.error = "Password tidak boleh kosong"
                    et_pass.requestFocus()
                    return@setOnClickListener
                }
                else -> {
                        loading?.show()
                        validateLogin(username, pass)
                }
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun validateLogin(uname: String, pass: String){
            loading?.dismiss()
            if (uname == "admin") {
                if (pass == "admin") {
                    Utils.isLogin = true
                    sharedPrefManager?.saveSPBoolean(SharedPrefManager.LOGIN, true)
                    sharedPrefManager?.saveSPString(SharedPrefManager.USERNAME, Utils.username)
                    RxBus.get().send(Utils.DASHBOARD)
                } else {
                    val mDialogView =
                        LayoutInflater.from(context).inflate(R.layout.dialog_warning, null)
                    val mBuilder = AlertDialog.Builder(context)
                        .setView(mDialogView)

                    val mAlertDialog = mBuilder.setCancelable(false).show()

                    mDialogView.bt_close_warning.setOnClickListener {
                        mAlertDialog.dismiss()
                    }

                    mDialogView.title_warning.setText("LOGIN FAILED! ")
                    mDialogView.content_warning.setText("Password yang dimasukkan salah!")
                }
            } else {
                val mDialogView =
                    LayoutInflater.from(context).inflate(R.layout.dialog_warning, null)
                val mBuilder = AlertDialog.Builder(context)
                    .setView(mDialogView)

                val mAlertDialog = mBuilder.setCancelable(false).show()

                mDialogView.bt_close_warning.setOnClickListener {
                    mAlertDialog.dismiss()
                }

                mDialogView.title_warning.setText("LOGIN FAILED! ")
                mDialogView.content_warning.setText("Username yang dimasukkan salah!")
            }
        }
}