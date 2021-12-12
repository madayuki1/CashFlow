package com.example.uas

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class dcTrasaction(
    var date: String,
    var category_name : String,
    var cash : Int,
    var transaction_type : String
):Parcelable{

    override fun toString(): String {
        return category_name
    }
}