package com.example.newomakase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MenuAdapter(private val menuList: List<String>) :
    RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewMenuItem: TextView = itemView.findViewById(R.id.textViewMenuItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu, parent, false)
        return MenuViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val currentItem = menuList[position]
        holder.textViewMenuItem.text = currentItem
    }

    override fun getItemCount() = menuList.size
}