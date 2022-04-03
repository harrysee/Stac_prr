package kr.hs.emirim.w2015.stac_prr.Model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

data class PlanModel (
    var isChecked: Boolean = false,
    var name : String? = null,
    var contents: String? = null,
    var memo : String? = null,
    var docId : String=""
)