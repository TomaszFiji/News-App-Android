package com.example.uniapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyCategories(activity: AppCompatActivity, index: Int) : Fragment() {

    private val activity = activity
    private val index = index
    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "on create " + index.toString())

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        inflater.inflate(R.layout.fragment_my_categories, container, false)!!

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("TAG", "onDestroyView " + index.toString())
    }

    override fun onPause() {
        super.onPause()
        Log.d("TAG", "onPause " + index.toString())
    }

    override fun onResume() {
        super.onResume()
        Log.d("TAG", "onResume " + index.toString())
        populateList()
    }

    fun updateCategories(imageModelArrayList: ArrayList<MyModel>) {
        recyclerView =
            activity.findViewById(R.id.my_recycler_view)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        Log.i("AAAAA", imageModelArrayList.size.toString())
        mAdapter = MyAdapter(imageModelArrayList, activity, this, index)
        recyclerView.adapter = mAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        Log.d("TAG", "onViewCreated " + index.toString())

    }

    fun populateList() {
        val list = ArrayList<MyModel>()

        val myImageNameList = arrayOf(
            R.string.cat1,
            R.string.cat2,
            R.string.cat3,
            R.string.cat4,
            R.string.cat5,
            R.string.cat6,
            R.string.cat7,
            R.string.cat8,
            R.string.cat9,
        )

        val myImageList = hashMapOf(
            getString(R.string.cat1) to R.drawable.country_icon,
            getString(R.string.cat2) to R.drawable.world_icon,
            getString(R.string.cat3) to R.drawable.bussines_icon,
            getString(R.string.cat4) to R.drawable.entertainment_icon,
            getString(R.string.cat5) to R.drawable.general_icon,
            getString(R.string.cat6) to R.drawable.health_icon,
            getString(R.string.cat7) to R.drawable.sports_icon,
            getString(R.string.cat8) to R.drawable.science_icon,
            getString(R.string.cat9) to R.drawable.technology_icon
        )



        if (index == 0) {
            Log.d("TAG", "index 0 ")
            // Display all categories
            for (i in 0..8) {
                val imageModel = MyModel()
                imageModel.setName(getString(myImageNameList[i]))
                val cat_n = getString(myImageNameList[i])
                myImageList.get(cat_n)?.let { imageModel.setImage(it) }
                list.add(imageModel)
                Log.d("TAG", "list: $list")
            }
            updateCategories(list)
        } else if (index >= 1) {
            Log.d("TAG", "index 1 ")
            val mAuth = FirebaseAuth.getInstance()
            val currentUser = mAuth.currentUser
            if (currentUser != null) {
                val email = currentUser.email
                val database = FirebaseDatabase.getInstance()
                val usersRef = database.getReference("users")
                val userEmail = email.toString().replace(".", ",")
                val favoritesRef = usersRef.child(userEmail).child("favorites")
                favoritesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val favorites = dataSnapshot.getValue(object :
                                GenericTypeIndicator<ArrayList<String>>() {})
                            if (favorites != null) {
                                for (favorite in favorites) {
                                    val myModel = MyModel()
                                    myModel.setName(favorite)
                                    myImageList.get(favorite)?.let { myModel.setImage(it) }
                                    list.add(myModel)
                                }
                                list.reverse()
                                updateCategories(list)

                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
        }
    }
}
