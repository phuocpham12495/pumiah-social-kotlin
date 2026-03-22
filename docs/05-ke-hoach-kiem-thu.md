# Kế Hoạch Kiểm Thử - Pumiah Social

## 1. Unit Tests

### AuthRepositoryImpl
```kotlin
@Test fun `signIn with valid credentials returns success`()
@Test fun `signIn with invalid credentials returns failure`()
@Test fun `signUp creates profile after registration`()
@Test fun `signOut invalidates session`()
@Test fun `observeAuthState emits correct states`()
```

### ProfileRepositoryImpl
```kotlin
@Test fun `fetchProfile returns profile from Supabase`()
@Test fun `fetchProfile caches to Room`()
@Test fun `updateProfile updates both remote and local`()
@Test fun `uploadAvatar returns public URL`()
```

### FriendsRepositoryImpl
```kotlin
@Test fun `sendFriendRequest creates request and notification`()
@Test fun `acceptFriendRequest inserts sorted user IDs into friends`()
@Test fun `declineFriendRequest updates status`()
@Test fun `removeFriend deletes from friends table`()
@Test fun `getFriendshipStatus returns correct status`()
```

### PostsRepositoryImpl
```kotlin
@Test fun `getFeedPosts returns only friends' posts`()
@Test fun `createPost with image uploads to storage`()
@Test fun `enrichPosts includes like and comment counts`()
```

### InteractionsRepositoryImpl
```kotlin
@Test fun `togglePostLike adds like when not liked`()
@Test fun `togglePostLike removes like when already liked`()
@Test fun `addComment creates notification for post author`()
@Test fun `deleteComment also deletes associated likes`()
```

## 2. Integration Tests

### Supabase Integration
```kotlin
@Test fun `full auth flow - signup, login, logout`()
@Test fun `create and fetch profile`()
@Test fun `send and accept friend request`()
@Test fun `create post and verify in feed`()
@Test fun `send and receive message`()
```

## 3. E2E Test Flows (theo User Stories)

### Flow 1: Đăng ký và Thiết lập Profile
1. Mở app → Hiển thị LoginScreen
2. Nhấn "Đăng ký" → SignUpScreen
3. Nhập email + password → Đăng ký thành công
4. Redirect đến EditProfileScreen
5. Nhập tên, bio → Lưu
6. Verify profile hiển thị đúng

### Flow 2: Kết bạn và Xem Feed
1. Đăng nhập → FeedScreen (trống)
2. Tìm user → ProfileScreen
3. Nhấn "Kết bạn" → Status thay đổi
4. User kia chấp nhận
5. Tạo bài viết → Hiển thị trong feed của bạn

### Flow 3: Tương tác
1. Xem post trong feed
2. Nhấn "Thích" → Count tăng
3. Nhấn "Bình luận" → PostDetailScreen
4. Viết comment → Hiển thị
5. Xóa comment → Biến mất

### Flow 4: Nhắn tin
1. Vào ConversationsListScreen
2. Mở chat → ChatScreen
3. Gửi tin nhắn → Hiển thị
4. Tin nhắn mới tự động xuất hiện (polling)

## 4. CI/CD Pipeline

```yaml
# .github/workflows/android.yml
name: Android CI

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
      - name: Grant execute permission
        run: chmod +x gradlew
      - name: Build debug APK
        run: ./gradlew assembleDebug
      - name: Run unit tests
        run: ./gradlew test
      - name: Run lint
        run: ./gradlew lint
```

### SearchRepositoryImpl (mới)
```kotlin
@Test fun `searchProfiles with valid query returns matching profiles`()
@Test fun `searchProfiles with short query returns empty list`()
@Test fun `searchProfiles matches on username or name`()
```

### SettingsViewModel (mới)
```kotlin
@Test fun `toggleDarkMode saves preference to DataStore`()
@Test fun `notification preferences persist across sessions`()
@Test fun `logout clears session and navigates to login`()
```

### NetworkMonitor (mới)
```kotlin
@Test fun `isOnline emits true when connected`()
@Test fun `isOnline emits false when disconnected`()
@Test fun `isOnline updates on network change`()
```

## 3.5 Test Bổ Sung Cho Tính Năng Mới

### Flow 5: Tìm kiếm người dùng
1. Vào FeedScreen → Nhấn icon tìm kiếm
2. SearchScreen hiển thị
3. Nhập tên → Kết quả hiển thị sau debounce
4. Nhấn vào user → ProfileScreen

### Flow 6: Image Picker
1. Vào EditProfileScreen → Nhấn "Đổi ảnh đại diện"
2. Gallery picker mở → Chọn ảnh
3. Ảnh upload lên Supabase Storage
4. Avatar cập nhật

### Flow 7: Dark Mode
1. Vào SettingsScreen
2. Bật toggle "Chế độ tối"
3. Giao diện chuyển sang dark theme ngay lập tức
4. Restart app → Dark mode vẫn được giữ

### Flow 8: Offline Mode
1. Tắt wifi/data
2. Banner đỏ "Không có kết nối mạng" hiển thị
3. Nội dung cached vẫn hiển thị
4. Bật lại mạng → Banner biến mất

## Mapping Test Cases → User Stories

| Test Case | User Story | Trạng thái |
|-----------|------------|------------|
| TC-US019-001 | Đăng ký | Sẵn sàng test |
| TC-US020-001 | Đăng nhập | Sẵn sàng test |
| TC-US001-001 | Tạo profile | Sẵn sàng test |
| TC-US003-001 | Sửa profile | Sẵn sàng test |
| TC-US004-002 | Tìm kiếm user | ✅ Đã triển khai |
| TC-US005-001 | Gửi lời mời kết bạn | Sẵn sàng test |
| TC-US006-001 | Chấp nhận lời mời | Sẵn sàng test |
| TC-US009-001 | Xóa bạn bè từ danh sách | ✅ Đã triển khai |
| TC-US010-001 | Tạo bài viết text | Sẵn sàng test |
| TC-US010-002 | Tạo bài viết ảnh (image picker) | ✅ Đã triển khai |
| TC-US011-001 | Feed chronological | Sẵn sàng test |
| TC-US012-001 | Like bài viết | Sẵn sàng test |
| TC-US013-001 | Comment bài viết | Sẵn sàng test |
| TC-US016-001 | Notification like | Sẵn sàng test |
| TC-US017-001 | Gửi tin nhắn | Sẵn sàng test |
| TC-US017-002 | Gửi tin nhắn từ profile | ✅ Đã triển khai |
| TC-US021-001 | Dark mode toggle | ✅ Đã triển khai |
| TC-US021-004 | Notification preferences | ✅ Đã triển khai |
| TC-US023-001 | Offline banner | ✅ Đã triển khai |
| TC-US024-* | Push notifications (FCM) | N/A - Không triển khai |
