package kr.hs.emirim.w2015.stac_prr

import android.graphics.Paint

class ItemModel {
    var items: ArrayList<ItemEntity?> = ArrayList()     // 여기가 데이터저장 배열

    // 각자 선택 메서드
    fun toggleEachItemClick(pos: Int) {
        items[pos]?.let {
            if(it.isChecked){
                it.isChecked = false
            }else if(!it.isChecked){
                it.isChecked = true
            }
        }
    }

    fun makeTestItems() {    //테스트 아이템
        items.clear()
        for (i in 0 until 100) {
            var item = ItemEntity()
            item.contents = "$i 번째 내용"
            item.isChecked = false
            items.add(item)
        }
    }

    inner class ItemEntity {     //체크되었는지랑 내용을 담은 클래스 생성
        var isChecked: Boolean = false
        var contents: String? = null
    }
}