# Bản Ghi Quyết Định Kiến Trúc (ADR) - Pumiah Social

## ADR-001: Mẫu Thiết Kế MVVM
**Trạng thái:** Được chấp nhận
**Ngày:** 2026-03-22

### Bối cảnh
Cần chọn mẫu kiến trúc cho ứng dụng Android với Jetpack Compose.

### Quyết định
Sử dụng MVVM (Model-View-ViewModel) với:
- **Model:** Data classes + Repository pattern
- **View:** Jetpack Compose screens
- **ViewModel:** Hilt-injected ViewModels với StateFlow

### Lý do
- MVVM là pattern được Google khuyến nghị cho Android
- Tích hợp tốt với Jetpack Compose (collectAsState)
- ViewModel tồn tại qua configuration changes
- Dễ unit test (mock repository)

### Đánh đổi
- (+) Separation of concerns rõ ràng
- (+) Testable
- (-) Boilerplate nhiều hơn MVC
- (-) Cần Hilt DI setup ban đầu

---

## ADR-002: Supabase Kotlin SDK
**Trạng thái:** Được chấp nhận

### Bối cảnh
Backend đã có sẵn trên Supabase. Cần chọn cách kết nối từ Android.

### Quyết định
Sử dụng `io.github.jan-tennert.supabase` SDK v3.1.1 với:
- GoTrue (Auth)
- Postgrest (Database CRUD)
- Realtime (WebSocket subscriptions)
- Storage (File uploads)

### Dependencies
- Ktor Client Android 3.1.1 (HTTP engine)
- Kotlinx Serialization 1.8.1 (JSON parsing)

### Đánh đổi
- (+) SDK chính thức, API type-safe
- (+) Tự động quản lý JWT tokens
- (-) Phụ thuộc vào Ktor (thêm dependency)
- (-) SDK còn phát triển, có thể có breaking changes

---

## ADR-003: Room Database cho Offline Caching
**Trạng thái:** Được chấp nhận

### Quyết định
Sử dụng Room 2.7.1 làm local database với pattern:
1. UI observe Room Flow (hiển thị cache ngay lập tức)
2. Background network fetch
3. Upsert kết quả vào Room
4. Flow tự động emit data mới

### Entities
- ProfileEntity, PostEntity, FriendEntity, ConversationEntity, MessageEntity

### Đánh đổi
- (+) Offline-first, UX mượt
- (+) Single source of truth
- (-) Cần sync logic giữa local/remote
- (-) Schema migrations khi thay đổi

---

## ADR-004: Hilt Dependency Injection
**Trạng thái:** Được chấp nhận

### Quyết định
Sử dụng Hilt 2.56.2 với KSP 2.2.10-1.0.31

### Modules
| Module | Scope | Provides |
|--------|-------|----------|
| SupabaseModule | Singleton | SupabaseClient, Auth, Postgrest, Realtime, Storage |
| DatabaseModule | Singleton | AppDatabase, DAOs |
| RepositoryModule | Singleton | Bind interfaces → implementations |
| DataStoreModule | Singleton | DataStore<Preferences> |

### Đánh đổi
- (+) Compile-time safety (vs Koin runtime)
- (+) Android-aware scoping
- (-) Build time tăng do code generation
- (-) Cần KSP plugin

---

## ADR-005: Navigation Compose
**Trạng thái:** Được chấp nhận

### Quyết định
Single NavHost trong MainActivity với route-based navigation.

### Cấu trúc Routes
- Auth: `login`, `signup`
- Main (bottom nav): `feed`, `friends_list`, `notifications`, `conversations`, `my_profile`
- Detail: `post_detail/{postId}`, `user_profile/{userId}`, `chat/{conversationId}`

### Đánh đổi
- (+) Type-safe navigation arguments
- (+) Deep linking support
- (-) String-based routes (có thể lỗi runtime)

---

## ADR-006: Coil 3 cho Image Loading
**Trạng thái:** Được chấp nhận

### Quyết định
Sử dụng Coil 3.1.0 với Ktor network integration.

### Lý do
- Kotlin-first, Compose-native
- Chia sẻ Ktor engine với Supabase SDK
- Tự động caching (memory + disk)

---

## ADR-007: Polling thay vì Realtime WebSocket cho Chat
**Trạng thái:** Được chấp nhận tạm thời

### Quyết định
Chat sử dụng polling mỗi 3 giây thay vì Supabase Realtime.

### Lý do
- Đơn giản hơn cho MVP
- Tránh complexity của channel management
- Đủ tốt cho use case nhắn tin cơ bản

### Kế hoạch nâng cấp
Chuyển sang Supabase Realtime channels trong phiên bản tiếp theo.

---

## ADR-008: Activity Result API cho Image Picker
**Trạng thái:** Được chấp nhận
**Ngày:** 2026-03-22

### Quyết định
Sử dụng `ActivityResultContracts.GetContent()` với `rememberLauncherForActivityResult` cho image picker.

### Lý do
- API hiện đại, thay thế startActivityForResult đã deprecated
- Compose-friendly với `rememberLauncherForActivityResult`
- Hỗ trợ cả gallery selection
- Đọc bytes từ ContentResolver để upload lên Supabase Storage

### Đánh đổi
- (+) Đơn giản, ít boilerplate
- (+) Lifecycle-aware
- (-) Không hỗ trợ multi-select (cần `PickMultipleVisualMedia` cho tương lai)
- (-) Không có camera capture trực tiếp (cần `TakePicture` contract riêng)

---

## ADR-009: DataStore cho User Preferences
**Trạng thái:** Được chấp nhận
**Ngày:** 2026-03-22

### Quyết định
Sử dụng DataStore Preferences cho:
- Dark mode toggle
- Notification preferences (likes, comments, friend requests, messages)

### Lý do
- Thay thế SharedPreferences (deprecated pattern)
- Coroutine-based, thread-safe
- Flow integration tự nhiên với Compose collectAsState

---

## ADR-010: ConnectivityManager NetworkCallback cho Network Monitoring
**Trạng thái:** Được chấp nhận
**Ngày:** 2026-03-22

### Quyết định
Sử dụng `ConnectivityManager.NetworkCallback` wrapped trong `callbackFlow` để theo dõi trạng thái mạng.

### Lý do
- API chính thức của Android cho network monitoring
- Realtime callback thay vì polling
- Kết hợp với `AnimatedVisibility` cho offline banner UX mượt
