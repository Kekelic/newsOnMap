package com.example.newsonmap.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.newsonmap.ui.MainActivity
import com.example.newsonmap.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Login"

        binding.tvRegister.setOnClickListener { startRegisterActivity() }

        binding.btnLogin.setOnClickListener { login() }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            startMainActivity()
        }

    }

    private fun login() {
        val email = binding.etLoginEmail.text.toString().trim { it <= ' ' }
        val password = binding.etLoginPassword.text.toString().trim { it <= ' ' }

        when {
            TextUtils.isEmpty(email) -> {
                makeToast("Please enter email.")
            }
            TextUtils.isEmpty(password) -> {
                makeToast("Please enter password.")
            }
            else -> {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            startMainActivity()
                        } else {
                            makeToast(task.exception!!.message.toString())
                        }
                    }
            }
        }
    }

    private fun makeToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun startMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
    }

    private fun startRegisterActivity() {
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(intent)
    }
}