//package com.example.uniapp
//
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//
//class FragmentActivity2 : Fragment() {
//    private lateinit var recyclerView : RecyclerView
//    private lateinit var mAdapter: AdapterActivity2
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
//        inflater.inflate(R.layout.fragment_activity_2, container, false)!!
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val imageModelArrayList = populateList()
//
//        recyclerView = view.findViewById(R.id.recycler_view_activity_2) // Bind to the recyclerview in the layout
//        val layoutManager = LinearLayoutManager(context)  // Get the layout manager
//        recyclerView.layoutManager = layoutManager
//        Log.i("AAAAA", imageModelArrayList.size.toString())
////        mAdapter = AdapterActivity2(imageModelArrayList)
//        recyclerView.adapter = mAdapter
//    }
//
//    private fun populateList(): ArrayList<ModelActivity2> {
//        val list = ArrayList<ModelActivity2>()
//        val myImageList = arrayOf(R.drawable.sport_icon_foreground, R.drawable.example_img1_foreground,
//            R.drawable.example_img2_foreground, R.drawable.example_img3_foreground,
//            R.drawable.example_img1_foreground, R.drawable.example_img2_foreground,
//            R.drawable.example_img3_foreground, R.drawable.example_img1_foreground,
//            R.drawable.example_img2_foreground, R.drawable.example_img3_foreground)
//        val myImageNameList = arrayOf(R.string.sport, R.string.example1, R.string.example2,
//            R.string.example3, R.string.example1, R.string.example2, R.string.example3,
//            R.string.example1, R.string.example2, R.string.example3)
//
//        for (i in 0..9) {
//            val imageModel = ModelActivity2()
//            imageModel.setNames(getString(myImageNameList[i]))
//            imageModel.setImages(myImageList[i])
//            list.add(imageModel)
//        }
//        return list
//    }
//}