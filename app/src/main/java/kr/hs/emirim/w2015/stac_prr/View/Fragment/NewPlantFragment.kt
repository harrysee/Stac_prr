package kr.hs.emirim.w2015.stac_prr.View.Fragment

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_new_journal.*
import kotlinx.android.synthetic.main.fragment_new_plant.*
import kotlinx.android.synthetic.main.fragment_plant_info.*
import kr.hs.emirim.w2015.stac_prr.View.Dialog.CustomDialog
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.Model.PlantModel
import kr.hs.emirim.w2015.stac_prr.R
import kr.hs.emirim.w2015.stac_prr.viewModel.AddPlantViewModel
import java.text.SimpleDateFormat
import java.util.*


class NewPlantFragment : Fragment() {
    var db: FirebaseFirestore? = null

    //val android_id = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
    private val FROM_ALBUM = 200
    private var isEdit: Boolean? = null
    private var docId: String? = null
    private var imgUri: String? = null
    private val model by lazy{ViewModelProvider(requireActivity()).get(AddPlantViewModel::class.java)}
    private val cal: Calendar = Calendar.getInstance()
    private var photoURI : Uri? = null
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_new_plant, container, false)
        isEdit = arguments?.getBoolean("isEdit")
        docId = arguments?.getString("docId")
        imgUri = arguments?.getString("imgUri")
        pref = context?.getSharedPreferences("pref", Context.MODE_PRIVATE)!!
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) //키보드 열린상태 스크롤
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as MainActivity
        db = FirebaseFirestore.getInstance()
        val pcnt = pref.getInt("PlantCnt", 0)   // 처음 생성시 식물개수 0
        newplant_input_scroll.bringToFront()    // 스크롤 위치 맨위로

        // 수정으로 인해서 호출됬을때 기본데이터 뿌리기
        if (isEdit == true) {
            Log.d("TAG,", "onViewCreated: 수정 > 식물정보 가져온 이미지 uri $imgUri")
            val backgound = view.findViewById<ImageButton>(R.id.newplant_upload_btn)
            Glide.with(requireContext()) //쓸곳
                .load(imgUri)  //이미지 받아올 경로
                .into(backgound)    // 받아온 이미지를 받을 공간

            model.getShowPlant(docId!!).observe(requireActivity(), androidx.lifecycle.Observer {
                newplant_name.hint = it.name as String? // 식물 이름 재설정
                newplant_spacies.hint = it.specise as String?  // 식물 종류 재설정
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                cal.time = it.date

                //설정하기
                newplant_date_btn.text = sdf.format(it.date)
                newplant_spacies.setText(it.specise as String?)
                newplant_temperature.setText(it.temperate as String?)
                newplant_led.setText(it.led as String?)
                newplant_water.setText(it.water as String?)
                newplant_memo.setText(it.memo as String?)
            })
            // 이름은 변경 못하게하기
            newplant_name.isEnabled = false
        }
        // 이름 변경 시도 시 이름변경 안되는거 안내
        newplant_name.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                Toast.makeText(requireContext(), "이름은 나중에 수정할수 없습니다", Toast.LENGTH_SHORT).show()
            }
        }
        // 이미지 화살표 눌렀을때
        img_btn_backhome.setOnClickListener() {
            val dir = CustomDialog(requireContext())
                .setMessage("작성중인 내용이 사라집니다\n취소하시겠습니까?")
                .setPositiveBtn("네") {
                    activity.fragmentChange_for_adapter(HomeFragment())
                }
                .setNegativeBtn("아니오") {}
                .show()
        }

        // 완료 눌렀을 때 - 올리기
        btn_completion.setOnClickListener {
            // 네트워크 상태 확인하고 연결 안됐을때는 토스트 띄우기
            Log.d(TAG, "onViewCreated: 사진uri "+ photoURI)
            var downloadUri: String? = null  // 다운로드 uri 저장변수
            val date: Date = cal.time
            // 올릴 필드 설정하기
            if (newplant_name.text == null || newplant_spacies.text == null) {
                Toast.makeText(requireContext(), "식물정보를 모두 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Toast.makeText(activity, "업로드 중...", Toast.LENGTH_LONG).show()
            val docData = PlantModel(
                newplant_name.text.toString(),
                date,   // 날짜
                downloadUri?:"",
                newplant_led.text.toString(),
                newplant_memo.text.toString(),
                newplant_spacies.text.toString(),
                newplant_temperature.text.toString(),
                newplant_water.text.toString(),
                dviceNum = ""
            )
            // 업로드 하기
            val message = when(isEdit){
                true -> { "업데이트" }
                else -> { "업로드" }
            }
            if (photoURI != null) { // 이미지 있을 때
                model.insertPlant(docId!!,isEdit,photoURI,docData).observe(requireActivity(),
                    androidx.lifecycle.Observer {
                        getResult(it?:false,message)
                        activity.fragmentChange_for_adapter(HomeFragment())
                    })
            } else {        // 사진이 없을경우
                model.insertPlantNimg(docId!!,isEdit,photoURI,docData).observe(requireActivity(),
                    androidx.lifecycle.Observer {
                        getResult(it?:false,message)
                        activity.fragmentChange_for_adapter(HomeFragment())
                    })
            }// 업로드 끝

        }

        newplant_upload_btn.setOnClickListener {
            //앨범 열기
            val intent = Intent(Intent.ACTION_PICK)

            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            intent.type = "image/*"
            //intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, FROM_ALBUM)

        }

        // 날짜 선택 다이얼로그
        newplant_date_btn.setOnClickListener {
            val setDateListener =
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, month)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    newplant_date_btn.text = "${year}-${month + 1}-${dayOfMonth}"
                }

            Log.d("TAG", "onViewCreated: 현재 시간 :${cal.time}")
            val now = System.currentTimeMillis() - 1000
            val datepicker = DatePickerDialog(
                activity,
                R.style.DatePicker,
                setDateListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH - 1),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            datepicker.datePicker.spinnersShown = true
            datepicker.datePicker.calendarViewShown = true
            datepicker.datePicker.maxDate = now
            datepicker.show()
        }
    }

    fun getResult(r:Boolean,msg:String?){
        if(r){
            Toast.makeText(activity, msg+" 완료 !", Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(activity, msg+" 실패 !", Toast.LENGTH_LONG).show()
        }
        val pCnt = pref.getInt("PlantCnt",0)
        with(pref.edit()){
            this.putInt("PlantCnt",pCnt+1)
            commit()
        }
        Toast.makeText(activity, "업로드 완료 !", Toast.LENGTH_LONG).show()
        Log.d("TAG", "onViewCreated: 파이어 업로드 완료")
    }
    // 사진 가져오기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onViewCreated444: 사진uri "+ data?.data)

        if (data?.data != null) {
            try {
                Log.d(TAG, "onViewCreated111: 사진uri "+ photoURI)
                photoURI = data.data!!
                val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, photoURI)
                newplant_upload_btn.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
