package com.example.uniapp

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class ThirdActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.third_activity)

        val mToolbar = findViewById<Toolbar>(R.id.toolbar_activity_3)
        mToolbar.title = "Article"
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

//        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
//        val viewPager = findViewById<ViewPager2>(R.id.pager_activity_2)
//
//        val tabTitles = resources.getStringArray(R.array.tabTitles)
//        viewPager.adapter = TabAdapter(this)
//        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            when (position) {
//                0 -> tab.text = tabTitles[0]
//                1 -> tab.text = tabTitles[1]
//                2 -> tab.text = tabTitles[2]
//            }
//        }.attach()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_3, menu)

        return super.onCreateOptionsMenu(menu)
    }
}
