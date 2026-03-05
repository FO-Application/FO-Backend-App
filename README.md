# Food Ordering Backend

Hệ thống backend cho ứng dụng đặt đồ ăn trực tuyến, được xây dựng theo kiến trúc **Microservices** với khả năng mở rộng và hiệu năng cao.

---

## Mục lục

1. [Công nghệ sử dụng](#công-nghệ-sử-dụng)
2. [Kiến trúc hệ thống](#kiến-trúc-hệ-thống)
3. [Cấu trúc dự án](#cấu-trúc-dự-án)
4. [Chi tiết các Microservices](#chi-tiết-các-microservices)
5. [API Endpoints](#api-endpoints)
6. [Yêu cầu hệ thống](#yêu-cầu-hệ-thống)
7. [Hướng dẫn cài đặt và chạy](#hướng-dẫn-cài-đặt-và-chạy)
8. [Cấu hình môi trường](#cấu-hình-môi-trường)
9. [Các cổng mặc định](#các-cổng-mặc-định)
10. [Luồng hoạt động chính](#luồng-hoạt-động-chính)
11. [Kafka Event-Driven](#kafka-event-driven)
12. [Các lỗi thường gặp](#các-lỗi-thường-gặp)

---

## Công nghệ sử dụng

| Thành phần | Công nghệ |
|------------|-----------|
| Ngôn ngữ | Java 21 |
| Framework | Spring Boot 3.3.5 |
| Microservices | Spring Cloud Netflix (Eureka), Spring Cloud Gateway, OpenFeign |
| Messaging | Apache Kafka |
| Database | MySQL 8.0 (5 database riêng biệt) |
| Caching & Geo | Redis (GEO, Caching, Rate Limiting, Heartbeat TTL) |
| Object Storage | MinIO |
| Payment Gateway | ZaloPay (Sandbox) |
| Push Notification | Firebase Cloud Messaging (FCM) |
| Email | JavaMailSender (SMTP) |
| Containerization | Docker, Docker Compose (14 containers) |
| Tunnel | Ngrok |
| Build Tool | Maven |
| API Documentation | Swagger UI / OpenAPI 3.0 (tập trung qua Gateway) |
| Security | JWT (HS512) + Cookie-based Authentication, BCrypt |

---

## Kiến trúc hệ thống

```
                                    ┌─────────────────┐
                                    │     Ngrok       │
                                    │   (Tunnel)      │
                                    └────────┬────────┘
                                             │
                                    ┌────────▼────────┐
                                    │   API Gateway   │
                                    │   (Port 8080)   │
                                    │  Rate Limiting  │
                                    │  JWT Auth Filter│
                                    └────────┬────────┘
                                             │
                    ┌─────────────────┬──────┼──────┬─────────────────┬───────────────┐
                    │                 │      │      │                 │               │
           ┌────────▼────────┐ ┌──────▼──────┐ ┌────▼────┐ ┌──────────▼──────────┐ ┌──▼──────────┐
           │  User Service   │ │   Order     │ │Merchant │ │   Delivery Service  │ │  Payment    │
           │                 │ │  Service    │ │ Service │ │                     │ │  Service    │
           │    (:8081)      │ │   (:8084)   │ │ (:8083) │ │       (:8085)       │ │  (:8086)    │
           └────────┬────────┘ └──────┬──────┘ └────┬────┘ └──────────┬──────────┘ └──────┬──────┘
                    │                 │             │                 │                    │
           ┌────────▼─────────────────▼─────────────▼─────────────────▼────────────────────▼──┐
           │                              Apache Kafka (11 Topics)                            │
           └────────┬─────────────────┬─────────────┬─────────────────┬──────────────────┬────┘
                    │                 │             │                 │                  │
           ┌────────▼────────┐ ┌──────▼──────┐ ┌────▼────┐ ┌──────────▼──────────┐ ┌────▼────┐
           │  Notification   │ │   MinIO     │ │  MySQL  │ │        Redis        │ │ Eureka  │
           │    Service      │ │  (Storage)  │ │ (5 DBs) │ │   (Cache+GEO+TTL)   │ │ (:8761) │
           │    (:8082)      │ │ (:9000)     │ │         │ │                     │ │         │
           └─────────────────┘ └─────────────┘ └─────────┘ └─────────────────────┘ └─────────┘
```

---

## Cấu trúc dự án

```
food-ordering-backend/
├── api-gateway/              # API Gateway - Cổng vào duy nhất (Routing, Rate Limiting, JWT Filter)
├── discovery-server/         # Eureka Server - Service Registry
├── user-service/             # Quản lý người dùng, Auth, OTP, Social Login
├── order-service/            # Quản lý đơn hàng, tính phí ship, review, merchant ops
├── merchant-service/         # Quản lý nhà hàng, menu, topping, ví merchant
├── delivery-service/         # Quản lý giao hàng, shipper (Redis GEO + Heartbeat TTL)
├── payment-service/          # Tích hợp thanh toán ZaloPay
├── notification-service/     # Gửi email (OTP) và push notification (FCM)
├── common-lib/               # Thư viện dùng chung (DTO, Exception, GlobalExceptionHandler)
├── mysql-init/               # Script khởi tạo database (5 databases)
├── minio_data/               # Dữ liệu MinIO (Object Storage)
├── docker-compose.yaml       # Cấu hình Docker Compose (14 containers)
├── KAFKA_EVENT_FLOWS.md      # Tài liệu chi tiết 11 Kafka topics
├── SYSTEM_FLOW.md            # Tài liệu luồng hoạt động hệ thống
├── .env                      # Biến môi trường
└── README.md
```

---

## Chi tiết các Microservices

### 1. API Gateway (Port 8080)
Cổng vào duy nhất của hệ thống:
- Routing đến 7 downstream services qua Eureka
- JWT Authentication Filter (Cookie → Header)
- Rate Limiting (Redis-based): 5-20 req/s tùy service
- CORS Configuration
- Swagger UI tập trung (`/swagger-ui.html`)

### 2. Discovery Server (Port 8761)
Eureka Server cho phép các service tự động đăng ký và tìm kiếm nhau.

### 3. User Service (Port 8081)
- Authentication: Login, Register (Customer/Merchant/Shipper), Refresh Token, Logout
- Social Login: Firebase Auth (Google/Facebook)
- OTP: Xác thực email khi đăng ký, quên mật khẩu
- User Profile: CRUD, Get Me (từ JWT)
- Security: BCrypt password hashing, JWT (HS512)

### 4. Order Service (Port 8084)
- **Customer**: Tạo đơn (checkout), xem lịch sử, hủy đơn
- **Merchant**: Confirm, Ready, Cancel đơn, thống kê doanh thu
- **Delivery**: Cập nhật trạng thái giao hàng (Shipper pickup, complete)
- **Internal**: API nội bộ giữa các service
- **Review**: Đánh giá + Rating (→ Kafka → cập nhật rating trung bình quán)
- Tính phí ship dựa trên tọa độ GPS

Trạng thái đơn hàng:
```
CREATED → PAID → PREPARING → READY → PICKED_UP → DELIVERING → COMPLETED
   Start                    Merchant      Shipper       Shipper       End
   |                        confirms      picks up      delivers       |
   └→ CANCELED (Khách hoặc Quán hủy)                                  |
```

### 5. Merchant Service (Port 8083)
- **Restaurant**: CRUD, tìm kiếm gần đây (lat/lon), lọc theo cuisine
- **Schedule**: Quản lý giờ mở cửa nhà hàng
- **Menu**: Category → Product → OptionGroup → OptionItem (Topping)
- **File Upload**: Ảnh nhà hàng/món ăn lưu trên MinIO
- **Wallet**: Số dư, lịch sử giao dịch, nạp tiền, rút tiền, export CSV, thống kê ngày

### 6. Delivery Service (Port 8085)
- **Redis GEO**: Lưu tọa độ shipper, tìm kiếm shipper gần nhất (`GEORADIUS`)
- **Heartbeat TTL**: Key `shipper:heartbeat:{id}` với TTL = 15s, tự xóa shipper mất kết nối
- **Order Matching**: Khi quán confirm → Kafka → tìm shipper gần → push notification
- **Shipper Flow**: Accept → Pickup → Complete delivery
- **Ví Shipper**: Quản lý thu nhập giao hàng
- **Shipper Registration**: Đăng ký thông tin xe

### 7. Payment Service (Port 8086)
- **ZaloPay Integration** (Sandbox):
  - `POST /zalopay/create`: Tạo link thanh toán, trả về `order_url`
  - `POST /callback`: Webhook nhận kết quả từ ZaloPay Server
  - `POST /zalopay/query`: Truy vấn trạng thái giao dịch

### 8. Notification Service (Port 8082)
- **Central Kafka Consumer**: Lắng nghe 9 topics, xử lý thông báo
- **Email**: JavaMailSender (OTP, order status updates)
- **Push Notification**: Firebase Cloud Messaging (FCM)
  - Đăng ký device token
  - Push cho Merchant (đơn mới, thanh toán, hủy đơn)
  - Push cho Shipper (mời nhận đơn, món xong)
  - Push cho Customer (có tài xế, đang giao)
- **Notification History**: CRUD lịch sử thông báo, đánh dấu đã đọc

---

## API Endpoints

> **Base path**: Tất cả API được truy cập qua API Gateway (`http://localhost:8080`).

### User Service — Authentication (`/api/v1/auth`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/auth/register/customer` | Đăng ký khách hàng (gửi OTP qua Kafka) |
| POST | `/auth/register/merchant` | Đăng ký chủ quán |
| POST | `/auth/register/shipper` | Đăng ký shipper |
| POST | `/auth/verify` | Xác thực OTP → tạo tài khoản chính thức |
| POST | `/auth/resend-otp` | Gửi lại mã OTP |
| POST | `/auth/login` | Đăng nhập → nhận JWT Cookie (access_token + refresh_token) |
| POST | `/auth/login/firebase` | Đăng nhập Social (Google/Facebook) qua Firebase Auth |
| POST | `/auth/refresh` | Tự động gia hạn access_token từ refresh_token |
| POST | `/auth/logout` | Đăng xuất → xóa Cookie + blacklist token |
| GET | `/auth/forgot-password` | Quên mật khẩu (gửi OTP qua email) |
| POST | `/auth/reset-password` | Đặt lại mật khẩu mới (sau khi verify OTP) |
| GET | `/auth/verify-otp` | Xác thực OTP quên mật khẩu |

### User Service — User Management (`/api/v1/user`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/user/me` | Lấy thông tin bản thân (từ JWT) |
| GET | `/user/{userId}` | Lấy chi tiết user theo ID |
| GET | `/user` | Danh sách user (phân trang: `page`, `size`) |
| PUT | `/user/{userId}` | Cập nhật thông tin user |
| DELETE | `/user/{userId}` | Xóa user |

### Merchant Service — Restaurant (`/api/v1/restaurant`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/restaurant` | Tạo nhà hàng (multipart: `data` JSON + `image`) |
| PUT | `/restaurant/{id}` | Cập nhật nhà hàng (multipart: `data` + `image` optional) |
| GET | `/restaurant/{id}` | Chi tiết nhà hàng |
| GET | `/restaurant` | Danh sách nhà hàng (phân trang) |
| GET | `/restaurant/nearby` | Tìm nhà hàng gần đây (`lat`, `lon`, `radius`, `cuisine`, `page`, `size`) |
| GET | `/restaurant/cuisine/{slug}` | Lọc nhà hàng theo loại ẩm thực (phân trang) |
| GET | `/restaurant/owner/{ownerId}` | Lấy nhà hàng theo chủ sở hữu (phân trang) |
| DELETE | `/restaurant/{id}` | Xóa nhà hàng |

### Merchant Service — Cuisine (`/api/v1/cuisine`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/cuisine` | Tạo loại ẩm thực (multipart: `data` JSON + `image`) |
| PUT | `/cuisine/{id}` | Cập nhật ẩm thực (multipart: `data` + `image` optional) |
| GET | `/cuisine/{id}` | Chi tiết loại ẩm thực |
| GET | `/cuisine` | Toàn bộ danh sách ẩm thực |
| DELETE | `/cuisine/{id}` | Xóa loại ẩm thực |

### Merchant Service — Category (`/api/v1/category`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/category` | Tạo danh mục món ăn |
| PUT | `/category/{id}` | Cập nhật danh mục |
| GET | `/category/{id}` | Chi tiết danh mục |
| GET | `/category/restaurant/{slug}` | Danh sách danh mục theo nhà hàng |
| DELETE | `/category/{id}` | Xóa danh mục |

### Merchant Service — Product (`/api/v1/product`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/product` | Tạo sản phẩm (multipart: `data` JSON + `image`) |
| PUT | `/product/{id}` | Cập nhật sản phẩm (multipart: `data` + `image` optional) |
| GET | `/product/{id}` | Chi tiết sản phẩm |
| GET | `/product/category/{categoryId}` | Danh sách sản phẩm theo danh mục |
| GET | `/product/products?productIds=1,2,3` | Lấy nhiều sản phẩm theo danh sách ID |
| GET | `/product/count?restaurantId=1` | Đếm số sản phẩm của nhà hàng |
| DELETE | `/product/{id}` | Xóa sản phẩm |

### Merchant Service — OptionGroup & OptionItem (`/api/v1/option-group`, `/api/v1/option-item`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| CRUD | `/option-group/**` | Quản lý nhóm tùy chọn (OptionGroup) |
| CRUD | `/option-item/**` | Quản lý tùy chọn bổ sung / Topping (OptionItem) |

### Merchant Service — Schedule (`/api/v1/restaurant-schedule`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/restaurant-schedule` | Tạo lịch hoạt động nhà hàng |
| PUT | `/restaurant-schedule/{id}` | Cập nhật lịch |
| GET | `/restaurant-schedule/{id}` | Chi tiết lịch |
| GET | `/restaurant-schedule/restaurant/{slug}` | Tất cả lịch theo nhà hàng (slug) |
| DELETE | `/restaurant-schedule/{id}` | Xóa lịch |

### Merchant Service — Wallet (`/api/v1/wallet`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/wallet` | Xem số dư ví Merchant |
| GET | `/wallet/transactions` | Lịch sử giao dịch (filter: `startDate`, `endDate`, `type` + phân trang) |
| GET | `/wallet/export` | Xuất CSV giao dịch (filter: `startDate`, `endDate`, `type`) |
| GET | `/wallet/statistics` | Thống kê doanh thu theo ngày |
| POST | `/wallet/withdraw?amount=X` | Rút tiền |
| POST | `/wallet/deposit?amount=X` | Nạp tiền (giả lập) |

### Merchant Service — File Upload (`/api/v1/file`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/file/upload` | Upload file ảnh lên MinIO |

### Order Service — Customer (`/api/v1/order`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/order` | Tạo đơn hàng (checkout) — tự động tính phí ship GPS |
| GET | `/order` | Lịch sử đơn hàng của tôi (phân trang) |
| GET | `/order/{id}` | Chi tiết đơn hàng |
| PATCH | `/order/{id}/cancel` | Khách hủy đơn (chỉ khi trạng thái CREATED) |

### Order Service — Merchant (`/api/v1/management/order/merchant`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/management/order/merchant/{id}` | Danh sách đơn của quán (filter: `status`, phân trang) |
| PUT | `/management/order/merchant/{id}/confirm` | Xác nhận đơn → PREPARING → trigger tìm Shipper |
| PUT | `/management/order/merchant/{id}/ready` | Báo món xong → READY → push noti Shipper |
| PUT | `/management/order/merchant/{id}/cancel` | Quán hủy đơn (CREATED/PAID/PREPARING) |
| GET | `/management/order/merchant/{id}/stats` | Thống kê đơn hàng, doanh thu, đánh giá |

### Order Service — Delivery Internal (`/api/v1/shipping/order`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/shipping/order/{id}` | Lấy chi tiết đơn (Feign Client từ Delivery Service) |
| PUT | `/shipping/order/{id}/delivering` | Cập nhật trạng thái → DELIVERING |
| PUT | `/shipping/order/{id}/completed` | Cập nhật trạng thái → COMPLETED |

### Order Service — Payment Internal (`/api/v1/internal/order`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/internal/order/{orderId}/update-trans-id` | Lưu mã giao dịch ZaloPay (appTransId) |
| POST | `/internal/order/update-status-by-trans-id` | Cập nhật trạng thái đơn theo mã giao dịch ZaloPay |

### Order Service — Review (`/api/v1/review`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/review` | Viết đánh giá (chỉ khi đơn COMPLETED + chưa đánh giá) |
| GET | `/review/merchant/{merchantId}` | Đánh giá của quán (phân trang) |
| GET | `/review/order/{orderId}` | Đánh giá của một đơn hàng |

### Delivery Service — Shipper (`/api/v1/delivery/shippers`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/shippers/location?lat=X&lon=Y` | Cập nhật vị trí GPS (+ heartbeat TTL 15s + set online) |
| POST | `/shippers/offline` | Đi offline (xóa Redis GEO + heartbeat + set offline) |
| POST | `/shippers/register` | Đăng ký shipper (thông tin xe, biển số) |
| GET | `/shippers/profile` | Xem profile shipper |
| POST | `/shippers/accept?orderId=X` | Nhận đơn giao hàng (DB Unique Lock) |
| POST | `/shippers/picked-up?orderId=X` | Đã lấy hàng tại quán |
| POST | `/shippers/complete?orderId=X` | Đã giao xong → cộng tiền ví shipper |
| GET | `/shippers/pending-orders` | Poll danh sách đơn đang chờ shipper |
| POST | `/shippers/deposit?amount=X` | Nạp tiền ví shipper |
| GET | `/shippers/wallet-stats` | Xem thống kê ví shipper |

### Payment Service (`/api/v1/payment`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/payment/zalopay/create?orderId=X&amount=Y` | Tạo link thanh toán ZaloPay → trả về `order_url` |
| POST | `/payment/callback` | Webhook callback từ ZaloPay Server (HMAC SHA256) |
| POST | `/payment/zalopay/query?appTransId=X` | Truy vấn trạng thái giao dịch ZaloPay |

### Notification Service (`/api/v1/notification`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/notification/device-token` | Đăng ký FCM token (gọi sau khi login) |
| GET | `/notification/history/{merchantId}` | Lịch sử thông báo |
| PUT | `/notification/{id}/read` | Đánh dấu đã đọc |
| PUT | `/notification/read-all/{merchantId}` | Đánh dấu tất cả đã đọc |
| DELETE | `/notification/{id}` | Xóa một thông báo |
| DELETE | `/notification/delete-all/{merchantId}` | Xóa tất cả thông báo |

---

## Yêu cầu hệ thống

1. **Java JDK 21**
2. **Maven**
3. **Docker & Docker Desktop**

---

## Hướng dẫn cài đặt và chạy

### 1. Cấu hình môi trường
Sao chép file `.env.example` thành `.env` và điền các giá trị phù hợp.

### 2. Build Source Code
```powershell
.\mvnw clean package -DskipTests -Dfile.encoding=UTF-8
```

### 3. Khởi động hệ thống
```powershell
docker-compose up -d --build
```
Lệnh này sẽ khởi động toàn bộ **14 containers** gồm: MySQL, Redis, Kafka, Zookeeper, MinIO, Ngrok và 8 Microservices.

### 4. Kiểm tra trạng thái
- Eureka Dashboard: http://localhost:8761
- Swagger UI: http://localhost:8080/swagger-ui.html
- MinIO Console: http://localhost:9001
- Ngrok Inspector: http://localhost:4040

### 5. Dừng hệ thống
```powershell
docker-compose down -v
```
(Thêm `-v` để xóa sạch dữ liệu database/cache nếu muốn reset)

---

## Cấu hình môi trường

Các cổng quan trọng trong file `.env`:

| Biến | Giá trị mặc định | Mô tả |
|------|-----------------|-------|
| `GATEWAY_PORT` | 8080 | Cổng Public API |
| `USER_SERVICE_PORT` | 8081 | User & Auth |
| `NOTIFICATION_SERVICE_PORT` | 8082 | Notification |
| `MERCHANT_SERVICE_PORT` | 8083 | Merchant & Restaurant |
| `ORDER_SERVICE_PORT` | 8084 | Order Management |
| `DELIVERY_SERVICE_PORT` | 8085 | Delivery & Shipper |
| `PAYMENT_SERVICE_PORT` | 8086 | Payment (ZaloPay) |
| `MYSQL_PORT` | 3306 | MySQL Database |
| `REDIS_PORT` | 6379 | Redis Cache |

---

## Luồng hoạt động chính

Xem chi tiết trong [SYSTEM_FLOW.md](./SYSTEM_FLOW.md).

1. **Đăng ký**: User → User Service (Validate) → Kafka → Notification Service (Mail OTP) → User verify OTP → Tạo tài khoản.
2. **Đặt hàng**: User → Order Service (Create + tính phí ship) → Kafka → Notification (Push Merchant).
3. **Thanh toán**: User → Payment Service (Tạo link ZaloPay) → ZaloPay → Callback → Order Service (Cập nhật PAID).
4. **Xác nhận**: Merchant (Confirm) → Order Service → Kafka → Delivery Service (Tìm Shipper bằng Redis GEO).
5. **Tìm xe**: Delivery Service → Redis GEO (findNearby + filter heartbeat) → Kafka → Notification (Push Shipper).
6. **Giao hàng**: Shipper (Accept → PickedUp → Complete) → Order Service → Kafka → Merchant Wallet (Cộng tiền) + Notification.
7. **Đánh giá**: User (Review) → Order Service → Kafka → Merchant Service (Cập nhật rating trung bình).

---

## Kafka Event-Driven

Hệ thống sử dụng **11 Topics** chính để điều phối hoạt động giữa các services.
Xem chi tiết cấu trúc Event trong [KAFKA_EVENT_FLOWS.md](./KAFKA_EVENT_FLOWS.md).

| Topic | Producer | Consumer | Hành động chính |
|-------|----------|----------|-----------------|
| `otp-mail-sender-topic` | user-service | notification-service | Gửi Email OTP |
| `order-created-topic` | order-service | notification-service | Push Noti Merchant (Đơn mới) |
| `order-confirmed-topic` | order-service | delivery-service | Tìm kiếm Shipper (Redis GEO) |
| `shipper-found-topic` | delivery-service | notification-service | Push Noti Shipper (Mời đơn) |
| `shipper-assigned-topic` | delivery-service | notification-service | Push Noti Customer (Có xe) |
| `order-ready-topic` | order-service | notification-service | Push Noti Shipper (Món xong) |
| `order-delivering-topic` | order-service | notification-service | Email Customer (Đang giao) |
| `order-completed-topic` | order-service | merchant-service | Cộng tiền Ví Merchant |
| `order-paid-topic` | order-service | notification-service | Push Noti Merchant (Tiền về) |
| `order-cancelled-topic` | order-service | notification-service | Push Noti Hủy đơn |
| `review-created-topic` | order-service | merchant-service | Tính lại Rating Quán |

---

## Các lỗi thường gặp

1. **Lỗi Gateway 503**: Service chưa khởi động xong. Đợi 1-2 phút hoặc check log Eureka (http://localhost:8761).
2. **Lỗi Connection Refused**: Container chưa connect được Database hoặc Kafka. Check `docker ps` và logs.
3. **ZaloPay Callback lỗi**: Kiểm tra Ngrok domain trong `.env` có khớp với domain đang chạy không.
4. **Shipper không nhận đơn**: Kiểm tra shipper đã online chưa (heartbeat key trong Redis). Heartbeat hết hạn sau 15s không cập nhật location.
5. **Push Notification không hoạt động**: Kiểm tra Firebase service account key và FCM token đã đăng ký chưa.

---

Cập nhật lần cuối: Tháng 02/2026
