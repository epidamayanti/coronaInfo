package edf.project.coronainfo.database

import android.provider.BaseColumns

object DBContract {
    class UserEntry : BaseColumns {
        companion object {

            val TABLE_COVID = "covid_info"
            val COLUMN_FID = "id"
            val COLUMN_KODE_PROVINSI = "kode_provinsi"
            val COLUMN_PROVINSI = "provinsi"
            val COLUMN_KASUS_POSITIF = "kasus_positif"
            val COLUMN_KASUS_SEMBUH = "kasus_sembuh"
            val COLUMN_KASUS_MENINGGAL = "kasus_meninggal"

        }
    }
}
