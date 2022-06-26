package com.example.newsonmap.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.newsonmap.R
import com.example.newsonmap.databinding.DialogMapSettingsBinding
import com.example.newsonmap.model.MapMode

class MapSettingsDialog : DialogFragment() {

    private lateinit var binding: DialogMapSettingsBinding
    private lateinit var mapMode: MapMode
    private var hours : Int = 168


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogMapSettingsBinding.inflate(layoutInflater)

        setupMapModeRadioGroup()
        setupLastNewsRadioGroup()

        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnSave.setOnClickListener { saveSettings() }

        setupStartingMapModeRadioButton()
        setupStartingLastNewsRadioButton()


        return binding.root
    }

    private fun setupMapModeRadioGroup() {
        binding.rgMapMode.setOnCheckedChangeListener { _, id ->
            mapMode = when (id) {
                binding.rbHybrid.id -> {
                    MapMode.HYBRID
                }
                binding.rbTerrain.id -> {
                    MapMode.TERRAIN
                }
                else ->
                    MapMode.NORMAL
            }
        }
    }

    private fun setupLastNewsRadioGroup(){
        binding.rgLastNews.setOnCheckedChangeListener { _, id ->
            hours = when (id) {
                binding.rbDay.id -> {
                    24
                }
                binding.rbHalfDay.id -> {
                    12
                }
                else ->
                    168
            }
        }
    }

    private fun saveSettings() {
        val preferenceManager = PreferenceManager()
        preferenceManager.setMapMode(mapMode.toString())
        preferenceManager.setTimeLastNews(hours)

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, MapsFragment())
            .commit();
        dismiss()
    }

    private fun setupStartingMapModeRadioButton() {
        val preferenceManager = PreferenceManager()
        when (MapMode.valueOf(preferenceManager.getMapMode()!!)) {
            MapMode.HYBRID -> {
                binding.rgMapMode.check(binding.rbHybrid.id)
                mapMode = MapMode.HYBRID
            }
            MapMode.TERRAIN -> {
                binding.rgMapMode.check(binding.rbTerrain.id)
                mapMode = MapMode.TERRAIN
            }
            else -> {
                binding.rgMapMode.check(binding.rbNormal.id)
                mapMode = MapMode.NORMAL
            }


        }
    }

    private fun setupStartingLastNewsRadioButton(){
        val preferenceManager = PreferenceManager()
        when (preferenceManager.getTimeLastNews()) {
            24 -> {
                binding.rgLastNews.check(binding.rbDay.id)
                hours = 24
            }
            12 -> {
                binding.rgLastNews.check(binding.rbHalfDay.id)
                hours = 12
            }
            else -> {
                binding.rgLastNews.check(binding.rbWeek.id)
                hours = 168
            }


        }
    }
}