package com.example.group7

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var recPostList: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private var postList: MutableList<Post> = mutableListOf()
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var txtViewUsername: TextView
    private lateinit var txtViewEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        txtViewUsername = findViewById(R.id.text_view_username)
        txtViewEmail = findViewById(R.id.text_view_email)
        recPostList = findViewById(R.id.rec_post_list)

        user = auth.currentUser

        if (user == null) {
            val i = Intent(applicationContext, LoginActivity::class.java)
            startActivity(i)
            finish()
        } else {
            txtViewEmail.text = user?.email ?: "Email not found"
        }

        val btnCandidate = findViewById<Button>(R.id.btn_candidate)
        btnCandidate.setOnClickListener {
            val intent = Intent(this@MainActivity, CandidateActivity::class.java)
            startActivity(intent)
        }

        // Display user's name (assuming it's stored in Firebase)
        user?.let { currentUser ->
            val databaseReference = FirebaseDatabase.getInstance().getReference("candidates").child(currentUser.uid)
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val username = snapshot.child("name").value.toString()
                        txtViewUsername.text = username
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }

        // Initialize RecyclerView for posts
        recPostList.layoutManager = LinearLayoutManager(this)
        postAdapter = PostAdapter(this, postList)
        recPostList.adapter = postAdapter

        // Fetch posts from connected candidates
        fetchPostsForConnectedCandidates()
    }

    private fun fetchPostsForConnectedCandidates() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val connectionsRef = FirebaseDatabase.getInstance().getReference("candidates").child(user.uid).child("connections")
            connectionsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // List to store candidate IDs
                        val connectedCandidateIds = mutableListOf<String>()

                        // Iterate through connected candidates and add their IDs to the list
                        for (candidateSnapshot in snapshot.children) {
                            connectedCandidateIds.add(candidateSnapshot.key ?: "")
                        }

                        // Fetch posts for connected candidates
                        fetchPostsForCandidates(connectedCandidateIds)
                    } else {
                        // No connected candidates, you may prompt the user to connect
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    private fun fetchPostsForCandidates(candidateIds: List<String>) {
        for (candidateId in candidateIds) {
            val postsRef = FirebaseDatabase.getInstance().getReference("posts")
            postsRef.orderByChild("candidateId").equalTo(candidateId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (postSnapshot in snapshot.children) {
                                val post = postSnapshot.getValue(Post::class.java)
                                post?.let { postList.add(it) }
                            }
                            postAdapter.notifyDataSetChanged()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
        }
    }



    fun logout(view: View) {
        FirebaseAuth.getInstance().signOut()
        // Redirect the user to the login activity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
