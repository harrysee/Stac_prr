package kr.hs.emirim.w2015.stac_prr.widget.extension

import com.chibatching.kotpref.KotprefModel

// object로 정의하여 싱글톤 패턴 적용(객체한번생성)
object UserPrefs : KotprefModel(){
    var useruid : String by stringPref()
}