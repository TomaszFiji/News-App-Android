package com.example.uniapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.koushikdutta.ion.Ion
import org.json.JSONObject
import java.net.URLEncoder


class ThirdActivity : AppCompatActivity() {

    private var url = " "
    private var time = "x"
    private var title = "x"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.third_activity)

        val extras = intent.extras

        val webView = findViewById<WebView>(R.id.web_view)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null) {
                    view?.loadUrl(url)
                }
                return true
            }
        }

        if (extras == null) {
            Log.d("TAG", "No extras in activity 3")
            webView.loadUrl("https://gizmodo.com/bitcoin-price-hack-217-btc-og-developer-luke-dashjr-1849944799")
        } else {
            Log.d("TAG", "Extras in activity 3: " + extras.getString("article_url").toString()+ extras.getString("time").toString())
            webView.loadUrl(extras.getString("article_url").toString())
            url = extras.getString("article_url").toString()
            time = extras.getString("time").toString()
            title = extras.getString("title").toString()
        }

        val articleTime = findViewById<MaterialTextView>(R.id.xxxxx)
        articleTime.text = time

        val mToolbar = findViewById<Toolbar>(R.id.toolbar_activity_3)
        mToolbar.title = "Article"
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val bottomSheet = findViewById<View>(R.id.bottom_sheet)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = 150

        populateList(title)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share -> {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, url)
                    type = "text/plain"
                }
                startActivity(shareIntent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_3, menu)

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
            }
        })
    }

    private fun populateList(title: String) {
        val list = ArrayList<ThumbnailModel>()

        var titles = emptyArray<String>()
        var authors = emptyArray<String>()
        var urls = emptyArray<String>()
        var times = emptyArray<String>()
        var images = emptyArray<String>()

        val apikeyGoogle = getString(R.string.apiKey1)

        var pattern = "[^A-Za-z0-9 ]"
        var title = title.replace(pattern.toRegex(), "")
        title = URLEncoder.encode(title.trim(), "utf-8")
        pattern = "+"
        title = title.replace(pattern, "+OR+")
        var urlGoogle = "https://gnews.io/api/v4/search?token=$apikeyGoogle&q=$title&lang=en&country=uk&max=10&sortby=relevance"

        Log.d("TAG", "Success $urlGoogle")


        Ion.with(this)
            .load(urlGoogle)
            .setHeader("user-agent", "application/json")
            .asString()
            .setCallback { _, jsonObject ->
                var response = JSONObject(jsonObject)
                Log.d("TAG", "Success $jsonObject")
                try {
                    val articles = response.getJSONArray("articles")

                    if (articles != null) {
                        for (i in 0 until articles.length()) {
                            val article = articles.getJSONObject(i)
                            titles += article.getString("title")

                            val source = article.getJSONObject("source")
                            authors += source.getString("name")
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

                        val thumbnailModel = ThumbnailModel()
                        thumbnailModel.setTitle(titles[i])
                        thumbnailModel.setAuthor(authors[i])
                        thumbnailModel.setSummary(" ")
                        thumbnailModel.setTime(times[i])
                        thumbnailModel.setURL(urls[i])
                        thumbnailModel.setImage(images[i])
                        list.add(thumbnailModel)
                    }

                    val recyclerView =
                        findViewById<View>(R.id.recycler_view_activity_3_bottom) as RecyclerView // Bind to the recyclerview in the layout

                    recyclerView.minimumWidth = 600
                    val layoutManager = LinearLayoutManager(
                        this,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    ) // Get the layout manager
                    recyclerView.layoutManager = layoutManager

                    Log.d("TAG", "list act: " + list)
                    val mAdapter = ThirdAdapter(list, this)
                    recyclerView.adapter = mAdapter
                } catch (e: Exception) {
                    var urlGoogle =
                        "https://gnews.io/api/v4/top-headlines?token=$apikeyGoogle&lang=en&country=uk&max=10"

                    Ion.with(this)
                        .load(urlGoogle)
                        .setHeader("user-agent", "application/json")
                        .asString()
                        .setCallback { _, jsonObject ->
                            var response = JSONObject(jsonObject)
                            Log.d("TAG", "Success $jsonObject")
                            // Iterate through the articles array and do something with each article
                            val articles = response.getJSONArray("articles")

                            if (articles != null) {
                                for (i in 0 until articles.length()) {
                                    val article = articles.getJSONObject(i)
                                    titles += article.getString("title")

                                    val source = article.getJSONObject("source")
                                    authors += source.getString("name")
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

                                val thumbnailModel = ThumbnailModel()
                                thumbnailModel.setTitle(titles[i])
                                thumbnailModel.setAuthor(authors[i])
                                thumbnailModel.setSummary(" ")
                                thumbnailModel.setTime(times[i])
                                thumbnailModel.setURL(urls[i])
                                thumbnailModel.setImage(images[i])
                                list.add(thumbnailModel)
                            }

                            val recyclerView =
                                findViewById<View>(R.id.recycler_view_activity_3_bottom) as RecyclerView // Bind to the recyclerview in the layout

                            val layoutManager = LinearLayoutManager(
                                this,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            ) // Get the layout manager
                            recyclerView.layoutManager = layoutManager

                            val mAdapter = ThirdAdapter(list, this)
                            recyclerView.adapter = mAdapter
                        }
                }
            }


    }
}
