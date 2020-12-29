package com.example.haris.galeriapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Item(
    val path: String,
    val title: String,
    val type: String
) : Parcelable