package com.example.newsonmap.ui.list

import android.content.ContentValues
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsonmap.databinding.FragmentListNewsBinding
import com.example.newsonmap.model.News
import com.example.newsonmap.model.NewsType
import com.example.newsonmap.ui.details.ShowNewsDialog
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ListNewsFragment : Fragment(), OnNewsClickListener {

    private lateinit var binding: FragmentListNewsBinding
    private lateinit var adapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListNewsBinding.inflate(layoutInflater)
        setupRecyclerView()
        fetchData()
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.rvNews.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = NewsAdapter()
        adapter.onNewsClickListener = this
        binding.rvNews.adapter = adapter
    }


    private fun fetchData() {
        val db = Firebase.firestore
        val documentReference = db.collection("news").orderBy("date", Query.Direction.DESCENDING)
        documentReference.get()
            .addOnSuccessListener { documents ->
                if (documents != null) {
                    for (document in documents) {
                        val news = News()
                        news.title = document["title"].toString()
                        news.description = document["description"].toString()
                        news.address = document["address"].toString()
                        news.latitude = document["latitude"].toString()
                        news.longitude = document["longitude"].toString()
                        news.timeCreated = document["date"].toString()
                        news.type = NewsType.valueOf(document["type"].toString())

                        val authorId = document["author id"].toString()
                        val profileDocumentReference = db.collection("profile").document(authorId)
                        profileDocumentReference.get()
                            .addOnSuccessListener { profileDocument ->
                                news.authorName = "${profileDocument["firstname"]}  ${profileDocument["lastname"].toString()}"
                            }
                            .addOnFailureListener { e ->
                                Log.w(ContentValues.TAG, "Error loading profile document", e)
                            }

                        val documentId = news.latitude + news.longitude
                        val storageReference = FirebaseStorage.getInstance().reference.child("images/$documentId")
                        val localFile = File.createTempFile("currentImage", "jpg")
                        storageReference.getFile(localFile)
                            .addOnSuccessListener {
                                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                                news.imageBitmap = bitmap
                                adapter.addNews(news)
                            }
                            .addOnFailureListener { e ->
                                adapter.addNews(news)
                                Log.w(ContentValues.TAG, "Error loading image", e)
                            }
                    }
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }



    }

    override fun onNewsClick(latLng: LatLng) {
        val dialog = ShowNewsDialog(latLng)
        dialog.show(requireActivity().supportFragmentManager, "show news dialog")
    }

}