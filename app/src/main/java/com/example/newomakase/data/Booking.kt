package com.example.newomakase.data

data class Booking(
    val bookingReferenceId: String = "",
    val courseName: String = "",
    val bookingDate: String = "",
    val bookingTime: String = "",
    val numberOfPeople: Int = 0,
    val customerName: String = "",
    val bookingStatus: String = "",
    val customerPhone: String = "",
    val customerEmail: String = "",
    val fullPrice: Double = 0.0,
    val depositAmount: Double = 0.0
)