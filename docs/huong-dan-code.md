# Huong Dan Code - Pumiah Social

## 1. Diem Vao -> Duyet Cay Component

```
MainActivity.kt (@AndroidEntryPoint)
├── DataStore -> isDarkMode -> PumiahSocialTheme(darkTheme)
├── NetworkMonitor -> isOnline -> Offline Banner (AnimatedVisibility)
└── PumiahSocialApp() (@Composable)
    ├── AuthViewModel.authState -> Loading/Unauthenticated/Authenticated
    │
    ├── [Unauthenticated] -> PumiahNavGraph(startDestination = "login")
    │   ├── LoginScreen -> AuthViewModel.login()
    │   └── SignUpScreen -> AuthViewModel.signUp()
    │
    └── [Authenticated] -> Scaffold + BottomNavigationBar
        └── PumiahNavGraph(startDestination = "feed")
            ├── FeedScreen -> FeedViewModel
            │   ├── PostCard (shared component, clickable links)
            │   └── Search icon -> SearchScreen
            ├── SearchScreen -> SearchViewModel (debounced 300ms)
            ├── FriendsListScreen -> FriendsViewModel
            │   └── FriendListItem (menu: huy ket ban)
            ├── FriendRequestsScreen -> FriendsViewModel
            ├── NotificationsScreen -> NotificationsViewModel
            ├── ConversationsListScreen -> ConversationsViewModel
            │   └── ChatScreen -> ChatViewModel (polling 3s)
            ├── ProfileScreen -> ProfileViewModel
            │   ├── EditProfileScreen -> EditProfileViewModel (image picker)
            │   └── "Nhan tin" button (khi FRIENDS)
            ├── PostDetailScreen -> PostDetailViewModel
            │   └── CommentItem (isOwnComment = currentUserId)
            ├── CreatePostScreen -> CreatePostViewModel (image picker)
            └── SettingsScreen -> SettingsViewModel
                ├── Dark mode toggle (DataStore)
                └── Notification preferences (DataStore)
```

## 2. Luong Quan Ly State

### Pattern: ViewModel -> StateFlow -> Composable

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

## 3. Chien Luoc Fetch va Cache Du Lieu

### Offline-First Pattern
```
User Action -> ViewModel -> Repository
                              |
                    +---------+---------+
                    |                   |
              Room (local)        Supabase (remote)
                    |                   |
                    |    <- upsert <-   |
                    |                   |
                Flow<Data> -> UI auto-updates
```

### Vi du: ProfileRepository
```kotlin
// 1. UI observe Room Flow (hien thi cache ngay)
fun getProfileFlow(userId: String): Flow<Profile?> =
    profileDao.getProfileById(userId).map { it?.toProfile() }

// 2. Fetch tu network, cache vao Room
suspend fun fetchProfile(userId: String): Result<Profile> = safeApiCall {
    val profile = postgrest.from("profiles")
        .select { filter { eq("id", userId) } }
        .decodeSingle<Profile>()
    profileDao.upsertProfile(ProfileEntity.fromProfile(profile))
    profile
}
```

## 4. Supabase Schema Mapping

### Cach @SerialName hoat dong
Kotlin data models dung ten property de doc (camelCase), nhung `@SerialName` chi dinh ten cot thuc te trong Supabase:

```kotlin
@Serializable
data class Post(
    val id: String = "",
    @SerialName("profile_id") val authorId: String = "",    // cot: profile_id
    @SerialName("post_type") val postType: String = "text", // cot: post_type
    val content: String? = null,                             // cot: content
    @SerialName("media_url") val imageUrl: String? = null,  // cot: media_url
    @SerialName("link_url") val linkUrl: String? = null,    // cot: link_url
    @SerialName("created_at") val createdAt: String = ""    // cot: created_at
)
```

### Bang mapping day du

| Supabase Table.Column | Kotlin Model.Property | Ghi chu |
|------------------------|----------------------|---------|
| profiles.full_name | Profile.name | @SerialName("full_name") |
| profiles.profile_photo_url | Profile.avatarUrl | @SerialName("profile_photo_url") |
| profiles.cover_photo_url | Profile.coverPhotoUrl | @SerialName("cover_photo_url") |
| posts.profile_id | Post.authorId | @SerialName("profile_id") |
| posts.media_url | Post.imageUrl | @SerialName("media_url") |
| comments.profile_id | Comment.authorId | @SerialName("profile_id") |
| likes.profile_id | Like.userId | @SerialName("profile_id") |
| likes.target_id | Like.targetId | Polymorphic: post ID hoac comment ID |
| likes.target_type | Like.targetType | "post" hoac "comment" |
| notifications.target_url | Notification.targetUrl | @SerialName("target_url") |
| messages.created_at | Message.createdAt | @SerialName("created_at") |

## 5. Cac Mau Thiet Ke Chinh

### Repository Pattern
- Interface + Implementation
- Abstraction layer giua data sources va ViewModels
- De mock cho testing

### Sealed Class cho UI State
```kotlin
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```
-> Exhaustive when expression, khong bo sot case

### safeApiCall Wrapper
```kotlin
suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) { throw e }
    catch (e: Exception) { Result.failure(e) }
}
```
-> Bat moi exception tru CancellationException (coroutine contract)

### Friends Table ID Ordering
```kotlin
// Constraint trong Supabase: user1_id < user2_id (bang friendships)
val (u1, u2) = if (currentUserId < otherUserId)
    currentUserId to otherUserId
else
    otherUserId to currentUserId
postgrest.from("friendships").insert(Friend(user1Id = u1, user2Id = u2))
```
-> Tranh duplicate entries (A,B) vs (B,A)

### Polymorphic Likes
```kotlin
// Like mot post
postgrest.from("likes").insert(mapOf(
    "profile_id" to currentUserId,
    "target_id" to postId,
    "target_type" to "post"
))

// Like mot comment
postgrest.from("likes").insert(mapOf(
    "profile_id" to currentUserId,
    "target_id" to commentId,
    "target_type" to "comment"
))

// Query likes cho posts
postgrest.from("likes").select {
    filter {
        isIn("target_id", postIds)
        eq("target_type", "post")
    }
}
```

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
// Goi: imagePickerLauncher.launch("image/*")
```
-> Compose-friendly, lifecycle-aware image selection

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
-> Realtime network state tracking, auto-cleanup on scope cancellation

### Search voi Debounce
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
-> Tranh spam API khi user dang go

## 6. Navigation Routes

| Route | Screen | Arguments |
|-------|--------|-----------|
| `login` | LoginScreen | - |
| `signup` | SignUpScreen | - |
| `feed` | FeedScreen | - |
| `create_post` | CreatePostScreen | - |
| `post_detail/{postId}` | PostDetailScreen | postId: String |
| `my_profile` | ProfileScreen | - (dung currentUserId) |
| `user_profile/{userId}` | ProfileScreen | userId: String |
| `edit_profile` | EditProfileScreen | - |
| `friends_list` | FriendsListScreen | - |
| `friend_requests` | FriendRequestsScreen | - |
| `notifications` | NotificationsScreen | - |
| `conversations` | ConversationsListScreen | - |
| `chat/{conversationId}` | ChatScreen | conversationId: String |
| `search` | SearchScreen | - |
| `settings` | SettingsScreen | - |

## 7. Dependency Injection Graph

```
@HiltAndroidApp PumiahSocialApp
    |
    +-- SupabaseModule (@Singleton)
    |   +-- SupabaseClient (URL + AnonKey)
    |   +-- Auth (GoTrue)
    |   +-- Postgrest
    |   +-- Realtime
    |   +-- Storage
    |
    +-- DatabaseModule (@Singleton)
    |   +-- AppDatabase (Room)
    |   +-- ProfileDao
    |   +-- PostDao
    |   +-- FriendDao
    |   +-- MessageDao
    |
    +-- RepositoryModule (@Singleton)
    |   +-- AuthRepository -> AuthRepositoryImpl
    |   +-- ProfileRepository -> ProfileRepositoryImpl
    |   +-- FriendsRepository -> FriendsRepositoryImpl
    |   +-- PostsRepository -> PostsRepositoryImpl
    |   +-- InteractionsRepository -> InteractionsRepositoryImpl
    |   +-- MessagesRepository -> MessagesRepositoryImpl
    |   +-- NotificationsRepository -> NotificationsRepositoryImpl
    |
    +-- DataStoreModule (@Singleton)
        +-- DataStore<Preferences>
```
