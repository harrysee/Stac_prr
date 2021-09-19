package kr.hs.emirim.w2015.stac_prr.Adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kr.hs.emirim.w2015.stac_prr.DataClass.JournalData
import kr.hs.emirim.w2015.stac_prr.Fragment.NewJournalFragment
import kr.hs.emirim.w2015.stac_prr.Dialog.JournalDialog
import kr.hs.emirim.w2015.stac_prr.R

class JournalAdapter(private val context: Context, private val activity: FragmentActivity?) :
    RecyclerView.Adapter<JournalAdapter.ViewHolder>() {
        var datas = mutableListOf<JournalData>()
        val storage = FirebaseStorage.getInstance()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(this.context).inflate(R.layout.journal_item, parent,false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = datas.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(datas[position])


        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val txtName: TextView = itemView.findViewById(R.id.journal_title)
            private val txtJournal: TextView = itemView.findViewById(R.id.journal_content)
            private val journalimg : ImageView = itemView.findViewById(R.id.journal_img)
            private val journaldate : TextView = itemView.findViewById(R.id.journal_item_date)

            fun bind(item: JournalData) {
                txtName.text = item.name
                txtJournal.text = item.journal
                journaldate.text = item.date

                if (item.imgUri != null){
                    Log.d("TAG", "bind: 일지어댑터 사진 null아님 : ${item.imgUri}")
                    Glide.with(context)
                        .load(item.imgUri)
                        .fitCenter()
                        .into(journalimg)
                }
                itemView.setOnClickListener{
                    val dir = JournalDialog(context)
                        .setTitle(item.name)
                        .setMessage(item.journal)
                        .setImg(item.imgUri)
                        .setEditBtn("수정"){
                            Log.d("TAG", "bind: 수정 클릭됨 일지")
                            val fragment = NewJournalFragment(); // Fragment 생성
                            val bundle = Bundle()
                            bundle.putBoolean("isEdit",true)
                            bundle.putString("docId", item.docId)
                            bundle.putString("imgUri", item.imgUri)
                            fragment.arguments = bundle
                            Log.i(bundle.toString(), "onViewCreated: bundle")
                            //activity.fragmentChange_for_adapter(AddPlanFragment())
                            Log.d(fragment.arguments.toString(), "plus버튼 클릭됨")
                            activity?.supportFragmentManager!!.beginTransaction()
                                .replace(R.id.container, fragment)
                                .commit()
                        }
                        .setDeleteBtn("삭제"){
                            Log.d("TAG", "bind: 삭제 클릭됨 일지")
                            val auth = FirebaseAuth.getInstance().currentUser
                            val db = FirebaseFirestore.getInstance()
                            if (auth != null) {
                                db.collection("journals")
                                    .document(auth.uid.toString())
                                    .collection("journal")
                                    .document(item.docId)
                                    .delete()
                                    .addOnSuccessListener {
                                        //각 아이템 삭제하기
                                        Toast.makeText(context,"정상적으로 삭제되었습니다",Toast.LENGTH_SHORT).show()
                                        notifyDataSetChanged()
                                    }// journal success end
                            }//auth null
                        }
                        .show()
                }
            }
        }
    }
