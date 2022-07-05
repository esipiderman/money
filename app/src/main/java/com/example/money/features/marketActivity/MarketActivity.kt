package com.example.money.features.marketActivity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.money.apiManager.ApiManager
import com.example.money.apiManager.models.CoinAboutData
import com.example.money.apiManager.models.CoinAboutItem
import com.example.money.apiManager.models.CoinsData
import com.example.money.databinding.ActivityMarketBinding
import com.example.money.features.coinActivity.CoinActivity
import com.google.gson.Gson

class MarketActivity : AppCompatActivity(), MarketAdapter.RecyclerCallback {
    lateinit var binding: ActivityMarketBinding
    private val apiManager = ApiManager()
    lateinit var dataNews: ArrayList<Pair<String, String>>
    lateinit var aboutDataMap: MutableMap<String, CoinAboutItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUi()
        getAboutDataFromAssets()

        binding.moduleRecyclerMarket.btnShowMore.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.livecoinwatch.com/"))
            startActivity(intent)
        }

        binding.swipeRefreshMain.setOnRefreshListener {
            initUi()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.swipeRefreshMain.isRefreshing = false
            }, 1500)
        }
    }
    override fun onResume() {
        super.onResume()

        initUi()
    }

    private fun initUi() {
        getNewsFromApi()
        getCoinsFromApi()
    }

    private fun getNewsFromApi() {

        apiManager.getNews(object : ApiManager.ApiCallback<ArrayList<Pair<String, String>>> {
            override fun onSuccess(data: ArrayList<Pair<String, String>>) {
                dataNews = data
                refreshNews()
            }

            override fun onError(message: String) {
                Toast.makeText(this@MarketActivity, message, Toast.LENGTH_SHORT).show()
            }

        })

    }
    private fun refreshNews() {
        val random = (0..45).random()
        binding.moduleNewsMarket.txtNews.text = dataNews[random].first

        binding.moduleNewsMarket.txtNews.setOnClickListener {
            refreshNews()
        }

        binding.moduleNewsMarket.imgMarket.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(dataNews[random].second))
            startActivity(intent)
        }
    }

    private fun getCoinsFromApi() {
        apiManager.getCoins(object : ApiManager.ApiCallback<List<CoinsData.Data>> {
            override fun onSuccess(data: List<CoinsData.Data>) {
                showDataInRecycler(data)
            }

            override fun onError(message: String) {
                Toast.makeText(this@MarketActivity, message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun showDataInRecycler(data: List<CoinsData.Data>) {
        val marketAdapter = MarketAdapter(ArrayList(data), this)
        binding.moduleRecyclerMarket.recyclerMarket.adapter = marketAdapter
        binding.moduleRecyclerMarket.recyclerMarket.layoutManager = LinearLayoutManager(this)
    }

    override fun onCoinItemClicked(coinData: CoinsData.Data) {
        val bundle = Bundle()
        bundle.putParcelable("bundle1", coinData)
        var notNullCoinAboutItem = CoinAboutItem(
            "no-data",
            "no-data",
            "no-data",
            "no-data",
            "no-data"
        )
        if (aboutDataMap.containsKey(coinData.coinInfo.name)){
            notNullCoinAboutItem = changeNullToNoData(aboutDataMap[coinData.coinInfo.name]!!)
        }

        bundle.putParcelable("bundle2", notNullCoinAboutItem)

        val intent = Intent(this, CoinActivity::class.java)
        intent.putExtra("dataToSend", bundle)
        startActivity(intent)
    }

    private fun getAboutDataFromAssets() {

        val fileInString = applicationContext.assets
            .open("currencyinfo.json")
            .bufferedReader()
            .use { it.readText() }

        aboutDataMap = mutableMapOf<String, CoinAboutItem>()
        val gson = Gson()
        val dataAboutAll = gson.fromJson(fileInString, CoinAboutData::class.java)
        dataAboutAll.forEach {
            aboutDataMap[it.currencyName] = CoinAboutItem(
                it.info.web,
                it.info.twt,
                it.info.github,
                it.info.reddit,
                it.info.desc
            )
        }
    }

    private fun changeNullToNoData(item: CoinAboutItem): CoinAboutItem {
        if (item.website.isNullOrBlank()) {
            item.website = "no-data"
        }
        if (item.github.isNullOrBlank()) {
            item.github = "no-data"
        }
        if (item.reddit.isNullOrBlank()) {
            item.reddit = "no-data"
        }
        if (item.twitter.isNullOrBlank()) {
            item.twitter = "no-data"
        }
        if (item.more.isNullOrBlank()) {
            item.more = "no-data"
        }
        return item
    }
}