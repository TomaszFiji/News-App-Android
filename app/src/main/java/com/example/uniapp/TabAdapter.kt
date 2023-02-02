package com.example.uniapp

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabAdapter (activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    val activity = activity
    override fun createFragment(index: Int): Fragment {

        Log.d("TAG", "TAb index: $index")
        if (index < 2) {
            return MyCategories(activity, index)
        } else if (index == 3) {
            return FavouritesFragment(activity)
        } else if (index == 2) {
            return WordsFragment(activity)
        }
        return MyCategories(activity, index)
    }

    override fun getItemCount(): Int
    {
        return 4
    }
}