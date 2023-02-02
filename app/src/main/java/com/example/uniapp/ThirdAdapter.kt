package com.example.uniapp

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class ThirdAdapter (private val thumbnailModelArrayList: MutableList<ThumbnailModel>, activity: AppCompatActivity) : RecyclerView.Adapter<ThirdAdapter.ThumbnailViewHolder>() {

    private val activity = activity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ThumbnailViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.recycle_view_activity_3, parent, false)
        Log.d("TAG" , " list ad: " +thumbnailModelArrayList.toString())

        return ThumbnailViewHolder(v)
    }

    override fun onBindViewHolder(holder: ThumbnailViewHolder, position: Int) {
        val info = thumbnailModelArrayList[position]

        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        holder.title.text = info.getTitle()
        holder.author.text = info.getAuthor()
        holder.time.text = info.getTime()
        holder.url = info.getURL()
        if (info.getImage().isNotEmpty()) {
            Picasso.get().load(info.getImage()).fit().into(holder.imageView)
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
            ContextCompat.startActivity(activity, newIntent, null)
        } catch (e: Exception) {
            Log.i("AAAAA", "actvity2")
        }
    }


    inner class ThumbnailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

        var title = itemView.findViewById<View>(R.id.mini_title_thumbnail) as MaterialTextView
        var author = itemView.findViewById<View>(R.id.mini_author_thumbnail) as MaterialTextView
        var time = itemView.findViewById<View>(R.id.mini_thumbnail_time) as MaterialTextView
        var url: String? = null
        val imageView: ImageView = itemView.findViewById(R.id.mini_thumbnail_image)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            lunchA3(url, time.text.toString(), title.text.toString())
        }
    }
}