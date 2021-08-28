package kr.hs.emirim.w2015.stac_prr.Fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_new_plant.*
import kr.hs.emirim.w2015.stac_prr.CustomDialog
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R


class NewPlantFragment : Fragment(){
    var db : FirebaseFirestore? = null
    //val android_id = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
    val FROM_ALBUM = 200
    lateinit var photoURI : Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_new_plant, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as MainActivity
        db = FirebaseFirestore.getInstance()

        // 이미지 화살표 눌렀을때
        img_btn_backhome.setOnClickListener(){
            val dir = CustomDialog(requireContext())
                .setMessage("작성중인 내용이 사라집니다\n취소하시겠습니까?")
                .setPositiveBtn("네"){
                    activity.fragmentChange_for_adapter(HomeFragment())
                }
                .setNegativeBtn("아니오"){}
                .show()
        }
        
        // 완료 눌렀을 때
        btn_completion.setOnClickListener{
            activity.fragmentChange_for_adapter(HomeFragment())
        }

        newplant_upload_btn.setOnClickListener{
            //앨범 열기
            val intent = Intent(Intent.ACTION_PICK)

            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            intent.type = "image/*"
            //intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, FROM_ALBUM)

        }
    }

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
