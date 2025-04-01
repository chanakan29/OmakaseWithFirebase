package com.example.newomakase.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newomakase.BookingAdapter
import com.example.newomakase.data.Booking
import com.example.newomakase.databinding.FragmentBookingListBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query // Import Query

class BookingListFragment : Fragment() {

    private var _binding: FragmentBookingListBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var bookingAdapter: BookingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewBookings.layoutManager = LinearLayoutManager(requireContext())
        bookingAdapter = BookingAdapter(emptyList()) // Initialize with an empty list
        binding.recyclerViewBookings.adapter = bookingAdapter

        fetchBookingData()
    }

    private fun fetchBookingData() {
        firestore.collection("reservations")
            .orderBy("timestamp", Query.Direction.DESCENDING) // เปลี่ยนตรงนี้เป็น DESCENDING
            .get()
            .addOnSuccessListener { querySnapshot ->
                val bookingList = mutableListOf<Booking>()
                for (document in querySnapshot) {
                    val bookingReferenceId = document.id
                    val courseName = document.getString("courseName") ?: ""
                    val bookingDate = document.getString("bookingDate") ?: ""
                    val bookingTime = document.getString("bookingTime") ?: ""
                    val numberOfPeople = document.getLong("numberOfPeople")?.toInt() ?: 0
                    val customerName = document.getString("customerName") ?: ""
                    val bookingStatus = document.getString("bookingStatus") ?: ""
                    val customerPhone = document.getString("customerPhone") ?: ""
                    val customerEmail = document.getString("customerEmail") ?: ""
                    val fullPrice = document.getDouble("fullPrice") ?: 0.0 // ดึงข้อมูลราคาเต็ม
                    val depositAmount = document.getDouble("depositAmount") ?: 0.0 // ดึงข้อมูลราคามัดจำ

                    val booking = Booking(
                        bookingReferenceId = bookingReferenceId,
                        courseName = courseName,
                        bookingDate = bookingDate,
                        bookingTime = bookingTime,
                        numberOfPeople = numberOfPeople,
                        customerName = customerName,
                        bookingStatus = bookingStatus,
                        customerPhone = customerPhone,
                        customerEmail = customerEmail,
                        fullPrice = fullPrice, // เพิ่มข้อมูลราคาเต็มใน Booking Object
                        depositAmount = depositAmount // เพิ่มข้อมูลราคามัดจำใน Booking Object
                    )
                    bookingList.add(booking)
                }
                bookingAdapter.updateList(bookingList)
                if (bookingList.isEmpty()) {
                    binding.textViewEmptyBookings.visibility = View.VISIBLE
                    binding.recyclerViewBookings.visibility = View.GONE
                } else {
                    binding.textViewEmptyBookings.visibility = View.GONE
                    binding.recyclerViewBookings.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { e ->
                Log.e("BookingListFragment", "Error fetching bookings: ${e.message}")
                binding.textViewEmptyBookings.visibility = View.VISIBLE
                binding.textViewEmptyBookings.text = "เกิดข้อผิดพลาดในการโหลดรายการจอง"
                binding.recyclerViewBookings.visibility = View.GONE
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}