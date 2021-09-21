package kr.hs.emirim.w2015.stac_prr.Fragment

import android.app.DatePickerDialog
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
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_new_journal.*
import kotlinx.android.synthetic.main.fragment_new_plant.*
import kotlinx.android.synthetic.main.fragment_plant_info.*
import kr.hs.emirim.w2015.stac_prr.Dialog.CustomDialog
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R
import java.text.SimpleDateFormat
import java.util.*


class NewPlantFragment : Fragment() {
    var db: FirebaseFirestore? = null

    //val android_id = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
    private val FROM_ALBUM = 200
    private var storage = FirebaseStorage.getInstance()
    private var storageRef = storage.reference
    private var photoURI: Uri? = null
    private val auth = Firebase.auth
    private var isEdit: Boolean? = null
    private var docId: String? = null
    private var imgUri: String? = null
    private val cal: Calendar = Calendar.getInstance()
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
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as MainActivity
        db = FirebaseFirestore.getInstance()
        val pcnt = pref.getInt("PlantCnt", 0)   // 처음 생성시 식물개수 0
        Log.d("TAG", "onViewCreated: ${auth.currentUser?.uid}")

        // 수정으로 인해서 호출됬을때 기본데이터 뿌리기
        if (isEdit == true) {
            Log.d("TAG,", "onViewCreated: 수정 > 식물정보 가져온 이미지 uri $imgUri")
            val backgound = view.findViewById<ImageButton>(R.id.newplant_upload_btn)
            Glide.with(requireContext()) //쓸곳
                .load(imgUri)  //이미지 받아올 경로
                .into(backgound)    // 받아온 이미지를 받을 공간

            db!!.collection("plant_info").document(docId!!)
                .get()
                .addOnSuccessListener {
                    Log.d("TAG", "onViewCreated: 식물정보 수정을 위한 기본데이터 가져옴")
                    newplant_name.hint = it["name"] as String? // 식물 이름 재설정
                    newplant_spacies.hint = it["specise"] as String?  // 식물 종류 재설정
                    val sdf = SimpleDateFormat("yyyy-MM-dd")
                    //일자수 차이 구하기
                    val timestamp = it["date"] as Timestamp
                    val d: Date = timestamp.toDate()
                    cal.time = d

                    //설정하기
                    newplant_date_btn.text = sdf.format(d)
                    newplant_spacies.setText(it["specise"] as String?)
                    newplant_temperature.setText(it["temperature"] as String?)
                    newplant_led.setText(it["led"] as String?)
                    newplant_water.setText(it["water"] as String?)
                    newplant_memo.setText(it["memo"] as String?)

                }.addOnFailureListener {
                    Log.d(it.toString(), "onViewCreated: 수정 > 식물정보를 가져오기 못함")
                }
            // 이름은 변경 못하게하기
            newplant_name.isEnabled = false
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
            var downloadUri: String? = null  // 다운로드 uri 저장변수
            val uid: String? = auth.uid

            val date: Date = cal.time
            // 올릴 필드 설정하기
            val newplant_name: EditText = view.findViewById(R.id.newplant_name)
            val newplant_spacies: EditText = view.findViewById(R.id.newplant_spacies)
            val newplant_led: EditText = view.findViewById(R.id.newplant_led)
            val newplant_water: EditText = view.findViewById(R.id.newplant_water)
            val newplant_temperature: EditText =
                view.findViewById(R.id.newplant_temperature)
            val newplant_memo: EditText = view.findViewById(R.id.newplant_memo)
            if (newplant_name.text == null || newplant_spacies.text == null) {
                Toast.makeText(requireContext(), "식물정보를 모두 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (photoURI != null) {
                val filename = "_" + System.currentTimeMillis()
                val imagesRef: StorageReference? = storageRef.child("info/" + filename)

                var file: Uri? = null
                try {
                    file = photoURI

                    // 잘올라갔으면 다운로드 url 가져오기
                    Log.d("TAG", "onViewCreated: 사진 uri ${photoURI}")
                    // 스토리지에 올리기
                    val uploadTask = imagesRef?.putFile(file!!)
                    Toast.makeText(requireContext(), "업로드중...", Toast.LENGTH_LONG).show()

                    uploadTask?.continueWithTask { task ->
                        Log.d("TAG", "onViewCreated: 새로운 식물 continue 들어옴")
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                Log.d("TAG", "onViewCreated: 사진 안올라감")
                                throw it
                            }
                        }
                        imagesRef.downloadUrl.addOnSuccessListener { task ->
                            downloadUri = task.toString()  // 다운로드 uri string으로 가져오기
                            Log.d(downloadUri.toString(), "onViewCreated: 사진 업로드 완료")

                            val docData = hashMapOf(
                                "name" to newplant_name.text.toString(),           //식물이름
                                "specise" to newplant_spacies.text.toString(),                  // 종류
                                "led" to newplant_led.text.toString(),                // 빛
                                "water" to newplant_water.text.toString(),              //급수주기
                                "temperature" to newplant_temperature.text.toString(),        // 온도
                                "memo" to newplant_memo.text.toString(),               //메모
                                "date" to Timestamp(date),   // 날짜
                                "imgUri" to downloadUri,        // 이미지 uri
                                "userId" to uid    // 식별가능한 유저 아이디
                            )
                            if (isEdit==false){// 콜렉션에 문서 생성하기
                                db!!.collection("plant_info").document()
                                    .set(docData)
                                    .addOnSuccessListener { Log.d("TAG", "파이어스토어 올라감") }
                                    .addOnFailureListener { e -> Log.w("TAG", "파이어스토어 업로드 오류", e) }

                                with(pref.edit()) {
                                    putInt("PlantCnt", pcnt + 1)
                                    commit()
                                }
                            }else{  // 수정일경우 업데이트
                                docId?.let { it1 ->
                                    val date: Date = cal.time
                                    db!!.collection("plant_info").document(it1)
                                        .update(mapOf(
                                            "specise" to newplant_spacies.text.toString(),                  // 종류
                                            "led" to newplant_led.text.toString(),                // 빛
                                            "water" to newplant_water.text.toString(),              //급수주기
                                            "temperature" to newplant_temperature.text.toString(),        // 온도
                                            "memo" to newplant_memo.text.toString(),               //메모
                                            "date" to Timestamp(date),   // 날짜
                                            "imgUri" to downloadUri,        // 이미지 uri
                                        ))
                                        .addOnSuccessListener {
                                            Log.d("TAG", "onViewCreated: 수정 성공 ")
                                        }.addOnCanceledListener {
                                            Log.d("TAG", "onViewCreated: 수정 성공 못함")
                                        }
                                }
                            }
                        }// 사진이 있으면 그냥 올리기
                    }// uploadtask end
                } catch (e: java.lang.Exception) {
                    Toast.makeText(requireContext(), "업로드 실패", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            } else {        // 사진이 없을경우
                val docData = hashMapOf(
                    "name" to newplant_name.text.toString(),           //식물이름
                    "specise" to newplant_spacies.text.toString(),                  // 종류
                    "led" to newplant_led.text.toString(),                // 빛
                    "water" to newplant_water.text.toString(),              //급수주기
                    "temperature" to newplant_temperature.text.toString(),        // 온도
                    "memo" to newplant_memo.text.toString(),               //메모
                    "date" to Timestamp(date),   // 날짜
                    "imgUri" to downloadUri,        // 이미지 uri
                    "userId" to uid    // 식별가능한 유저 아이디
                )
                if (isEdit==false){// 콜렉션에 문서 생성하기
                    db!!.collection("plant_info").document()
                        .set(docData)
                        .addOnSuccessListener { Log.d("TAG", "파이어스토어 올라감") }
                        .addOnFailureListener { e -> Log.w("TAG", "파이어스토어 업로드 오류", e) }

                    with(pref.edit()) {
                        putInt("PlantCnt", pcnt + 1)
                        commit()
                    }
                }else{  // 수정일경우 업데이트
                    docId?.let { it1 ->
                        val date: Date = cal.time
                        db!!.collection("plant_info").document(it1)
                            .update(mapOf(
                                "specise" to newplant_spacies.text.toString(),                  // 종류
                                "led" to newplant_led.text.toString(),                // 빛
                                "water" to newplant_water.text.toString(),              //급수주기
                                "temperature" to newplant_temperature.text.toString(),        // 온도
                                "memo" to newplant_memo.text.toString(),               //메모
                                "date" to Timestamp(date),   // 날짜
                            ))
                            .addOnSuccessListener {
                                Log.d("TAG", "onViewCreated: 수정 성공 ")
                            }.addOnCanceledListener {
                                Log.d("TAG", "onViewCreated: 수정 성공 못함")
                            }
                    }
                }
            }// 업로드 끝
            Toast.makeText(activity, "업로드 완료 !", Toast.LENGTH_LONG).show()
            Log.d("TAG", "onViewCreated: 파이어 업로드 완료")

            activity.fragmentChange_for_adapter(HomeFragment())
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

    // 사진 가져오기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data?.data != null) {
            try {
                photoURI = data.data!!
                val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, photoURI)
                newplant_upload_btn.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
