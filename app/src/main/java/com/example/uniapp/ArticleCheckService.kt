package com.example.uniapp

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.format.DateUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.koushikdutta.ion.Ion
import com.squareup.picasso.Picasso
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class ArticleCheckService : JobService() {

    private var i = 0
    override fun onStartJob(params: JobParameters): Boolean {

        retrieveArticlesFromApi()
        return false
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        TODO("Not yet implemented")
    }

    fun getTime(time: String): String {

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val date = dateFormat.parse(time)

        val now = System.currentTimeMillis()
        val relativeTimeSpanString =
            DateUtils.getRelativeTimeSpanString(date.time, now, DateUtils.MINUTE_IN_MILLIS)


        return relativeTimeSpanString.toString()
    }

    private fun displayNotifications(articles: List<Article>) {

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        for (article in articles) {
            val notificationIntent = Intent(this, ThirdActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            notificationIntent.putExtra("time", article.time)
            Log.d("TAG", "Putting extra time: " + getTime(article.time))
            notificationIntent.putExtra("article_url", article.url)

            notificationIntent.putExtra("title", article.title)

            val notificationPendingIntent = PendingIntent.getActivity(
                this,
                article.hashCode(),
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(this, "notification_channel_id")

                .setSmallIcon(R.drawable.no_notification_icon_foreground)
                .setContentTitle(article.title)
                .setContentText(article.summary)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .setStyle(NotificationCompat.BigTextStyle().bigText(article.summary))
                .build()

            // Display the notification
            notificationManager.notify(article.hashCode(), notification)

        }
    }

    private fun createContentIntent(articleUrl: String): PendingIntent {
        val intent = Intent(this, ThirdActivity::class.java).apply {
            putExtra("article_url", articleUrl)
        }
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }


    private fun compareArticles(articles: List<Article>) {
        val newArticles = articles as MutableList<Article>

        Log.d("TAG", "service compareArticles start")
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val email = currentUser.email
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("users")
            val userEmail = email.toString().replace(".", ",")
            val favoritesRef = usersRef.child(userEmail).child("articleUrls")
            favoritesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val articles = dataSnapshot.getValue(object :
                            GenericTypeIndicator<ArrayList<String>>() {})

                        val toRemove = ArrayList<Article>()
                        for (article in newArticles) {
                            if (articles != null) {
                                if (articles.contains(article.url)) {
                                    toRemove.add(article)
                                }

                            }
                            Log.d("TAG", "service in compare " + toRemove.size)

                        }
                        for (article in toRemove) {
                            newArticles.remove(article)
                        }
                        Log.d("TAG", "service in compare after remove " + newArticles.size)

                        displayNotifications(newArticles)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("TAG", "service onCanceled")
                }
            })
        }

    }

    private fun retrieveArticlesFromApi() {

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
                        val favourites = dataSnapshot.getValue(object :
                            GenericTypeIndicator<ArrayList<String>>() {})

                        if (favourites != null) {
                            for (category in favourites) {
                                if (category != null) {
                                    getDataFromAPI(category)
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("TAG", "service onCanceled")
                }
            })
        }
    }

    private fun getDataFromAPI(category: String) {
        val articles = ArrayList<Article>()

        Log.d("TAG", "notification category: $category")
        val apikeyGoogle = getString(R.string.apiKey1)
        val urlGoogle =
            "https://gnews.io/api/v4/top-headlines?token=$apikeyGoogle&topic=$category&lang=en&country=uk&max=10"


        val client = OkHttpClient()
        val request = Request.Builder()
            .url(urlGoogle)
            .header("User-Agent", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                val responseString = response.body?.string()
                val responseJson = JSONObject(responseString)
                val articlesArray = responseJson.getJSONArray("articles")

                Log.d("TAG", "service Success $responseJson")
                val articles = ArrayList<Article>()
                for (i in 0 until articlesArray.length()) {
                    val articleJson = articlesArray.getJSONObject(i)
                    val author = articleJson.getJSONObject("source").getString("name")
                    val title = articleJson.getString("title")
                    val summary = articleJson.getString("description")
                    val url = articleJson.getString("url")
                    val time = articleJson.getString("publishedAt")
                    val image = articleJson.getString("image")
                    val article = Article(title, author, summary, url, image, time)
                    articles.add(article)
                }
                Log.d("TAG", "service befor compare $articles")
                compareArticles(articles)

            }
        })
        Log.d("TAG", "service finish $articles")
    }


}