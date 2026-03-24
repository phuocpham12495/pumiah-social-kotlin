# Ban Ghi Quyet Dinh Kien Truc (ADR) - Pumiah Social

## ADR-001: Mau Thiet Ke MVVM
**Trang thai:** Duoc chap nhan
**Ngay:** 2026-03-22

### Boi canh
Can chon mau kien truc cho ung dung Android voi Jetpack Compose.

### Quyet dinh
Su dung MVVM (Model-View-ViewModel) voi:
- **Model:** Data classes + Repository pattern
- **View:** Jetpack Compose screens
- **ViewModel:** Hilt-injected ViewModels voi StateFlow

### Ly do
- MVVM la pattern duoc Google khuyen nghi cho Android
- Tich hop tot voi Jetpack Compose (collectAsState)
- ViewModel ton tai qua configuration changes
- De unit test (mock repository)

### Danh doi
- (+) Separation of concerns ro rang
- (+) Testable
- (-) Boilerplate nhieu hon MVC
- (-) Can Hilt DI setup ban dau

---

## ADR-002: Supabase Kotlin SDK
**Trang thai:** Duoc chap nhan

### Boi canh
Backend da co san tren Supabase. Can chon cach ket noi tu Android.

### Quyet dinh
Su dung `io.github.jan-tennert.supabase` SDK v3.1.1 voi:
- GoTrue (Auth)
- Postgrest (Database CRUD)
- Realtime (WebSocket subscriptions)
- Storage (File uploads)

### Dependencies
- Ktor Client Android 3.1.1 (HTTP engine)
- Kotlinx Serialization 1.7.3 (JSON parsing)

### Danh doi
- (+) SDK chinh thuc, API type-safe
- (+) Tu dong quan ly JWT tokens
- (-) Phu thuoc vao Ktor (them dependency)
- (-) SDK con phat trien, co the co breaking changes

---

## ADR-003: Room Database cho Offline Caching
**Trang thai:** Duoc chap nhan

### Quyet dinh
Su dung Room 2.6.1 lam local database voi pattern:
1. UI observe Room Flow (hien thi cache ngay lap tuc)
2. Background network fetch
3. Upsert ket qua vao Room
4. Flow tu dong emit data moi

### Entities
- ProfileEntity, PostEntity, FriendEntity, ConversationEntity, MessageEntity

### Danh doi
- (+) Offline-first, UX muot
- (+) Single source of truth
- (-) Can sync logic giua local/remote
- (-) Schema migrations khi thay doi

---

## ADR-004: Hilt Dependency Injection
**Trang thai:** Duoc chap nhan

### Quyet dinh
Su dung Hilt 2.54 voi KSP 2.1.0-1.0.29

### Modules
| Module | Scope | Provides |
|--------|-------|----------|
| SupabaseModule | Singleton | SupabaseClient, Auth, Postgrest, Realtime, Storage |
| DatabaseModule | Singleton | AppDatabase, DAOs |
| RepositoryModule | Singleton | Bind interfaces -> implementations |
| DataStoreModule | Singleton | DataStore<Preferences> |

### Danh doi
- (+) Compile-time safety (vs Koin runtime)
- (+) Android-aware scoping
- (-) Build time tang do code generation
- (-) Can KSP plugin

---

## ADR-005: Navigation Compose
**Trang thai:** Duoc chap nhan

### Quyet dinh
Single NavHost trong MainActivity voi route-based navigation.

### Cau truc Routes
- Auth: `login`, `signup`
- Main (bottom nav): `feed`, `friends_list`, `notifications`, `conversations`, `my_profile`
- Detail: `post_detail/{postId}`, `user_profile/{userId}`, `chat/{conversationId}`
- Utility: `search`, `settings`, `edit_profile`, `create_post`, `friend_requests`

### Danh doi
- (+) Type-safe navigation arguments
- (+) Deep linking support
- (-) String-based routes (co the loi runtime)

---

## ADR-006: Coil 3 cho Image Loading
**Trang thai:** Duoc chap nhan

### Quyet dinh
Su dung Coil 3.0.4 voi Ktor network integration (coil-network-ktor3).

### Ly do
- Kotlin-first, Compose-native
- Chia se Ktor engine voi Supabase SDK
- Tu dong caching (memory + disk)

---

## ADR-007: Polling thay vi Realtime WebSocket cho Chat
**Trang thai:** Duoc chap nhan tam thoi

### Quyet dinh
Chat su dung polling moi 3 giay thay vi Supabase Realtime.

### Ly do
- Don gian hon cho MVP
- Tranh complexity cua channel management
- Du tot cho use case nhan tin co ban

### Ke hoach nang cap
Chuyen sang Supabase Realtime channels trong phien ban tiep theo.

---

## ADR-008: Activity Result API cho Image Picker
**Trang thai:** Duoc chap nhan
**Ngay:** 2026-03-22

### Quyet dinh
Su dung `ActivityResultContracts.GetContent()` voi `rememberLauncherForActivityResult` cho image picker.

### Ly do
- API hien dai, thay the startActivityForResult da deprecated
- Compose-friendly voi `rememberLauncherForActivityResult`
- Ho tro ca gallery selection
- Doc bytes tu ContentResolver de upload len Supabase Storage

### Danh doi
- (+) Don gian, it boilerplate
- (+) Lifecycle-aware
- (-) Khong ho tro multi-select (can `PickMultipleVisualMedia` cho tuong lai)
- (-) Khong co camera capture truc tiep (can `TakePicture` contract rieng)

---

## ADR-009: DataStore cho User Preferences
**Trang thai:** Duoc chap nhan
**Ngay:** 2026-03-22

### Quyet dinh
Su dung DataStore Preferences 1.1.1 cho:
- Dark mode toggle
- Notification preferences (likes, comments, friend requests, messages)

### Ly do
- Thay the SharedPreferences (deprecated pattern)
- Coroutine-based, thread-safe
- Flow integration tu nhien voi Compose collectAsState

---

## ADR-010: ConnectivityManager NetworkCallback cho Network Monitoring
**Trang thai:** Duoc chap nhan
**Ngay:** 2026-03-22

### Quyet dinh
Su dung `ConnectivityManager.NetworkCallback` wrapped trong `callbackFlow` de theo doi trang thai mang.

### Ly do
- API chinh thuc cua Android cho network monitoring
- Realtime callback thay vi polling
- Ket hop voi `AnimatedVisibility` cho offline banner UX muot

---

## ADR-011: Polymorphic Likes voi target_id/target_type
**Trang thai:** Duoc chap nhan
**Ngay:** 2026-03-22

### Boi canh
Supabase database su dung thiet ke polymorphic cho bang `likes` thay vi cot rieng `post_id`/`comment_id`.

### Schema thuc te
```sql
likes: [id, profile_id, target_id, target_type, created_at]
-- target_type: "post" hoac "comment"
-- target_id: ID cua post hoac comment
```

### Quyet dinh
- Model Like dung `targetId` + `targetType` thay vi `postId`/`commentId`
- Repository filter theo `target_type` khi query likes
- Toggle like: kiem tra exists theo `profile_id` + `target_id` + `target_type`

### Ly do
- Phu hop voi schema Supabase da co san
- Mo rong duoc (co the like bat ky entity nao trong tuong lai)
- Giam so luong cot nullable

---

## ADR-012: Supabase Schema Mapping Strategy
**Trang thai:** Duoc chap nhan
**Ngay:** 2026-03-22

### Boi canh
Supabase database co ten cot khac voi convention cua Kotlin model. Can strategy de mapping.

### Quyet dinh
Su dung `@SerialName` annotation cua Kotlinx Serialization de map giua Kotlin property names va Supabase column names.

### Mapping chinh
| Supabase Column | Kotlin Property | Table |
|-----------------|-----------------|-------|
| `full_name` | `name` | profiles |
| `profile_photo_url` | `avatarUrl` | profiles |
| `cover_photo_url` | `coverPhotoUrl` | profiles |
| `profile_id` | `authorId` | posts, comments, likes |
| `media_url` | `imageUrl` | posts |
| `target_id` | `targetId` | likes |
| `target_type` | `targetType` | likes |
| `target_url` | `targetUrl` | notifications |

### Ly do
- Giu Kotlin code doc duoc (camelCase, ten co y nghia)
- Khong can thay doi Supabase schema (read-only backend)
- `@SerialName` la cach tieu chuan cua Kotlinx Serialization

### Bai hoc
- Luon verify schema bang Supabase REST API (`/rest/v1/`) truoc khi code
- Khong gia dinh ten cot tu documentation hoac convention

---

## ADR-013: Profile Check Redirect Pattern
**Trang thai:** Duoc chap nhan
**Ngay:** 2026-03-23

### Boi canh
User moi sau dang ky co account nhung chua co profile. Can redirect den man hinh tao profile.

### Quyet dinh
- Tao `ProfileCheckViewModel` kiem tra profile ton tai trong Supabase
- Trong `MainActivity`, sau khi `AuthState.Authenticated`, kiem tra profile
- Neu chua co profile -> `startDestination = Screen.CreateProfile.route`
- Neu da co -> `startDestination = Screen.Feed.route`
- `CreateProfileScreen` dung chung `EditProfileViewModel` de tranh duplicate logic

### Danh doi
- (+) Reuse ViewModel, khong duplicate code
- (+) User experience muot (redirect tu dong)
- (-) Them 1 API call khi khoi dong app (kiem tra profile)

---

## ADR-014: Supabase Storage Bucket Naming
**Trang thai:** Duoc chap nhan
**Ngay:** 2026-03-24

### Boi canh
Upload anh that bai vi code dung sai ten bucket. Supabase dashboard cho thay buckets la `profile_photos` va `post_images`, nhung code dung `avatars`, `covers`, `post-images`.

### Quyet dinh
- Avatar va cover photo deu upload vao bucket `profile_photos`
- Post images upload vao bucket `post_images`
- Path format: `{userId}/{filename}` (vd: `abc123/avatar_1711234567.jpg`)

### Bai hoc
- Luon kiem tra ten bucket thuc te tren Supabase dashboard truoc khi code
- Bucket names dung underscore (`_`), khong dung dash (`-`)

---

## ADR-015: Friendly Error Messages Pattern
**Trang thai:** Duoc chap nhan
**Ngay:** 2026-03-23

### Boi canh
Supabase tra ve raw error messages bao gom URLs, tokens, HTTP headers -> khong phu hop hien thi cho user.

### Quyet dinh
- Tao `toFriendlyError()` extension function trong AuthViewModel
- Map Supabase error keywords sang thong bao tieng Viet
- Pattern: kiem tra `message.contains(keyword)` -> tra ve friendly message
- Fallback: "Da xay ra loi, vui long thu lai"

### Danh doi
- (+) UX tot hon, khong lo thong tin ky thuat
- (-) Can cap nhat mapping khi Supabase thay doi error format

---

## ADR-016: buildJsonObject thay vi mapOf cho Postgrest Upsert
**Trang thai:** Duoc chap nhan
**Ngay:** 2026-03-23

### Boi canh
`ProfileRepositoryImpl.updateProfile()` dung `mapOf<String, Any?>` de tao JSON body cho Postgrest upsert. Kotlinx Serialization khong co serializer cho `Any` type -> crash runtime.

### Quyet dinh
Su dung `buildJsonObject` voi `JsonPrimitive` tu `kotlinx.serialization.json`:
```kotlin
val data = buildJsonObject {
    put("id", JsonPrimitive(profile.id))
    put("full_name", profile.name?.let { JsonPrimitive(it) } ?: JsonNull)
    // ...
}
postgrest.from("profiles").upsert(data)
```

### Ly do
- `buildJsonObject` tao `JsonObject` truc tiep, khong can serializer cho `Any`
- Type-safe: chi chap nhan `JsonElement` (JsonPrimitive, JsonNull, JsonObject, JsonArray)
- Xu ly nullable fields dung `JsonNull`
