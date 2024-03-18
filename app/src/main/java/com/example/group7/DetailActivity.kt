
package com.example.group7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class DetailActivity : AppCompatActivity() {

    private lateinit var txtName: TextView
    private lateinit var txtTitle: TextView
    private lateinit var txtDetail: TextView
    private lateinit var productImage: ImageView
    private lateinit var btnConnect: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var connectionsRef: DatabaseReference
    private lateinit var candidateId: String
    private lateinit var postsList: MutableList<Post>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Initialize Firebase components
        auth = FirebaseAuth.getInstance()
        val db = FirebaseDatabase.getInstance()
        currentUser = auth.currentUser!!
        connectionsRef = db.reference.child("candidates").child(currentUser.uid).child("connections")

        // Get intent extras
        val name = intent.getStringExtra("name") ?: ""
        val title = intent.getStringExtra("title") ?: ""
        val moreDetail = intent.getStringExtra("moreDetails") ?: ""
        val image = intent.getStringExtra("image")
        candidateId = intent.getStringExtra("candidateId") ?: ""

        // Initialize views
        txtName = findViewById(R.id.txt_Can_d_name)
        txtTitle = findViewById(R.id.txt_Can_d_title)
        txtDetail = findViewById(R.id.txt_Can_d_details)
        productImage = findViewById(R.id.Candidate_d_img)
        btnConnect = findViewById(R.id.btn_Connect_D)

        // Set candidate name moreDetail and title
        txtName.text = name
        txtTitle.text = title
        txtDetail.text = moreDetail

        // Load candidate image if available
        if (!image.isNullOrEmpty()) {
            Picasso.get().load(image).into(productImage)
        } else {
            // Handle case when image URL is empty or null
            // For example, show a placeholder image
            productImage.setImageResource(R.drawable.ic_launcher_background)
        }

        // Initialize postsList
        postsList = mutableListOf()

        // Check if the current user is connected with the candidate
        connectionsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.hasChild(candidateId)) {
                    // User is connected with the candidate
                    btnConnect.text = "Connected"
                    btnConnect.isEnabled = false // Disable button as user is already connected
                } else {
                    // User is not connected with the candidate
                    btnConnect.text = "Connect"
                    btnConnect.isEnabled = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        val postsRef = FirebaseDatabase.getInstance().getReference("candidates").child(candidateId).child("posts")
        postsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    postsList = mutableListOf() // Initialize the activity-level postsList
                    for (postSnapshot in snapshot.children) {
                        val post = postSnapshot.getValue(Post::class.java)
                        post?.let { postsList.add(it) }
                    }
                    // Display posts data (e.g., populate a RecyclerView, display in TextViews, etc.)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        // Set click listener for the Connect button
        btnConnect.setOnClickListener {
            // Add logic here to connect with the candidate
            // For example, you can add the candidate to the user's connections
            connectionsRef.child(candidateId).setValue(true)
                .addOnSuccessListener {
                    btnConnect.text = "Connected"
                    btnConnect.isEnabled = false
                    Toast.makeText(this@DetailActivity, "Connected with $name", Toast.LENGTH_SHORT)
                        .show()
                    // Pass the postsList to MainActivity
                    val intent = Intent(this@DetailActivity, MainActivity::class.java)
                    intent.putExtra("postsList", postsList.toTypedArray())
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this@DetailActivity,
                        "Failed to connect with $name",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}
