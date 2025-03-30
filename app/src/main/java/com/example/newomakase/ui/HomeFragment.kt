package com.example.newomakase.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.newomakase.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var imageViewRestaurant: ImageView
    private lateinit var textViewRestaurantName: TextView
    private lateinit var textViewRestaurantPhone: TextView
    private lateinit var buttonViewCourses: Button

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get references to the views
        imageViewRestaurant = view.findViewById(R.id.imageViewRestaurant)
        textViewRestaurantName = view.findViewById(R.id.textViewRestaurantName)
        textViewRestaurantPhone = view.findViewById(R.id.textViewRestaurantPhone)
        buttonViewCourses = view.findViewById(R.id.buttonViewCourses)

        // Set an image for the restaurant
        imageViewRestaurant.setImageResource(R.drawable.restaurant_image) // Replace with your actual image resource

        // Fetch restaurant information from Firestore
        fetchRestaurantInfo()

        // Set onClickListeners for the buttons
        buttonViewCourses.setOnClickListener {
            // TODO: Navigate to the course listing screen
            val navController = requireView().findNavController()
            navController.navigate(R.id.action_homeFragment_to_courseListFragment)
            Toast.makeText(context, "ไปหน้าเลือกคอร์ส", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchRestaurantInfo() {
        firestore.collection("restaurant_info")
            .document("main_restaurant")
            .get()
            .addOnSuccessListener { document ->
                if (isAdded && document.exists()) {
                    val restaurantName = document.getString("name") ?: "ไม่พบชื่อร้าน"
                    val restaurantPhone = document.getString("phone") ?: "ไม่พบเบอร์โทร"

                    textViewRestaurantName.text = restaurantName
                    textViewRestaurantPhone.text = getString(R.string.restaurant_phone, restaurantPhone)
                }
            }
            .addOnFailureListener { e ->
                if (isAdded) {
                    textViewRestaurantName.text = "เกิดข้อผิดพลาดในการดึงข้อมูล"
                    textViewRestaurantPhone.text = ""
                    android.util.Log.e("HomeFragment", "Error fetching restaurant info: ", e)
                }
            }
    }
}
