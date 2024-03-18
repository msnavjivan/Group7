package com.example.group7

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var progressbar: ProgressBar
    private lateinit var txtSignUp: TextView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var usersRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        usersRef = FirebaseDatabase.getInstance().getReference("users")
        txtEmail = findViewById(R.id.txtUser)
        txtPassword = findViewById(R.id.txtPass)
        btnLogin = findViewById(R.id.btn_Login)
        progressbar = findViewById(R.id.progressbar)
        txtSignUp = findViewById(R.id.txt_sign_in)

        txtSignUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, Registration::class.java)
            startActivity(intent)
            finish()
        }

        btnLogin.setOnClickListener {
            progressbar.visibility = View.VISIBLE
            val email = txtEmail.text.toString()
            val password = txtPassword.text.toString()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this@LoginActivity, "Enter Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this@LoginActivity, "Enter Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this@LoginActivity, OnCompleteListener<AuthResult> { task ->
                    progressbar.visibility = View.GONE
                    if (task.isSuccessful) {
                        Toast.makeText(this@LoginActivity, "Login Successfully.", Toast.LENGTH_SHORT).show()

                        val currentUser = mAuth.currentUser
                        currentUser?.uid?.let { userId ->
                            usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (!snapshot.exists()) {
                                        // If the user node doesn't exist, create it and connect with existing candidates
                                        val candidatesRef = FirebaseDatabase.getInstance().getReference("candidates")
                                        candidatesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(candidatesSnapshot: DataSnapshot) {
                                                if (candidatesSnapshot.exists()) {
                                                    for (candidateSnapshot in candidatesSnapshot.children) {
                                                        val candidateId = candidateSnapshot.key
                                                        candidateId?.let {
                                                            usersRef.child(userId).child("connections").child(candidateId).setValue(true)
                                                        }
                                                    }
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                // Handle error
                                            }
                                        })
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Handle error
                                }
                            })
                        }

                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
