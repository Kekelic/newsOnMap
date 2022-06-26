package com.example.newsonmap.ui.details

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import com.example.newsonmap.R
import com.example.newsonmap.databinding.DialogCreateNewsBinding
import com.example.newsonmap.model.News
import com.example.newsonmap.model.NewsType
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class CreateNewsDialog(
    private val latLng: LatLng,
    private val address: String,
    private val onCreateNewsListener: OnCreateNewsListener
) : DialogFragment() {

    private lateinit var binding: DialogCreateNewsBinding
    private var imageUri: Uri? = null
    private lateinit var photoFile: File
    private val fileName = "NewsOnMapPhoto"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogCreateNewsBinding.inflate(layoutInflater)

        binding.ibAddImage.setOnClickListener { openGallery() }
        binding.ibCamera.setOnClickListener { takePhoto() }

        binding.btnCreate.setOnClickListener { saveData() }
        binding.btnCancel.setOnClickListener { this.dismiss() }

        setAdapter()

        return binding.root
    }

    private fun setAdapter() {
        val types = resources.getStringArray(R.array.news_types)
        val arrayAdapter = TypesArrayAdapter(
            requireContext(),
            R.layout.dropdown_item_types,
            types,
            getTypeImagesResourceID()
        )
        binding.autoCompleteTextType.setAdapter(arrayAdapter)
    }

    private fun getTypeImagesResourceID(): MutableList<Int> {
        val images = mutableListOf<Int>()
        images.add(R.drawable.ic_event)
        images.add(R.drawable.ic_ad)
        images.add(R.drawable.ic_info)
        return images
    }

    private val cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                imageUri = photoFile.absoluteFile.toUri()
                val imageBitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                binding.ivImage.setImageBitmap(imageBitmap)
            }
        }

    private fun takePhoto() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFile()

        val fileProvider = FileProvider.getUriForFile(
            requireContext(),
            "com.example.newsonmap.fileprovider",
            photoFile
        )
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        cameraResultLauncher.launch(cameraIntent)
    }

    private fun getPhotoFile(): File {
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDir)
    }

    private val galleryResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data?.data
                binding.ivImage.setImageURI(imageUri)
            }
        }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        galleryResultLauncher.launch(galleryIntent)
    }

    private fun saveData() {
        val news = News()
        news.title = binding.etCreateTitle.text.toString().trim()
        news.description = binding.etCreateDescription.text.toString()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm")
            news.timeCreated = LocalDateTime.now().format(formatter)
        } else {
            val formatter = SimpleDateFormat("dd.MM.yyyy. HH:mm")
            news.timeCreated = formatter.format(Date())
        }
        val type = binding.autoCompleteTextType.text.toString()

        when {
            TextUtils.isEmpty(news.title) -> {
                makeToast("Please enter title.")
            }
            TextUtils.isEmpty(news.description) -> {
                makeToast("Please enter description.")
            }
            TextUtils.isEmpty(type) -> {
                makeToast("Please select type.")
            }
            else -> {
                news.type = NewsType.valueOf(type)

                val firebaseUser = FirebaseAuth.getInstance().currentUser
                firestoreSaveNews(news, firebaseUser!!.uid)
                storageSaveImage()

                onCreateNewsListener.createMarker(latLng, news.type.toString())
                dialog!!.dismiss()
            }
        }

    }

    private fun firestoreSaveNews(news: News, authorId: String) {
        val db = Firebase.firestore
        val documentId = latLng.latitude.toString() + latLng.longitude.toString()
        val documentReference =
            db.collection("news").document(documentId)

        val hashMapNews = hashMapOf(
            "author id" to authorId,
            "title" to news.title,
            "description" to news.description,
            "address" to address,
            "date" to news.timeCreated,
            "type" to news.type.toString(),
            "latitude" to latLng.latitude,
            "longitude" to latLng.longitude
        )
        documentReference.set(hashMapNews)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "News added")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }

    private fun storageSaveImage() {
        val documentId = latLng.latitude.toString() + latLng.longitude.toString()
        val storageReference = FirebaseStorage.getInstance().getReference("images/$documentId")
        imageUri?.let { storageReference.putFile(it) }
    }

    private fun makeToast(message: String){
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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