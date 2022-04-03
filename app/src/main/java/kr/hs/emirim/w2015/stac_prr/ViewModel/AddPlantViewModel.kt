package kr.hs.emirim.w2015.stac_prr.ViewModel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kr.hs.emirim.w2015.stac_prr.Model.PlantModel
import kr.hs.emirim.w2015.stac_prr.Repository.PlantRepository

class AddPlantViewModel : ViewModel(){
    private var plantLiveData = MutableLiveData<PlantModel>()
    private var isComplate = MutableLiveData<Boolean>()
    val plantRepository = PlantRepository

    fun getShowPlant(docId:String): MutableLiveData<PlantModel> {
        viewModelScope.launch {
            plantLiveData = plantRepository.getPlantLiveData(docId)
        }
        return plantLiveData
    }

    fun insertPlant(docId: String, isEdit:Boolean?, photoURI: Uri?, docData:PlantModel,isImg:Boolean): MutableLiveData<Boolean> {
        viewModelScope.launch {
            isComplate.value = true
            when(isImg){
                false ->{
                    val img = plantRepository.createPlantImg(photoURI,docData,isEdit?:false)
                    if(img.equals("")){ // 이미지 없으면
                        isComplate.value = false
                        return@launch
                    }
                    if (isEdit?:false){
                        plantRepository.modifyPlant(docId,docData,isImg)
                    }else{
                        plantRepository.createPlant(docData)
                    }
                }
                true ->{
                    if (isEdit?:false){
                        plantRepository.modifyPlant(docId,docData,isImg)
                    }else{
                        plantRepository.createPlant(docData)
                    }
                }
            }
        }
        return isComplate
    }

}