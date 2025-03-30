package com.example.newomakase

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.newomakase.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView // เพิ่มตัวแปร
    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "AvailabilitySetup"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        bottomNavigationView = findViewById(R.id.bottomNavigationView) // Get Reference
        bottomNavigationView.setupWithNavController(navController) // Setup with NavController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment,
                R.id.bookingListFragment,
                R.id.navigationFragment,
                R.id.notificationsFragment,
                R.id.profileFragment -> {
                    bottomNavigationView.visibility = View.VISIBLE
                }
                else -> {
                    bottomNavigationView.visibility = View.GONE
                }
            }
        }

        setupInitialAvailability() // คุณสามารถเก็บ Function นี้ไว้ได้
    }

    private fun setupInitialAvailability() {
        val today = LocalDate.now()
        val daysAhead = 70 // สร้างล่วงหน้า 2 เดือน

        for (i in 0..daysAhead) {
            val date = today.plusDays(i.toLong())
            val formattedDate = date.format(DateTimeFormatter.ISO_DATE)
            val courseIds = listOf("regular", "premium")

            for (courseId in courseIds) {
                val documentId = "${formattedDate}_${courseId}"
                val availabilityRef = firestore.collection("availability").document(documentId)

                firestore.runTransaction { transaction ->
                    val document = transaction.get(availabilityRef)
                    if (!document.exists()) {
                        val timeSlotsMap = when (courseId) {
                            "premium" -> hashMapOf(
                                "10:00-12:00" to hashMapOf("bookedCount" to 0, "totalCapacity" to 8),
                                "15:00-17:00" to hashMapOf("bookedCount" to 0, "totalCapacity" to 8)
                            )
                            "regular" -> hashMapOf(
                                "12:00-13:30" to hashMapOf("bookedCount" to 0, "totalCapacity" to 8),
                                "14:00-15:30" to hashMapOf("bookedCount" to 0, "totalCapacity" to 8),
                                "16:00-17:30" to hashMapOf("bookedCount" to 0, "totalCapacity" to 8),
                                "18:00-19:30" to hashMapOf("bookedCount" to 0, "totalCapacity" to 8)
                            )
                            else -> hashMapOf()
                        }

                        val availabilityData = hashMapOf(
                            "date" to formattedDate,
                            "courseId" to courseId,
                            "timeSlots" to timeSlotsMap
                        )

                        transaction.set(availabilityRef, availabilityData)
                        Log.d(TAG, "Availability created for $documentId")
                    }
                }.addOnFailureListener { e ->
                    Log.w(TAG, "Error creating availability for $documentId", e)
                }
            }
        }
    }
}