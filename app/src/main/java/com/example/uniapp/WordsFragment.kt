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
import com.koushikdutta.ion.Ion
import org.json.JSONObject
import java.net.URLEncoder
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class WordsFragment(activity: AppCompatActivity) : Fragment() {

    private lateinit var mAdapter: SecondAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var oldest = ""
    private var moreArticlesCalled = false
    private val list = ArrayList<ThumbnailModel>()
    private var scrollPosition = 0


    private val activity = activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "Started words... ")

        populateList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) =
        inflater.inflate(R.layout.fragment_favourites, container, false)!!

    override fun onResume() {
        super.onResume()


    }

    fun addArticleUrlToUser(urls: List<String>) {
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        val email = currentUser?.email
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        val userEmail = email.toString().replace(".", ",")
        val articleUrlsRef = usersRef.child(userEmail).child("articleUrls")

        articleUrlsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val articleUrls =
                        dataSnapshot.getValue(object : GenericTypeIndicator<ArrayList<String>>() {})
                    if (articleUrls != null) {
                        for (url in urls) {
                            // Check if the url already exists in the list
                            if (!articleUrls.contains(url)) {
                                articleUrls.add(url)
                            }
                        }
                        articleUrlsRef.setValue(articleUrls)
                    }
                } else {
                    articleUrlsRef.setValue(urls)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // An error occurred
            }
        })
    }

    fun moreAricles() {
        moreArticlesCalled = true
        populateList()

    }

    fun isOlder(str1: String?, str2: String?): Boolean {
        val date1: ZonedDateTime = ZonedDateTime.parse(str1)
        val date2: ZonedDateTime = ZonedDateTime.parse(str2)

        return date1.isBefore(date2)
    }

    private fun populateList() {

        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        val userEmail: String
        var country: String? = null

        if (currentUser != null) {
            val email = currentUser.email
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("users")
            userEmail = email.toString().replace(".", ",")

            usersRef.child(userEmail).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    country = dataSnapshot.child("country").getValue(String::class.java)

                    val wordsRef = usersRef.child(userEmail).child("words")
                    wordsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                val words = dataSnapshot.getValue(object :
                                    GenericTypeIndicator<ArrayList<String>>() {})
                                Log.d("TAG", "pop words profile $words")

                                var wordsToSearch = "everything"
                                if (words != null) {
                                    wordsToSearch = "a"
                                    for (word in words) {
                                        if (word != null) {
                                            val pattern = "[^A-Za-z0-9 ]"
                                            var temp = word.replace(pattern.toRegex(), "")
                                            temp = URLEncoder.encode(temp.trim(), "utf-8")
                                            wordsToSearch += "+OR+" + temp
                                        }
                                    }
                                }

                                Log.d("TAG", ",,,$wordsToSearch,,,")

                                var titles = emptyArray<String>()
                                var authors = emptyArray<String>()
                                var summaries = emptyArray<String>()
                                var urls = emptyArray<String>()
                                var times = emptyArray<String>()
                                var images = emptyArray<String>()


                                val apikeyGoogle = getString(R.string.apiKey1)
                                var urlGoogle =
                                    "https://gnews.io/api/v4/search?token=$apikeyGoogle&q=$wordsToSearch&lang=en&country=" +
                                            countryCodes.get(country) + "&max=10&sortby=relevance"

                                if (oldest != "") {
                                    val formatter =
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                                    val zonedDateTime = ZonedDateTime.parse(oldest)
                                    oldest = formatter.format(zonedDateTime.minusSeconds(1))

                                    urlGoogle += "&to=$oldest"
                                }

                                Log.d("TAG", urlGoogle)
                                Ion.with(activity)
                                    .load(urlGoogle)
                                    .setHeader("user-agent", "application/json")
                                    .asString()
                                    .setCallback { _, jsonObject ->
                                        var response = JSONObject(jsonObject)
                                        Log.d("TAG", "Success $jsonObject")
                                        val articles = response.getJSONArray("articles")

                                        if (articles != null) {
                                            for (i in 0 until articles.length()) {
                                                val article = articles.getJSONObject(i)
                                                titles += article.getString("title")

                                                val source = article.getJSONObject("source")
                                                authors += source.getString("name")
                                                summaries += article.getString("description")
                                                urls += article.getString("url")
                                                times += article.getString("publishedAt")
                                                if (article.has("image")) {
                                                    images += article.getString("image")
                                                } else {
                                                    images += ""
                                                }
                                            }
                                        }

                                        addArticleUrlToUser(urls.toList())

                                        for (i in titles.indices) {

                                            if (oldest == "" || !isOlder(oldest, times[i])) {
                                                oldest = times[i]
                                            }

                                            val thumbnailModel = ThumbnailModel()
                                            thumbnailModel.setTitle(titles[i])
                                            thumbnailModel.setAuthor(authors[i])
                                            thumbnailModel.setSummary(summaries[i])
                                            thumbnailModel.setTime(times[i])
                                            thumbnailModel.setURL(urls[i])
                                            thumbnailModel.setImage(images[i])
                                            list.add(thumbnailModel)
                                        }
                                        Log.d("TAG", "was here")

                                        recyclerView = activity.findViewById<View>(R.id.
                                            recycler_view_fragment_favourites) as RecyclerView
                                        layoutManager = LinearLayoutManager(activity)

                                        recyclerView.layoutManager = layoutManager
                                        mAdapter = SecondAdapter(list, activity, false)

                                        recyclerView.adapter = mAdapter
                                        layoutManager.scrollToPosition(scrollPosition)

                                        recyclerView.addOnScrollListener(object :
                                            RecyclerView.OnScrollListener() {
                                            override fun onScrolled(
                                                recyclerView: RecyclerView,
                                                dx: Int,
                                                dy: Int
                                            ) {
                                                super.onScrolled(recyclerView, dx, dy)

                                                val totalItemCount = layoutManager.itemCount
                                                val lastVisibleItem =
                                                    layoutManager.findLastVisibleItemPosition()


                                                if (totalItemCount <= lastVisibleItem + 1) {
                                                    Log.d("TAG", "Last element is visible")
                                                }
                                                if (totalItemCount <= lastVisibleItem + 1 && !moreArticlesCalled) {

                                                    scrollPosition = layoutManager.findFirstCompletelyVisibleItemPosition()

                                                    moreAricles()
                                                }
                                            }
                                        })
                                        moreArticlesCalled = false
                                    }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

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