package com.example.newomakase.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.newomakase.R
import com.example.newomakase.databinding.FragmentPaymentDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import androidx.navigation.fragment.findNavController
import java.text.NumberFormat
import java.util.Locale
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestoreException

class PaymentDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentPaymentDialogBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()

    private var fullPrice: Double = 0.0
    private var courseName: String = ""
    private var bookingDateTime: String = ""
    private var numberOfPeople: Int = 0
    private var customerName: String = ""
    private var customerPhone: String = ""
    private var customerEmail: String = ""
    private var customerNotes: String = ""
    private var depositAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val pricePerPerson = it.getDouble("fullPrice")
            courseName = it.getString("courseName") ?: ""
            bookingDateTime = it.getString("bookingDateTime") ?: ""
            numberOfPeople = it.getInt("numberOfPeople")
            customerName = it.getString("customerName") ?: ""
            customerPhone = it.getString("customerPhone") ?: ""
            customerEmail = it.getString("customerEmail") ?: ""
            customerNotes = it.getString("customerNotes") ?: ""

            fullPrice = pricePerPerson * numberOfPeople
            val depositPercentage = 0.5
            depositAmount = fullPrice * depositPercentage
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("th", "TH"))

        binding.textViewFullPrice.text = currencyFormat.format(fullPrice)
        binding.textViewPaymentTotalPrice.text = currencyFormat.format(depositAmount)
        binding.textViewPaymentCourseName.text = courseName
        binding.textViewPaymentDateTime.text = bookingDateTime
        binding.textViewPaymentNumberOfPeople.text = getString(R.string.num_of_people, numberOfPeople)
        binding.textViewPaymentCustomerName.text = customerName
        binding.textViewPaymentCustomerPhone.text = customerPhone
        binding.textViewPaymentCustomerEmail.text = customerEmail

        binding.buttonPaymentConfirm.text = String.format("ชำระเงิน %s", currencyFormat.format(depositAmount))

        binding.buttonPaymentConfirm.setOnClickListener {
            val dateTimeParts = bookingDateTime.split(", ")
            val bookingDateRaw = dateTimeParts.getOrNull(0) ?: ""
            val bookingTime = dateTimeParts.getOrNull(1) ?: ""

            val bookingDate = bookingDateRaw.trim()
            val courseIdRaw = when (courseName) {
                "คอร์สธรรมดา" -> "regular"
                "คอร์สพรีเมี่ยม" -> "premium"
                else -> courseName
            }
            val courseId = courseIdRaw.trim()
            val availabilityDocId = "${bookingDate}_${courseId}"

            // เพิ่ม Log เพื่อตรวจสอบค่าต่างๆ
            Log.d("PaymentDialogFragment", "courseName received: $courseName")
            Log.d("PaymentDialogFragment", "bookingDateRaw: $bookingDateRaw")
            Log.d("PaymentDialogFragment", "bookingDate: $bookingDate")
            Log.d("PaymentDialogFragment", "bookingTime: $bookingTime")
            Log.d("PaymentDialogFragment", "courseIdRaw: $courseIdRaw")
            Log.d("PaymentDialogFragment", "courseId: $courseId")
            Log.d("PaymentDialogFragment", "availabilityDocId: $availabilityDocId")

            val reservationData = hashMapOf(
                "courseId" to courseId,
                "courseName" to courseName,
                "bookingDate" to bookingDate,
                "bookingTime" to bookingTime,
                "numberOfPeople" to numberOfPeople,
                "customerName" to customerName,
                "customerPhone" to customerPhone,
                "customerEmail" to customerEmail,
                "customerNotes" to customerNotes,
                "fullPrice" to fullPrice,
                "depositAmount" to depositAmount,
                "bookingStatus" to "Active",
                "timestamp" to Timestamp.now()
            )

            firestore.collection("reservations")
                .add(reservationData)
                .addOnSuccessListener { documentReference ->
                    val bookingReferenceId = documentReference.id
                    Log.d("PaymentDialogFragment", "Reservation added with ID: $bookingReferenceId")

                    // ทำการตรวจสอบและอัปเดต availability
                    updateAvailability(availabilityDocId, bookingTime) { success ->
                        if (success) {
                            val bundle = Bundle().apply {
                                putString("bookingReferenceId", documentReference.id)
                                putString("courseName", courseName)
                                putString("bookingDate", bookingDate)
                                putString("bookingTime", bookingTime)
                                putInt("numberOfPeople", numberOfPeople)
                            }
                            findNavController().navigate(R.id.action_paymentDialogFragment_to_bookingSuccessFragment, bundle)
                            Log.d("PaymentDialogFragment", "Navigation to BookingSuccessFragment")
                        } else {
                            dismiss()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("PaymentDialogFragment", "Error adding reservation: ${e.message}")
                    dismiss()
                }
        }

        binding.buttonPaymentCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun updateAvailability(availabilityDocId: String, bookingTime: String, completion: (Boolean) -> Unit) {
        firestore.collection("availability")
            .document(availabilityDocId)
            .get()
            .addOnSuccessListener { availabilityDoc ->
                if (availabilityDoc.exists()) {
                    val timeSlots = availabilityDoc.data
                    val selectedTimeSlot = timeSlots?.get(bookingTime) as? Map<*, *>
                    val bookedCount = selectedTimeSlot?.get("bookedCount") as? Long ?: 0
                    val totalCapacity = selectedTimeSlot?.get("totalCapacity") as? Long ?: 8

                    if (bookedCount + numberOfPeople <= totalCapacity) {
                        val newBookedCount = bookedCount + numberOfPeople
                        val updates = mapOf(
                            "timeSlots.$bookingTime.bookedCount" to newBookedCount
                        )

                        firestore.collection("availability")
                            .document(availabilityDocId)
                            .update(updates)
                            .addOnSuccessListener {
                                Log.d("BookingSuccess", "Availability updated successfully.")
                                completion(true)
                            }
                            .addOnFailureListener { exception ->
                                Log.e("FirestoreError", "Error updating availability: ", exception)
                                completion(false)
                            }
                    } else {
                        Log.d("BookingError", "No available slots for this time.")
                        completion(false)
                    }
                } else {
                    Log.e("FirestoreError", "Availability document not found.")
                    completion(false)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error getting availability document: ", exception)
                completion(false)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}