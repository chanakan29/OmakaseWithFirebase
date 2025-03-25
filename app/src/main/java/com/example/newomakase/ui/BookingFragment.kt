package com.example.newomakase.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.newomakase.R
import com.example.newomakase.databinding.FragmentBookingBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookingFragment : Fragment() {

    private var _binding: FragmentBookingBinding? = null
    private val binding get() = _binding!!

    private var courseName: String? = null
    private var availableTimes: List<String>? = null
    private var maxSeats: Int = 8 // กำหนดค่าเริ่มต้น
    private var courseId: String? = null // เพิ่ม courseId

    private val firestore = FirebaseFirestore.getInstance() // เพิ่ม Firestore instance

    // เพิ่ม Map เพื่อเก็บจำนวนที่นั่งที่ว่างสำหรับแต่ละรอบเวลา
    private val remainingSeatsMap = mutableMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            courseName = it.getString("name")
            availableTimes = it.getStringArrayList("menu")?.toList()
            maxSeats = it.getInt("seats", 8)
            courseId = it.getString("id")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun fetchAvailability(date: String) {
        courseId?.let { id -> // ใช้ courseId
            firestore.collection("availability")
                .whereEqualTo("date", date)
                .whereEqualTo("courseId", id)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val availabilityDocument = documents.first()
                        val timeSlots = availabilityDocument.get("timeSlots") as? Map<*, *>
                        val formattedTimeSlots = timeSlots?.mapNotNull { (key, value) ->
                            val time = key as? String
                            val slotDetails = value as? Map<*, *>

                            time?.let {
                                val bookedCount = (slotDetails?.get("bookedCount") as? Long) ?: 0L
                                val totalCapacity = (slotDetails?.get("totalCapacity") as? Long) ?: maxSeats.toLong() // ใช้ maxSeats
                                time to mapOf("bookedCount" to bookedCount, "totalCapacity" to totalCapacity)
                            }
                        }?.toMap() ?: emptyMap()

                        updateTimeSpinner(formattedTimeSlots)
                    } else {
                        availableTimes?.let { updateTimeSpinner(it.associateWith { mapOf("bookedCount" to 0L, "totalCapacity" to maxSeats.toLong()) }) } // ใช้ maxSeats
                    }
                }
                .addOnFailureListener { exception ->
                    android.util.Log.w("BookingFragment", "Error getting availability documents.", exception)
                    // Handle error appropriately
                }
        }
    }

    private fun updateTimeSpinner(timeSlots: Map<String, Map<String, Long>>) {
        val availableTimeSlotsFormatted = mutableListOf<String>()
        remainingSeatsMap.clear() // เคลียร์ Map ก่อนอัปเดต

        // สร้าง List ของรอบเวลาและ Sort ตามเวลาเริ่มต้น
        val sortedTimeSlots = timeSlots.keys.sortedWith { time1, time2 ->
            val startTime1 = time1.split("-")[0]
            val startTime2 = time2.split("-")[0]
            startTime1.compareTo(startTime2) // เปรียบเทียบตามเวลาเริ่มต้นที่เป็น String
        }

        sortedTimeSlots.forEach { time ->
            val capacity = timeSlots[time] ?: emptyMap()
            val bookedCount = capacity["bookedCount"] ?: 0
            val totalCapacity = capacity["totalCapacity"] ?: maxSeats.toLong()
            val remainingSeats = totalCapacity - bookedCount
            if (remainingSeats > 0) {
                val formattedTimeSlot = "$time (เหลือ $remainingSeats ที่นั่ง)"
                availableTimeSlotsFormatted.add(formattedTimeSlot)
                remainingSeatsMap[formattedTimeSlot] = remainingSeats.toInt() // เก็บจำนวนที่นั่งที่ว่าง
            }
            // คุณอาจจะต้องการแสดงรอบเวลาที่เต็มแล้วด้วย แต่มีสถานะต่างกัน (เช่น สีเทา)
        }

        val adapterTime = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, availableTimeSlotsFormatted)
        binding.spinnerTime.adapter = adapterTime

        // ตั้งค่า Listener ให้กับ Spinner เวลา (เหมือนเดิม)
        binding.spinnerTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedTimeFormatted = availableTimeSlotsFormatted[position]
                val availableSeats = remainingSeatsMap[selectedTimeFormatted] ?: 0
                updatePeopleSpinner(availableSeats) // อัปเดต Spinner จำนวนคน
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // ทำอะไรเมื่อไม่มีการเลือก (ถ้าต้องการ)
            }
        }
    }

    private fun updatePeopleSpinner(maxAvailable: Int) {
        val peopleOptions = (1..maxAvailable).map { it.toString() }
        val adapterPeople = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, peopleOptions)
        binding.spinnerPeople.adapter = adapterPeople
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewCourseName.text = courseName ?: "ชื่อคอร์ส"

        val calendarMin = Calendar.getInstance()
        calendarMin.add(Calendar.DAY_OF_YEAR, 3)
        binding.datePicker.minDate = calendarMin.timeInMillis

        // กำหนดวันที่สูงสุดที่เลือกได้ (วันที่ต่ำสุด + 2 เดือน)
        val calendarMax = Calendar.getInstance()
        calendarMax.timeInMillis = calendarMin.timeInMillis // เริ่มต้นจากวันที่ต่ำสุด
        calendarMax.add(Calendar.MONTH, 2)
        binding.datePicker.maxDate = calendarMax.timeInMillis

        // ดึงข้อมูล Availability สำหรับวันที่ต่ำสุดที่เลือกได้เมื่อ Fragment ถูกสร้าง
        val initialDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendarMin.time)
        fetchAvailability(initialDate)

        // ตั้งค่า Listener ให้กับ DatePicker (เหมือนเดิม)
        binding.datePicker.setOnDateChangedListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth)
            fetchAvailability(selectedDate)
        }

        binding.buttonBookCourse.setOnClickListener {
            // TODO: Implement logic for booking the course
            // คุณสามารถดึงข้อมูลที่เลือกจาก UI Elements ได้ที่นี่
            val selectedDate = "${binding.datePicker.year}-${binding.datePicker.month + 1}-${binding.datePicker.dayOfMonth}"
            val selectedTime = binding.spinnerTime.selectedItem.toString()
            val numberOfPeople = binding.spinnerPeople.selectedItem.toString().toInt()

            val bundle = Bundle().apply {
                putString("courseId", courseId)
                putString("selectedDate", selectedDate)
                putString("selectedTime", selectedTime)
                putInt("numberOfPeople", numberOfPeople)
            }
            findNavController().navigate(R.id.actionBookingToUserDetails, bundle)
        }

        // ตั้งค่า Spinner สำหรับเลือกจำนวนคน (เริ่มต้นให้เลือกได้ถึง maxSeats)
        val peopleOptions = (1..maxSeats).map { it.toString() }
        val adapterPeople = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, peopleOptions)
        binding.spinnerPeople.adapter = adapterPeople
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}