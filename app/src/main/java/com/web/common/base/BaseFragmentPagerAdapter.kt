package com.web.common.base

import android.view.ViewGroup
import androidx.annotation.NonNull

class BaseFragmentPagerAdapter(private val fm: androidx.fragment.app.FragmentManager, private val mList: List<androidx.fragment.app.Fragment>) : androidx.fragment.app.FragmentPagerAdapter(fm) {


    override fun getCount(): Int =mList.size

    /**
     * 重写instantiateItem
     * 当缓存的fragment和List里面的fragment不一致时，使用List里面的最新fragment
     * @param container
     * @param position
     * @return
     */
    @NonNull
    override fun instantiateItem(@NonNull container: ViewGroup, position: Int): Any {
        //**获取旧数据
        var fragment = super.instantiateItem(container, position) as androidx.fragment.app.Fragment
        //**对比数据的一致性
        if (fragment === getItem(position)) return fragment
        //**新数据替换旧数据
        val mCurTransaction = fm.beginTransaction()
        val tag = fragment.tag
        mCurTransaction.remove(fragment)
        fragment = getItem(position)
        mCurTransaction.add(container.id, fragment, tag)
        mCurTransaction.attach(fragment)
        mCurTransaction.commit()
        return fragment
    }

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return mList[position]
    }


    override fun getItemPosition(@NonNull `object`: Any): Int {
        for(i in mList.indices){
            if(mList[i]==`object`) return i
        }
        return POSITION_NONE
    }

    override fun getItemId(position: Int): Long {
        return mList[position].hashCode().toLong()
    }
}