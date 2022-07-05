package com.example.money.apiManager.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CoinAboutItem(

    var website: String?,
    var twitter: String?,
    var github: String?,
    var reddit: String?,
    var more: String?

) : Parcelable
