package com.example.newomakase.ui

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.newomakase.R
import com.example.newomakase.databinding.FragmentUserDetailsBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserDetailsFragment : Fragment() {

    private var _binding: FragmentUserDetailsBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()
    private val alphabetOnlyFilter = InputFilter { source, _, _, _, _, _ ->
        if (source.all { Character.isLetter(it) }) null else ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val courseId = it.getString("courseId") ?: ""
            val selectedDate = it.getString("selectedDate") ?: ""
            val selectedTimeWithSeats = it.getString("selectedTime") ?: ""
            val numberOfPeople = it.getInt("numberOfPeople")

            val courseNameThai = when (courseId) {
                "regular" -> "คอร์สธรรมดา"
                "premium" -> "คอร์สพรีเมี่ยม"
                else -> courseId
            }

            val inputDateFormat = SimpleDateFormat("yyyy-M-d", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = try {
                val date = inputDateFormat.parse(selectedDate)
                outputDateFormat.format(date ?: Date())
            } catch (e: Exception) {
                selectedDate
            }

            val timeSlotOnly = selectedTimeWithSeats.split(" ").getOrNull(0) ?: selectedTimeWithSeats
            val bookingDateTimeString = "$formattedDate, $timeSlotOnly"

            binding.apply {
                textViewSelectedCourse.text = getString(R.string.selected_course, courseNameThai)
                textViewBookingDateTime.text = getString(R.string.booking_datetime, formattedDate, timeSlotOnly)
                textViewNumberOfPeople.text = getString(R.string.number_of_people, numberOfPeople)

                editTextFirstName.filters = arrayOf(alphabetOnlyFilter)
                editTextLastName.filters = arrayOf(alphabetOnlyFilter)
                editTextPhone.filters = arrayOf(InputFilter.LengthFilter(10))
                editTextPhone.addTextChangedListener(phoneWatcher)
                editTextEmail.addTextChangedListener(emailWatcher)

                buttonConfirmBooking.setOnClickListener {
                    if (isFormValid()) {
                        val firstName = editTextFirstName.text.toString()
                        val lastName = editTextLastName.text.toString()
                        val phone = editTextPhone.text.toString()
                        val email = editTextEmail.text.toString()
                        val customerName = "$firstName $lastName"
                        val customerNotes = editTextNotes.text.toString()

                        firestore.collection("courses")
                            .document(courseId)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val fullPrice = document.getDouble("price") ?: 0.0

                                    val bundle = Bundle().apply {
                                        putDouble("fullPrice", fullPrice)
                                        putString("courseName", courseNameThai)
                                        putString("bookingDateTime", bookingDateTimeString)
                                        putInt("numberOfPeople", numberOfPeople)
                                        putString("customerName", customerName)
                                        putString("customerPhone", phone)
                                        putString("customerEmail", email)
                                        putString("customerNotes", customerNotes)
                                    }

                                    findNavController().navigate(R.id.action_userDetailsFragment_to_paymentDialogFragment, bundle)

                                } else {
                                    // TODO: Handle กรณีไม่พบข้อมูลคอร์ส
                                }
                            }
                            .addOnFailureListener { e ->
                                // TODO: Handle กรณีเกิดข้อผิดพลาดในการดึงข้อมูล
                            }
                    }
                }
            }
        }
    }

    private fun isFormValid(): Boolean {
        val firstName = binding.editTextFirstName.text.toString()
        val lastName = binding.editTextLastName.text.toString()
        val phone = binding.editTextPhone.text.toString()
        val email = binding.editTextEmail.text.toString()

        return when {
            firstName.isBlank() -> {
                binding.editTextFirstName.error = "กรุณากรอกชื่อ"
                false
            }
            lastName.isBlank() -> {
                binding.editTextLastName.error = "กรุณากรอกนามสกุล"
                false
            }
            !phone.matches(Regex("^[0-9]{10}$")) -> {
                binding.editTextPhone.error = "กรุณากรอกเบอร์โทรศัพท์ให้ครบ 10 ตัว"
                false
            }
            email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.editTextEmail.error = "กรุณากรอกอีเมลให้ถูกต้อง"
                false
            }
            else -> true
        }
    }

    private val phoneWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            binding.editTextPhone.error = if (s?.matches(Regex("^[0-9]{10}$")) == true) null else "กรุณากรอกเบอร์โทรศัพท์ 10 ตัว"
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private val emailWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            val email = s.toString()
            binding.editTextEmail.error = if (email.isBlank() || Patterns.EMAIL_ADDRESS.matcher(email).matches()) null else "กรุณากรอกอีเมลให้ถูกต้อง"
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}