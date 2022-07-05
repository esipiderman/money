package com.example.money.apiManager

import android.util.Log
import com.example.money.apiManager.models.ChartData
import com.example.money.apiManager.models.CoinsData
import com.example.money.apiManager.models.NewsData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ApiManager {

    private val apiService: ApiService

    init {
        val retrofit = Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    fun getNews(apiCallback: ApiCallback<ArrayList<Pair<String, String>>>) {
        apiService.getNews().enqueue(object : Callback<NewsData> {
            override fun onResponse(call: Call<NewsData>, response: Response<NewsData>) {
                val data = response.body()!!

                val dataToSend: ArrayList<Pair<String, String>> = arrayListOf()
                data.data.forEach {
                    dataToSend.add(Pair(it.title, it.url))
                }

                apiCallback.onSuccess(dataToSend)

            }

            override fun onFailure(call: Call<NewsData>, t: Throwable) {

                apiCallback.onError(t.message!!)

            }

        })
    }

    fun getCoins(apiCallback: ApiCallback<List<CoinsData.Data>>) {
        apiService.getCoins().enqueue(object : Callback<CoinsData> {
            override fun onResponse(call: Call<CoinsData>, response: Response<CoinsData>) {
                val data = response.body()!!
                apiCallback.onSuccess(data.data)
            }

            override fun onFailure(call: Call<CoinsData>, t: Throwable) {
                apiCallback.onError(t.message!!)
            }

        })
    }

    fun getChartData(symbol: String, period: String, apiCallback: ApiCallback<Pair<List<ChartData.Data.Data>, ChartData.Data.Data?>>) {

        var histoPeriod = ""
        var limit = 30
        var aggregate = 1

        when (period) {
            HOUR -> {
                histoPeriod = HISTO_MINUTE
                limit = 60
                aggregate = 12
            }
            HOURS24 -> {
                histoPeriod = HISTO_HOUR
                limit = 24
            }
            WEEK -> {
                histoPeriod = HISTO_HOUR
                aggregate = 6
            }
            MONTH -> {
                histoPeriod = HISTO_DAY
                limit = 30
            }
            MONTH3 -> {
                histoPeriod = HISTO_DAY
                limit = 90
            }
            YEAR -> {
                histoPeriod = HISTO_DAY
                aggregate = 13
            }
            ALL -> {
                histoPeriod = HISTO_DAY
                limit = 2000
                aggregate = 30
            }

        }

        apiService.getDataChart(histoPeriod, symbol, limit, aggregate)
            .enqueue(object : Callback<ChartData> {
                override fun onResponse(call: Call<ChartData>, response: Response<ChartData>) {
                    val fullData = response.body()!!
                    val data1 = fullData.data.data
                    val data2 = data1.maxByOrNull { it.close.toFloat() }

                    apiCallback.onSuccess(Pair(data1, data2))
                }

                override fun onFailure(call: Call<ChartData>, t: Throwable) {
                    apiCallback.onError(t.message!!)
                }

            })

    }

    interface ApiCallback<T> {
        fun onSuccess(data: T)
        fun onError(message: String)
    }

}