package com.example.newomakase

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.newomakase.data.Booking
import com.example.newomakase.databinding.ItemBookingBinding
import java.text.NumberFormat
import java.util.Locale

class BookingAdapter(private var bookingList: List<Booking>) :
    RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(booking: Booking)
    }

    interface OnCancelClickListener {
        fun onCancelClick(booking: Booking)
    }

    private var itemClickListener: OnItemClickListener? = null
    private var cancelClickListener: OnCancelClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.itemClickListener = listener
    }

    fun setOnCancelClickListener(listener: OnCancelClickListener) {
        this.cancelClickListener = listener
    }

    inner class BookingViewHolder(val binding: ItemBookingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(booking: Booking) {
            val context = binding.root.context

            binding.textViewBookingCourseName.text = booking.courseName
            binding.textViewBookingDateTime.text = context.getString(R.string.booking_date_time, booking.bookingDate, booking.bookingTime)
            binding.textViewBookingPeople.text = context.getString(R.string.booking_people, booking.numberOfPeople)
            binding.textViewBookingCustomerName.text = context.getString(R.string.customer_name_short, booking.customerName)
            binding.textViewBookingCustomerPhone.text = context.getString(R.string.customer_phone_short, booking.customerPhone)
            binding.textViewBookingCustomerEmail.text = context.getString(R.string.customer_email_short, booking.customerEmail)
            binding.textViewBookingStatus.text = context.getString(R.string.booking_status, booking.bookingStatus)

            // เพิ่มส่วนนี้
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("th", "TH"))
            binding.textViewBookingFullPrice.text = context.getString(R.string.booking_full_price, currencyFormat.format(booking.fullPrice))
            binding.textViewBookingDepositAmount.text = context.getString(R.string.booking_deposit_amount, currencyFormat.format(booking.depositAmount))

            binding.root.setOnClickListener {
                itemClickListener?.onItemClick(booking)
            }

            binding.buttonCancel.setOnClickListener {
                cancelClickListener?.onCancelClick(booking)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemBookingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(bookingList[position])
    }

    override fun getItemCount(): Int {
        return bookingList.size
    }

    fun updateList(newList: List<Booking>) {
        bookingList = newList
        notifyDataSetChanged()
    }
}