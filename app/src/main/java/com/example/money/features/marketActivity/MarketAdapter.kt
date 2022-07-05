package com.example.money.features.marketActivity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.money.R
import com.example.money.apiManager.BASE_URL_IMAGE
import com.example.money.apiManager.models.CoinsData
import com.example.money.databinding.ItemRecyclerMarketBinding

class MarketAdapter(
    private val data : ArrayList<CoinsData.Data>,
    private val recyclerCallback: RecyclerCallback
    ) : RecyclerView.Adapter<MarketAdapter.MarketViewHolder>()   {
    lateinit var binding: ItemRecyclerMarketBinding

    inner class MarketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        @SuppressLint("SetTextI18n")
        fun bindViews(coinData:CoinsData.Data){

            binding.txtCoinNameMarket.text = coinData.coinInfo.fullName
            binding.txtPriceMarket.text = coinData.dISPLAY.uSD.pRICE

            binding.txtNumChangeMarket.text = coinData.dISPLAY.uSD.mKTCAP

            val change = coinData.rAW.uSD.cHANGE24HOUR
            if (change>0){
                binding.txtChangeMarket.setTextColor(ContextCompat.getColor(binding.root.context, R.color.colorGain))
                binding.txtChangeMarket.text = coinData.dISPLAY.uSD.cHANGE24HOUR
            }else if (change<0){
                binding.txtChangeMarket.setTextColor(ContextCompat.getColor(binding.root.context, R.color.colorLoss))
                binding.txtChangeMarket.text = coinData.dISPLAY.uSD.cHANGE24HOUR
            }else{
                binding.txtChangeMarket.text = coinData.dISPLAY.uSD.cHANGE24HOUR
            }

            Glide
                .with(itemView)
                .load(BASE_URL_IMAGE + coinData.coinInfo.imageUrl)
                .into(binding.imgItemRecyclerMarket)

            itemView.setOnClickListener {
                recyclerCallback.onCoinItemClicked(coinData)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemRecyclerMarketBinding.inflate(inflater, parent, false)

        return MarketViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: MarketViewHolder, position: Int) {
        holder.bindViews(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    interface RecyclerCallback{
        fun onCoinItemClicked(coinData:CoinsData.Data)
    }
}