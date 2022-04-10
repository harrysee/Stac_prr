package kr.hs.emirim.w2015.stac_prr.viewModel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kr.hs.emirim.w2015.stac_prr.Model.NoticeModel
import kr.hs.emirim.w2015.stac_prr.Repository.NoticeRepository

class NoticeViewModel : ViewModel() {
    val count = MutableLiveData<Int>()
    var notices = MutableLiveData<ArrayList<NoticeModel>>()
    val noticeRep = NoticeRepository
    init {
        viewModelScope.launch {
            notices = noticeRep.getNoticesrep()
            Log.i("TAG", "공지사항: "+notices)
        }
    }

    fun getNoticeDot(): MutableLiveData<Int> {
        viewModelScope.launch(Dispatchers.IO) {
            async {
                val result = noticeRep.getNoticeDot()
                count.postValue(result.value)
                Log.i("TAG", "getNoticeDot: 공지사항 디비 "+result)
            }.await()
        }
        return count
    }

    @JvmName("getNotices1")
    fun getNotices(): MutableLiveData<ArrayList<NoticeModel>> {
        return notices
    }

}