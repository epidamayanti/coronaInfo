package edf.project.coronainfo.views

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import edf.project.coronainfo.R
import edf.project.coronainfo.adapter.CustomAdapter
import edf.project.coronainfo.commons.LoadingAlert
import edf.project.coronainfo.commons.RxBaseFragment
import edf.project.coronainfo.commons.Utils
import edf.project.coronainfo.database.DbHelper
import edf.project.coronainfo.models.Attributes
import edf.project.coronainfo.models.DataCovid
import edf.project.coronainfo.services.ServiceGetData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_no_internet.view.*
import kotlinx.android.synthetic.main.dialog_warning.view.*
import kotlinx.android.synthetic.main.filter_sort_item.view.*
import kotlinx.android.synthetic.main.fragment_dashboard.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATION")
class DashboardFragment : RxBaseFragment() {

    private var loading: Dialog? = null
    private var data: MutableList<Attributes> = mutableListOf()
    private var total_positif = 0
    private var total_sembuh = 0
    private var total_meninggal = 0
    private var sort_by = ""
    private var onClickFilter = false
    private var filterProv = mutableListOf<String>()
    private lateinit var dataAdapter: CustomAdapter
    private lateinit var DbHelper : DbHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loading = LoadingAlert.progressDialog(this.context!!, this.activity!!)
        DbHelper = DbHelper(this.requireContext())

        list_covid.layoutManager = LinearLayoutManager(this.context)

        if(DbHelper.readAllData().isEmpty()) {
            loading?.show()
            total_meninggal = 0
            total_positif = 0
            total_sembuh = 0
            initData()
        }

        readAlldata()
        dataAdapter = CustomAdapter(this.requireContext(), data)
        list_covid.adapter = dataAdapter

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onResume() {
        super.onResume()
        tv_total_positif.text = ""+Utils.positif
        tv_total_semb.text = ""+Utils.sembuh
        tv_total_meni.text = ""+Utils.meninggal

        btn_search.setOnClickListener {
            val search =  et_search.text.toString()
            val searchData = mutableListOf<Attributes>()
            var searchonData: MutableList<Attributes>
            loading?.show()

            Handler().postDelayed(Runnable {
                for (dataSearch in data) {
                    if (dataSearch.Provinsi.toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT))) {
                        searchData.add(dataSearch)
                    }
                }

                if (searchData.isEmpty()) {
                    val mDialogView =
                        LayoutInflater.from(context).inflate(R.layout.dialog_warning, null)
                    val mBuilder = AlertDialog.Builder(context)
                        .setView(mDialogView)

                    val mAlertDialog = mBuilder.setCancelable(false).show()

                    mDialogView.bt_close_warning.setOnClickListener {
                        data.clear()
                        searchonData = DbHelper.readAllData()
                        data.addAll(searchonData)

                        list_covid.post {
                            dataAdapter.notifyDataSetChanged()
                        }
                        loading?.dismiss()
                        mAlertDialog.dismiss()
                    }

                    mDialogView.title_warning.setText("DATA TIDAK DITEMUKAN! ")
                    mDialogView.content_warning.setText("provinsi yang dicari tidak ada")
                } else {
                    data.clear()
                    data.addAll(searchData)
                    list_covid.post {
                        dataAdapter.notifyDataSetChanged()
                    }
                    loading?.dismiss()
                }
            }, 1000)

        }

        refresh.setOnClickListener {
            loading?.show()
            DbHelper.deleteAllData()
            total_meninggal = 0
            total_positif = 0
            total_sembuh = 0
            initData()
        }

        filter.setOnClickListener {
            val mDialogView = LayoutInflater.from(this.requireContext()).inflate(R.layout.filter_sort_item, null)
            val mBuilder = AlertDialog.Builder(this.requireContext()).setView(mDialogView)
            val mAlertDialog = mBuilder.setCancelable(false).show()

            mDialogView.sort_prov.setBackgroundColor(R.color.colorPrimaryDark)
            mDialogView.sort_positif.setBackgroundColor(R.color.colorPrimaryDark)

            if(sort_by.equals("provinsi")){
                mDialogView.sort_prov.setBackgroundColor(R.color.grey_400)
                mDialogView.sort_positif.setBackgroundColor(R.color.colorPrimaryDark)
            }
            else if(sort_by.equals("kasus_positif")){
                mDialogView.sort_prov.setBackgroundColor(R.color.colorPrimaryDark)
                mDialogView.sort_positif.setBackgroundColor(R.color.grey_400)
            }

            mDialogView.btn_cancel.setOnClickListener {
                mAlertDialog.dismiss()
                if(!onClickFilter){
                    filterProv.clear()
                    sort_by = ""
                }
            }

            mDialogView.btn_reset.setOnClickListener {
                filterProv.clear()
                sort_by = ""
                onClickFilter = false
                setChipProvince(filterProv, mDialogView.chipsGroup)
                mDialogView.sort_prov.setBackgroundColor(R.color.colorPrimaryDark)
                mDialogView.sort_positif.setBackgroundColor(R.color.colorPrimaryDark)
            }

            mDialogView.sort_prov.setOnClickListener {
                mDialogView.sort_prov.setBackgroundColor(R.color.grey_400)
                mDialogView.sort_positif.setBackgroundColor(R.color.colorPrimaryDark)
                sort_by = "provinsi"
            }

            mDialogView.sort_positif.setOnClickListener {
                mDialogView.sort_prov.setBackgroundColor(R.color.colorPrimaryDark)
                mDialogView.sort_positif.setBackgroundColor(R.color.grey_400)
                sort_by = "kasus_positif"
            }

            setChipProvince(Utils.list_provinsi, mDialogView.chipsGroup)

            mDialogView.btn_filter.setOnClickListener {
                loading?.show()
                onClickFilter = true

                val dataForFilter = DbHelper.readAllData()
                val filter = filterData(dataForFilter, filterProv, sort_by)
                data.clear()
                data.addAll(filter)
                Timer("Waiting..", false).schedule(1000) {
                    list_covid.post{
                        dataAdapter.notifyDataSetChanged()
                    }
                    loading?.dismiss()
                    mAlertDialog.dismiss()
                }
            }
        }

    }


    @SuppressLint("SetTextI18n")
    private fun initData(){
        subscriptions.add(
                provideService()
                .getData( "*", "4326", "json")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    resp ->
                    insertToDB(resp.features)

                    readAlldata()
                    dataAdapter = CustomAdapter(this.requireContext(), data)
                    list_covid.adapter = dataAdapter

                    loading?.dismiss()
                }) {
                    err ->
                    loading?.dismiss()
                    if (err.localizedMessage.toString().contains("resolve host")) {
                        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_no_internet, null)
                        val mBuilder = AlertDialog.Builder(context).setView(mDialogView)

                        val  mAlertDialog = mBuilder.setCancelable(false).show()

                        mDialogView.bt_close.setOnClickListener {
                            mAlertDialog.dismiss()
                        }

                    } else {

                        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_warning, null)
                        val mBuilder = AlertDialog.Builder(context)
                                .setView(mDialogView)

                        val  mAlertDialog = mBuilder.setCancelable(false).show()

                        mDialogView.bt_close_warning.setOnClickListener {
                            mAlertDialog.dismiss()
                        }

                        mDialogView.title_warning.setText("FAILED TO GET DATA! ")
                        mDialogView.content_warning.setText(err.localizedMessage)
                        Log.d("errorr", err.localizedMessage)

                    }
                }
        )
    }

    @SuppressLint("InflateParams")
    private fun setChipProvince(listProv: MutableList<String>, chipsGroup: ChipGroup) {
        for (city in listProv) {

            val mChip = this.layoutInflater.inflate(R.layout.item_chip, null, false) as Chip
            val paddingDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, resources.displayMetrics).toInt()

            mChip.text = city
            mChip.setPadding(paddingDp, 0, paddingDp, 0)

            if(filterProv.isNotEmpty())
                if(filterProv.contains(city))
                    mChip.isChecked = true

            mChip.setOnCheckedChangeListener { _, b ->
                if(b)
                    filterProv.add(mChip.text.toString())
                else
                    filterProv.remove(mChip.text.toString())

            }
            chipsGroup.addView(mChip)
        }
    }

    private fun insertToDB(dataCovid: MutableList<DataCovid>){
        for (data in dataCovid){
            DbHelper.insertData(data.attributes)

        }
    }

    private fun readAlldata(){
        data.clear()
        data = DbHelper.readAllData()
    }

    private fun filterData(
        dataCovid: MutableList<Attributes>,
        list_provinsi: MutableList<String>,
        sort_by: String): Collection<Attributes>
    {
        val filterData = mutableListOf<Attributes>()
        var dataCovidSort = mutableListOf<Attributes>()

        if(sort_by.isEmpty()) {
            for (data in dataCovid) {
                if (list_provinsi.isNotEmpty()) {
                    for (provinsi in list_provinsi) {
                        if (data.Provinsi == provinsi) {
                            filterData.add(data)
                        }
                    }
                }
                else {
                    filterData.add(data)
                }
            }
        }
        else{
            if(sort_by.equals("provinsi")){
                dataCovidSort = DbHelper.sortByProvince()
                for (data in dataCovidSort) {
                    if (list_provinsi.isNotEmpty()) {
                        for (provinsi in list_provinsi)
                            if (data.Provinsi == provinsi)
                                filterData.add(data)
                    }
                    else {
                        filterData.add(data)
                    }
                }
            }
            else if(sort_by.equals("kasus_positif")){
                dataCovidSort = DbHelper.sortByPositif()

                for (data in dataCovidSort) {
                    if (list_provinsi.isNotEmpty()) {
                        for (provinsi in list_provinsi)
                            if (data.Provinsi == provinsi)
                                filterData.add(data)
                    }
                    else {
                        filterData.add(data)
                    }
                }
            }
        }
        return filterData
    }

    private fun provideService(): ServiceGetData {
        val clientBuilder: OkHttpClient.Builder = Utils.buildClient()
        val retrofit = Retrofit.Builder()
                .baseUrl(Utils.ENDPOINT)
                .client(
                        clientBuilder
                                .connectTimeout(10, TimeUnit.SECONDS)
                                .writeTimeout(10, TimeUnit.SECONDS)
                                .readTimeout(10, TimeUnit.SECONDS)
                                .retryOnConnectionFailure(true)
                                .build()
                )
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()
        return retrofit.create(ServiceGetData::class.java)
    }

}