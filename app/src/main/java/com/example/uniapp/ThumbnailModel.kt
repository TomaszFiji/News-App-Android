package com.example.uniapp

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

class ThumbnailModel {
    private var title: String? = null
    private var author: String? = null
    private var summary: String? = null
    private var time: String? = null
    private var liked: Boolean = false
    private var url: String? = null
    private var image: String? = null

    fun getImage(): String {
        return image.toString()
    }

    fun setImage(image: String) {
        this.image = image
    }

    fun getTitle(): String {
        return title.toString()
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun getAuthor(): String {
        return author.toString()
    }

    fun setAuthor(author: String) {
        this.author = author
    }

    fun getSummary(): String {
        return summary.toString()
    }

    fun setSummary(summary: String) {
        this.summary = summary
    }

    fun getTime(): String {

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val date = dateFormat.parse(time)

        val now = System.currentTimeMillis()
        val relativeTimeSpanString = DateUtils.getRelativeTimeSpanString(date.time, now, DateUtils.MINUTE_IN_MILLIS)


        return relativeTimeSpanString.toString()
    }

    fun setTime(time: String) {
        this.time = time
    }

    fun getLiked(): String {
        return liked.toString()
    }

    fun setLiked(liked: Boolean) {
        this.liked = liked
    }

    fun getURL(): String {
        return url.toString()
    }

    fun setURL(url: String) {
        this.url = url
    }

    fun toHashMap():
            HashMap<String, String> {
        val map = HashMap<String, String>()
        map["author"] = this.getAuthor()
        map["image"] = this.getImage()
        map["liked"] = this.getLiked()
        map["summary"] = this.getSummary()
        map["time"] = this.getOrginalTime()
        map["title"] = this.getTitle()
        map["url"] = this.getURL()
        return map
    }

    private fun getOrginalTime(): String {
        return time.toString()
    }
}
