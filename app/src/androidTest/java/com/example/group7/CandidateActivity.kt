package com.example.group7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class CandidateActivity : AppCompatActivity(), CandidateAdapter.OnMoreDetailsClickListener {

    private lateinit var recCandidate: RecyclerView
    private lateinit var candidateAdapter: CandidateAdapter
    private var candidateList: MutableList<Candidate> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_candidate)

        recCandidate = findViewById(R.id.can_rec)
        recCandidate.layoutManager = LinearLayoutManager(this)
        candidateAdapter = CandidateAdapter(this, candidateList)
        recCandidate.adapter = candidateAdapter

        // Set the listener
        candidateAdapter.setOnMoreDetailsClickListener(this)

        // Populate candidate data from Firebase
        fetchCandidateData()

        // Button to return to MainActivity
        val btnHome = findViewById<Button>(R.id.btn_home)
        btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onMoreDetailsClick(candidate: Candidate) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("name", candidate.name)
        intent.putExtra("title", candidate.title)
        intent.putExtra("image", candidate.photoUrl)
        intent.putExtra("candidateId", candidate.id)
        intent.putExtra("moreDetails", candidate.moreDetails)
        startActivity(intent)
    }

    private fun fetchCandidateData() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("candidates")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (candidateSnapshot in snapshot.children) {
                        val id = candidateSnapshot.key ?: ""
                        val photoUrl = candidateSnapshot.child("photoUrl").getValue(String::class.java) ?: ""
                        val name = candidateSnapshot.child("name").getValue(String::class.java) ?: ""
                        val title = candidateSnapshot.child("title").getValue(String::class.java) ?: ""
                        val moreDetails = candidateSnapshot.child("moreDetails").getValue(String::class.java) ?: ""

                        // Retrieve connections as Map<String, Boolean>
                        val connections = candidateSnapshot.child("connections").value as? Map<String, Boolean> ?: emptyMap()

                        // Retrieve posts data as Map<String, Post>
                        val postsMap = mutableMapOf<String, Post>()
                        candidateSnapshot.child("connections").children.forEach { postSnapshot ->
                            val postId = postSnapshot.key ?: ""
                            val post = snapshot.child("posts").child(postId).getValue(Post::class.java)
                            post?.let { postsMap[postId] = it }
                        }

                        val candidate = Candidate(id, photoUrl, name, title, moreDetails, connections, postsMap)
                        candidateList.add(candidate)
                    }
                    candidateAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}
