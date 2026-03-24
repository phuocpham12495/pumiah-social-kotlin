# Nhat Ky Xay Dung - Pumiah Social

## Giai Doan 1: Nen Tang Du An
**Thoi gian:** 2026-03-22

### Buoc 1: Cau hinh Dependencies
- **Hanh dong:** Cap nhat `libs.versions.toml` voi tat ca thu vien can thiet
- **Ly do:** Su dung Version Catalog cua Gradle de quan ly dependency tap trung, tranh xung dot phien ban
- **Thu vien chinh:**
  - Supabase Kotlin SDK 3.1.1 (gotrue, postgrest, realtime, storage)
  - Ktor Client Android 3.1.1 (HTTP client cho Supabase SDK)
  - Hilt 2.54 (Dependency Injection)
  - Room 2.6.1 (Local database cho offline caching)
  - Navigation Compose 2.8.5 (Dieu huong)
  - Coil 3.0.4 (Tai anh)
  - Kotlin 2.1.0, KSP 2.1.0-1.0.29
  - Kotlinx Serialization 1.7.3, Coroutines 1.9.0

### Buoc 2: Tao Application Class
- **Hanh dong:** Tao `PumiahSocialApp.kt` voi `@HiltAndroidApp`
- **Ly do:** Diem khoi tao cho Hilt DI container

### Buoc 3: Cau hinh DI Modules
- **Hanh dong:** Tao 4 Hilt modules (Supabase, Database, Repository, DataStore)
- **Ly do:** Tach biet viec tao doi tuong, de test va bao tri

### Buoc 4: Thiet lap Navigation
- **Hanh dong:** Tao `Screen.kt`, `PumiahNavGraph.kt`, `BottomNavItem.kt`
- **Ly do:** Single NavHost pattern voi nested navigation cho auth/main flows

### Buoc 5: Shared Components
- **Hanh dong:** Tao LoadingIndicator, ErrorMessage, UserAvatar, BottomNavigationBar, PostCard, CommentItem
- **Ly do:** Tai su dung components giua cac man hinh, dam bao consistency

## Giai Doan 2: Xac Thuc
**Thoi gian:** 2026-03-22

- **Hanh dong:** Implement AuthRepository voi Supabase GoTrue
- **Ly do:** Email/password authentication voi auto session management
- **Quyet dinh:** Tu dong tao profile sau khi dang ky thanh cong (insert vao bang `profiles` voi `full_name` va `username`)
- **Man hinh:** LoginScreen, SignUpScreen voi Material3 design

## Giai Doan 3-4: Profile & Friends
**Thoi gian:** 2026-03-22

- **Hanh dong:** CRUD profile qua Postgrest, upload anh qua Storage
- **Ly do:** Offline-first pattern voi Room caching
- **Quyet dinh quan trong:** Bang `friendships` co constraint `user1_id < user2_id` -> repository sap xep UUID truoc khi insert
- **Luu y:** Bang trong Supabase ten la `friendships` (khong phai `friends`)

## Giai Doan 5-6: Posts & Interactions
**Thoi gian:** 2026-03-22

- **Hanh dong:** Feed chronological tu ban be, like/comment polymorphic
- **Ly do:** Enrich posts voi author profile, like/comment counts trong mot lan query
- **Quyet dinh:** Tao notification tu dong khi like/comment
- **Schema quan trong:**
  - Posts su dung `profile_id` (khong phai `author_id` hay `user_id`)
  - Likes su dung `target_id` + `target_type` (polymorphic, khong phai `post_id`/`comment_id` rieng)
  - Comments su dung `profile_id` (khong phai `author_id`)

## Giai Doan 7-8: Notifications & Messaging
**Thoi gian:** 2026-03-22

- **Hanh dong:** In-app notifications, real-time chat voi polling
- **Ly do:** Polling 3s cho chat thay vi WebSocket do don gian hon cho MVP
- **Quyet dinh:** Khong dung Firebase FCM, chi in-app notifications
- **Schema:** Notifications su dung `target_url` (khong phai `entity_id`), Messages su dung `created_at` (khong phai `sent_at`)

## Giai Doan 9: Settings & Offline
**Thoi gian:** 2026-03-22

- **Hanh dong:** Settings screen, Room offline caching
- **Ly do:** Offline-first: Room Flow -> UI, network fetch -> Room upsert

## Giai Doan 10: Bo Sung Tinh Nang Thieu (Gap Analysis)
**Thoi gian:** 2026-03-22

### Bug Fix
- **isOwnComment bug:** PostDetailScreen so sanh `comment.authorId == post.authorId` thay vi `currentUserId` -> sua bang cach inject AuthRepository vao PostDetailViewModel va expose `currentUserId` StateFlow

### Tinh nang moi
1. **Remove Friend tu FriendsListScreen:** Them menu 3 cham (MoreVert) voi "Huy ket ban" cho moi friend item
2. **Send Message tu ProfileScreen:** Them nut "Nhan tin" khi friendshipStatus == FRIENDS, su dung `getOrCreateConversation` tu MessagesRepository
3. **Image Picker (Activity Result API):**
   - EditProfileScreen: `rememberLauncherForActivityResult(GetContent)` cho avatar va cover photo
   - CreatePostScreen: `GetContent` launcher cho anh bai viet, hien thi preview voi nut xoa
4. **Clickable Links trong PostCard:** Su dung `Intent(ACTION_VIEW, Uri.parse(url))` de mo browser
5. **User Search:** Them SearchScreen voi debounced search (300ms), SearchViewModel, `searchProfiles` API (ilike tren username/full_name). Nut search icon tren FeedScreen
6. **Dark Mode Toggle:** DataStore preferences cho dark mode, SettingsViewModel expose isDarkMode StateFlow, MainActivity doc preference va truyen vao PumiahSocialTheme
7. **Notification Preferences:** 4 toggle switches trong SettingsScreen (likes, comments, friend requests, messages) luu vao DataStore
8. **Network Monitor + Offline Banner:** ConnectivityManager NetworkCallback -> Flow<Boolean>, AnimatedVisibility banner do "Khong co ket noi mang" tren dau app

## Giai Doan 11: Sua Loi Schema Mismatch
**Thoi gian:** 2026-03-22

### Van de
Khi chay app tren thiet bi that, gap nhieu loi runtime do ten bang va ten cot trong code khong khop voi Supabase database thuc te.

### Phuong phap chan doan
- Su dung Supabase REST API OpenAPI endpoint (`/rest/v1/`) de lay schema thuc te cua tat ca cac bang
- So sanh voi `@SerialName` annotations trong Kotlin data models

### Cac sua doi chinh

#### Data Models (7 files)
| Model | Truoc | Sau |
|-------|-------|-----|
| Profile | `name`, `avatar_url`, `email` | `full_name`, `profile_photo_url`, bo `email` |
| Post | `author_id`, `content_text`, `image_url` | `profile_id`, `content`, `media_url`, them `post_type` |
| Comment | `author_id` | `profile_id` |
| Like | `post_id`, `comment_id`, `user_id` | `target_id`, `target_type`, `profile_id` |
| Notification | `entity_id` | `target_url` |
| Conversation | (thieu `last_message_at`) | them `lastMessageAt` |
| Message | `sent_at` | `created_at` |

#### Room Entities (3 files)
- PostEntity: them `postType`, `contentText` -> `content`
- ProfileEntity: bo `email`
- MessageEntity: `sentAt` -> `createdAt`

#### Repositories (5 files)
- InteractionsRepositoryImpl: viet lai hoan toan cho likes polymorphic (`target_id`/`target_type`)
- PostsRepositoryImpl: `author_id` -> `profile_id`, `from("friends")` -> `from("friendships")`
- ProfileRepositoryImpl: `ilike("name",...)` -> `ilike("full_name",...)`
- MessagesRepositoryImpl: `order("sent_at",...)` -> `order("created_at",...)`
- AuthRepositoryImpl: profile creation dung `mapOf` voi `full_name`

#### UI Files (6 files)
- PostCard, PostDetailScreen: `contentText` -> `content`
- SettingsScreen: bo `email`, thay bang `username`
- ChatScreen, ConversationsListScreen: `sentAt` -> `createdAt`
- NotificationsScreen: `entityId` -> `targetUrl`

### Ket qua
- Build thanh cong: `./gradlew assembleDebug` PASSED
- APK debug: 21.4 MB
- Thoi gian build (cold): ~1 phut 23 giay

## Giai Doan 12: UX Improvements & Bug Fixes
**Thoi gian:** 2026-03-23 ~ 2026-03-24

### 12.1 Friendly Error Messages
- **Hanh dong:** Them `toFriendlyError()` trong AuthViewModel
- **Ly do:** Supabase tra ve raw error voi URLs/tokens, khong phu hop hien thi cho user
- **Mapping:** `invalid_credentials` -> "Email hoac mat khau khong dung", `user_already_exists` -> "Email nay da duoc dang ky", `over_email_send_rate_limit` -> "Gui qua nhieu yeu cau", network errors -> "Khong the ket noi"

### 12.2 Password Visibility Toggle
- **Hanh dong:** Them eye icon toggle cho tat ca password fields (Login, SignUp)
- **Ly do:** UX tieu chuan, giup user xac nhan mat khau da nhap

### 12.3 Signup Email Confirmation
- **Hanh dong:** Them AlertDialog sau khi dang ky thanh cong
- **Ly do:** Supabase gui email xac nhan nhung app khong thong bao user -> user khong biet can check email

### 12.4 Create Profile Screen + Redirect
- **Hanh dong:** Tao CreateProfileScreen, ProfileCheckViewModel, redirect tu MainActivity
- **Ly do:** User moi sau dang ky chua co profile -> can man hinh tao profile rieng
- **Quyet dinh:** Dung EditProfileViewModel chung cho ca CreateProfile va EditProfile

### 12.5 Clear All Notifications
- **Hanh dong:** Them nut xoa tat ca thong bao + AlertDialog xac nhan
- **Ly do:** User can don dep thong bao cu
- **Bug fix:** Can them DELETE RLS policy tren Supabase (`supabase/add_delete_notifications_policy.sql`)

### 12.6 New Conversation Screen
- **Hanh dong:** Tao NewConversationScreen + NewConversationViewModel
- **Ly do:** User can chon ban be de bat dau cuoc hoi thoai moi tu tab Tin nhan
- **Cai tien:** Them search filter de loc ban be theo ten/username

### 12.7 Storage Bucket Fix
- **Hanh dong:** Sua ten bucket trong ProfileRepositoryImpl va PostsRepositoryImpl
- **Ly do:** Code dung sai ten bucket (`avatars`, `covers`, `post-images`) -> upload that bai
- **Sua doi:**
  - `avatars` -> `profile_photos`
  - `covers` -> `profile_photos`
  - `post-images` -> `post_images`

### 12.8 Styled Input Fields
- **Hanh dong:** Chuyen tat ca input sang filled TextField voi RoundedCornerShape, transparent indicators
- **Ly do:** Thong nhat giao dien theo Material3 design reference

### 12.9 Serialization Fix
- **Hanh dong:** Chuyen `mapOf<String, Any?>` sang `buildJsonObject` voi `JsonPrimitive` trong ProfileRepositoryImpl.updateProfile
- **Ly do:** Kotlinx Serialization khong ho tro serialize `Any` type
