package com.example.newomakase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newomakase.ui.CourseListFragment
import com.example.newomakase.ui.MenuDialogFragment

class CourseAdapter(
    private val coursesList: List<CourseListFragment.Course>,
    private val onItemClick: (CourseListFragment.Course) -> Unit
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageViewCourse: ImageView = itemView.findViewById(R.id.imageViewCourse)
        private val textViewCourseName: TextView = itemView.findViewById(R.id.textViewCourseName)
        private val textViewCoursePrice: TextView = itemView.findViewById(R.id.textViewCoursePrice)
        private val textViewMaxSeats: TextView = itemView.findViewById(R.id.textViewMaxSeats)
        private val textViewAvailableTimes: TextView = itemView.findViewById(R.id.textViewAvailableTimes)

        fun bind(course: CourseListFragment.Course, onItemClick: (CourseListFragment.Course) -> Unit) {
            textViewCourseName.text = course.name
            textViewCoursePrice.text = itemView.context.getString(R.string.price_format, course.price)
            textViewMaxSeats.text = itemView.context.getString(R.string.max_seats_format, course.maxSeats)
            val formattedTimes = course.availableTimes.joinToString("\n") { "- $it" }
            textViewAvailableTimes.text = itemView.context.getString(R.string.available_times_format, "\n$formattedTimes")


            // กำหนดรูปภาพตามชื่อคอร์ส
            when (course.name) {
                "คอร์สธรรมดา" -> {
                    imageViewCourse.setImageResource(R.drawable.ic_course_regular) // เปลี่ยนเป็นชื่อ Resource ของคุณ
                }
                "คอร์สพรีเมี่ยม" -> {
                    imageViewCourse.setImageResource(R.drawable.ic_course_premium) // เปลี่ยนเป็นชื่อ Resource ของคุณ
                }
                else -> {
                    imageViewCourse.setImageResource(R.drawable.course_placeholder) // รูปภาพ Default ถ้าไม่ตรงกับชื่อใดๆ
                }
            }

            itemView.setOnClickListener {
                // สร้าง Instance ของ MenuDialogFragment พร้อมส่งข้อมูล
                val menuDialogFragment = MenuDialogFragment.newInstance(course.name, course.menu, course.id, course.maxSeats)

                // แสดง DialogFragment
                val fragmentManager = (itemView.context as? androidx.fragment.app.FragmentActivity)?.supportFragmentManager
                fragmentManager?.let {
                    menuDialogFragment.show(it, "MenuDialog")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_course, parent, false)
        return CourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val currentCourse = coursesList[position]
        holder.bind(currentCourse, onItemClick)
    }

    override fun getItemCount() = coursesList.size
}