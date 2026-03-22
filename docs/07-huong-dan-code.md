# Hướng Dẫn Code - Pumiah Social

## 1. Điểm Vào → Duyệt Cây Component

```
MainActivity.kt (@AndroidEntryPoint)
├── DataStore → isDarkMode → PumiahSocialTheme(darkTheme)
├── NetworkMonitor → isOnline → Offline Banner (AnimatedVisibility)
└── PumiahSocialApp() (@Composable)
    ├── AuthViewModel.authState → Loading/Unauthenticated/Authenticated
    │
    ├── [Unauthenticated] → PumiahNavGraph(startDestination = "login")
    │   ├── LoginScreen → AuthViewModel.login()
    │   └── SignUpScreen → AuthViewModel.signUp()
    │
    └── [Authenticated] → Scaffold + BottomNavigationBar
        └── PumiahNavGraph(startDestination = "feed")
            ├── FeedScreen → FeedViewModel
            │   ├── PostCard (shared component, clickable links)
            │   └── 🔍 Search icon → SearchScreen
            ├── SearchScreen → SearchViewModel (debounced search)
            ├── FriendsListScreen → FriendsViewModel
            │   └── FriendListItem (menu: hủy kết bạn)
            ├── NotificationsScreen → NotificationsViewModel
            ├── ConversationsListScreen → ConversationsViewModel
            │   └── ChatScreen → ChatViewModel
            ├── ProfileScreen → ProfileViewModel
            │   ├── EditProfileScreen → EditProfileViewModel (image picker)
            │   └── 💬 Nhắn tin button (khi FRIENDS)
            ├── PostDetailScreen → PostDetailViewModel
            │   └── CommentItem (isOwnComment = currentUserId)
            ├── CreatePostScreen → CreatePostViewModel (image picker)
            └── SettingsScreen → SettingsViewModel
                ├── Dark mode toggle (DataStore)
                └── Notification preferences (DataStore)
```

## 2. Luồng Quản Lý State

### Pattern: ViewModel → StateFlow → Composable

```kotlin
// ViewModel
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val postsRepository: PostsRepository
) : ViewModel() {
    private val _feedPosts = MutableStateFlow<UiState<List<PostWithDetails>>>(UiState.Loading)
    val feedPosts: StateFlow<UiState<List<PostWithDetails>>> = _feedPosts.asStateFlow()

    fun loadFeed() {
        viewModelScope.launch {
            _feedPosts.value = UiState.Loading
            postsRepository.getFeedPosts().fold(
                onSuccess = { _feedPosts.value = UiState.Success(it) },
                onFailure = { _feedPosts.value = UiState.Error(it.message) }
            )
        }
    }
}

// Composable
@Composable
fun FeedScreen(viewModel: FeedViewModel = hiltViewModel()) {
    val feedPosts by viewModel.feedPosts.collectAsState()
    when (val state = feedPosts) {
        is UiState.Loading -> LoadingIndicator()
        is UiState.Error -> ErrorMessage(state.message)
        is UiState.Success -> LazyColumn { items(state.data) { ... } }
    }
}
```

## 3. Chiến Lược Fetch và Cache Dữ Liệu

### Offline-First Pattern
```
User Action → ViewModel → Repository
                              ↓
                    ┌─────────┴─────────┐
                    │                   │
              Room (local)        Supabase (remote)
                    │                   │
                    │    ← upsert ←     │
                    │                   │
                Flow<Data> → UI auto-updates
```

### Ví dụ: ProfileRepository
```kotlin
// 1. UI observe Room Flow (hiển thị cache ngay)
fun getProfileFlow(userId: String): Flow<Profile?> =
    profileDao.getProfileById(userId).map { it?.toProfile() }

// 2. Fetch từ network, cache vào Room
suspend fun fetchProfile(userId: String): Result<Profile> = safeApiCall {
    val profile = postgrest.from("profiles")
        .select { filter { eq("id", userId) } }
        .decodeSingle<Profile>()
    profileDao.upsertProfile(ProfileEntity.fromProfile(profile))
    profile
}
```

## 4. Các Mẫu Thiết Kế Chính

### Repository Pattern
- Interface + Implementation
- Abstraction layer giữa data sources và ViewModels
- Dễ mock cho testing

### Sealed Class cho UI State
```kotlin
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```
→ Exhaustive when expression, không bỏ sót case

### safeApiCall Wrapper
```kotlin
suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) { throw e }
    catch (e: Exception) { Result.failure(e) }
}
```
→ Bắt mọi exception trừ CancellationException (coroutine contract)

### Friends Table ID Ordering
```kotlin
// Constraint: user1_id < user2_id
val (u1, u2) = if (currentUserId < otherUserId)
    currentUserId to otherUserId
else
    otherUserId to currentUserId
```
→ Tránh duplicate entries (A,B) vs (B,A)

### Activity Result API cho Image Picker
```kotlin
val imagePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
) { uri: Uri? ->
    uri?.let {
        val bytes = context.contentResolver.openInputStream(it)?.readBytes()
        if (bytes != null) {
            viewModel.uploadAvatar(bytes, "avatar_${System.currentTimeMillis()}.jpg")
        }
    }
}
// Gọi: imagePickerLauncher.launch("image/*")
```
→ Compose-friendly, lifecycle-aware image selection

### Network Monitoring
```kotlin
@Singleton
class NetworkMonitor @Inject constructor(@ApplicationContext context: Context) {
    val isOnline: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { trySend(true) }
            override fun onLost(network: Network) { trySend(false) }
        }
        connectivityManager.registerNetworkCallback(request, callback)
        awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
}
```
→ Realtime network state tracking, auto-cleanup on scope cancellation

### Search với Debounce
```kotlin
fun updateQuery(value: String) {
    _query.value = value
    searchJob?.cancel()
    if (value.length < 2) { _results.value = emptyList(); return }
    searchJob = viewModelScope.launch {
        delay(300) // debounce
        profileRepository.searchProfiles(value)
    }
}
```
→ Tránh spam API khi user đang gõ
