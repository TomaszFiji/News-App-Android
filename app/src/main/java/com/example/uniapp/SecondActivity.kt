package com.example.uniapp

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.koushikdutta.ion.Ion
import org.json.JSONObject
import java.net.URLEncoder
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class SecondActivity : AppCompatActivity() {

    private lateinit var mAdapter: SecondAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var cat_name = "News"
    private var search = ""
    private var oldest = ""
    private var moreArticlesCalled = false
    private val list = ArrayList<ThumbnailModel>()
    private var scrollPosition = 0
    private val activity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val extras = intent.extras



        if (extras == null) {
            Log.d("TAG", "No extras in activity 2")
        } else {
            cat_name = extras.getString("cat_name").toString()
            search = extras.getString("query").toString()
        }
        setContentView(R.layout.second_activity)
        val mToolbar = findViewById<Toolbar>(R.id.toolbar_activity_2)
        mToolbar.title = cat_name
        if (cat_name != "null") {
            mToolbar.title = cat_name
        } else if (search != "null") {
            mToolbar.title = search
        }
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        recyclerView =
            findViewById<View>(R.id.recycler_view_activity_2) as RecyclerView // Bind to the recyclerview in the layout
        layoutManager = LinearLayoutManager(this) // Get the layout manager
        recyclerView.layoutManager = layoutManager
        mAdapter = SecondAdapter(list, this, false)

        populateList(cat_name, search)


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_2, menu)

        return super.onCreateOptionsMenu(menu)
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
            }
        })
    }

    fun moreAricles() {
        moreArticlesCalled = true
        populateList(cat_name, search)

    }

    fun isOlder(str1: String?, str2: String?): Boolean {
        val date1: ZonedDateTime = ZonedDateTime.parse(str1)
        val date2: ZonedDateTime = ZonedDateTime.parse(str2)

        return date1.isBefore(date2)
    }


    private fun populateList(cat_name: String, search: String) {

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


                    var titles = emptyArray<String>()
                    var authors = emptyArray<String>()
                    var summaries = emptyArray<String>()
                    var urls = emptyArray<String>()
                    var times = emptyArray<String>()
                    var images = emptyArray<String>()


                    var topic = ""
                    when (cat_name) {
                        "Country News" -> topic = "&topic=nation"
                        "World News" -> topic = "&topic=worls"
                        "Business" -> topic = "&topic=business"
                        "Entertainment" -> topic = "&topic=entertainment"
                        "General" -> topic = "&topic=breaking-news"
                        "Health" -> topic = "&topic=health"
                        "Science" -> topic = "&topic=science"
                        "Sports" -> topic = "&topic=sports"
                        "Technology" -> topic = "&topic=technology"
                    }


                    val apikeyGoogle = getString(R.string.apiKey1)
                    var urlGoogle: String

                    Log.d("TAG", ".$search. .$cat_name.")

                    if (search != "null") {
                        val pattern = "[^A-Za-z0-9 ]"
                        var search = search.replace(pattern.toRegex(), "")
                        search = URLEncoder.encode(search.trim(), "utf-8")
                        urlGoogle =
                            "https://gnews.io/api/v4/search?token=$apikeyGoogle&q=$search&lang=en&country=" +
                                    countryCodes.get(country) + "&max=10&sortby=relevance"

                    } else if (cat_name != "null") {
                        urlGoogle =
                            "https://gnews.io/api/v4/top-headlines?token=$apikeyGoogle$topic&lang=en&country=" +
                                    countryCodes.get(country) + "&max=10"
                    } else {
                        urlGoogle =
                            "https://gnews.io/api/v4/top-headlines?token=$apikeyGoogle&lang=en&country=" +
                                    countryCodes.get(country) + "&max=10"
                    }

                    if (oldest != "") {
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
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
                            Log.d("TAG", "Success $jsonObject")
                            var response = JSONObject(jsonObject)
                            // Iterate through the articles array and do something with each article
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


                            Log.d("TAG", "length: " + titles.size + " " + title.indices)
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
                                        Log.d("TAG", "LAst visible")
                                    }
                                    if (totalItemCount <= lastVisibleItem + 1 && !moreArticlesCalled) {

                                        scrollPosition =
                                            layoutManager.findFirstCompletelyVisibleItemPosition()

                                        Log.d("TAG", "Scroll  $scrollPosition")
                                        moreAricles()

                                    }
                                }
                            })
                            moreArticlesCalled = false
                        }
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