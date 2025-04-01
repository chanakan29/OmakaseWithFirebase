package com.example.newomakase.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newomakase.NotificationAdapter
import com.example.newomakase.data.Notification
import com.example.newomakase.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var notificationAdapter: NotificationAdapter
    private val notificationList = mutableListOf<Notification>() // Replace with your data source

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewNotifications.layoutManager = LinearLayoutManager(requireContext())
        notificationAdapter = NotificationAdapter(notificationList)
        binding.recyclerViewNotifications.adapter = notificationAdapter

        // **TODO: ดึงข้อมูลการแจ้งเตือนจากแหล่งข้อมูลของคุณ**
        // ตัวอย่างข้อมูลจำลอง
        notificationList.add(Notification(title = "การจองสำเร็จ", message = "การจองของคุณเสร็จสิ้น!"))
        notificationList.add(Notification(title = "โปรโมชั่นพิเศษ", message = "ห้ามพลาด! ส่วนลดพิเศษสำหรับลูกค้าที่มาจองวันเสาร์-อาทิตย์"))
        notificationAdapter.updateList(notificationList)

        if (notificationList.isEmpty()) {
            binding.textViewEmptyNotifications.visibility = View.VISIBLE
            binding.recyclerViewNotifications.visibility = View.GONE
        } else {
            binding.textViewEmptyNotifications.visibility = View.GONE
            binding.recyclerViewNotifications.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}