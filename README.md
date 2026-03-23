# Huong Dan Cai Dat - Pumiah Social

## Yeu Cau Tien Quyet

| Yeu cau | Phien ban toi thieu |
|----------|---------------------|
| Android Studio | Ladybug 2024.3+ |
| JDK | 11+ |
| Android SDK | API 24 (Android 7.0)+ |
| Gradle | 9.3.1 (da bao gom wrapper) |
| Kotlin | 2.1.0 |

## Cac Buoc Cai Dat

### 1. Clone du an
```bash
git clone <repository-url>
cd pumiah-social-kotlin
```

### 2. Mo du an trong Android Studio
- File -> Open -> Chon thu muc `pumiah-social-kotlin`
- Doi Gradle sync hoan tat

### 3. Cau hinh Supabase (da duoc cau hinh san)
Thong tin Supabase da duoc cau hinh trong `app/build.gradle.kts`:
```kotlin
buildConfigField("String", "SUPABASE_URL", "\"https://syaappwmkvajximwyhhj.supabase.co\"")
buildConfigField("String", "SUPABASE_ANON_KEY", "\"...\"")
```

> **Luu y bao mat:** Trong production, nen chuyen sang `local.properties` hoac bien moi truong.

### 4. Build APK
```bash
./gradlew assembleDebug
```
APK output: `app/build/outputs/apk/debug/app-debug.apk` (~21.4 MB)

Hoac nhan Run trong Android Studio.

### 5. Cai dat tren thiet bi
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Bang Tham Chieu Bien Moi Truong

| Bien | Mo ta | Bat buoc |
|------|--------|----------|
| SUPABASE_URL | URL cua Supabase project | Co |
| SUPABASE_ANON_KEY | Anonymous key cua Supabase | Co |

## Cau Truc Thu Muc Chinh

```
app/src/main/java/com/phuocpham/pumiahsocial/
├── PumiahSocialApp.kt          # Application class (@HiltAndroidApp)
├── MainActivity.kt              # Entry point + offline banner
├── di/                          # Hilt DI modules
│   ├── SupabaseModule.kt       # SupabaseClient, Auth, Postgrest, Storage
│   ├── DatabaseModule.kt       # Room AppDatabase + DAOs
│   ├── RepositoryModule.kt     # Bind interfaces -> implementations
│   └── DataStoreModule.kt      # DataStore<Preferences>
├── data/
│   ├── model/                   # @Serializable data classes (12 files)
│   │   ├── Profile.kt          # profiles table
│   │   ├── Post.kt             # posts table (profile_id, post_type)
│   │   ├── Comment.kt          # comments table (profile_id)
│   │   ├── Like.kt             # likes table (target_id, target_type)
│   │   ├── Notification.kt     # notifications table (target_url)
│   │   ├── Message.kt          # messages table (created_at)
│   │   ├── Conversation.kt     # conversations table
│   │   ├── Friend.kt           # friendships table
│   │   ├── FriendRequest.kt    # friend_requests table
│   │   └── ...
│   ├── repository/              # 7 repository interfaces + implementations
│   └── local/                   # Room database
│       ├── AppDatabase.kt
│       ├── dao/                 # 4 DAOs
│       └── entity/              # 5 Room entities
├── ui/
│   ├── auth/                    # Dang nhap/Dang ky
│   ├── feed/                    # Bang tin + Tao bai viet (image picker)
│   ├── profile/                 # Ho so (image picker cho avatar/cover)
│   ├── friends/                 # Ban be (remove friend tu danh sach)
│   ├── notifications/           # Thong bao
│   ├── messaging/               # Nhan tin
│   ├── search/                  # Tim kiem nguoi dung (debounced)
│   ├── settings/                # Cai dat (dark mode, notification prefs)
│   ├── components/              # 6 UI components dung chung
│   └── theme/                   # Theme, Colors, Typography
├── navigation/                  # Screen routes + NavGraph + BottomNavItem
└── util/                        # UiState, Extensions, DateFormatter, NetworkMonitor
```

## Supabase Database Schema

| Bang | Cot chinh |
|------|-----------|
| profiles | id, username, full_name, bio, profile_photo_url, cover_photo_url, location, date_of_birth, created_at |
| posts | id, profile_id, post_type, content, media_url, link_url, created_at |
| comments | id, post_id, profile_id, content, created_at |
| likes | id, profile_id, target_id, target_type, created_at |
| friendships | id, user1_id, user2_id, created_at |
| friend_requests | id, sender_id, receiver_id, status, created_at |
| conversations | id, user1_id, user2_id, last_message_at, created_at |
| messages | id, conversation_id, sender_id, content, is_read, created_at |
| notifications | id, recipient_id, sender_id, type, message, target_url, is_read, created_at |

## Khac Phuc Su Co

### Gradle sync that bai
- Dam bao JDK 11+ da cai dat
- File -> Invalidate Caches -> Restart

### Loi ket noi Supabase
- Kiem tra SUPABASE_URL va SUPABASE_ANON_KEY
- Dam bao thiet bi co ket noi internet
- Kiem tra RLS policies tren Supabase dashboard

### Loi "Could not find table"
- Xac minh ten bang trong code khop voi Supabase schema
- Luu y: bang la `friendships` (khong phai `friends`)

### Loi "column does not exist"
- Kiem tra `@SerialName` annotations trong data models
- So sanh voi schema thuc te qua Supabase REST API endpoint
