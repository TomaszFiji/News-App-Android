package com.example.uniapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class WordAdapter ( private val wordsList: MutableList<WordModel>, private val profileActivity: ProfileActivity) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.recycle_view_word, parent, false)

        return WordViewHolder(v)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val info = wordsList[position]

        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        holder.word.text = info.getName()

        holder.removeButton.setOnClickListener{
            if (currentUser != null) {
                val email = currentUser.email
                val database = FirebaseDatabase.getInstance()
                val usersRef = database.getReference("users")
                val userEmail = email.toString().replace(".", ",")
                val wordsRef = usersRef.child(userEmail).child("words")
                // Check if the favorites list already exists in the database
                wordsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val words = dataSnapshot.getValue(object :
                                GenericTypeIndicator<ArrayList<String>>() {})
                            if (words != null) {
                                if (words.contains(info.getName())) {
                                    words.remove(info.getName())

                                }
                                wordsRef.setValue(words)
                                profileActivity.populateWords()
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
        }

    }

    override fun getItemCount(): Int {
        return wordsList.size
    }

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        var word = itemView.findViewById<View>(R.id.word_text) as MaterialTextView
        var removeButton = itemView.findViewById<View>(R.id.remove_icon) as AppCompatImageButton

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

        }

    }
}