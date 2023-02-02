package com.example.uniapp

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyAdapter(
    private val imageModelArrayList: MutableList<MyModel>,
    activity: AppCompatActivity,
    myCategories: MyCategories,
    index: Int
) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    private val activity = activity
    private val myCategories = myCategories
    private val index = index

    /*
     * Inflate our views using the layout defined in row_layout.xml
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.recycle_category, parent, false)

        return ViewHolder(v)
    }

    /*
     * Bind the data to the child views of the ViewHolder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = imageModelArrayList[position]

        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        holder.imgView.setImageResource(info.getImage())
        holder.txtMsg.text = info.getName()

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
                        if (favorites != null && favorites.contains(info.getName())) {
                            holder.notification.setImageResource(R.drawable.notification_foreground)
                        } else {
                            holder.notification.setImageResource(R.drawable.no_notification_icon_foreground)
                        }
                    } else {
                        holder.notification.setImageResource(R.drawable.no_notification_icon_foreground)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

        holder.notification.setOnClickListener {

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
                                if (favorites.contains(info.getName())) {
                                    favorites.remove(info.getName())
                                    holder.notification.setImageResource(R.drawable.no_notification_icon_foreground)
                                    Thread.sleep(10)
                                } else {
                                    favorites.add(info.getName())
                                    holder.notification.setImageResource(R.drawable.notification_foreground)
                                }
                                favoritesRef.setValue(favorites)
                                if (index == 1) {
                                    myCategories.populateList()
                                }
                            }
                        } else {
                            favoritesRef.setValue(listOf(info.getName()))
                            holder.notification.setImageResource(R.drawable.notification_foreground)

                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
        }
    }


    private fun switchNotification(holder: ViewHolder): View.OnClickListener {

        return View.OnClickListener {
            if (holder.not_state) {
                holder.notification.setImageResource(R.drawable.notification_foreground)
            } else {
                holder.notification.setImageResource(R.drawable.no_notification_icon_foreground)
            }
        }
    }


    /*
     * Get the maximum size of the
     */
    override fun getItemCount(): Int {
        return imageModelArrayList.size
    }


    fun lunchA2(cat_name: String) {
        try {
            val newIntent = Intent(activity, SecondActivity::class.java)
            newIntent.putExtra("cat_name", cat_name)
            startActivity(activity, newIntent, null)
        } catch (e: Exception) {
            Log.i("AAAAA", "actvity2")
        }
    }

    /*
     * The parent class that handles layout inflation and child view use
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        var not_state = false
        var imgView = itemView.findViewById<View>(R.id.icon) as ImageView
        var txtMsg = itemView.findViewById<View>(R.id.category_text) as TextView
        var notification = itemView.findViewById<View>(R.id.notification) as ImageView


        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val msg = txtMsg.text
            val snackbar = Snackbar.make(v, "$msg", Snackbar.LENGTH_LONG)
            snackbar.show()
            lunchA2(txtMsg.text.toString())
        }
    }
}