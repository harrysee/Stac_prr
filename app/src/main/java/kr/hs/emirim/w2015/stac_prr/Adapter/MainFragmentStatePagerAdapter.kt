package kr.hs.emirim.w2015.stac_prr.Adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import kr.hs.emirim.w2015.stac_prr.Fragment.*


class MainFragmentStatePagerAdapter(fm : FragmentManager, val fragmentCount : Int, var frag1 : Fragment) : FragmentStatePagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        Log.d("TAG", "getItem:프레그1")
        when(position){
            0 -> return frag1
            1 -> return CalenderFragment()
            2 -> return JournalFragment()
            3 -> return SetFragment()
            else -> return HomeFragment()
        }
    }

    override fun getCount(): Int = fragmentCount // 자바에서는 { return fragmentCount }

    }
