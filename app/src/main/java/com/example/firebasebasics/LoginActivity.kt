package com.example.firebasebasics

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasebasics.databinding.ActivityLogInBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogInBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null)
            startActivity(Intent(this, MainActivity::class.java)).also {
                finish()
            }

        binding.loginBtn.setOnClickListener {
            hideKeyboard()
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            if (email.isEmpty())
                return@setOnClickListener binding.loginEmail.run {
                    error = "Email required"
                    requestFocus()
                    showKeyboard()
                }

            if (password.isEmpty())
                return@setOnClickListener binding.loginPassword.run {
                    error = "Password required"
                    requestFocus()
                    showKeyboard()
                }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                return@setOnClickListener binding.loginEmail.run {
                    error = "Not an email!"
                    requestFocus()
                    showKeyboard()
                }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful)
                    return@addOnCompleteListener startActivity(
                        Intent(
                            this@LoginActivity,
                            MainActivity::class.java
                        )
                    ).also {
                        finish()
                    }

                Snackbar.make(
                    binding.root,
                    "${task.exception?.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        binding.goToSignUp.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SignUpActivity::class.java
                )
            )
        }
    }
}