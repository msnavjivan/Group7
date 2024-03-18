package com.example.group7

import android.os.Parcel
import android.os.Parcelable

data class Candidate(
    val id: String,
    val photoUrl: String,
    val name: String,
    val title: String,
    val moreDetails: String,
    val connections: Map<String, Boolean>,
    val posts: Map<String, Post>
) : Parcelable {
    // No-argument constructor required by Firebase
    constructor() : this("", "", "", "", "", emptyMap(), emptyMap())

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        mutableMapOf<String, Boolean>().apply {
            parcel.readMap(this, Boolean::class.java.classLoader)
        },
        mutableMapOf<String, Post>().apply {
            parcel.readMap(this, Post::class.java.classLoader)
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(photoUrl)
        parcel.writeString(name)
        parcel.writeString(title)
        parcel.writeString(moreDetails)
        parcel.writeMap(connections)
        parcel.writeMap(posts)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Candidate> {
        override fun createFromParcel(parcel: Parcel): Candidate {
            return Candidate(parcel)
        }

        override fun newArray(size: Int): Array<Candidate?> {
            return arrayOfNulls(size)
        }
    }
}
