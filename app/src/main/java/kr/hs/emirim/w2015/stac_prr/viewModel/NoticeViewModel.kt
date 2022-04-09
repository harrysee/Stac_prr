package kr.hs.emirim.w2015.stac_prr.viewModel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.hs.emirim.w2015.stac_prr.Model.NoticeModel
import kr.hs.emirim.w2015.stac_prr.Repository.NoticeRepository

class NoticeViewModel : ViewModel() {
    val isComplate = MutableLiveData<Boolean>()
    var notices = MutableLiveData<ArrayList<NoticeModel>>()
    val noticeRep = NoticeRepository
    init {
        viewModelScope.launch {
            notices = noticeRep.getNoticesrep()
            Log.i("TAG", "공지사항: "+notices)
        }
    }

    fun getNoticeDot(context:Activity?): MutableLiveData<Boolean> {
        viewModelScope.launch(Dispatchers.IO) {
            val result = noticeRep.getNoticeDot(context)
            isComplate.postValue(result)
            Log.i("TAG", "getNoticeDot: 공지사항 디비 "+result)
        }
        return isComplate
    }

    @JvmName("getNotices1")
    fun getNotices(): MutableLiveData<ArrayList<NoticeModel>> {
        return notices
    }

}