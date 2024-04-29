package com.example.smart_finder_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        val login : AppCompatButton = findViewById(R.id.login_bttn)
        var signup_bttn : TextView = findViewById(R.id.sign_up_bttn)
        val Email : TextInputEditText = findViewById(R.id.email)
        val Password : TextInputEditText = findViewById(R.id.password)
        val ProgressBar : ProgressBar = findViewById(R.id.signInProgressBar)
        val password_lay = findViewById<TextInputLayout>(R.id.password_layout)


        login.setOnClickListener {
            ProgressBar.visibility = View.VISIBLE
            password_lay.isPasswordVisibilityToggleEnabled = true

            val email = Email.text.toString()
            val password = Password.text.toString()

            if (email.isEmpty() || password.isEmpty()){
                if (email.isEmpty()){
                    Email.error = "Enter email address"
                }
                if (password.isEmpty()){
                    password_lay.isPasswordVisibilityToggleEnabled = false
                    Password.error = "Enter your password"
                }
                Toast.makeText(this,"All fields are Required!", Toast.LENGTH_SHORT).show()
                ProgressBar.visibility = View.GONE
            } else if (!email.matches(emailPattern.toRegex())){
                ProgressBar.visibility = View.GONE
                Email.error="Enter valid email address"
                Toast.makeText(this,"Enter valid email address", Toast.LENGTH_SHORT).show()
            } else if (password.length < 6){
                password_lay.isPasswordVisibilityToggleEnabled = false
                ProgressBar.visibility = View.GONE
                Password.error="Wrong Password"
                Toast.makeText(this,"Enter your password more than 8 characters", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful){
                        ProgressBar.visibility = View.GONE
                        val intent = Intent(this, HomeDashboard::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        Toast.makeText(this, "Login Successfully", Toast.LENGTH_SHORT).show()
                    }else{
                        ProgressBar.visibility = View.GONE
                        Toast.makeText(this, "Something went wrong, try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        signup_bttn.setOnClickListener {
            val intent = Intent (this, Signup::class.java)
            startActivity(intent)
        }
    }
}