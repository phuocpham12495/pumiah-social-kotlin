package com.phuocpham.pumiahsocial.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.phuocpham.pumiahsocial.data.model.Profile

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: String,
    val username: String,
    val name: String?,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String?,
    @ColumnInfo(name = "cover_photo_url") val coverPhotoUrl: String?,
    val bio: String?,
    val location: String?,
    @ColumnInfo(name = "join_date") val joinDate: String,
    @ColumnInfo(name = "date_of_birth") val dateOfBirth: String?
) {
    fun toProfile() = Profile(
        id = id,
        username = username,
        name = name,
        avatarUrl = avatarUrl,
        coverPhotoUrl = coverPhotoUrl,
        bio = bio,
        location = location,
        joinDate = joinDate,
        dateOfBirth = dateOfBirth
    )

    companion object {
        fun fromProfile(profile: Profile) = ProfileEntity(
            id = profile.id,
            username = profile.username,
            name = profile.name,
            avatarUrl = profile.avatarUrl,
            coverPhotoUrl = profile.coverPhotoUrl,
            bio = profile.bio,
            location = profile.location,
            joinDate = profile.joinDate,
            dateOfBirth = profile.dateOfBirth
        )
    }
}
