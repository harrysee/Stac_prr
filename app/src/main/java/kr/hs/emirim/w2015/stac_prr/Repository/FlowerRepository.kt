package kr.hs.emirim.w2015.stac_prr.Repository

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import kr.hs.emirim.w2015.stac_prr.Model.Flowers

object FlowerRepository {
    val flowerData = MutableLiveData<ArrayList<Flowers>>()

    suspend fun insertFlow(flowerPref : SharedPreferences): MutableLiveData<ArrayList<Flowers>> {
        val flowers = ArrayList<Flowers>()
        flowers.add(Flowers("행운목","#행운이#아닌#약속"))
        flowers.add(Flowers("스파티필름","#세심한#사랑"))
        flowers.add(Flowers("브로멜리아드","#미래#즐기며#만족"))
        flowers.add(Flowers("산세베리아","#관용"))
        flowers.add(Flowers("페페로미아","#행운#사랑"))
        flowers.add(Flowers("스킨답서스","#우아한#심성"))
        flowers.add(Flowers("아레카야자","#승리#부활"))
        flowers.add(Flowers("아디안텀","#애교"))
        flowers.add(Flowers("선인장","#불타는#마음"))
        //추가
        flowers.add(Flowers("목련","#숭고한#정신#고귀한"))
        flowers.add(Flowers("나팔꽃","#기쁨#결속#기쁜소식"))
        flowers.add(Flowers("데이지","#평화#순수#순진"))
        flowers.add(Flowers("라일락","#우애#아름다움"))
        flowers.add(Flowers("매화","#결백#정조#충실"))
        flowers.add(Flowers("이끼","#모성애#고독#쓸쓸한"))
        flowers.add(Flowers("시클라멘","#수줍음#시기#질투"))
        flowerData.postValue(flowers)

        flowerPref.edit {
            for (i in 0..15){
                putString((i.toString()+"n"),flowers[i].name)
                putString((i.toString()+"s"),flowers[i].species)
            }
            commit()
        }

        return flowerData
    }
}