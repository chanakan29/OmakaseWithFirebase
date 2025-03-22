package com.example.newomakase.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newomakase.CourseAdapter
import com.example.newomakase.R
import com.google.firebase.firestore.FirebaseFirestore

class CourseListFragment : Fragment() {

    data class Course(
        val id: String,
        val name: String,
        val price: Int,
        val menu: List<String>,
        val availableTimes: List<String>,
        val maxSeats: Int // เพิ่ม maxSeats
    )

    private lateinit var recyclerViewCourses: RecyclerView
    private lateinit var courseAdapter: CourseAdapter
    private val coursesList = mutableListOf<Course>() // List to hold course data
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_course_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewCourses = view.findViewById(R.id.recyclerViewCourses)
        recyclerViewCourses.layoutManager = LinearLayoutManager(context)

        // Initialize the adapter
        courseAdapter = CourseAdapter(coursesList) { course ->
            // Handle item click: Show the menu popup
            showCourseMenuPopup(course)
        }
        recyclerViewCourses.adapter = courseAdapter

        // Fetch courses from Firestore
        fetchCourses()
    }

    private fun fetchCourses() {
        firestore.collection("courses")
            .get()
            .addOnSuccessListener { result ->
                coursesList.clear() // Clear the list before adding new data
                for (document in result) {
                    val courseName = document.getString("name")
                    val coursePrice = document.getLong("price")?.toInt() ?: 0
                    val courseMenu = (document.get("menu") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                    val availableTimes = (document.get("availableTimes") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
                    val maxSeats = document.getLong("maxSeats")?.toInt() ?: 0 // ดึงข้อมูล maxSeats

                    if (courseName != null) {
                        val course = Course(document.id, courseName, coursePrice, courseMenu, availableTimes, maxSeats)
                        val currentPosition = coursesList.size
                        coursesList.add(course)
                        courseAdapter.notifyItemInserted(currentPosition)
                    }
                }
            }
            .addOnFailureListener { exception ->
                android.util.Log.w("CourseListFragment", "Error getting documents.", exception)
                Toast.makeText(context, "เกิดข้อผิดพลาดในการดึงข้อมูลคอร์ส", Toast.LENGTH_LONG).show()
            }
    }

    private fun showCourseMenuPopup(course: Course) {
        // TODO: Implement the logic to show the menu popup (DialogFragment or BottomSheetDialogFragment)
        // You will likely create and show a DialogFragment here, passing the course.menu
    }
}