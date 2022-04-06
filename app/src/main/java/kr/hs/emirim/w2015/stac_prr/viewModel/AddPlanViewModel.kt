package kr.hs.emirim.w2015.stac_prr.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.launch
import kr.hs.emirim.w2015.stac_prr.Repository.PlanRepository
import kr.hs.emirim.w2015.stac_prr.Repository.PlantRepository

class AddPlanViewModel:ViewModel() {
    private var Names = MutableLiveData<ArrayList<String>>()     // 여기가 데이터저장 배열
    private var DaysList = MutableLiveData<ArrayList<CalendarDay>>()     // 여기가 데이터저장 배열
    private val planRepository = PlanRepository
    private val plantRepository = PlantRepository
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    fun insertPlan(docData: Map<String,Any>){
        auth.uid?.let {
            db.collection("schedule").document(it).collection("plans").document()
                .set(docData)
                .addOnSuccessListener { Log.d("TAG", "파이어스토어 올라감 : schedule") }
                .addOnFailureListener { e -> Log.w("TAG", "파이어스토어 업로드 오류 : schedule", e) }
        }
    }

    fun getNames(): MutableLiveData<ArrayList<String>> {
        viewModelScope.launch {
            Names = plantRepository.getNames()
        }
        return Names
    }

}