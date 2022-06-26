package com.example.newsonmap.ui.details

import android.app.Dialog
import android.content.ContentValues
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.newsonmap.R
import com.example.newsonmap.databinding.DialogShowNewsBinding
import com.example.newsonmap.model.News
import com.example.newsonmap.model.NewsType
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

        binding.btnClose.setOnClickListener { dismiss() }

        return binding.root
    }

    private fun loadNewsInfo() {

        val db = Firebase.firestore
        val documentId = latLng.latitude.toString() + latLng.longitude.toString()
        val newsDocumentReference = db.collection("news").document(documentId)

        val news = News()

        newsDocumentReference.get()
            .addOnSuccessListener { newsDocument ->
                news.title = newsDocument["title"].toString()
                news.description = newsDocument["description"].toString()
                news.address = newsDocument["address"].toString()
                news.timeCreated = newsDocument["date"].toString()
                news.type = NewsType.valueOf(newsDocument["type"].toString())

                val authorId = newsDocument["author id"].toString()
                val profileDocumentReference = db.collection("profile").document(authorId)
                profileDocumentReference.get()
                    .addOnSuccessListener { profileDocument ->
                        news.authorName =
                            "${profileDocument["firstname"].toString()} ${profileDocument["lastname"].toString()}"
                        setupNewsData(news)
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
                news.imageBitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                setupNewsImage(news)
            }
            .addOnFailureListener { e ->
                setupAlternativeNewsImage(news)
                Log.w(ContentValues.TAG, "Error loading image", e)
            }

    }

    private fun setupNewsData(news: News) {
        binding.tvTitle.text = news.title
        binding.tvDescription.text = news.description
        val address = "Address: ${news.address}"
        binding.tvAddress.text = address
        val timeCreated = "Creation time: ${news.timeCreated}"
        binding.tvTimeCreated.text = timeCreated
        val author = "Author: ${news.authorName}"
        binding.tvAuthor.text = author
        val type = "Type: ${news.type.toString().lowercase()}"
        binding.tvType.text = type
    }

    private fun setupNewsImage(news: News) {
        binding.ivImage.setImageBitmap(news.imageBitmap)
    }

    private fun setupAlternativeNewsImage(news: News){
        when (news.type) {
                NewsType.EVENT -> {
                    binding.ivImage.setImageResource(R.drawable.ic_event)
                }
                NewsType.AD -> {
                    binding.ivImage.setImageResource(R.drawable.ic_ad)

                }
                else -> {
                    binding.ivImage.setImageResource(R.drawable.ic_info)

                }
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