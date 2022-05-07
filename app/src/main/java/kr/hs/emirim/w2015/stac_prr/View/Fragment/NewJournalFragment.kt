package kr.hs.emirim.w2015.stac_prr.View.Fragment

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
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import kotlinx.android.synthetic.main.fragment_new_journal.*
import kr.hs.emirim.w2015.stac_prr.View.Dialog.CustomDialog
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R
import kr.hs.emirim.w2015.stac_prr.databinding.FragmentNewJournalBinding
import kr.hs.emirim.w2015.stac_prr.viewModel.AddJournalViewModel
import java.text.SimpleDateFormat
import java.util.*

class NewJournalFragment : Fragment() {
    private lateinit var nadapter: ArrayAdapter<String>
    private val FROM_ALBUM = 200
    private var photoURI: Uri? = null
    private var isEdit: Boolean = false
    private var docId: String? = null
    private var imgUri: String? = null
    private lateinit var binding : FragmentNewJournalBinding
    private val model by lazy{
        ViewModelProvider(requireActivity()).get(AddJournalViewModel::class.java)
    }
    val cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        isEdit = arguments?.getBoolean("isEdit")?:false
        docId = arguments?.getString("docId")
        imgUri = arguments?.getString("imgUri")
        binding = FragmentNewJournalBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as MainActivity

        // 등록된 식물 스피너 설정
        model.getPlantName().observe(requireActivity(), androidx.lifecycle.Observer {
            val plantnames = it
            nadapter = ArrayAdapter<String>(
                activity,
                R.layout.spinner_custom_name,
                plantnames
            )
            Log.d("TAG", "onViewCreated: 어댑터 완성")
            nadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.choiceSpinner.adapter = nadapter
            nadapter.notifyDataSetChanged()
        })

        // 수정일 경우 데이터 뿌리기
        if (isEdit == true) {
            model.getJournal(docId!!).observe(requireActivity(), androidx.lifecycle.Observer {
                val date = it.date as Timestamp
                val pos = nadapter.getPosition(it.name as String?)
                binding.newjournalDateBtn.text = SimpleDateFormat("yy-MM-dd").format(date.toDate())
                cal.time = date.toDate()
                binding.journalContent.setText(it.journal as String?)
                binding.choiceSpinner.setSelection(pos)

                if (imgUri != null && !imgUri.equals("")) {
                    Glide.with(requireContext())
                        .load(imgUri)
                        .fitCenter()
                        .into(binding.addImgBtn)
                }
            })
        }

        // 기본 날짜 세팅
        var y = 0
        var m = 0
        var d = 0
        y = cal[Calendar.YEAR]
        m = cal[Calendar.MONTH] + 1
        d = cal[Calendar.DAY_OF_MONTH]
        newjournal_date_btn.text = ("$y. $m. $d")

        // 날짜 선택기
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
            val date: Date = cal.time
            var msg = "업로드"
            // 올릴 필드 설정하기
            val journal_content: EditText = view.findViewById(R.id.journal_content)
            val choice_spinner: Spinner = view.findViewById(R.id.choice_spinner)
            val docData = mapOf<String, Any?>(
                "content" to binding.journalContent.text.toString(),
                "name" to binding.choiceSpinner.selectedItem.toString(),
                "date" to Timestamp(date),   // 날짜
                "bookmark" to false,
            )
            msg = when(isEdit){
                true ->{"업데이트"}
                else ->{"업로드"}
            }
            if (photoURI != null) { // 이미지 있을 때
                model.setJournalImg( docData, photoURI, docId, isEdit)
                Toast.makeText(requireContext(), msg+" 완료!", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "onViewCreated: 파이어 업로드 완료 사진 : journal")
            } else {        // 사진이 없을경우
                model.setJournal( docData,  docId, isEdit).observe(requireActivity(),
                    androidx.lifecycle.Observer {
                        showMsg(it?:false)
                    })
                Toast.makeText(requireContext(), msg+" 완료!", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "onViewCreated: 파이어 업로드 완료 : journal")
            }// 업로드 끝
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

    fun showMsg(r: Boolean){
        if (r){
            Toast.makeText(requireContext(),"업로드 완료",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(requireContext(),"업로드 실패",Toast.LENGTH_SHORT).show()
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

}

