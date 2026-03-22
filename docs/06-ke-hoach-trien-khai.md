# Kế Hoạch Triển Khai - Pumiah Social

## Phân Tích Tính Năng Theo Mức Ưu Tiên

### P0 - Bắt buộc (MVP)
| Tính năng | Module | Trạng thái |
|-----------|--------|------------|
| Đăng ký/Đăng nhập/Đăng xuất | auth | ✅ Hoàn thành |
| Tạo/Xem/Sửa hồ sơ | profile | ✅ Hoàn thành |
| Gửi/Chấp nhận/Từ chối lời mời kết bạn | friends | ✅ Hoàn thành |
| Danh sách bạn bè | friends | ✅ Hoàn thành |
| Tạo bài viết (text/image/link) | feed | ✅ Hoàn thành |
| Bảng tin chronological | feed | ✅ Hoàn thành |

### P1 - Quan trọng
| Tính năng | Module | Trạng thái |
|-----------|--------|------------|
| Like/Unlike bài viết | interactions | ✅ Hoàn thành |
| Bình luận bài viết | interactions | ✅ Hoàn thành |
| Xóa bình luận | interactions | ✅ Hoàn thành |
| Like bình luận | interactions | ✅ Hoàn thành |
| Thông báo in-app | notifications | ✅ Hoàn thành |
| Xóa bạn bè | friends | ✅ Hoàn thành |

### P2 - Nên có
| Tính năng | Module | Trạng thái |
|-----------|--------|------------|
| Nhắn tin trực tiếp | messaging | ✅ Hoàn thành |
| Danh sách hội thoại | messaging | ✅ Hoàn thành |
| Cài đặt | settings | ✅ Hoàn thành |
| Offline caching | local | ✅ Hoàn thành |
| Theme sáng/tối | theme | ✅ Hoàn thành |

### P2.5 - Đã bổ sung (Gap Analysis 2026-03-22)
| Tính năng | Module | Trạng thái |
|-----------|--------|------------|
| Tìm kiếm người dùng | search | ✅ Hoàn thành |
| Image picker tích hợp | profile/feed | ✅ Hoàn thành |
| Xóa bạn từ danh sách | friends | ✅ Hoàn thành |
| Gửi tin nhắn từ profile | messaging | ✅ Hoàn thành |
| Link clickable trong PostCard | feed | ✅ Hoàn thành |
| Dark mode toggle | settings | ✅ Hoàn thành |
| Notification preferences | settings | ✅ Hoàn thành |
| Network monitor + offline banner | util | ✅ Hoàn thành |
| Fix isOwnComment bug | feed | ✅ Hoàn thành |

### P3 - Tương lai
| Tính năng | Module | Trạng thái |
|-----------|--------|------------|
| Push notifications (FCM) | - | ❌ Không triển khai (theo yêu cầu) |
| Realtime WebSocket cho chat | messaging | ❌ Đang dùng polling |
| Responsive tablet/desktop | ui | ❌ Chưa triển khai |
| Pagination (Paging3) | feed | ❌ Chưa triển khai |
| Camera capture | profile/feed | ❌ Chỉ hỗ trợ gallery picker |

## Đồ Thị Phụ Thuộc

```
auth ← profile ← friends ← feed ← interactions
                     ↑                    ↓
                     └── messaging    notifications
                                          ↓
                                      settings
```

## Đánh Giá Rủi Ro

| Rủi ro | Mức độ | Giảm thiểu |
|--------|--------|------------|
| Supabase SDK không tương thích Kotlin 2.2.10 | Trung bình | Pin version, fallback AGP 8.x |
| RLS policies chặn queries | Cao | Test kỹ mỗi repository |
| Room schema migration | Thấp | fallbackToDestructiveMigration() cho dev |
| Image upload lỗi trên mạng chậm | Trung bình | Compress ảnh trước upload |
| Realtime subscription leak | Thấp | DisposableEffect / onCleared |
