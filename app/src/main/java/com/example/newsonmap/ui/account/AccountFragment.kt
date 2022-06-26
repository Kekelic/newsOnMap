package com.example.newsonmap.ui.account

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.newsonmap.databinding.FragmentAccountBinding
import com.example.newsonmap.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(layoutInflater)

        loadProfile()
        binding.btnSaveChanges.setOnClickListener { saveChanges() }

        return binding.root
    }


    private fun loadProfile() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val db = Firebase.firestore
        val documentReference = db.collection("profile").document(firebaseUser?.uid!!)
        documentReference.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    binding.etAccountFirstname.setText(document["firstname"].toString())
                    binding.etAccountLastname.setText(document["lastname"].toString())
                    binding.etAccountEmail.setText(firebaseUser.email)
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun saveChanges() {
        val user = User()
        user.firstname = binding.etAccountFirstname.text.toString().trim { it <= ' ' }
        user.lastname = binding.etAccountLastname.text.toString().trim { it <= ' ' }

        val email = binding.etAccountEmail.text.toString().trim { it <= ' ' }
        val password = binding.etAccountPassword.text.toString().trim { it <= ' ' }

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
                val firebaseUser = FirebaseAuth.getInstance().currentUser

                val credential = EmailAuthProvider.getCredential(firebaseUser?.email!!, password)

                firebaseUser.reauthenticate(credential)
                    .addOnCompleteListener { credentialTask ->
                        when {
                            credentialTask.isSuccessful -> {
                                firebaseUser.updateEmail(email)
                                    .addOnCompleteListener { emailTask ->
                                        if (emailTask.isSuccessful) {
                                            user.id = firebaseUser.uid
                                            firestoreUpdateProfile(user)
                                        }
                                        else{
                                            makeToast(emailTask.exception!!.message.toString())
                                        }
                                    }
                            }
                            credentialTask.exception is FirebaseAuthInvalidCredentialsException -> {
                                binding.etAccountPassword.error = "Invalid password"
                                binding.etAccountPassword.requestFocus()
                            }
                        }
                    }
            }
        }
    }

    private fun makeToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun firestoreUpdateProfile(user: User) {
        val documentReference = Firebase.firestore.collection("profile").document(user.id)
        val profile = hashMapOf(
            "firstname" to user.firstname,
            "lastname" to user.lastname
        )
        documentReference.set(profile, SetOptions.merge())
            .addOnSuccessListener {
                makeToast("data changed successfully")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }


}