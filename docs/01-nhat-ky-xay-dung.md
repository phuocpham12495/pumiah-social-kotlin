# Nhật Ký Xây Dựng - Pumiah Social

## Giai Đoạn 1: Nền Tảng Dự Án
**Thời gian:** 2026-03-22

### Bước 1: Cấu hình Dependencies
- **Hành động:** Cập nhật `libs.versions.toml` với tất cả thư viện cần thiết
- **Lý do:** Sử dụng Version Catalog của Gradle để quản lý dependency tập trung, tránh xung đột phiên bản
- **Thư viện chính:**
  - Supabase Kotlin SDK 3.1.1 (gotrue, postgrest, realtime, storage)
  - Ktor Client Android 3.1.1 (HTTP client cho Supabase SDK)
  - Hilt 2.56.2 (Dependency Injection)
  - Room 2.7.1 (Local database cho offline caching)
  - Navigation Compose 2.9.0 (Điều hướng)
  - Coil 3.1.0 (Tải ảnh)

### Bước 2: Tạo Application Class
- **Hành động:** Tạo `PumiahSocialApp.kt` với `@HiltAndroidApp`
- **Lý do:** Điểm khởi tạo cho Hilt DI container

### Bước 3: Cấu hình DI Modules
- **Hành động:** Tạo 4 Hilt modules (Supabase, Database, Repository, DataStore)
- **Lý do:** Tách biệt việc tạo đối tượng, dễ test và bảo trì

### Bước 4: Thiết lập Navigation
- **Hành động:** Tạo `Screen.kt`, `PumiahNavGraph.kt`, `BottomNavItem.kt`
- **Lý do:** Single NavHost pattern với nested navigation cho auth/main flows

### Bước 5: Shared Components
- **Hành động:** Tạo LoadingIndicator, ErrorMessage, UserAvatar, BottomNavigationBar, PostCard, CommentItem
- **Lý do:** Tái sử dụng components giữa các màn hình, đảm bảo consistency

## Giai Đoạn 2: Xác Thực
**Thời gian:** 2026-03-22

- **Hành động:** Implement AuthRepository với Supabase GoTrue
- **Lý do:** Email/password authentication với auto session management
- **Quyết định:** Tự động tạo profile sau khi đăng ký thành công
- **Màn hình:** LoginScreen, SignUpScreen với Material3 design

## Giai Đoạn 3-4: Profile & Friends
**Thời gian:** 2026-03-22

- **Hành động:** CRUD profile qua Postgrest, upload ảnh qua Storage
- **Lý do:** Offline-first pattern với Room caching
- **Quyết định quan trọng:** Bảng `friends` có constraint `user1_id < user2_id` → repository sắp xếp UUID trước khi insert

## Giai Đoạn 5-6: Posts & Interactions
**Thời gian:** 2026-03-22

- **Hành động:** Feed chronological từ bạn bè, like/comment polymorphic
- **Lý do:** Enrich posts với author profile, like/comment counts trong một lần query
- **Quyết định:** Tạo notification tự động khi like/comment

## Giai Đoạn 7-8: Notifications & Messaging
**Thời gian:** 2026-03-22

- **Hành động:** In-app notifications, real-time chat với polling
- **Lý do:** Polling 3s cho chat thay vì WebSocket do đơn giản hơn cho MVP
- **Quyết định:** Không dùng Firebase FCM, chỉ in-app notifications

## Giai Đoạn 9: Settings & Offline
**Thời gian:** 2026-03-22

- **Hành động:** Settings screen, Room offline caching
- **Lý do:** Offline-first: Room Flow → UI, network fetch → Room upsert

## Giai Đoạn 10: Bổ Sung Tính Năng Thiếu (Gap Analysis)
**Thời gian:** 2026-03-22

### Bug Fix
- **isOwnComment bug:** PostDetailScreen so sánh `comment.authorId == post.authorId` thay vì `currentUserId` → sửa bằng cách inject AuthRepository vào PostDetailViewModel và expose `currentUserId` StateFlow

### Tính năng mới
1. **Remove Friend từ FriendsListScreen:** Thêm menu 3 chấm (MoreVert) với "Hủy kết bạn" cho mỗi friend item
2. **Send Message từ ProfileScreen:** Thêm nút "Nhắn tin" khi friendshipStatus == FRIENDS, sử dụng `getOrCreateConversation` từ MessagesRepository
3. **Image Picker (Activity Result API):**
   - EditProfileScreen: `rememberLauncherForActivityResult(GetContent)` cho avatar và cover photo
   - CreatePostScreen: `GetContent` launcher cho ảnh bài viết, hiển thị preview với nút xóa
4. **Clickable Links trong PostCard:** Sử dụng `Intent(ACTION_VIEW, Uri.parse(url))` để mở browser
5. **User Search:** Thêm SearchScreen với debounced search (300ms), SearchViewModel, `searchProfiles` API (ilike trên username/name). Nút search icon trên FeedScreen
6. **Dark Mode Toggle:** DataStore preferences cho dark mode, SettingsViewModel expose isDarkMode StateFlow, MainActivity đọc preference và truyền vào PumiahSocialTheme
7. **Notification Preferences:** 4 toggle switches trong SettingsScreen (likes, comments, friend requests, messages) lưu vào DataStore
8. **Network Monitor + Offline Banner:** ConnectivityManager NetworkCallback → Flow<Boolean>, AnimatedVisibility banner đỏ "Không có kết nối mạng" trên đầu app
