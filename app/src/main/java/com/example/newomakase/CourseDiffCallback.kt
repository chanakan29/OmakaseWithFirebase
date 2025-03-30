package com.example.newomakase

import androidx.recyclerview.widget.DiffUtil
import com.example.newomakase.ui.CourseListFragment

class CourseDiffCallback : DiffUtil.ItemCallback<CourseListFragment.Course>() {
    override fun areItemsTheSame(oldItem: CourseListFragment.Course, newItem: CourseListFragment.Course): Boolean {
        // ตรวจสอบว่า Item สองรายการนี้เป็น Item เดียวกันหรือไม่ (โดยทั่วไปจะดูจาก ID)
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CourseListFragment.Course, newItem: CourseListFragment.Course): Boolean {
        // ตรวจสอบว่า Content ของ Item สองรายการนี้เหมือนกันหรือไม่
        return oldItem == newItem // ถ้า Course เป็น Data Class จะมีการ Implement equals() ให้อัตโนมัติ
    }
}