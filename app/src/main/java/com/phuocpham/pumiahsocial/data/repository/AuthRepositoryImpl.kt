package com.phuocpham.pumiahsocial.data.repository

import com.phuocpham.pumiahsocial.data.model.AuthState
import com.phuocpham.pumiahsocial.data.model.Profile
import com.phuocpham.pumiahsocial.util.safeApiCall
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: Auth,
    private val postgrest: Postgrest
) : AuthRepository {

    override fun observeAuthState(): Flow<AuthState> {
        return auth.sessionStatus.map { status ->
            when (status) {
                is SessionStatus.Authenticated -> {
                    val user = status.session.user
                    AuthState.Authenticated(
                        userId = user?.id ?: "",
                        email = user?.email ?: ""
                    )
                }
                is SessionStatus.NotAuthenticated -> AuthState.Unauthenticated
                is SessionStatus.Initializing -> AuthState.Loading
                is SessionStatus.RefreshFailure -> AuthState.Error("Phiên đã hết hạn")
            }
        }
    }

    override suspend fun signUp(email: String, password: String): Result<Unit> = safeApiCall {
        auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
        // Create initial profile
        val userId = auth.currentUserOrNull()?.id ?: return@safeApiCall
        val username = email.substringBefore("@")
        postgrest.from("profiles").insert(
            mapOf(
                "id" to userId,
                "username" to username,
                "full_name" to username
            )
        )
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> = safeApiCall {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signOut(): Result<Unit> = safeApiCall {
        auth.signOut()
    }

    override suspend fun getCurrentUserId(): String? {
        return auth.currentUserOrNull()?.id
    }
}
