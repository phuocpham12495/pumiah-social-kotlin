package com.phuocpham.pumiahsocial.data.repository

import com.phuocpham.pumiahsocial.data.local.dao.ProfileDao
import com.phuocpham.pumiahsocial.data.local.entity.ProfileEntity
import com.phuocpham.pumiahsocial.data.model.Profile
import com.phuocpham.pumiahsocial.util.safeApiCall
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage,
    private val profileDao: ProfileDao
) : ProfileRepository {

    override fun getProfileFlow(userId: String): Flow<Profile?> {
        return profileDao.getProfileById(userId).map { it?.toProfile() }
    }

    override suspend fun fetchProfile(userId: String): Result<Profile> = safeApiCall {
        val profile = postgrest.from("profiles")
            .select { filter { eq("id", userId) } }
            .decodeSingle<Profile>()
        profileDao.upsertProfile(ProfileEntity.fromProfile(profile))
        profile
    }

    override suspend fun updateProfile(profile: Profile): Result<Unit> = safeApiCall {
        val upsertData = buildJsonObject {
            put("id", JsonPrimitive(profile.id))
            put("full_name", profile.name?.let { JsonPrimitive(it) } ?: JsonNull)
            put("username", JsonPrimitive(profile.username))
            put("bio", profile.bio?.let { JsonPrimitive(it) } ?: JsonNull)
            put("location", profile.location?.let { JsonPrimitive(it) } ?: JsonNull)
            put("profile_photo_url", profile.avatarUrl?.let { JsonPrimitive(it) } ?: JsonNull)
            put("cover_photo_url", profile.coverPhotoUrl?.let { JsonPrimitive(it) } ?: JsonNull)
            put("date_of_birth", profile.dateOfBirth?.let { JsonPrimitive(it) } ?: JsonNull)
        }
        postgrest.from("profiles").upsert(upsertData)
        profileDao.upsertProfile(ProfileEntity.fromProfile(profile))
    }

    override suspend fun uploadAvatar(
        userId: String,
        imageBytes: ByteArray,
        fileName: String
    ): Result<String> = safeApiCall {
        val path = "$userId/$fileName"
        storage.from("avatars").upload(path, imageBytes) { upsert = true }
        storage.from("avatars").publicUrl(path)
    }

    override suspend fun uploadCoverPhoto(
        userId: String,
        imageBytes: ByteArray,
        fileName: String
    ): Result<String> = safeApiCall {
        val path = "$userId/$fileName"
        storage.from("covers").upload(path, imageBytes) { upsert = true }
        storage.from("covers").publicUrl(path)
    }

    override suspend fun searchProfiles(query: String): Result<List<Profile>> = safeApiCall {
        postgrest.from("profiles")
            .select {
                filter {
                    or {
                        ilike("username", "%$query%")
                        ilike("full_name", "%$query%")
                    }
                }
            }
            .decodeList<Profile>()
    }

    override suspend fun getAllProfiles(): Result<List<Profile>> = safeApiCall {
        postgrest.from("profiles")
            .select()
            .decodeList<Profile>()
    }
}
