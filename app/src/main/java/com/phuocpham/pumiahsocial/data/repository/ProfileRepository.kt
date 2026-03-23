package com.phuocpham.pumiahsocial.data.repository

import com.phuocpham.pumiahsocial.data.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfileFlow(userId: String): Flow<Profile?>
    suspend fun fetchProfile(userId: String): Result<Profile>
    suspend fun updateProfile(profile: Profile): Result<Unit>
    suspend fun uploadAvatar(userId: String, imageBytes: ByteArray, fileName: String): Result<String>
    suspend fun uploadCoverPhoto(userId: String, imageBytes: ByteArray, fileName: String): Result<String>
    suspend fun searchProfiles(query: String): Result<List<Profile>>
    suspend fun getAllProfiles(): Result<List<Profile>>
}
