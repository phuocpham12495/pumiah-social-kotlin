# Hướng Dẫn Cài Đặt - Pumiah Social

## Yêu Cầu Tiên Quyết

| Yêu cầu | Phiên bản tối thiểu |
|----------|---------------------|
| Android Studio | Ladybug 2024.3+ |
| JDK | 11+ |
| Android SDK | API 24 (Android 7.0)+ |
| Gradle | 9.3.1 (đã bao gồm wrapper) |
| Kotlin | 2.2.10 |

## Các Bước Cài Đặt

### 1. Clone dự án
```bash
git clone <repository-url>
cd pumiah-social-kotlin
```

### 2. Mở dự án trong Android Studio
- File → Open → Chọn thư mục `pumiah-social-kotlin`
- Đợi Gradle sync hoàn tất

### 3. Cấu hình Supabase (đã được cấu hình sẵn)
Thông tin Supabase đã được cấu hình trong `app/build.gradle.kts`:
```kotlin
buildConfigField("String", "SUPABASE_URL", "\"https://syaappwmkvajximwyhhj.supabase.co\"")
buildConfigField("String", "SUPABASE_ANON_KEY", "\"...\"")
```

> **Lưu ý bảo mật:** Trong production, nên chuyển sang `gradle.properties` hoặc biến môi trường.

### 4. Chạy ứng dụng
```bash
./gradlew assembleDebug
```
Hoặc nhấn Run (▶) trong Android Studio.

## Bảng Tham Chiếu Biến Môi Trường

| Biến | Mô tả | Bắt buộc |
|------|--------|----------|
| SUPABASE_URL | URL của Supabase project | Có |
| SUPABASE_ANON_KEY | Anonymous key của Supabase | Có |

## Cấu Trúc Thư Mục Chính

```
app/src/main/java/com/phuocpham/pumiahsocial/
├── PumiahSocialApp.kt       # Application class
├── MainActivity.kt           # Entry point + offline banner
├── di/                       # Hilt DI modules
├── data/
│   ├── model/               # Data classes
│   ├── repository/          # Business logic
│   └── local/               # Room database
├── ui/
│   ├── auth/                # Đăng nhập/Đăng ký
│   ├── feed/                # Bảng tin + Tạo bài viết (image picker)
│   ├── profile/             # Hồ sơ (image picker cho avatar/cover)
│   ├── friends/             # Bạn bè (remove friend từ danh sách)
│   ├── notifications/       # Thông báo
│   ├── messaging/           # Nhắn tin
│   ├── search/              # Tìm kiếm người dùng
│   ├── settings/            # Cài đặt (dark mode, notification prefs)
│   ├── components/          # UI components dùng chung
│   └── theme/               # Theme, Colors, Typography
├── navigation/              # Navigation graph
└── util/                    # Utilities + NetworkMonitor
```

## Khắc Phục Sự Cố

### Gradle sync thất bại
- Đảm bảo JDK 11+ đã cài đặt
- File → Invalidate Caches → Restart

### Lỗi kết nối Supabase
- Kiểm tra SUPABASE_URL và SUPABASE_ANON_KEY
- Đảm bảo thiết bị có kết nối internet
- Kiểm tra RLS policies trên Supabase dashboard
