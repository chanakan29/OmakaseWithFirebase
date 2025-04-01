package com.example.newomakase.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.newomakase.databinding.FragmentNavigationBinding

class NavigationFragment : Fragment() {

    private var _binding: FragmentNavigationBinding? = null
    private val binding get() = _binding!!

    private val restaurantLocationName = "มหาวิทยาลัยเกษตรศาสตร์ วิทยาเขตศรีราชา" // ชื่อสถานที่

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNavigationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        openGoogleMaps()
    }

    private fun openGoogleMaps() {
        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(restaurantLocationName)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}