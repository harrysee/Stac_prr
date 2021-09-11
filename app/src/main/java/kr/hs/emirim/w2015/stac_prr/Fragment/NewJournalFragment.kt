package kr.hs.emirim.w2015.stac_prr.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_add_plan.*
import kotlinx.android.synthetic.main.fragment_new_journal.*
import kr.hs.emirim.w2015.stac_prr.CustomDialog
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R
import java.util.*


class NewJournalFragment : Fragment() {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_new_journal, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cal = Calendar.getInstance()
        var y = 0
        var m = 0
        var d = 0

        y = cal[Calendar.YEAR]
        m = cal[Calendar.MONTH] + 1
        d = cal[Calendar.DAY_OF_MONTH]

        now_date.text = ("$y. $m. $d")

        val activity = activity as MainActivity
        R.style.AlertDialog_AppCompat

        // 이미지 화살표 눌렀을때
        addjournal_pass_btn.setOnClickListener(){
            val dir = CustomDialog(requireContext())
                .setMessage("작성중인 내용이 사라집니다\n취소하시겠습니까?")
                .setPositiveBtn("네"){
                    activity.fragmentChange_for_adapter(JournalFragment())
                }
                .setNegativeBtn("아니오"){}
                .show()
        }

        // 완료 눌렀을 때
        addjournal_complate_btn.setOnClickListener{
            Toast.makeText(requireContext(),"업로드 중..", Toast.LENGTH_SHORT)
            // 파이어스토어에 데이터 저장
            val uid: String = auth.uid!!
            val date: Date = cal.time
            // 올릴 필드 설정하기
            val journal_content : EditText = view.findViewById(R.id.journal_content)
            val choice_spinner : Spinner = view.findViewById(R.id.choice_spinner)

            val docData = hashMapOf(
                "content" to journal_content.text,
                "name" to choice_spinner.selectedItem.toString(),
                "date" to Timestamp(date),   // 날짜
            )
            // 콜렉션에 문서 생성하기
            db!!.collection("journals").document(uid).collection("journal").document()
                .set(docData)
                .addOnSuccessListener { Log.d("TAG", "파이어스토어 올라감 : journal") }
                .addOnFailureListener { e -> Log.w("TAG", "파이어스토어 업로드 오류 : journal", e) }

            Toast.makeText(requireContext(), "업로드 완료!", Toast.LENGTH_LONG).show()
            Log.d("TAG", "onViewCreated: 파이어 업로드 완료 : journal")
            activity.fragmentChange_for_adapter(JournalFragment())
        }

        // 스피너 설정
        val plantnames = getNames()
        var nadapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_custom_name,
            plantnames
        )
        Log.d("TAG", "onViewCreated: 어댑터 완성")
        nadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        choice_spinner.adapter = nadapter
        nadapter.notifyDataSetChanged()

    }
    fun getNames(): ArrayList<String?> {
        val auth = Firebase.auth.currentUser
        val names = ArrayList<String?>()

        db.collection("plant_info")
            .whereEqualTo("userId", auth?.uid)
            .get()
            .addOnSuccessListener {
                for (doc in it) {
                    names.add(doc["name"] as String?)
                }
            }.addOnFailureListener {
                Log.d("TAG", "getNames: spinner 식물 이름들 보여주기 실패")
            }
        return names
    }
}