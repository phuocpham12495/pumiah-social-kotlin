# Ke Hoach Trien Khai - Pumiah Social

## Phan Tich Tinh Nang Theo Muc Uu Tien

### P0 - Bat buoc (MVP)
| Tinh nang | Module | Trang thai |
|-----------|--------|------------|
| Dang ky/Dang nhap/Dang xuat | auth | Hoan thanh |
| Tao/Xem/Sua ho so | profile | Hoan thanh |
| Gui/Chap nhan/Tu choi loi moi ket ban | friends | Hoan thanh |
| Danh sach ban be | friends | Hoan thanh |
| Tao bai viet (text/image/link) | feed | Hoan thanh |
| Bang tin chronological | feed | Hoan thanh |

### P1 - Quan trong
| Tinh nang | Module | Trang thai |
|-----------|--------|------------|
| Like/Unlike bai viet (polymorphic target_id/target_type) | interactions | Hoan thanh |
| Binh luan bai viet (profile_id) | interactions | Hoan thanh |
| Xoa binh luan (+ xoa likes lien quan) | interactions | Hoan thanh |
| Like binh luan (target_type=comment) | interactions | Hoan thanh |
| Thong bao in-app (target_url) | notifications | Hoan thanh |
| Xoa ban be tu danh sach | friends | Hoan thanh |

### P2 - Nen co
| Tinh nang | Module | Trang thai |
|-----------|--------|------------|
| Nhan tin truc tiep | messaging | Hoan thanh |
| Danh sach hoi thoai | messaging | Hoan thanh |
| Cai dat | settings | Hoan thanh |
| Offline caching (Room) | local | Hoan thanh |
| Theme sang/toi | theme | Hoan thanh |

### P2.5 - Da bo sung (Gap Analysis + Schema Fix)
| Tinh nang | Module | Trang thai |
|-----------|--------|------------|
| Tim kiem nguoi dung (debounced, ilike full_name) | search | Hoan thanh |
| Image picker tich hop (avatar, cover, post) | profile/feed | Hoan thanh |
| Xoa ban tu danh sach (MoreVert menu) | friends | Hoan thanh |
| Gui tin nhan tu profile | messaging | Hoan thanh |
| Link clickable trong PostCard | feed | Hoan thanh |
| Dark mode toggle (DataStore) | settings | Hoan thanh |
| Notification preferences (4 toggles) | settings | Hoan thanh |
| Network monitor + offline banner | util | Hoan thanh |
| Fix isOwnComment bug | feed | Hoan thanh |
| Fix schema mismatch (friends->friendships, author_id->profile_id, etc.) | data | Hoan thanh |

### P2.6 - Da bo sung (UX Improvements 2026-03-23~24)
| Tinh nang | Module | Trang thai |
|-----------|--------|------------|
| Friendly error messages (Vietnamese) | auth | Hoan thanh |
| Password visibility toggle (eye icon) | auth | Hoan thanh |
| Signup email confirmation dialog | auth | Hoan thanh |
| Create profile screen + redirect | profile | Hoan thanh |
| Clear all notifications + RLS policy | notifications | Hoan thanh |
| New conversation screen voi search filter | messaging | Hoan thanh |
| Storage bucket fix (profile_photos, post_images) | data | Hoan thanh |
| Styled filled TextFields (Material3) | ui | Hoan thanh |
| buildJsonObject fix (Kotlinx Serialization) | data | Hoan thanh |

### P3 - Tuong lai
| Tinh nang | Module | Trang thai |
|-----------|--------|------------|
| Push notifications (FCM) | - | Khong trien khai (theo yeu cau) |
| Realtime WebSocket cho chat | messaging | Dang dung polling 3s |
| Responsive tablet/desktop | ui | Chua trien khai |
| Pagination (Paging3) | feed | Chua trien khai |
| Camera capture | profile/feed | Chi ho tro gallery picker |
| Multi-image selection | feed | Chua trien khai |
| Image compression truoc upload | util | Chua trien khai |

## Do Thi Phu Thuoc

```
auth <- profile <- friends <- feed <- interactions
                     ^                    |
                     +-- messaging    notifications
                                          |
                                      settings

Shared: util (NetworkMonitor, Extensions, UiState)
        di (SupabaseModule, DatabaseModule, RepositoryModule, DataStoreModule)
        navigation (Screen, NavGraph, BottomNavItem)
        ui/components (PostCard, CommentItem, UserAvatar, etc.)
```

## Supabase Database Tables

```
profiles ----< posts (profile_id)
    |    ----< comments (profile_id)
    |    ----< likes (profile_id)
    |    ----< friend_requests (sender_id, receiver_id)
    |    ----< friendships (user1_id, user2_id)  [constraint: user1_id < user2_id]
    |    ----< conversations (user1_id, user2_id)
    |    ----< messages (sender_id)
    +--- ----< notifications (recipient_id, sender_id)

posts ----< comments (post_id)
      ----< likes (target_id where target_type='post')

comments ----< likes (target_id where target_type='comment')
```

## Danh Gia Rui Ro

| Rui ro | Muc do | Giam thieu |
|--------|--------|------------|
| Schema mismatch giua code va Supabase | Da xay ra | Da fix. Luon verify schema qua REST API truoc khi code |
| Supabase SDK khong tuong thich Kotlin version | Trung binh | Pin Kotlin 2.1.0, theo doi SDK releases |
| RLS policies chan queries | Cao | Test ky moi repository voi user that |
| Room schema migration | Thap | fallbackToDestructiveMigration() cho dev |
| Image upload loi tren mang cham | Trung binh | Compress anh truoc upload, retry logic |
| Polling 3s gay load server | Thap | Chi polling khi ChatScreen active, tang interval neu can |
| Ten bang/cot thay doi tren Supabase | Trung binh | Centralize @SerialName mappings, verify truoc khi release |

## Thong Ke Du An

| Metric | Gia tri |
|--------|---------|
| Tong so file Kotlin | ~65+ |
| ViewModels | 14 |
| Repository interfaces | 7 |
| Room Entities | 5 |
| Room DAOs | 4 |
| Hilt Modules | 4 |
| UI Screens | 17+ |
| Shared Components | 6 |
| APK size (debug) | 21.4 MB |
| Build time (cold) | ~83s |
