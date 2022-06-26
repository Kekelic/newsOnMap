package com.example.newsonmap.ui.details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.newsonmap.R

class TypesArrayAdapter(
    context: Context,
    private val resource: Int,
    private val types: Array<String>,
    private val images: MutableList<Int>
) :
    ArrayAdapter<String>(context, resource, types) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = convertView ?: LayoutInflater.from(context)
            .inflate(resource, parent, false)

        val imageType = view.findViewById<ImageView>(R.id.iv_dropdown_type)
        imageType.setImageResource(images[position])

        val textType = view.findViewById<TextView>(R.id.tv_dropdown_type)
        textType.text = types[position]

        return view
    }


}