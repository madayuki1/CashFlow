package com.example.uas

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class dcCategory(
    var category_id : String,
    var category_name : String
):Parcelable{

    override fun toString(): String {
        return category_name
    }
}