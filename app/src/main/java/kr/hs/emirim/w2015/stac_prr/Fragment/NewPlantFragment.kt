package kr.hs.emirim.w2015.stac_prr.Fragment

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_new_journal.*
import kotlinx.android.synthetic.main.fragment_new_plant.*
import kr.hs.emirim.w2015.stac_prr.CustomDialog
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R
import java.util.*


class NewPlantFragment : Fragment() {
    var db: FirebaseFirestore? = null

    //val android_id = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
    private val FROM_ALBUM = 200
    private var storage = FirebaseStorage.getInstance()
    private var storageRef = storage.reference
    private lateinit var photoURI: Uri
    private val auth = Firebase.auth
    private val cal: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_new_plant, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as MainActivity
        db = FirebaseFirestore.getInstance()
        Log.d("TAG", "onViewCreated: ${auth.currentUser?.uid}")
        
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
            val filename = "_" + System.currentTimeMillis()
            val imagesRef: StorageReference? = storageRef.child("info/" + filename)
            var downloadUri: String  // 다운로드 uri 저장변수

            if(photoURI.path.equals("")){
                Toast.makeText(requireContext(), "사진을 업로드하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            var file: Uri? = null
            try {
                file = photoURI
            }catch (e : java.lang.Exception){
                Toast.makeText(requireContext(), "이미지를 업로드하세요", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            Log.d("TAG", "onViewCreated: 사진 uri ${photoURI}")
            // 스토리지에 올리기
            val uploadTask = imagesRef?.putFile(file!!)
            Toast.makeText(requireContext(), "업로드중...", Toast.LENGTH_LONG).show()

            // 잘올라갔으면 다운로드 url 가져오기
            uploadTask?.continueWithTask { task ->
                Log.d("TAG", "onViewCreated: 새로운 식물 continue 들어옴")
                if (!task.isSuccessful) {
                    task.exception?.let {
                        Log.d("TAG", "onViewCreated: 사진 안올라감")
                        throw it
                    }
                }
                val uid: String? = auth.uid
                imagesRef.downloadUrl.addOnSuccessListener { task ->
                    downloadUri = task.toString()  // 다운로드 uri string으로 가져오기
                    Log.d(downloadUri.toString(), "onViewCreated: 사진 업로드 완료")

                    val date : Date= cal.time
                    // 올릴 필드 설정하기
                    val newplant_name : EditText =view.findViewById(R.id.newplant_name)
                    val newplant_spacies : EditText =view.findViewById(R.id.newplant_spacies)
                    val newplant_led : EditText =view.findViewById(R.id.newplant_led)
                    val newplant_water : EditText =view.findViewById(R.id.newplant_water)
                    val newplant_temperature : EditText =view.findViewById(R.id.newplant_temperature)
                    val newplant_memo : EditText =view.findViewById(R.id.newplant_memo)

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
                    // 콜렉션에 문서 생성하기
                    db!!.collection("plant_info").document()
                        .set(docData)
                        .addOnSuccessListener { Log.d("TAG", "파이어스토어 올라감") }
                        .addOnFailureListener { e -> Log.w("TAG", "파이어스토어 업로드 오류", e) }


                    Toast.makeText(activity, "업로드 완료 !", Toast.LENGTH_LONG).show()
                    Log.d("TAG", "onViewCreated: 파이어 업로드 완료")
                }
            }// uploadtask end
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
                val setDateListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                        cal.set(Calendar.YEAR,year)
                        cal.set(Calendar.MONTH,month)
                        cal.set(Calendar.DAY_OF_MONTH,dayOfMonth)
                        newplant_date_btn.text = "${year}-${month+1}-${dayOfMonth}"
                    }

                Log.d("TAG", "onViewCreated: 현재 시간 :${cal.time}")
                val now = System.currentTimeMillis() - 1000
                val datepicker = DatePickerDialog(
                    activity,
                    R.style.DatePicker,
                    setDateListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH-1),
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
