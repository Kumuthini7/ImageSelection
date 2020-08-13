package com.example.imageselection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.image_item.view.*
import java.util.ArrayList

/**
 * Created by Kumuthini.N on 12-08-2020
 */
class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    private var list = ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(list, position)
    }

    fun update(list: ArrayList<String>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItem(item: ArrayList<String>, position: Int) {

            item[position].let {
                Picasso.get().load("file://"+item[position]).fit().centerCrop().into(itemView.image)
            }
        }
    }
}
