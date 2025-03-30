package com.example.newomakase.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newomakase.CourseAdapter
import com.example.newomakase.R
import com.example.newomakase.databinding.FragmentCourseListBinding
import com.google.firebase.firestore.FirebaseFirestore
import androidx.navigation.fragment.findNavController

class CourseListFragment : Fragment() {

    data class Course(
        val id: String,
        val name: String,
        val price: Int,
        val menu: List<String>,
        val availableTimes: List<String>,
        val maxSeats: Int // เพิ่ม maxSeats
    )

    private var _binding: FragmentCourseListBinding? = null
    private val binding get() = _binding!!

    private lateinit var courseAdapter: CourseAdapter
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewCourses.layoutManager = LinearLayoutManager(context)

        // Initialize the adapter
        courseAdapter = CourseAdapter { course ->
            showCourseMenuPopup(course)
        }
        binding.recyclerViewCourses.adapter = courseAdapter

        // Fetch courses from Firestore
        fetchCourses()
    }

    private fun fetchCourses() {
        firestore.collection("courses")
            .get()
            .addOnSuccessListener { result ->
                val courseList = result.documents.mapNotNull { document ->
                    val courseName = document.getString("name")
                    val coursePrice = document.getLong("price")?.toInt() ?: 0
                    val courseMenu = (document.get("menu") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                    val availableTimes = (document.get("availableTimes") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                    val maxSeats = document.getLong("maxSeats")?.toInt() ?: 0

                    if (courseName != null) {
                        Course(document.id, courseName, coursePrice, courseMenu, availableTimes, maxSeats)
                    } else {
                        null
                    }
                }
                courseAdapter.submitList(courseList) // ใช้ submitList() เพื่อส่งรายการใหม่ไปยัง Adapter
            }
            .addOnFailureListener { exception ->
                android.util.Log.w("CourseListFragment", "Error getting documents.", exception)
                Toast.makeText(context, "เกิดข้อผิดพลาดในการดึงข้อมูลคอร์ส", Toast.LENGTH_LONG).show()
            }
    }

    private fun showCourseMenuPopup(course: Course) {
        val bundle = Bundle()
        bundle.putString("name", course.name)
        bundle.putStringArrayList("menu", ArrayList(course.menu))
        bundle.putString("id", course.id)
        bundle.putInt("seats", course.maxSeats)

        findNavController().navigate(R.id.action_courseListFragment_to_menuDialogFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerViewCourses.adapter = null
        _binding = null
    }
}