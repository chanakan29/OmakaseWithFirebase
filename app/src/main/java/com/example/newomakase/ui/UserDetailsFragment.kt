package com.example.newomakase.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.newomakase.databinding.FragmentUserDetailsBinding

class UserDetailsFragment : Fragment() {

    private var _binding: FragmentUserDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ดึงข้อมูลที่ส่งมาจาก BookingFragment (ถ้ามี)
        arguments?.let {
            val courseId = it.getString("courseId")
            val selectedDate = it.getString("selectedDate")
            val selectedTime = it.getString("selectedTime")
            val numberOfPeople = it.getInt("numberOfPeople")

            // นำข้อมูลเหล่านี้ไปใช้งาน หรือแสดงผลบน UI
        }

        // TODO: Implement UI logic for collecting user details
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}