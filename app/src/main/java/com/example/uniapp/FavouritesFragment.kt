package com.example.uniapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FavouritesFragment(activity: AppCompatActivity) : Fragment() {

    private val frag = this
    private val activity = activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        inflater.inflate(R.layout.fragment_favourites, container, false)!!

    override fun onResume() {
        super.onResume()
        populateList()
    }

    private fun populateList() {
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        if (currentUser != null) {
            val email = currentUser.email
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("users")
            val userEmail = email.toString().replace(".", ",")
            val favoritesRef = usersRef.child(userEmail).child("likedArticles")
            favoritesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val favorites = dataSnapshot.getValue(object :
                            GenericTypeIndicator<ArrayList<HashMap<String, String>>>() {})
                        if (favorites != null) {

                            var list = ArrayList<ThumbnailModel>()
                            for (article in favorites) {
                                try {
                                    val thumbnailModel = ThumbnailModel()
                                    article.get("title")?.let { thumbnailModel.setTitle(it) }
                                    article.get("author")?.let { thumbnailModel.setAuthor(it) }
                                    article.get("summary")?.let { thumbnailModel.setSummary(it) }
                                    article.get("time")?.let { thumbnailModel.setTime(it) }
                                    article.get("url")?.let { thumbnailModel.setURL(it) }
                                    article.get("image")?.let { thumbnailModel.setImage(it) }
                                    list.add(thumbnailModel)
                                }catch (e: Exception) {}
                            }
                            list.reverse()
                            val recyclerView = activity.findViewById<View>(R.id.recycler_view_fragment_favourites) as RecyclerView
                            val layoutManager = LinearLayoutManager(activity)
                            recyclerView.layoutManager = layoutManager
                            val mAdapter = SecondAdapter(list, activity, true)
                            recyclerView.adapter = mAdapter
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }


}