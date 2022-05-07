package kr.hs.emirim.w2015.stac_prr.Model

import java.util.*

data class PlanModel (
    var isChecked: Boolean = false,
    var name : String? = null,
    var contents: String? = null,
    var memo : String? = null,
    var alarmId : Long = 0,
    var docId : String="",
    var create : Date = Date(),
)