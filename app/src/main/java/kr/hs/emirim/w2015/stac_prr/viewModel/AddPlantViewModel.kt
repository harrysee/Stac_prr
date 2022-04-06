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
        viewModelScope.launch(Dispatchers.IO) {
            plantLiveData = plantRepository.getPlantLiveData(docId)
        }
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
    fun insertPlantNimg(docId: String?, isEdit:Boolean?, photoURI: Uri?, docData:PlantModel): MutableLiveData<Boolean> {
        viewModelScope.launch(Dispatchers.IO) {
            isComplate.postValue(false)
            var result = when(isEdit){
                true->{plantRepository.createPlant(docData)}
                else->{plantRepository.modifyPlant(docId,docData)}
            }
            Log.i("TAG", "insertPlant: 돌아왔다"+result.value)
            isComplate.postValue(result.value)
        }
        return isComplate
    }
}