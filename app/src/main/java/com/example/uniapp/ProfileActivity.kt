package com.example.uniapp

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.concurrent.TimeUnit

class ProfileActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: WordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_activity)


        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        val userEmail: String
        var country: String? = null

        if (currentUser != null) {
            val email = currentUser.email
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("users")
            userEmail = email.toString().replace(".", ",")

            val countryLabel = findViewById<MaterialTextView>(R.id.country_label)
            val emailLabel = findViewById<MaterialTextView>(R.id.email_label)
            emailLabel.text = email.toString()

            val listView = findViewById<ExpandableListView>(R.id.countries_expandable_list_view)

            val adapter = ExpandableListAdapter(this, groupData, childData, emailLabel)
            Log.d("TAG", "t1")

            listView.setAdapter(adapter)

            listView.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
                val selectedCountry = adapter.getChild(groupPosition, childPosition) as String
                adapter.selectedCountry = selectedCountry
                adapter.notifyDataSetChanged()


                countryLabel.text = selectedCountry

                val database = FirebaseDatabase.getInstance()
                val usersRef = database.getReference("users")
                usersRef.child(userEmail).child("country").setValue(selectedCountry)

                true
            }

            usersRef.child(userEmail).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    country = dataSnapshot.child("country").getValue(String::class.java)
                    if (country != null) {
                        Log.d("TAG", "t2")
                        Log.d("TAG", country!!)
                        adapter.selectedCountry = country
                        adapter.notifyDataSetChanged()
                        countryLabel.text = country
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })

            val words_edit_text = findViewById<EditText>(R.id.favourite_categories_edit_text)
            val addWordButton = findViewById<MaterialButton>(R.id.add_word_button)
            addWordButton.setOnClickListener {
                val word = words_edit_text.text.toString()
                val wordssRef = usersRef.child(userEmail).child("words")
                // Check if the favorites list already exists in the database
                wordssRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (word.isNotEmpty()) {
                            if (dataSnapshot.exists()) {
                                val words = dataSnapshot.getValue(object :
                                    GenericTypeIndicator<ArrayList<String>>() {})
                                if (words != null) {
                                    if (!words.contains(word)) {
                                        words.add(word)
                                    }
                                    wordssRef.setValue(words)
                                    populateWords()
                                    words_edit_text.setText("")
                                }
                            } else {
                                wordssRef.setValue(listOf(word))
                                populateWords()
                                words_edit_text.setText("")
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
            populateWords()
        }
    }

    fun populateWords() {
        val list = ArrayList<WordModel>()
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val email = currentUser.email
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("users")
            val userEmail = email.toString().replace(".", ",")
            val wordsRef = usersRef.child(userEmail).child("words")
            wordsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val words = dataSnapshot.getValue(object :
                            GenericTypeIndicator<ArrayList<String>>() {})
                        Log.d("TAG", "pop words profile $words")
                        if (words != null) {
                            for (word in words) {
                                if (word != null) {
                                    val wordModel = WordModel()
                                    wordModel.setName(word)
                                    list.add(wordModel)
                                }
                            }
                            list.reverse()
                            Log.d("TAG", "2pop words profile $list")
                            updateWordsList(list)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }

    fun logOut (view: View) {
        val mAuth = FirebaseAuth.getInstance()
        mAuth.signOut()
        try {
            val newIntent = Intent(this, LoginActivity::class.java)
            startActivity(newIntent)
        } catch (e: Exception) {}

    }

    private fun updateWordsList(list: ArrayList<WordModel>) {
        recyclerView =
            findViewById(R.id.recycler_view_profile_activity) // Bind to the recyclerview in the layout
        val layoutManager = GridLayoutManager(this, 3)  // Get the layout manager
        recyclerView.layoutManager = layoutManager
        mAdapter = WordAdapter(list, this)
        recyclerView.adapter = mAdapter
    }

    fun testNotification (view: View) {
        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        val jobInfo = JobInfo.Builder(1, ComponentName(this, ArticleCheckService::class.java))
            .setMinimumLatency(TimeUnit.SECONDS.toMillis(1))
            .setPersisted(true)
            .build()

        jobScheduler.schedule(jobInfo)

        Log.d("TAG", "Notifications scheduled")
    }

    // Data for the expandable list
    val groupData = listOf("Change your country")
    val childData = mapOf(
        "Change your country" to listOf(
            "United Kingdom",
            "United States",
            "Argentina",
            "Australia",
            "Austria",
            "Belgium",
            "Brazil",
            "Bulgaria",
            "Canada",
            "China",
            "Colombia",
            "Cuba",
            "Czech Republic",
            "Egypt",
            "France",
            "Germany",
            "Greece",
            "Hungary",
            "India",
            "Indonesia",
            "Ireland",
            "Israel",
            "Italy",
            "Japan",
            "Morocco",
            "Netherlands",
            "New Zealand",
            "Nigeria",
            "Norway",
            "Poland",
            "Portugal",
            "Romania",
            "Russia",
            "Serbia",
            "Slovakia",
            "Slovenia",
            "South Africa",
            "South Korea",
            "Spain",
            "Sweden",
            "Switzerland",
            "Taiwan",
            "Thailand",
            "Turkey"
        )
    )


    val countryCodes = mapOf(
        "Argentina" to "ar",
        "Australia" to "au",
        "Austria" to "at",
        "Belgium" to "be",
        "Brazil" to "br",
        "Bulgaria" to "bg",
        "Canada" to "ca",
        "China" to "cn",
        "Colombia" to "co",
        "Cuba" to "cu",
        "Czech Republic" to "cz",
        "Egypt" to "eg",
        "France" to "fr",
        "Germany" to "de",
        "Greece" to "gr",
        "Hong Kong" to "hk",
        "Hungary" to "hu",
        "India" to "in",
        "Indonesia" to "id",
        "Ireland" to "ie",
        "Israel" to "il",
        "Italy" to "it",
        "Japan" to "jp",
        "Latvia" to "lv",
        "Lithuania" to "lt",
        "Malaysia" to "my",
        "Mexico" to "mx",
        "Morocco" to "ma",
        "Netherlands" to "nl",
        "New Zealand" to "nz",
        "Nigeria" to "ng",
        "Norway" to "no",
        "Philippines" to "ph",
        "Poland" to "pl",
        "Portugal" to "pt",
        "Romania" to "ro",
        "Russia" to "ru",
        "Saudi Arabia" to "sa",
        "Serbia" to "rs",
        "Singapore" to "sg",
        "Slovakia" to "sk",
        "Slovenia" to "si",
        "South Africa" to "za",
        "South Korea" to "kr",
        "Sweden" to "se",
        "Switzerland" to "ch",
        "Taiwan" to "tw",
        "Thailand" to "th",
        "Turkey" to "tr",
        "UAE" to "ae",
        "Ukraine" to "ua",
        "United Kingdom" to "gb",
        "United States" to "us",
        "Venuzuela" to "ve"
    )
}
