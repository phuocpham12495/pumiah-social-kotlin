# Đánh Giá Hiệu Năng - Pumiah Social

## Chỉ Số Hiệu Năng

| Chỉ số | Mục tiêu | Thực tế | Đạt/Không |
|--------|----------|---------|-----------|
| Build time (lạnh) | < 120s | Cần đo | - |
| Build time (ấm) | < 30s | Cần đo | - |
| Kích thước APK (debug) | < 30MB | Cần đo | - |
| Kích thước APK (release, minified) | < 15MB | Cần đo | - |
| Thời gian khởi động | < 3s | Cần đo | - |
| Bộ nhớ runtime (idle) | < 100MB | Cần đo | - |
| Bộ nhớ runtime (scrolling feed) | < 200MB | Cần đo | - |
| Frame rate (scrolling) | 60fps | Cần đo | - |

## Tối Ưu Hóa Đã Áp Dụng

### 1. Image Loading (Coil)
- Automatic memory + disk caching
- Lazy loading trong LazyColumn
- Circular crop cho avatars

### 2. LazyColumn
- Key-based item identity (`key = { it.post.id }`)
- Tránh recomposition không cần thiết
- Pull-to-refresh thay vì auto-refresh

### 3. StateFlow
- `WhileSubscribed(5000)` cho auth state
- Tránh memory leak khi screen không active

### 4. Room Database
- Offline-first giảm network calls
- Flow-based queries (chỉ emit khi data thay đổi)
- Upsert thay vì delete + insert

### 5. Network
- Batch queries khi có thể (enrich posts trong 1 lần)
- Limit 50 posts per feed load
- Polling interval 3s cho chat (có thể tăng)

### 6. Network Monitoring
- ConnectivityManager callback → Flow (distinctUntilChanged)
- Offline banner chỉ hiển thị khi mất mạng (AnimatedVisibility)
- Tránh unnecessary API calls khi offline

### 7. Search Optimization
- Debounce 300ms trên search query (tránh spam API)
- Minimum 2 ký tự trước khi search
- Cancel previous search job khi query thay đổi

### 8. Image Picker
- Đọc bytes từ ContentResolver (streaming, không buffer toàn bộ trong memory)
- Tên file unique (timestamp-based) tránh cache conflicts

## Ghi Chú
- Các chỉ số "Cần đo" sẽ được cập nhật sau khi build thành công trên thiết bị thật
- Sử dụng Android Studio Profiler để đo chi tiết
- ProGuard/R8 chưa bật cho debug builds
