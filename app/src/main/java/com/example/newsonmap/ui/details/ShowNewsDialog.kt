package com.example.newsonmap.ui.details

import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import com.example.newsonmap.databinding.DialogShowNewsBinding
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ShowNewsDialog(private val latLng: LatLng) : DialogFragment() {

    private lateinit var binding: DialogShowNewsBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogShowNewsBinding.inflate(layoutInflater)

        loadNewsInfo()

        return binding.root
    }

    private fun loadNewsInfo() {

        val db = Firebase.firestore
        val documentId = latLng.latitude.toString() + latLng.longitude.toString()
        val newsDocumentReference =
            db.collection("news").document(documentId)

        newsDocumentReference.get()
            .addOnSuccessListener { document ->
                binding.tvTitle.text = document["title"].toString()
                binding.tvDescription.text = document["description"].toString()
                val address = "Address: ${document["address"].toString()}"
                binding.tvAddress.text = address
                val timeCreated = "Creation time: ${document["date"].toString()}"
                binding.tvTimeCreated.text = timeCreated

                val authorId = document["author id"].toString()
                val profileDocumentReference = db.collection("profile").document(authorId)
                profileDocumentReference.get()
                    .addOnSuccessListener { document ->
                        val author =
                            "Author: ${document["firstname"].toString()} ${document["lastname"].toString()}"
                        binding.tvAuthor.text = author
                    }
                    .addOnFailureListener { e ->
                        Log.w(ContentValues.TAG, "Error loading profile document", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error loading news document", e)
            }

        val storageReference = FirebaseStorage.getInstance().reference.child("images/$documentId")
        val localFile = File.createTempFile("currentImage", "jpg")
        storageReference.getFile(localFile)
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                binding.ivImage.setImageBitmap(bitmap)
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error loading image", e)
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