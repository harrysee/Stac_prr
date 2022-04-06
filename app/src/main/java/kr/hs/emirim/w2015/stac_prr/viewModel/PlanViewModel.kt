package kr.hs.emirim.w2015.stac_prr.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.launch
import kr.hs.emirim.w2015.stac_prr.Model.PlanModel
import kr.hs.emirim.w2015.stac_prr.Repository.PlanRepository

class PlanViewModel : ViewModel() {
    private var plansList = MutableLiveData<ArrayList<PlanModel>>()     // 여기가 데이터저장 배열
    private var DaysList = MutableLiveData<ArrayList<CalendarDay>>()     // 여기가 데이터저장 배열
    private val planRepository = PlanRepository
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    fun getDatePlans(date:String?): MutableLiveData<ArrayList<PlanModel>> {
        viewModelScope.launch {
            if (date != null) {
                plansList = planRepository.getDatePlan(date)
            }
        }
        return plansList
    }

    fun setChecked(docId:String,isChecked:Boolean){
        viewModelScope.launch {
            planRepository.checked(docId,isChecked)
        }
    }

    fun getAllPlans(): MutableLiveData<ArrayList<CalendarDay>> {
        viewModelScope.launch {
            DaysList = planRepository.getAllPlan()
        }
        return DaysList
    }
}