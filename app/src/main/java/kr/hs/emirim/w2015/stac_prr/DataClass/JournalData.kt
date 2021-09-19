package kr.hs.emirim.w2015.stac_prr.DataClass

data class JournalData (
    val name : String,
    val journal : String,
    val date : String,
    val imgUri : String?,
    val docId : String
)