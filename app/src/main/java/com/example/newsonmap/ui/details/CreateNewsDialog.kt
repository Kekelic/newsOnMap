package com.example.newsonmap.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.newsonmap.databinding.DialogCreateNewsBinding
import com.example.newsonmap.databinding.FragmentMapsBinding
import com.google.android.gms.location.LocationServices

class CreateNewsDialog : DialogFragment() {

    private lateinit var binding: DialogCreateNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogCreateNewsBinding.inflate(layoutInflater)
        return binding.root
    }
}