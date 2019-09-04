package com.lucianbc.receiptscan.presentation.home.exports.form

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

@SuppressLint("WrongConstant")
class OptionsAdapter(
    fm: FragmentManager,
    cloudOption: () -> Unit,
    localOption: () -> Unit
)
    : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val fragmentList = listOf(
        DateRangeFragment(),
        LocalOrCloudFragment(localOption, cloudOption),
        ContentFragment(),
        ContentFormatFragment()
    )

    override fun getItem(position: Int): Fragment = fragmentList[position]

    override fun getCount(): Int = fragmentList.size
}