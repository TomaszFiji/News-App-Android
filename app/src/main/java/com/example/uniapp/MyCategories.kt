package com.example.uniapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyCategories : Fragment() {

    private lateinit var recyclerView : RecyclerView
    private lateinit var mAdapter: MyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_my_categories, container, false)!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageModelArrayList = populateList()

        recyclerView = view.findViewById(R.id.my_recycler_view) // Bind to the recyclerview in the layout
        val layoutManager = LinearLayoutManager(context)  // Get the layout manager
        recyclerView.layoutManager = layoutManager
        Log.i("AAAAA", imageModelArrayList.size.toString())
        mAdapter = MyAdapter(imageModelArrayList)
        recyclerView.adapter = mAdapter
    }

    private fun populateList(): ArrayList<MyModel> {
        val list = ArrayList<MyModel>()
        val myImageList = arrayOf(R.drawable.example_img1_foreground, R.drawable.example_img2_foreground,
             R.drawable.example_img3_foreground,
            R.drawable.example_img1_foreground, R.drawable.example_img2_foreground,
            R.drawable.example_img3_foreground, R.drawable.example_img1_foreground,
            R.drawable.example_img2_foreground, R.drawable.example_img3_foreground,
            R.drawable.example_img1_foreground)
        val myImageNameList = arrayOf(R.string.cat1, R.string.cat2, R.string.cat3, R.string.cat4,
            R.string.cat5, R.string.cat6, R.string.cat7, R.string.cat8, R.string.cat9, R.string.cat10, )

        for (i in 0..9) {
            val imageModel = MyModel()
            imageModel.setNames(getString(myImageNameList[i]))
            imageModel.setImages(myImageList[i])
            list.add(imageModel)
        }
        return list
    }
}
