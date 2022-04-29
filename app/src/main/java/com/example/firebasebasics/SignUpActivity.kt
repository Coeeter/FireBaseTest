package com.example.firebasebasics

import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasebasics.databinding.ActivitySignUpBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        binding.goToLogin.setOnClickListener { finish() }

        binding.signUpBtn.setOnClickListener {
            hideKeyboard()
            val email = binding.signUpEmail.text.toString()
            val password = binding.signUpPassword.text.toString()
            val confirmPassword = binding.signUpConfirmPassword.text.toString()

            if (email.isEmpty())
                return@setOnClickListener binding.signUpEmail.run {
                    error = "Email required"
                    requestFocus()
                    showKeyboard()
                }

            if (password.isEmpty())
                return@setOnClickListener binding.signUpEmail.run {
                    error = "Password required"
                    requestFocus()
                    showKeyboard()
                }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                return@setOnClickListener binding.signUpEmail.run {
                    error = "Invalid email"
                    requestFocus()
                    showKeyboard()
                }

            if (password != confirmPassword)
                return@setOnClickListener run {
                    binding.signUpPassword.error = "Passwords do not match!"
                    binding.signUpConfirmPassword.apply {
                        error = "Passwords do not match!"
                        requestFocus()
                    }
                    showKeyboard()
                }

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Snackbar.make(
                        binding.root,
                        "Successfully created account",
                        Snackbar.LENGTH_SHORT
                    ).addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar?>() {
                        override fun onShown(transientBottomBar: Snackbar?) {
                            super.onShown(transientBottomBar)
                            Handler(mainLooper).postDelayed({ finish() }, 1000)
                        }
                    }).show()
                    return@addOnCompleteListener
                }

                if (task.exception is FirebaseAuthUserCollisionException)
                    return@addOnCompleteListener binding.signUpEmail.run {
                        error = "Email is already used"
                        requestFocus()
                        showKeyboard()
                    }

                task.exception?.message?.let { message ->
                    Snackbar.make(
                        binding.root,
                        message,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}