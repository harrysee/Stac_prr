package kr.hs.emirim.w2015.stac_prr.Controller.Fragment

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_new_journal.*
import kr.hs.emirim.w2015.stac_prr.Dialog.CustomDialog
import kr.hs.emirim.w2015.stac_prr.Controller.Activity.MainActivity
import kr.hs.emirim.w2015.stac_prr.R
import java.text.SimpleDateFormat
import java.util.*


class NewJournalFragment : Fragment() {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    private var storage = FirebaseStorage.getInstance()
    private var storageRef = storage.reference
    private lateinit var nadapter: ArrayAdapter<String>
    private val FROM_ALBUM = 200
    private var photoURI: Uri? = null
    private var isEdit: Boolean? = false
    private var docId: String? = null
    private var imgUri: String? = null
    val cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        isEdit = arguments?.getBoolean("isEdit")
        docId = arguments?.getString("docId")
        imgUri = arguments?.getString("imgUri")
        val view = inflater.inflate(R.layout.fragment_new_journal, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as MainActivity

        // 등록된 식물 스피너 설정
        val plantnames = getNames()
        nadapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_custom_name,
            plantnames
        )
        Log.d("TAG", "onViewCreated: 어댑터 완성")
        nadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        choice_spinner.adapter = nadapter
        nadapter.notifyDataSetChanged()
        if (isEdit == true) {
            putData()
        }
        var y = 0
        var m = 0
        var d = 0

        y = cal[Calendar.YEAR]
        m = cal[Calendar.MONTH] + 1
        d = cal[Calendar.DAY_OF_MONTH]

        newjournal_date_btn.text = ("$y. $m. $d")

        newjournal_date_btn.setOnClickListener {
            val setDateListener =
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, month)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    newjournal_date_btn.text = "${year}. ${month + 1}. ${dayOfMonth}"
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

        R.style.AlertDialog_AppCompat

        // 이미지 화살표 눌렀을때
        addjournal_pass_btn.setOnClickListener() {
            val dir = CustomDialog(requireContext())
                .setMessage("작성중인 내용이 사라집니다\n취소하시겠습니까?")
                .setPositiveBtn("네") {
                    activity.fragmentChange_for_adapter(JournalFragment())
                }
                .setNegativeBtn("아니오") {}
                .show()
        }

        // 완료 눌렀을 때
        addjournal_complate_btn.setOnClickListener {
            Toast.makeText(requireContext(), "업로드 중..", Toast.LENGTH_SHORT)
            // 파이어스토어에 데이터 저장
            val uid: String = auth.uid!!
            val date: Date = cal.time
            // 올릴 필드 설정하기
            val journal_content: EditText = view.findViewById(R.id.journal_content)
            val choice_spinner: Spinner = view.findViewById(R.id.choice_spinner)
            var downloadUri: String? = null // 다운로드 uri 저장변수

            if (photoURI != null) {// 파일 업로드
                val filename = "_" + System.currentTimeMillis()
                val imagesRef: StorageReference? = storageRef.child("journal/" + filename)

                //스토리지 업로드
                var file: Uri? = null
                try {
                    file = photoURI!!
                    Log.d("TAG", "onViewCreated: 사진 URI : $file")
                    val uploadTask = imagesRef?.putFile(file)
                    Toast.makeText(requireContext(), "업로드중...", Toast.LENGTH_LONG).show()

                    uploadTask?.continueWithTask { task ->
                        Log.d("TAG", "onViewCreated: 새로운 일지 continue 들어옴")
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                Log.d("TAG", "onViewCreated: 일지 안올라감")
                                throw it
                            }
                        }
                        imagesRef.downloadUrl.addOnSuccessListener { task ->
                            Log.d("TAG", "onViewCreated: 다운로드 Uri : ${task.toString()}")
                            downloadUri = task.toString()
                            val docData = hashMapOf(
                                "content" to journal_content.text.toString(),
                                "name" to choice_spinner.selectedItem.toString(),
                                "date" to Timestamp(date),   // 날짜
                                "imgUri" to downloadUri
                            )
                            // 콜렉션에 문서 생성하기
                            if (isEdit == true){    //수정일경우 업데이트
                                if (uid != null) {
                                    db!!.collection("journals").document(uid).collection("journal").document(docId!!)
                                        .update(mapOf(
                                            "content" to journal_content.text.toString(),
                                            "name" to choice_spinner.selectedItem.toString(),
                                            "date" to Timestamp(date),   // 날짜
                                            "imgUri" to downloadUri
                                        ))
                                        .addOnSuccessListener { Log.d("TAG", "파이어스토어 올라감 : journal") }
                                        .addOnFailureListener { e -> Log.w("TAG", "파이어스토어 업로드 오류 : journal", e) }
                                }
                            }else{  //아닐경우 업로드
                                if (uid != null) {
                                    db!!.collection("journals").document(uid).collection("journal").document()
                                        .set(docData)
                                        .addOnSuccessListener { Log.d("TAG", "파이어스토어 올라감 : journal") }
                                        .addOnFailureListener { e -> Log.w("TAG", "파이어스토어 업로드 오류 : journal", e) }
                                }
                            }//else end
                        }//imagesRef end
                    }//continueWithTask end
                } catch (e: java.lang.Exception) {
                    Log.d("TAG", "onViewCreated: 사진 안들어감 : ${e.toString()}")
                }
            } else if (photoURI == null) {  //사진이 들어잇는경우 업로드
                //파이어베이스 업로드
                val docData = hashMapOf(
                    "content" to journal_content.text.toString(),
                    "name" to choice_spinner.selectedItem.toString(),
                    "date" to Timestamp(date),   // 날짜
                    "imgUri" to downloadUri
                )
                // 콜렉션에 문서 생성하기
                if (isEdit == true){    //수정일경우 업데이트
                    if (uid != null) {
                        db!!.collection("journals").document(uid).collection("journal").document(docId!!)
                            .update(mapOf(
                                "content" to journal_content.text.toString(),
                                "name" to choice_spinner.selectedItem.toString(),
                                "date" to Timestamp(date),   // 날짜
                            ))
                            .addOnSuccessListener { Log.d("TAG", "파이어스토어 올라감 : journal") }
                            .addOnFailureListener { e -> Log.w("TAG", "파이어스토어 업로드 오류 : journal", e) }
                    }
                }else{  //아닐경우 업로드
                    if (uid != null) {
                        db!!.collection("journals").document(uid).collection("journal").document()
                            .set(docData)
                            .addOnSuccessListener { Log.d("TAG", "파이어스토어 올라감 : journal") }
                            .addOnFailureListener { e -> Log.w("TAG", "파이어스토어 업로드 오류 : journal", e) }
                    }
                }
                Toast.makeText(requireContext(), "업로드 완료!", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "onViewCreated: 파이어 업로드 완료 : journal")
            }

            activity.fragmentChange_for_adapter(JournalFragment())
        }

        add_img_btn.setOnClickListener {
            //앨범 열기
            val intent = Intent(Intent.ACTION_PICK)

            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            intent.type = "image/*"
            //intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, FROM_ALBUM)

        }

    }

    // 사진 가져오기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data?.data != null) {
            try {
                photoURI = data.data!!
                val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, photoURI)
                add_img_btn.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        Log.d("TAG", "onActivityResult: 파일 업로드 : $photoURI")

    }

    //수정일경우 데이터 뿌리기
    fun putData() {
        val uid: String = auth.uid!!
        if (uid != null) {
            db!!.collection("journals")
                .document(uid).collection("journal").document(docId!!)
                .get()
                .addOnSuccessListener {
                    val date = it["date"] as Timestamp
                    val pos = nadapter.getPosition(it["name"] as String?)
                    newjournal_date_btn.text = SimpleDateFormat("yy-MM-dd").format(date.toDate())
                    cal.time = date.toDate()
                    journal_content.setText(it["content"] as String?)
                    choice_spinner.setSelection(pos)

                    if (imgUri != null) {
                        Glide.with(requireContext())
                            .load(imgUri)
                            .fitCenter()
                            .into(add_img_btn)
                    }
                }
        }
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
                nadapter.notifyDataSetChanged()
            }.addOnFailureListener {
                Log.d("TAG", "getNames: spinner 식물 이름들 보여주기 실패")
            }
        return names
    }
}

