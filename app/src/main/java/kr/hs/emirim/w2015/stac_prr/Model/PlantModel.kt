package kr.hs.emirim.w2015.stac_prr.Model

import java.util.*

data class PlantModel (
    val name : String,
    val date : Date,
    val imgUrl : String,
    val led : String,
    val memo : String,
    val specise :String,
    val temperate : String,
    val water : Int,
    val dviceNum : String
)
