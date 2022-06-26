package com.example.newsonmap.ui.authentication

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newsonmap.databinding.ActivityRegisterBinding
import com.example.newsonmap.model.User
import com.example.newsonmap.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = "Register"
        binding.tvLogin.setOnClickListener { onBackPressed() }
        binding.btnRegister.setOnClickListener { register() }

    }

    private fun register() {
        val user = User()
        user.firstname = binding.etRegisterFirstname.text.toString().trim { it <= ' ' }
        user.lastname = binding.etRegisterLastname.text.toString().trim { it <= ' ' }

        val email = binding.etRegisterEmail.text.toString().trim { it <= ' ' }
        val password = binding.etRegisterPassword.text.toString().trim { it <= ' ' }

        when {
            TextUtils.isEmpty(user.firstname) -> {
                makeToast("Please enter firstname.")
            }
            TextUtils.isEmpty(user.lastname) -> {
                makeToast("Please enter lastname.")
            }
            TextUtils.isEmpty(email) -> {
                makeToast("Please enter email.")
            }
            TextUtils.isEmpty(password) -> {
                makeToast("Please enter password.")
            }
            else -> {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser = task.result!!.user!!

                            user.id = firebaseUser.uid
                            firestoreSaveProfile(user)

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

    private fun firestoreSaveProfile(user: User) {
        val documentReference = Firebase.firestore.collection("profile").document(user.id)
        val profile = hashMapOf(
            "firstname" to user.firstname,
            "lastname" to user.lastname
        )
        documentReference.set(profile)
            .addOnSuccessListener {
                Log.d(TAG, "User added")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun startMainActivity() {
        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
        startActivity(intent)
    }
}