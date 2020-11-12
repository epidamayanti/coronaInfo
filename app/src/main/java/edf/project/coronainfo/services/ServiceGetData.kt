package edf.project.coronainfo.services

import edf.project.coronainfo.commons.Utils
import edf.project.coronainfo.models.DataResponse
import io.reactivex.Observable
import retrofit2.http.POST
import retrofit2.http.Query

interface ServiceGetData {
    @POST(Utils.PATH)
    fun getData(@Query("outFields") outFields:String,
                @Query("outSR") outSR:String,
                @Query("f")R:String
    ): Observable<DataResponse>
}