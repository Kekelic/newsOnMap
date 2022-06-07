package com.example.newsonmap.ui.details

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.newsonmap.databinding.DialogCreateNewsBinding
import com.example.newsonmap.ui.MainActivity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class CreateNewsDialog(private val latLng: LatLng,private val address: String) : DialogFragment() {

    private lateinit var binding: DialogCreateNewsBinding
    private var imageUri: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogCreateNewsBinding.inflate(layoutInflater)

        binding.ibAddImage.setOnClickListener{openGallery()}

        binding.btnCreate.setOnClickListener { saveData() }

        return binding.root
    }

    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 100) {
            imageUri = data?.data
        }
    }

    private fun saveData() {
        val title = binding.etCreateTitle.text.toString().trim()
        val description = binding.etCreateDescription.text.toString()
        val date: String
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm")
            date =  LocalDateTime.now().format(formatter)
        } else {
            val formatter = SimpleDateFormat("dd.MM.yyyy. HH:mm")
            date = formatter.format(Date())
        }

        val user = FirebaseAuth.getInstance().currentUser

        val db = Firebase.firestore
        val documentReference =
            db.collection("news").document(latLng.latitude.toString() + latLng.longitude.toString())


        val news = hashMapOf(
            "author id" to user!!.uid,
            "title" to title,
            "description" to description,
            "address" to address,
            "date" to date
        )
        documentReference.set(news)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "News added")
                Toast.makeText(context, title, Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }


    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
        }
    }
}