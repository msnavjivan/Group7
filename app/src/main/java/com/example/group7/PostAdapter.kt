package com.example.group7

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.group7.R
import com.squareup.picasso.Picasso

class PostAdapter(private val context: Context, private val postList: List<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]

        // Bind post data to views
        holder.txtPostName.text = post.name
        holder.txtPostTitle.text = post.title
        holder.txtPostDetail.text = post.detail

        // Check if the imageUrl is not empty before loading the image
        if (post.imageUrl.isNotEmpty()) {
            Picasso.get()
                .load(post.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(holder.postImage)
        } else {
            // Handle the case where imageUrl is empty
            // For example, show a placeholder image or hide the ImageView
            holder.postImage.setImageResource(R.drawable.ic_launcher_background)
            // Or you can hide the ImageView
            // holder.postImage.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImage: ImageView = itemView.findViewById(R.id.post_image)
        val txtPostName: TextView = itemView.findViewById(R.id.txt_post_name)
        val txtPostTitle: TextView = itemView.findViewById(R.id.txt_post_title)
        val txtPostDetail: TextView = itemView.findViewById(R.id.txt_post_detail)
    }
}
