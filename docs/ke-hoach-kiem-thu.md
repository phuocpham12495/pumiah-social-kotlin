# Ke Hoach Kiem Thu - Pumiah Social

## 1. Unit Tests

### AuthRepositoryImpl
```kotlin
@Test fun `signIn with valid credentials returns success`()
@Test fun `signIn with invalid credentials returns failure`()
@Test fun `signUp creates profile with full_name and username`()
@Test fun `signOut invalidates session`()
@Test fun `observeAuthState emits correct states`()
@Test fun `getCurrentUserId returns user ID when authenticated`()
```

### ProfileRepositoryImpl
```kotlin
@Test fun `fetchProfile returns profile from Supabase profiles table`()
@Test fun `fetchProfile caches to Room via ProfileEntity`()
@Test fun `updateProfile updates both remote and local`()
@Test fun `uploadAvatar uploads to avatars bucket and returns public URL`()
@Test fun `uploadCoverPhoto uploads to covers bucket`()
@Test fun `searchProfiles uses ilike on username and full_name`()
@Test fun `getProfileFlow returns Room Flow mapped to Profile`()
```

### FriendsRepositoryImpl
```kotlin
@Test fun `sendFriendRequest creates request and notification in Supabase`()
@Test fun `acceptFriendRequest inserts sorted user IDs into friendships table`()
@Test fun `acceptFriendRequest ensures user1_id less than user2_id`()
@Test fun `declineFriendRequest updates status to declined`()
@Test fun `removeFriend deletes from friendships table with sorted IDs`()
@Test fun `getFriendsList queries friendships table both directions`()
@Test fun `getFriendshipStatus returns FRIENDS when friendship exists`()
@Test fun `getFriendshipStatus returns PENDING_SENT when request exists`()
@Test fun `getFriendshipStatus returns NONE when no relationship`()
@Test fun `refreshFriendsList syncs friendships to Room FriendEntity`()
```

### PostsRepositoryImpl
```kotlin
@Test fun `getFeedPosts queries friendships table for friend IDs`()
@Test fun `getFeedPosts filters posts by profile_id in friend IDs`()
@Test fun `createPost with image uploads to post-images bucket`()
@Test fun `createPost sets correct post_type based on content`()
@Test fun `enrichPosts queries likes with target_type eq post`()
@Test fun `enrichPosts counts comments by post_id`()
@Test fun `enrichPosts checks isLikedByMe via profile_id match`()
```

### InteractionsRepositoryImpl
```kotlin
@Test fun `togglePostLike uses target_id and target_type eq post`()
@Test fun `togglePostLike removes like when already liked`()
@Test fun `togglePostLike creates notification for post author`()
@Test fun `toggleCommentLike uses target_id and target_type eq comment`()
@Test fun `addComment inserts with profile_id as current user`()
@Test fun `addComment creates notification with target_url`()
@Test fun `deleteComment also deletes associated likes by target_id`()
@Test fun `getCommentsForPost enriches with author profile and like counts`()
```

### MessagesRepositoryImpl
```kotlin
@Test fun `getConversations queries conversations both user1_id and user2_id`()
@Test fun `getMessages orders by created_at ascending`()
@Test fun `getMessages caches to Room via MessageEntity`()
@Test fun `sendMessage inserts with sender_id as current user`()
@Test fun `getOrCreateConversation checks both orderings before creating`()
@Test fun `markConversationRead updates is_read for other user messages`()
```

### NotificationsRepositoryImpl
```kotlin
@Test fun `getNotifications filters by recipient_id`()
@Test fun `markAsRead updates is_read to true`()
@Test fun `markAllAsRead updates all unread for recipient`()
```

### SearchViewModel
```kotlin
@Test fun `updateQuery debounces search by 300ms`()
@Test fun `updateQuery with less than 2 chars returns empty`()
@Test fun `updateQuery cancels previous search job`()
```

### SettingsViewModel
```kotlin
@Test fun `toggleDarkMode saves preference to DataStore`()
@Test fun `notification preferences persist across sessions`()
@Test fun `logout clears session and invokes callback`()
```

### NetworkMonitor
```kotlin
@Test fun `isOnline emits true when network available`()
@Test fun `isOnline emits false when network lost`()
@Test fun `isOnline uses distinctUntilChanged`()
```

## 2. Integration Tests

### Supabase Integration
```kotlin
@Test fun `full auth flow - signup login logout`()
@Test fun `create and fetch profile from profiles table`()
@Test fun `send and accept friend request creates friendships entry`()
@Test fun `create post with profile_id and verify in feed`()
@Test fun `send and receive message in conversation`()
@Test fun `like post creates entry with target_type eq post`()
@Test fun `search profiles by username and full_name`()
```

## 3. E2E Test Flows (theo User Stories)

### Flow 1: Dang ky va Thiet lap Profile
1. Mo app -> Hien thi LoginScreen
2. Nhan "Dang ky" -> SignUpScreen
3. Nhap email + password -> Dang ky thanh cong
4. Redirect den FeedScreen (profile tu dong tao voi username tu email)
5. Vao EditProfileScreen -> Nhap ten, bio, upload avatar
6. Verify profile hien thi dung

### Flow 2: Ket ban va Xem Feed
1. Dang nhap -> FeedScreen (trong)
2. Nhan icon search -> SearchScreen
3. Tim user bang username hoac full_name
4. Vao ProfileScreen -> Nhan "Ket ban"
5. User kia chap nhan -> Status thay doi thanh FRIENDS
6. Tao bai viet -> Hien thi trong feed cua ban

### Flow 3: Tuong tac (Like/Comment)
1. Xem post trong feed
2. Nhan "Thich" -> Like count tang (target_type = "post")
3. Nhan vao post -> PostDetailScreen
4. Viet comment -> Hien thi (profile_id = currentUserId)
5. Xoa comment cua minh -> Bien mat (kem likes lien quan)

### Flow 4: Nhan tin
1. Vao ConversationsListScreen
2. Mo chat -> ChatScreen
3. Gui tin nhan -> Hien thi
4. Tin nhan moi tu dong xuat hien (polling 3s)
5. Hoac: tu ProfileScreen nhan "Nhan tin" -> tao/mo conversation

### Flow 5: Tim kiem nguoi dung
1. Vao FeedScreen -> Nhan icon tim kiem
2. SearchScreen hien thi
3. Nhap ten (>= 2 ky tu) -> Ket qua hien thi sau 300ms debounce
4. Nhan vao user -> ProfileScreen

### Flow 6: Image Picker
1. Vao EditProfileScreen -> Nhan "Doi anh dai dien"
2. Gallery picker mo -> Chon anh
3. Anh upload len Supabase Storage (avatars bucket)
4. Avatar cap nhat
5. Tuong tu cho cover photo va anh bai viet

### Flow 7: Dark Mode
1. Vao SettingsScreen
2. Bat toggle "Che do toi"
3. Giao dien chuyen sang dark theme ngay lap tuc
4. Restart app -> Dark mode van duoc giu (DataStore)

### Flow 8: Offline Mode
1. Tat wifi/data
2. Banner do "Khong co ket noi mang" hien thi (AnimatedVisibility)
3. Noi dung cached van hien thi tu Room
4. Bat lai mang -> Banner bien mat

### Flow 9: Xoa ban be
1. Vao FriendsListScreen
2. Nhan menu 3 cham (MoreVert) tren friend item
3. Chon "Huy ket ban"
4. Friend bi xoa khoi danh sach va bang friendships

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

## 5. Mapping Test Cases -> User Stories

| Test Case | User Story | Trang thai |
|-----------|------------|------------|
| TC-US019-001 | Dang ky | San sang test |
| TC-US020-001 | Dang nhap | San sang test |
| TC-US001-001 | Tao profile | San sang test |
| TC-US003-001 | Sua profile (+ image picker) | San sang test |
| TC-US004-002 | Tim kiem user (debounced) | San sang test |
| TC-US005-001 | Gui loi moi ket ban | San sang test |
| TC-US006-001 | Chap nhan loi moi | San sang test |
| TC-US009-001 | Xoa ban be tu danh sach | San sang test |
| TC-US010-001 | Tao bai viet text | San sang test |
| TC-US010-002 | Tao bai viet anh (image picker) | San sang test |
| TC-US011-001 | Feed chronological | San sang test |
| TC-US012-001 | Like bai viet (target_type=post) | San sang test |
| TC-US013-001 | Comment bai viet (profile_id) | San sang test |
| TC-US016-001 | Notification like | San sang test |
| TC-US017-001 | Gui tin nhan | San sang test |
| TC-US017-002 | Gui tin nhan tu profile | San sang test |
| TC-US021-001 | Dark mode toggle | San sang test |
| TC-US021-004 | Notification preferences | San sang test |
| TC-US023-001 | Offline banner | San sang test |
| TC-US024-* | Push notifications (FCM) | N/A - Khong trien khai |
