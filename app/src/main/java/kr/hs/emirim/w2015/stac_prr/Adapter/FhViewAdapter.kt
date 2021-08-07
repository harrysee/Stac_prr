package kr.hs.emirim.w2015.stac_prr.Adapter

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.slider_item.view.*
import kr.hs.emirim.w2015.stac_prr.Fragment.NewPlantFragment
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.R


class FhViewAdapter(a : ArrayList<Int>, newtext : ArrayList<String>, val fragment_s:FragmentActivity?) : RecyclerView.Adapter<FhViewAdapter.MyViewholder>() {
    var item_img = a
    var ntxt = newtext
    var activity: MainActivity?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewholder(parent)

    override fun onBindViewHolder(holder: MyViewholder, position: Int) {
        holder.bind(item_img[position], ntxt[position])
        holder.itemView.setOnClickListener{
            Log.d("클릭", "클릭됨 ")
            var fragment:Fragment= NewPlantFragment()
            var bundle: Bundle = Bundle()

            fragment.arguments = bundle

            activity = fragment_s as MainActivity?
            activity?.fragmentChange_for_adapter(fragment)
            Log.d("프레그먼트", "프레그먼트 갔다옴 ")
        }
    }

    override fun getItemCount(): Int {
        return item_img.size
    }

    inner class MyViewholder(parent:ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.slider_item, parent, false)) {
        val imageView = itemView.img_register_plant
        val textView = itemView.text_register_plant

        fun bind(img:Int, txt:String){
            imageView.setImageResource(img)
            textView.text = txt
            Log.d(txt, "바인드 실행 : ")

        }

    }
}

