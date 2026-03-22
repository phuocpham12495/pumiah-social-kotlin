package com.phuocpham.pumiahsocial.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.phuocpham.pumiahsocial.data.local.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles WHERE id = :id")
    fun getProfileById(id: String): Flow<ProfileEntity?>

    @Upsert
    suspend fun upsertProfile(profile: ProfileEntity)

    @Query("SELECT * FROM profiles WHERE id IN (:ids)")
    suspend fun getProfilesByIds(ids: List<String>): List<ProfileEntity>

    @Query("DELETE FROM profiles")
    suspend fun clearAll()
}
