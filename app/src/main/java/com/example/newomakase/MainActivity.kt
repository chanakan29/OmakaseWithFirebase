package com.example.newomakase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.newomakase.databinding.ActivityMainBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "AvailabilitySetup"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

                availabilityRef.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (!document.exists()) {
                            // สร้าง Document ใหม่พร้อมรอบเวลาที่ถูกต้องตามประเภทคอร์ส
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
                                else -> hashMapOf() // กรณีมี courseId อื่นๆ เพิ่มเติม
                            }

                            val availabilityData = hashMapOf(
                                "date" to formattedDate,
                                "courseId" to courseId,
                                "timeSlots" to timeSlotsMap
                            )
                            availabilityRef.set(availabilityData)
                                .addOnSuccessListener {
                                    Log.d(TAG, "Availability created for $documentId")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error creating availability for $documentId", e)
                                }
                        }
                    } else {
                        Log.d(TAG, "Error getting availability for $documentId", task.exception)
                    }
                }
            }
        }
    }
}