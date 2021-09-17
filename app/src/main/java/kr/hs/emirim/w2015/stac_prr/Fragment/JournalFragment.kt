package kr.hs.emirim.w2015.stac_prr.Fragment

import android.R.attr.button
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.app.NotificationCompat.getColor
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_journal.*
import kotlinx.android.synthetic.main.fragment_plant_info.*
import kr.hs.emirim.w2015.stac_prr.Adapter.JournalAdapter
import kr.hs.emirim.w2015.stac_prr.Adapter.JournalTabAdapter
import kr.hs.emirim.w2015.stac_prr.JournalData
import kr.hs.emirim.w2015.stac_prr.R
import java.text.SimpleDateFormat


class JournalFragment : Fragment() {
    lateinit var journalTabAdapter: JournalTabAdapter
    private val datas = mutableListOf<JournalData>()
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_journal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val journalAdapter = JournalAdapter(requireContext())
        initRecycler(journalAdapter)
        journal_recycler.setOnScrollListener(onScrollListener)

        fab.setOnClickListener(){
            val fragment = NewJournalFragment(); // Fragment 생성
            val bundle = Bundle()
            fragment.arguments = bundle
            Log.i(bundle.toString(), "onViewCreated: bundle")
            //activity.fragmentChange_for_adapter(AddPlanFragment())
            Log.d(fragment.arguments.toString(), "plus버튼 클릭됨")
            activity?.supportFragmentManager!!.beginTransaction()
                .replace(R.id.container, fragment)
                .commit()
        }

        up_btn.setOnClickListener{
            journal_recycler.scrollToPosition(0)
        }
        down_btn.setOnClickListener{
            journal_recycler.scrollToPosition(journalAdapter.itemCount-1)
        }



        plant_name_btn1.setOnClickListener() {
            plant_name_btn1.isSelected = !plant_name_btn1.isSelected
            if(plant_name_btn1.isSelected){
                plant_name_btn2.isSelected = !plant_name_btn1.isSelected
                plant_name_btn3.isSelected = !plant_name_btn1.isSelected
                plant_name_btn4.isSelected = !plant_name_btn1.isSelected
            }
        }
        plant_name_btn2.setOnClickListener() {
            plant_name_btn2.isSelected = !plant_name_btn2.isSelected
            if(plant_name_btn2.isSelected){
                plant_name_btn1.isSelected = !plant_name_btn2.isSelected
                plant_name_btn3.isSelected = !plant_name_btn2.isSelected
                plant_name_btn4.isSelected = !plant_name_btn2.isSelected
            }
        }
        plant_name_btn3.setOnClickListener() {
            plant_name_btn3.isSelected = !plant_name_btn3.isSelected
            if(plant_name_btn3.isSelected){
                plant_name_btn1.isSelected = !plant_name_btn3.isSelected
                plant_name_btn2.isSelected = !plant_name_btn3.isSelected
                plant_name_btn4.isSelected = !plant_name_btn3.isSelected
            }
        }
        plant_name_btn4.setOnClickListener() {
            plant_name_btn4.isSelected = !plant_name_btn4.isSelected
            if(plant_name_btn4.isSelected){
                plant_name_btn1.isSelected = !plant_name_btn4.isSelected
                plant_name_btn2.isSelected = !plant_name_btn4.isSelected
                plant_name_btn3.isSelected = !plant_name_btn4.isSelected
            }
        }

    }


    val onScrollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy);
            if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                fab.hide()
            } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                fab.show()
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            fab.show()
        }
    }

    private fun initRecycler(journalAdapter : JournalAdapter) {
        journal_recycler.adapter = journalAdapter

        // 일지 목록 리사이클 설정
        val obj = object :JournalTabAdapter.ItemClickListener{
            override fun onItemClick(position: String) {
                db.collection("journals")
                    .document(auth.uid.toString())
                    .collection("journal")
                    .whereEqualTo("name",position)
                    .get()
                    .addOnSuccessListener {
                        Log.d("", "makeTestItems: 해당 날짜 데이터 가져오기 성공")
                        for (document in it){
                            datas.add(JournalData(document["name"] as String,document["content"] as String,SimpleDateFormat("yy-MM-dd").format(document["date"]), document["imgUri"] as String?))
                        }
                    }
                Log.i("파이어베이스데이터", datas.toString());
                journalAdapter.datas = datas
                journalAdapter.notifyDataSetChanged()
            }
        }

  /*      datas.add(JournalData(name = "초록이", journal = "일지", date="날짜"))
        datas.add(JournalData(name = "무럭이", journal = "일지", date="날짜"))
        datas.add(JournalData(name = "완전멋진이름", journal = "일지", date="날짜"))
        datas.add(JournalData(name = "초록이", journal = "일지", date="날짜"))
        datas.add(JournalData(name = "무럭이", journal = "일지", date="날짜"))
        datas.add(JournalData(name = "초록이", journal = "일지", date="날짜"))
        datas.add(JournalData(name = "무럭이", journal = "일지", date="날짜"))
        datas.add(JournalData(name = "완전멋진이름", journal = "일지", date="날짜"))
        datas.add(JournalData(name = "초록이", journal = "일지", date="날짜"))
        datas.add(JournalData(name = "무럭이", journal = "일지", date="날짜"))
        datas.add(JournalData(name = "초록이", journal = "일지", date="날짜"))
        datas.add(JournalData(name = "무럭이", journal = "일지", date="날짜"))
        datas.add(JournalData(name = "완전멋진이름", journal = "일지", date="날짜"))
        datas.add(JournalData(name = "초록이", journal = "일지", date="날짜"))
        datas.add(JournalData(name = "무럭이", journal = "일지", date="날짜"))*/



    }



}


