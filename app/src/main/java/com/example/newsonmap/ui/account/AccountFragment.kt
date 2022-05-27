package com.example.newsonmap.ui.account

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.newsonmap.R
import com.example.newsonmap.databinding.FragmentAccountBinding
import com.example.newsonmap.ui.MainActivity
import com.google.firebase.auth.*
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(layoutInflater)

        loadProfile()
        binding.btnSaveChanges.setOnClickListener { saveChanges() }

        return binding.root
        //return inflater.inflate(R.layout.fragment_account, container, false)
    }

    private fun saveChanges() {
        when {
            TextUtils.isEmpty(
                binding.etAccountFirstname.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    context,
                    "Please enter firstname.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            TextUtils.isEmpty(
                binding.etAccountLastname.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    context,
                    "Please enter lastname.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            TextUtils.isEmpty(binding.etAccountEmail.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    context,
                    "Please enter email.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            TextUtils.isEmpty(
                binding.etAccountPassword.text.toString().trim { it <= ' ' }) -> {
                Toast.makeText(
                    context,
                    "Please enter password.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                val firstname: String =
                    binding.etAccountFirstname.text.toString().trim { it <= ' ' }
                val lastname: String =
                    binding.etAccountLastname.text.toString().trim { it <= ' ' }
                val email: String = binding.etAccountEmail.text.toString().trim { it <= ' ' }
                val password: String =
                    binding.etAccountPassword.text.toString().trim { it <= ' ' }

                val user = FirebaseAuth.getInstance().currentUser

                val db = Firebase.firestore
                val documentReference =
                    db.collection("profile").document(user?.uid!!)
                val profile = hashMapOf(
                    "firstname" to firstname,
                    "lastname" to lastname
                )

                val credential = EmailAuthProvider
                    .getCredential(user.email!!, password)

                user.reauthenticate(credential)
                    .addOnCompleteListener {task->
                        when{
                            task.isSuccessful ->{
                                user.updateEmail(email)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful){
                                            documentReference.set(profile, SetOptions.merge())
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        context,
                                                        "data changed successfully",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                        }
                                    }
                            }
                            task.exception is FirebaseAuthInvalidCredentialsException ->{
                                binding.etAccountPassword.error = "Invalid password"
                                binding.etAccountPassword.requestFocus()
                            }
                        }


                    }

            }

        }
    }

    private fun loadProfile() {
        val user = FirebaseAuth.getInstance().currentUser
        val db = Firebase.firestore
        val documentReference = db.collection("profile").document(user?.uid!!)
        documentReference.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    binding.etAccountFirstname.setText(document["firstname"].toString())
                    binding.etAccountLastname.setText(document["lastname"].toString())
                    binding.etAccountEmail.setText(user.email)
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }

    }

//    companion object {
//
//    }
}