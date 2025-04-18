package com.example.newomakase.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newomakase.MenuAdapter
import com.example.newomakase.databinding.FragmentMenuDialogBinding
import com.example.newomakase.R

class MenuDialogFragment : DialogFragment() {

    private var _binding: FragmentMenuDialogBinding? = null
    private val binding get() = _binding!!

    private var courseName: String? = null
    private var courseMenu: List<String>? = null
    private var courseId: String? = null
    private var maxSeats: Int? = null

    companion object {
        private const val ARG_COURSE_NAME = "courseName"
        private const val ARG_COURSE_MENU = "courseMenu"
        private const val ARG_COURSE_ID = "courseId"
        private const val ARG_MAX_SEATS = "maxSeats"

        fun newInstance(courseName: String, courseMenu: List<String>, courseId: String, maxSeats: Int): MenuDialogFragment {
            return MenuDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_COURSE_NAME, courseName)
                    putStringArrayList(ARG_COURSE_MENU, ArrayList(courseMenu))
                    putString(ARG_COURSE_ID, courseId)
                    putInt(ARG_MAX_SEATS, maxSeats)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.8).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            courseName = it.getString(ARG_COURSE_NAME)
            courseMenu = it.getStringArrayList(ARG_COURSE_MENU)?.toList()
            courseId = it.getString(ARG_COURSE_ID)
            maxSeats = it.getInt(ARG_MAX_SEATS, 8)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        android.util.Log.d("MenuDialogFragment", "onViewCreated is called")

        binding.textViewCourseName.text = courseName ?: "ชื่อคอร์ส"

        binding.recyclerViewMenu.layoutManager = LinearLayoutManager(requireContext())
        val menuAdapter = courseMenu?.let { MenuAdapter(it) }
        binding.recyclerViewMenu.adapter = menuAdapter

        binding.buttonSelectCourse.setOnClickListener {
            courseName?.let { name ->
                courseMenu?.let { menu ->
                    courseId?.let { id ->
                        maxSeats?.let { seats ->
                            val bundle = Bundle().apply {
                                putString("name", name)
                                putStringArrayList("menu", ArrayList(menu))
                                putString("id", id)
                                putInt("seats", seats)
                            }
                            activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.globalActionMenuToBooking, bundle)

                            dismiss()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}