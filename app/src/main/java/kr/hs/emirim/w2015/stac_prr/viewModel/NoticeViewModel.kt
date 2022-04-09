package kr.hs.emirim.w2015.stac_prr.viewModel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.hs.emirim.w2015.stac_prr.Model.NoticeModel
import kr.hs.emirim.w2015.stac_prr.Repository.NoticeRepository

class NoticeViewModel : ViewModel() {
    val isComplate = MutableLiveData<Boolean>()
    var notices = MutableLiveData<ArrayList<NoticeModel>>()
    val noticeRep = NoticeRepository

    fun getNoticeDot(context:Activity?): MutableLiveData<Boolean> {
        val result = noticeRep.getNoticeDot(context)
        isComplate.postValue(result.value)
        return isComplate
    }

    @JvmName("getNotices1")
    fun getNotices(): MutableLiveData<ArrayList<NoticeModel>> {
        notices = noticeRep.getNoticesrep()
        return notices
    }

}