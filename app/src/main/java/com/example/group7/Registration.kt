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
import com.google.firebase.auth.FirebaseUser

class Registration : AppCompatActivity() {

    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var btnReg: Button
    private lateinit var progressbar: ProgressBar
    private lateinit var txtSignIn: TextView
    private lateinit var mAuth: FirebaseAuth

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        mAuth = FirebaseAuth.getInstance()
        txtEmail = findViewById(R.id.txtEmail)
        txtPassword = findViewById(R.id.txtPassword)
        btnReg = findViewById(R.id.btn_Reg)
        progressbar = findViewById(R.id.progressbar)
        txtSignIn = findViewById(R.id.txt_sign_in)

        txtSignIn.setOnClickListener {
            val i = Intent(applicationContext, LoginActivity::class.java)
            startActivity(i)
            finish()
        }

        btnReg.setOnClickListener {
            progressbar.visibility = View.VISIBLE
            val email = txtEmail.text.toString()
            val password = txtPassword.text.toString()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this@Registration, "Enter Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(this@Registration, "Enter Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this@Registration, OnCompleteListener { task ->
                    progressbar.visibility = View.GONE
                    if (task.isSuccessful) {
                        Toast.makeText(this@Registration, "Account Created.", Toast.LENGTH_SHORT).show()
                        val i = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(i)
                        finish()
                    } else {
                        Toast.makeText(this@Registration, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
