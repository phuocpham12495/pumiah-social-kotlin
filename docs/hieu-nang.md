# Danh Gia Hieu Nang - Pumiah Social

## Chi So Hieu Nang

| Chi so | Muc tieu | Thuc te | Dat/Khong |
|--------|----------|---------|-----------|
| Build time (lanh) | < 120s | ~83s (1m 23s) | Dat |
| Build time (am) | < 30s | ~15-25s (UP-TO-DATE tasks) | Dat |
| Kich thuoc APK (debug) | < 30MB | 21.4 MB | Dat |
| Kich thuoc APK (release, minified) | < 15MB | Chua do (R8 chua bat) | - |
| Thoi gian khoi dong | < 3s | Can do tren thiet bi | - |
| Bo nho runtime (idle) | < 100MB | Can do tren thiet bi | - |
| Bo nho runtime (scrolling feed) | < 200MB | Can do tren thiet bi | - |
| Frame rate (scrolling) | 60fps | Can do tren thiet bi | - |

## Thong Tin Build

| Thong so | Gia tri |
|----------|---------|
| Gradle version | 9.3.1 |
| AGP version | 8.9.1 |
| Kotlin version | 2.1.0 |
| Compile SDK | 36 (Android 15) |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 36 |
| JVM target | 11 |
| Total Gradle tasks | 40 |

## Toi Uu Hoa Da Ap Dung

### 1. Image Loading (Coil 3.0.4)
- Automatic memory + disk caching
- Lazy loading trong LazyColumn
- Circular crop cho avatars
- Chia se Ktor engine voi Supabase SDK

### 2. LazyColumn
- Key-based item identity (`key = { it.post.id }`)
- Tranh recomposition khong can thiet
- Pull-to-refresh thay vi auto-refresh

### 3. StateFlow
- `WhileSubscribed(5000)` cho auth state
- Tranh memory leak khi screen khong active

### 4. Room Database (2.6.1)
- Offline-first giam network calls
- Flow-based queries (chi emit khi data thay doi)
- Upsert thay vi delete + insert

### 5. Network
- Batch queries khi co the (enrich posts trong 1 lan)
- Limit 50 posts per feed load
- Polling interval 3s cho chat (co the tang)

### 6. Network Monitoring
- ConnectivityManager callback -> Flow (distinctUntilChanged)
- Offline banner chi hien thi khi mat mang (AnimatedVisibility)
- Tranh unnecessary API calls khi offline

### 7. Search Optimization
- Debounce 300ms tren search query (tranh spam API)
- Minimum 2 ky tu truoc khi search
- Cancel previous search job khi query thay doi

### 8. Image Picker
- Doc bytes tu ContentResolver (streaming, khong buffer toan bo trong memory)
- Ten file unique (timestamp-based) tranh cache conflicts

## De Xuat Toi Uu Tiep Theo
- Bat R8/ProGuard cho release build de giam kich thuoc APK
- Su dung Paging3 cho feed pagination thay vi limit(50) co dinh
- Chuyen chat tu polling sang Supabase Realtime WebSocket
- Image compression truoc khi upload len Storage
- Baseline Profiles cho khoi dong nhanh hon
