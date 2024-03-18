package com.example.group7

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class CandidateAdapter(private val context: Context, private val candidateList: List<Candidate>) :
    RecyclerView.Adapter<CandidateAdapter.CandidateItemViewHolder>() {

    private var onMoreDetailsClickListener: OnMoreDetailsClickListener? = null

    fun setOnMoreDetailsClickListener(listener: OnMoreDetailsClickListener) {
        this.onMoreDetailsClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.candidate_list, parent, false)
        return CandidateItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CandidateItemViewHolder, position: Int) {
        val candidate = candidateList[position]

        // Bind candidate data to views
        holder.bind(candidate)

        // Set OnClickListener for the "More Details" button
        onMoreDetailsClickListener?.let { listener ->
            holder.btnCanDetail.setOnClickListener {
                listener.onMoreDetailsClick(candidate)
            }
        }
    }

    override fun getItemCount(): Int {
        return candidateList.size
    }

    inner class CandidateItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val canImage: ImageView = itemView.findViewById(R.id.can_image)
        private val txtCanName: TextView = itemView.findViewById(R.id.txt_can_name)
        private val txtCanTitle: TextView = itemView.findViewById(R.id.txt_can_title)
        val btnCanDetail: Button = itemView.findViewById(R.id.btn_can_detail)

        fun bind(candidate: Candidate) {
            txtCanName.text = candidate.name
            txtCanTitle.text = candidate.title

            // Load image using Picasso if photoURL is not empty or null
            if (!candidate.photoUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(candidate.photoUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(canImage)
            }
        }
    }

    // Interface for handling "More Details" button click
    interface OnMoreDetailsClickListener {
        fun onMoreDetailsClick(candidate: Candidate)
    }
}
