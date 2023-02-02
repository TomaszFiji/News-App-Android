package com.example.uniapp

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabAdapter (activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun createFragment(index: Int): Fragment {

        return MyCategories()
    }

    // get item count - equal to number of tabs
    override fun getItemCount(): Int
    {
        return 4
    }
}