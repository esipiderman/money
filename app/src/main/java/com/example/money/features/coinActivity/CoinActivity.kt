package com.example.money.features.coinActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.money.R
import com.example.money.apiManager.*
import com.example.money.apiManager.models.ChartData
import com.example.money.apiManager.models.CoinAboutItem
import com.example.money.apiManager.models.CoinsData
import com.example.money.databinding.ActivityCoinBinding

class CoinActivity : AppCompatActivity() {
    lateinit var binding: ActivityCoinBinding
    lateinit var data: CoinsData.Data
    lateinit var aboutData: CoinAboutItem
    val apiManager = ApiManager()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fromIntent = intent.getBundleExtra("dataToSend")!!
        data = fromIntent.getParcelable("bundle1")!!
        aboutData = fromIntent.getParcelable("bundle2")!!

        binding.moduleToolbarCoin.toolbar.title = data.coinInfo.fullName

        initUi()
    }

    private fun initUi() {
        initChartUi()
        initStatisticUi()
        initAboutUi()
    }

    @SuppressLint("SetTextI18n")
    private fun initAboutUi() {
        binding.moduleAboutCoin.txtWebsiteCoin.text = aboutData.website
        binding.moduleAboutCoin.txtGithubCoin.text = aboutData.github
        binding.moduleAboutCoin.txtRedditCoin.text = aboutData.reddit
        if (aboutData.twitter!!.toString() == "no-data") {
            binding.moduleAboutCoin.txtTwitterCoin.text = aboutData.twitter
        } else {
            binding.moduleAboutCoin.txtTwitterCoin.text = "@" + aboutData.twitter
        }
        binding.moduleAboutCoin.txtAboutCoin.text = aboutData.more

        binding.moduleAboutCoin.txtWebsiteCoin.setOnClickListener {
            openUrl(aboutData.website!!)
        }
        binding.moduleAboutCoin.txtGithubCoin.setOnClickListener {
            openUrl(aboutData.github!!)
        }
        binding.moduleAboutCoin.txtRedditCoin.setOnClickListener {
            openUrl(aboutData.reddit!!)
        }
        binding.moduleAboutCoin.txtTwitterCoin.setOnClickListener {
            if (!aboutData.twitter.equals("no-data")) {
                openUrl(BASE_URL_TWITTER + aboutData.twitter)
            }

        }
    }

    private fun initStatisticUi() {
        val statisticData = data.dISPLAY.uSD

        binding.moduleStatisticCoin.openCoin.text = statisticData.oPEN24HOUR
        binding.moduleStatisticCoin.highCoin.text = statisticData.hIGH24HOUR
        binding.moduleStatisticCoin.lowCoin.text = statisticData.lOW24HOUR
        binding.moduleStatisticCoin.changeCoin.text = statisticData.cHANGE24HOUR
        binding.moduleStatisticCoin.algorithmCoin.text = data.coinInfo.algorithm
        binding.moduleStatisticCoin.volumeCoin.text = statisticData.tOTALVOLUME24H
        binding.moduleStatisticCoin.marketCoin.text = statisticData.mKTCAP
        binding.moduleStatisticCoin.supplyCoin.text = statisticData.sUPPLY


    }

    @SuppressLint("SetTextI18n")
    private fun initChartUi() {
        var period = HOUR
        requestForChartData(period)
        binding.moduleChartsCoin.radioGroupChart.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_12h -> {
                    period = HOUR
                }
                R.id.radio_1d -> {
                    period = HOURS24
                }
                R.id.radio_1w -> {
                    period = WEEK
                }
                R.id.radio_1m -> {
                    period = MONTH
                }
                R.id.radio_3m -> {
                    period = MONTH3
                }
                R.id.radio_1y -> {
                    period = YEAR
                }
                R.id.radio_all -> {
                    period = ALL
                }
            }

            requestForChartData(period)
        }

        binding.moduleChartsCoin.txtPriceCoin.text = data.dISPLAY.uSD.pRICE
        binding.moduleChartsCoin.txtChangeCoin2.text = data.dISPLAY.uSD.cHANGEPCT24HOUR + "%"
        binding.moduleChartsCoin.txtChangeCoin1.text = data.dISPLAY.uSD.cHANGE24HOUR

        if (data.rAW.uSD.cHANGE24HOUR>0.0){
            binding.moduleChartsCoin.txtChangeUpDown.text = "▲"
            binding.moduleChartsCoin.txtChangeCoin2.setTextColor(ContextCompat.getColor(binding.root.context,R.color.colorGain ))
            binding.moduleChartsCoin.txtChangeUpDown.setTextColor(ContextCompat.getColor(binding.root.context,R.color.colorGain ))

            binding.moduleChartsCoin.sparkViewCoin.lineColor = ContextCompat.getColor(binding.root.context, R.color.colorGain)

        }else if (data.rAW.uSD.cHANGE24HOUR<0.0){
            binding.moduleChartsCoin.txtChangeUpDown.text = "▼"
            binding.moduleChartsCoin.txtChangeCoin2.setTextColor(ContextCompat.getColor(binding.root.context,R.color.colorLoss ))
            binding.moduleChartsCoin.txtChangeUpDown.setTextColor(ContextCompat.getColor(binding.root.context,R.color.colorLoss ))

            binding.moduleChartsCoin.sparkViewCoin.lineColor = ContextCompat.getColor(binding.root.context, R.color.colorLoss)

        }else{
            binding.moduleChartsCoin.txtChangeUpDown.text = ""
            binding.moduleChartsCoin.txtChangeCoin2.setTextColor(ContextCompat.getColor(binding.root.context,R.color.tertiaryTextColor ))
            binding.moduleChartsCoin.txtChangeUpDown.setTextColor(ContextCompat.getColor(binding.root.context,R.color.tertiaryTextColor ))

            binding.moduleChartsCoin.sparkViewCoin.lineColor = ContextCompat.getColor(binding.root.context, R.color.tertiaryTextColor)

        }

        binding.moduleChartsCoin.sparkViewCoin.setScrubListener {
            if (it==null){
                binding.moduleChartsCoin.txtPriceCoin.text = data.dISPLAY.uSD.pRICE
            }else{
                binding.moduleChartsCoin.txtPriceCoin.text = "$ " + (it as ChartData.Data.Data).close.toString()
            }
        }

    }

    private fun openUrl(url: String) {
        if (url != "no-data") {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }
    private fun requestForChartData(period : String){
        apiManager.getChartData(
            data.coinInfo.name,
            period,
            object : ApiManager.ApiCallback<Pair<List<ChartData.Data.Data>, ChartData.Data.Data?>> {
                override fun onSuccess(data: Pair<List<ChartData.Data.Data>, ChartData.Data.Data?>) {
                    val chartAdapter = ChartAdapter(data.first, data.second?.open.toString())
                    binding.moduleChartsCoin.sparkViewCoin.adapter = chartAdapter
                }

                override fun onError(message: String) {
                    binding.moduleChartsCoin.txtError.visibility = TextView.VISIBLE
                    Toast.makeText(this@CoinActivity, message, Toast.LENGTH_SHORT).show()
                }

            })
    }
}