package kr.hs.emirim.w2015.stac_prr.Adapter

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kr.hs.emirim.w2015.stac_prr.Model.ItemModel
import kr.hs.emirim.w2015.stac_prr.databinding.PlanItemViewBinding
import java.text.SimpleDateFormat
import java.util.*

class PlanAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: ItemModel? = ItemModel()
    var date : String = SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().time)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d("TAG", "onCreateViewHolder: 일정 어댑터 실행됨")
        var binding = PlanItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemHolder) {
            Log.d("TAG", "onBindViewHolder: 일정어댑터 아이템: ${items?.items?.get(position)}")
            holder.bind(items?.items?.get(position))
        }

    }
    // 개수 반환
    override fun getItemCount(): Int {
        items?.items?.let {
            return it.size
        }
        return 0
    }
    
    companion object {
        // 작성했던 레이아웃 bind 가져오기
        class ItemHolder(var binding: PlanItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
            var item: ItemModel.ItemEntity? = null
            val auth = FirebaseAuth.getInstance()
            val db = FirebaseFirestore.getInstance()

            /*init {  // 체크박스가 온클릭 되면 바로 리스너 이동시키기
                binding.checkBox.setOnCheckedChangeListener(this)
            }*/
            @JvmName("getItem1")
            fun getItem(): ItemModel.ItemEntity?{
                return item
            }
            // 아이템 모델의 데이터 클래스 가져와서 새로 업데이트 시키기
            fun bind(item: ItemModel.ItemEntity?){
                item?.let{
                    this.item = item
                    binding.checkBox.isChecked = it.isChecked
                    if (binding.checkBox.isChecked){
                        binding.content.setPaintFlags(binding.content.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                    }else{
                        binding.content.setPaintFlags(0)
                    }
                    binding.content.text = it.name.toString()+" | "+it.contents.toString() +" | "+it.memo.toString()
                    
                    //체크 확인하기
                    binding.checkBox.setOnClickListener {
                        if(item.isChecked){
                            binding.content.setPaintFlags(0)
                            item.isChecked = false
                        }else if(!item.isChecked){
                            item.isChecked = true
                            binding.content.setPaintFlags(binding.content.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                        }
                        //체크박스 선택부분 업데이트
                        db.collection("schedule")
                            .document(auth.uid.toString())
                            .collection("plans")
                            .document(item.docId)
                            .update("checkbox",item.isChecked)
                            .addOnSuccessListener {

                            }
                        Log.d("checked", "${item.isChecked}")
                    }
                }

                Log.d("TAG", "onBindViewHolder: 일정어댑터 아이템 실행: ${item?.isChecked}")
            }
            }//onCheckedChanged end
        }// OnCheckedChangeListener end
    }// companion object end