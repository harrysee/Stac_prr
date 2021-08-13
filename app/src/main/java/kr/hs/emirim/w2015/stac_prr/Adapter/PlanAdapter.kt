package kr.hs.emirim.w2015.stac_prr.Adapter

import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import kr.hs.emirim.w2015.stac_prr.ItemModel
import kr.hs.emirim.w2015.stac_prr.databinding.PlanItemViewBinding

class PlanAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: ItemModel? = ItemModel()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var binding = PlanItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemHolder) {
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
        class ItemHolder(var binding: PlanItemViewBinding) : RecyclerView.ViewHolder(binding.root),
            CompoundButton.OnCheckedChangeListener {
            var item: ItemModel.ItemEntity? = null

            init {  // 체크박스가 온클릭 되면 바로 리스너 이동시키기
                binding.checkBox.setOnCheckedChangeListener(this)
            }
            @JvmName("getItem1")
            fun getItem(): ItemModel.ItemEntity?{
                return item
            }
            // 아이템 모델의 데이터 클래스 가져와서 새로 업데이트 시키기
            fun bind(item: ItemModel.ItemEntity?){
                item?.let{
                    this.item = item
                    binding.checkBox.isChecked = it.isChecked
                    binding.content.text = it.contents  
                }
            }

            //체크박스 눌러졌을때
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                item?.let{
                    if(it.isChecked){
                        binding.content.setPaintFlags(0)
                        it.isChecked = false
                    }else if(!it.isChecked){
                        it.isChecked = true
                        binding.content.setPaintFlags(binding.content.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                    }
                    Log.d("checked", "${it.isChecked}")
                }
            }//onCheckedChanged end
        }// OnCheckedChangeListener end
    }// companion object end

}