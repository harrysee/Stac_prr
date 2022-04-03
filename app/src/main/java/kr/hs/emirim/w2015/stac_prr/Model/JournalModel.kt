package kr.hs.emirim.w2015.stac_prr.Model

import com.google.firebase.Timestamp


data class JournalModel (
    val name : String,
    val journal : String,
    val date : Timestamp,
    val imgUri : String?,
    val docId : String
)