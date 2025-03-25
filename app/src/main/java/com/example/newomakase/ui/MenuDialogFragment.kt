package com.example.newomakase.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newomakase.MenuAdapter // เราจะสร้าง Adapter นี้ในภายหลัง
import com.example.newomakase.databinding.FragmentMenuDialogBinding
import androidx.navigation.fragment.findNavController
import com.example.newomakase.R
import com.example.newomakase.findSafeNavController

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

        fun newInstance(courseName: String, courseMenu: List<String>, courseId: String, maxSeats: Int): MenuDialogFragment { // maxSeats เป็น Int แล้ว
            return MenuDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_COURSE_NAME, courseName)
                    putStringArrayList(ARG_COURSE_MENU, ArrayList(courseMenu))
                    putString(ARG_COURSE_ID, courseId)
                    putInt(ARG_MAX_SEATS, maxSeats) // ส่งค่า maxSeats ที่เป็น Int โดยตรง
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.8).toInt(),  // 80% ของจอ
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            courseName = it.getString(ARG_COURSE_NAME)
            courseMenu = it.getStringArrayList(ARG_COURSE_MENU)?.toList()
            courseId = it.getString(ARG_COURSE_ID) // ดึง courseId จาก arguments
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

        // ตั้งค่า RecyclerView สำหรับแสดงเมนู
        binding.recyclerViewMenu.layoutManager = LinearLayoutManager(requireContext())
        val menuAdapter = courseMenu?.let { MenuAdapter(it) } // เราจะสร้าง MenuAdapter ในขั้นตอนถัดไป
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

                            // ใช้ NavController ของ Activity เพื่อเรียก Global Action
                            activity?.findNavController(R.id.nav_host_fragment)?.navigate(R.id.globalActionMenuToBooking, bundle)

                            // สั่งให้ Dialog Dismiss ตัวเอง
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