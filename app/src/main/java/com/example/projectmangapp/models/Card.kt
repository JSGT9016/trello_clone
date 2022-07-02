package com.example.projectmangapp.models

import android.os.Parcel
import android.os.Parcelable

data class Card (
    val name :String = "",
    val createdBy: String ="",
    val assignedTo: ArrayList<String> = ArrayList(),
    val labelColor : String = "",
    val dueDate : Long = 0
    ):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.readLong()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel){
        writeString(name)
        writeString(createdBy)
        writeStringList(assignedTo)
        writeString(labelColor)
        writeLong(dueDate)
    }

    override fun describeContents()=0

    companion object{
        @JvmField
        val CREATOR : Parcelable.Creator<Card> = object : Parcelable.Creator<Card>{
            override fun createFromParcel(source: Parcel): Card = Card(source)

            override fun newArray(size: Int): Array<Card?> = arrayOfNulls(size)
        }
    }

}