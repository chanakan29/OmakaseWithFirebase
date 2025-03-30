package com.example.newomakase.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.newomakase.R
import com.example.newomakase.databinding.FragmentBookingSuccessBinding

class BookingSuccessFragment : Fragment() {

    private var _binding: FragmentBookingSuccessBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookingSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val bookingReferenceId = it.getString("bookingReferenceId")
            val courseName = it.getString("courseName")
            val bookingDate = it.getString("bookingDate")
            val bookingTime = it.getString("bookingTime")
            val numberOfPeople = it.getInt("numberOfPeople")

            binding.textViewBookingReferenceId.text = bookingReferenceId
            binding.textViewCourseName.text = courseName
            binding.textViewDateTime.text = getString(R.string.booking_dateandtime, bookingDate, bookingTime)
            binding.textViewNumberOfPeople.text = getString(R.string.num_of_people, numberOfPeople)
            binding.textViewRestaurantName.text = "ร้านโอมากาเสะของคุณ" // Replace with your actual restaurant name
        }

        binding.buttonGoHome.setOnClickListener {
            findNavController().navigate(R.id.action_bookingSuccessFragment_to_homeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}