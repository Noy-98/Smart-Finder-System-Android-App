package com.example.smart_finder_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Signup : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val name = findViewById<TextInputEditText>(R.id.name)
        val email = findViewById<TextInputEditText>(R.id.email)
        val phone_num = findViewById<TextInputEditText>(R.id.phone_num)
        val address = findViewById<TextInputEditText>(R.id.address)
        val password = findViewById<TextInputEditText>(R.id.password)
        val password_lay = findViewById<TextInputLayout>(R.id.password_layout)
        val signup = findViewById<AppCompatButton>(R.id.signup_bttn)
        val ProgressBar : ProgressBar = findViewById(R.id.signUpProgressBar)

        signup.setOnClickListener {
            val nam = name.text.toString()
            val em = email.text.toString()
            val num = phone_num.text.toString()
            val add = address.text.toString()
            val pass = password.text.toString()

            ProgressBar.visibility = View.VISIBLE
            password_lay.isPasswordVisibilityToggleEnabled = true

            if (nam.isEmpty() || em.isEmpty() || num.isEmpty() || add.isEmpty() || pass.isEmpty()){
                if (nam.isEmpty()){
                    name.error = "Enter your name"
                }
                if (em.isEmpty()){
                    email.error = "Enter your email"
                }
                if (num.isEmpty()){
                    phone_num.error = "Enter your phone number"
                }
                if (add.isEmpty()){
                    address.error = "Enter your address"
                }
                if (pass.isEmpty()){
                    password_lay.isPasswordVisibilityToggleEnabled = false
                    password.error = "Enter your password"
                }
                Toast.makeText(this,"All fields are Required!", Toast.LENGTH_SHORT).show()
                ProgressBar.visibility = View.GONE
            } else if (!em.matches(emailPattern.toRegex())){
                ProgressBar.visibility = View.GONE
                email.error="Enter valid email address"
                Toast.makeText(this,"Enter valid email address", Toast.LENGTH_SHORT).show()
            } else if (num.length < 11){
                ProgressBar.visibility = View.GONE
                phone_num.error="Enter your valid phone number"
                Toast.makeText(this,"Enter your valid phone number", Toast.LENGTH_SHORT).show()
            }else if (pass.length < 5){
                password_lay.isPasswordVisibilityToggleEnabled = false
                ProgressBar.visibility = View.GONE
                password.error="Enter your password more than 6 characters"
                Toast.makeText(this,"Enter your password more than 6 characters", Toast.LENGTH_SHORT).show()
            } else {
                auth.createUserWithEmailAndPassword(em, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val databaseRef = database.reference.child("Users").child(auth.currentUser!!.uid)
                        val users: Users = Users(nam, em, num, add, pass, auth.currentUser!!.uid)

                        databaseRef.setValue(users).addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                ProgressBar.visibility = View.GONE
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                Toast.makeText(this, "Sign Up Successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                ProgressBar.visibility = View.GONE
                                Log.d("Firebase", "Database write failed: ${dbTask.exception}")
                                Toast.makeText(this, "Sign Up Failed!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        ProgressBar.visibility = View.GONE
                        Log.d("Firebase", "User creation failed: ${task.exception}")
                        Toast.makeText(
                            this,
                            "Do you have internet Connection? or Do you have already account?, try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}