package kr.hs.emirim.w2015.stac_prr.Adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.slider_item.view.*
import kr.hs.emirim.w2015.stac_prr.DataClass.HomeData
import kr.hs.emirim.w2015.stac_prr.Fragment.NewPlantFragment
import kr.hs.emirim.w2015.stac_prr.MainActivity
import kr.hs.emirim.w2015.stac_prr.Fragment.PlantInfoFragment
import kr.hs.emirim.w2015.stac_prr.R


class FhViewAdapter(val datas : ArrayList<HomeData>, val fragment_s:FragmentActivity?) : RecyclerView.Adapter<FhViewAdapter.MyViewholder>() {
    var activity: MainActivity?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewholder(parent)

    override fun onBindViewHolder(holder: MyViewholder, position: Int) {
        holder.bind(datas[position])
        holder.itemView.setOnClickListener{
            Log.d(">>>>."+position.toString(), "클릭됨 ")
            var fragment:Fragment= PlantInfoFragment()
            var bundle: Bundle = Bundle()
            activity = fragment_s as MainActivity?
            fragment.arguments = bundle
            activity?.fragmentChange_for_adapter(fragment)
            Log.d("프레그먼트", "프레그먼트 갔다옴 ")
        }
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    inner class MyViewholder(parent:ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.slider_item, parent, false)) {
        val imageView = itemView.img_register_plant
        val textView = itemView.text_register_plant
        val textSpace = itemView.text_register_spacies

        fun bind(item: HomeData){
            imageView.setImageResource(item.imgUrl)
            textView.text = item.name
            textSpace.text = item.spacies
            Log.d(item.name, "바인드 실행 : ")

        }

    }
}

