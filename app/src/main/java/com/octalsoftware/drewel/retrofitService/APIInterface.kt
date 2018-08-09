package com.octalsoftware.drewel.retrofitService

import com.os.drewel.apicall.responsemodel.googledirectionresultmodel.DirectionResults
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface APIInterface {

    @POST("{path1}")
    fun dataRequestRx(@Path("path1") path1: String, @Body fieldValue: RequestBody): Observable<Any>

    @POST("{path1}/{path2}")
    fun dataRequestRx(@Path("path1") path1: String, @Path("path2") path2: String, @Body fieldValue: RequestBody): Observable<Any>

    @Multipart
    @POST("{path1}")
    fun dataRequestMultiPartRx(@Path("path1") path1: String, @Part("data") requestBody: RequestBody, @Part files: List<MultipartBody.Part>): Observable<Any>

    @Multipart
    @POST("{path1}/{path2}")
    fun dataRequestMultiPartRx(@Path("path1") path1: String, @Path("path2") path2: String, @Part("data") requestBody: RequestBody, @Part files: List<MultipartBody.Part>): Observable<Any>

    @GET("{path1}")
    fun dataRequestGetRx(@Path("path1") path1: String, @QueryMap(encoded = true) fieldValue: RequestBody): Observable<Any>

    @GET("{path1}/{path2}")
    fun dataRequestGetRx(@Path("path1") path1: String, @Path("path2") path2: String, @QueryMap(encoded = true) action: RequestBody): Observable<Any>

    @GET("{path1}")
    fun dataRequestGetRxWithoutParam(@Path("path1") path1: String): Observable<Any>

    @GET("{path1}/{path2}")
    fun dataRequestGetRxWithoutParam(@Path("path1") path1: String, @Path("path2") path2: String): Observable<Any>
    @GET
    fun getGoogleDirection(@Url url: String): Observable<DirectionResults>
}