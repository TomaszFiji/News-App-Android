//package com.example.uniapp
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.google.android.material.snackbar.Snackbar
//
//class AdapterActivity2 (private val imageModelArrayList: MutableList<ModelActivity2>) : RecyclerView.Adapter<ModelActivity2.ViewHolder>() {
//
//    /*
//     * Inflate our views using the layout defined in row_layout.xml
//     */
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.ViewHolder {
//        val inflater = LayoutInflater.from(parent.context)
//        val v = inflater.inflate(R.layout.recycle_category, parent, false)
//
//        return ViewHolder(v)
//    }
//
//    /*
//     * Bind the data to the child views of the ViewHolder
//     */
//    override fun onBindViewHolder(holder: MyAdapter.ViewHolder, position: Int) {
//        val info = imageModelArrayList[position]
//
//        holder.imgView.setImageResource(info.getImages())
//        holder.txtMsg.text = info.getNames()
//    }
//
//    /*
//     * Get the maximum size of the
//     */
//    override fun getItemCount(): Int {
//        return imageModelArrayList.size
//    }
//
//
//
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
//
//        var imgView = itemView.findViewById<View>(R.id.icon) as ImageView
//        var txtMsg = itemView.findViewById<View>(R.id.category_text) as TextView
//
//        init {
//            itemView.setOnClickListener(this)
//        }
//
//        override fun onClick(v: View) {
//            val msg = txtMsg.text
//            val snackbar = Snackbar.make(v, "$msg", Snackbar.LENGTH_LONG)
//            snackbar.show()
//        }
//    }
//}
