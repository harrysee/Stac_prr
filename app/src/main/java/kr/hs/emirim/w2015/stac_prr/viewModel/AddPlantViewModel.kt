package kr.hs.emirim.w2015.stac_prr.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kr.hs.emirim.w2015.stac_prr.Model.PlantModel
import kr.hs.emirim.w2015.stac_prr.Repository.PlantRepository

class AddPlantViewModel : ViewModel(){
    private var plantLiveData = MutableLiveData<PlantModel>()
    private var isComplate = MutableLiveData<Boolean>()
    val plantRepository = PlantRepository

    fun getShowPlant(docId:String): MutableLiveData<PlantModel> {
        viewModelScope.launch{
            plantLiveData = plantRepository.getPlantLiveData(docId)
        }
        Log.i("TAG", "getPlant: 정보 보내기"+plantLiveData)
        return plantLiveData
    }

    // 사진이 있을 때 호출
    fun insertPlant(docId: String, isEdit:Boolean?, photoURI: Uri?, docData:PlantModel): MutableLiveData<Boolean> {
        viewModelScope.launch (Dispatchers.IO) {
            Log.d("TAG", "insertPlant: plantUri"+photoURI)
            isComplate.postValue(false)
            val result = plantRepository.createPlantImg(docId,photoURI,docData,isEdit?:false)
            isComplate.postValue(result.value)
            Log.i("TAG", "insertPlant: 돌아옴"+isComplate)
        }
        return isComplate
    }
    // 사진이 없을때 호출
    fun insertPlantNimg(docData:PlantModel): MutableLiveData<Boolean> {
        Log.i("","식물 추가하기"+"생성")
        viewModelScope.launch {
            isComplate = plantRepository.createPlant(docData)
        }

        Log.i("TAG", "insertPlant: 돌아왔다"+isComplate.value)

        return isComplate
    }

    fun insertPlantEdit(docId: String?,docData:PlantModel): MutableLiveData<Boolean> {
        Log.i("","식물 추가하기"+"수정")
        viewModelScope.launch {
            isComplate = plantRepository.modifyPlant(docId,docData)
        }

        Log.i("TAG", "insertPlant: 돌아왔다"+isComplate.value)

        return isComplate
    }
}