package com.example.uniapp

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class SecondAdapter(
    private val thumbnailModelArrayList: MutableList<ThumbnailModel>,
    private val activity: AppCompatActivity,
    private val isFromFavourites: Boolean
) : RecyclerView.Adapter<SecondAdapter.ThumbnailViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.recycle_view_activity_2, parent, false)

        return ThumbnailViewHolder(v)
    }

    override fun onBindViewHolder(holder: ThumbnailViewHolder, position: Int) {
        val info = thumbnailModelArrayList[position]

        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        holder.title.text = info.getTitle()
        holder.author.text = info.getAuthor()
        holder.summary.text = info.getSummary()
        holder.time.text = info.getTime()
        holder.url = info.getURL()
        if (info.getImage().isNotEmpty()) {
            Picasso.get().load(info.getImage()).fit().into(holder.imageView)
        }

        holder.likeButton.setOnClickListener {

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

                                if (favorites?.any { it["url"] == info.getURL() } == true) {

                                    var element: HashMap<String, String>? = null
                                    for (hashMap in favorites) {
                                        if (hashMap["url"] == info.getURL()) {
                                            element = hashMap
                                            break
                                        }
                                    }
                                    favorites.remove(element)
                                    holder.likeButton.setImageResource(R.drawable.favourite_border_foreground)


                                    if (isFromFavourites) {
                                        activity
                                    }

                                } else {
                                    favorites.add(info.toHashMap())
                                    holder.likeButton.setImageResource(R.drawable.favourite)
                                }
                                favoritesRef.setValue(favorites)
                            }
                        } else {
                            favoritesRef.setValue(arrayListOf(info.toHashMap()))
                            holder.likeButton.setImageResource(R.drawable.favourite)

                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }

        }
        if (currentUser != null && !isFromFavourites) {
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
                            Log.d("TAG", "hashmap test: " + favorites.toString() )
                            if (favorites.any { it["url"] == info.getURL()}) {

                                holder.likeButton.setImageResource(R.drawable.favourite)

                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
        if (isFromFavourites) {
            holder.likeButton.setImageResource(R.drawable.favourite)
        }
    }

    override fun getItemCount(): Int {
        return thumbnailModelArrayList.size
    }

    fun lunchA3(url: String?, time: String, title: String) {
        try {
            val newIntent = Intent(activity, ThirdActivity::class.java)
            newIntent.putExtra("article_url", url)
            newIntent.putExtra("time", time)
            newIntent.putExtra("title", title)
            startActivity(activity, newIntent, null)
        } catch (e: Exception) {
            Log.i("AAAAA", "actvity2")
        }
    }


    inner class ThumbnailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        var title = itemView.findViewById<View>(R.id.title_thumbnail) as MaterialTextView
        var author = itemView.findViewById<View>(R.id.author_thumbnail) as MaterialTextView
        var summary = itemView.findViewById<View>(R.id.thumbnail_summary) as MaterialTextView
        var time = itemView.findViewById<View>(R.id.thumbnail_time) as MaterialTextView
        var url: String? = null
        val imageView: ImageView = itemView.findViewById(R.id.thumbnail_image)
        var likeButton = itemView.findViewById<View>(R.id.like_button) as AppCompatImageButton

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val msg = title.text
            val snackbar = Snackbar.make(v, "$msg", Snackbar.LENGTH_LONG)
            snackbar.show()
            lunchA3(url, time.text.toString(), title.text.toString())
        }
    }
}
