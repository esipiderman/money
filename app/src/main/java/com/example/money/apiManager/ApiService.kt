package com.example.money.apiManager

import com.example.money.apiManager.models.ChartData
import com.example.money.apiManager.models.CoinsData
import com.example.money.apiManager.models.NewsData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @Headers(API_KEY)
    @GET("v2/news/")
    fun getNews(
        @Query(" sortOrder") sortOrder: String = "popular"
    ): Call<NewsData>

    @Headers(API_KEY)
    @GET("top/totalvolfull")
    fun getCoins(
        @Query("tsym") changeTo: String = "USD",
        @Query("limit") limit: Int = 10
    ): Call<CoinsData>

    @Headers(API_KEY)
    @GET("v2/{period}")
    fun getDataChart(
        @Path("period") period: String,
        @Query("fsym") fromSymbol: String,
        @Query("limit") limit: Int,
        @Query("aggregate") aggregate: Int,
        @Query("tsym") toSymbol: String = "USD"
    ): Call<ChartData>
}