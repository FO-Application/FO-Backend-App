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
| Microservices | Spring Cloud Netflix (Eureka), Spring Cloud Gateway |
| Messaging | Apache Kafka |
| Database | MySQL 8.0 |
| Caching | Redis |
| Object Storage | MinIO |
| Payment Gateway | ZaloPay |
| Push Notification | Firebase Cloud Messaging (FCM) |
| Containerization | Docker, Docker Compose |
| Tunnel | Ngrok |
| Build Tool | Maven |

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
                                    │   (Port 8222)   │
                                    └────────┬────────┘
                                             │
                    ┌─────────────────┬──────┼──────┬─────────────────┐
                    │                 │      │      │                 │
           ┌────────▼────────┐ ┌──────▼──────┐ ┌────▼────┐ ┌──────────▼──────────┐
           │  User Service   │ │   Order     │ │Merchant │ │   Delivery Service  │
           │                 │ │  Service    │ │ Service │ │                     │
           └────────┬────────┘ └──────┬──────┘ └────┬────┘ └──────────┬──────────┘
                    │                 │             │                 │
                    │                 │             │                 │
           ┌────────▼─────────────────▼─────────────▼─────────────────▼────────┐
           │                         Apache Kafka                              │
           └────────┬─────────────────┬─────────────┬─────────────────┬────────┘
                    │                 │             │                 │
           ┌────────▼────────┐ ┌──────▼──────┐ ┌────▼────┐ ┌──────────▼──────────┐
           │  Notification   │ │   Payment   │ │  MySQL  │ │        Redis        │
           │    Service      │ │   Service   │ │         │ │       (Cache)       │
           └─────────────────┘ └─────────────┘ └─────────┘ └─────────────────────┘
```

---

## Cấu trúc dự án

```
food-ordering-backend/
├── api-gateway/              # API Gateway - Cổng vào duy nhất
├── discovery-server/         # Eureka Server - Service Registry
├── user-service/             # Quản lý người dùng và xác thực
├── order-service/            # Quản lý đơn hàng
├── merchant-service/         # Quản lý nhà hàng, món ăn
├── delivery-service/         # Quản lý giao hàng và shipper
├── payment-service/          # Tích hợp thanh toán ZaloPay
├── notification-service/     # Gửi email và push notification
├── common-lib/               # Thư viện dùng chung (DTO, Exception)
├── mysql-init/               # Script khởi tạo database
├── minio_data/               # Dữ liệu MinIO (Object Storage)
├── docker-compose.yaml       # Cấu hình Docker Compose
├── .env                      # Biến môi trường
└── README.md
```

---

## Chi tiết các Microservices

### 1. API Gateway (Port 8080)

Cổng vào duy nhất của hệ thống, xử lý:
- Routing request đến các service phù hợp
- Xác thực JWT Token
- Rate limiting
- CORS configuration

### 2. Discovery Server (Port 8761)

Eureka Server cho phép các service:
- Tự động đăng ký khi khởi động
- Tìm kiếm và gọi nhau theo tên service
- Health check và load balancing

### 3. User Service (Port 8081)

Quản lý người dùng và xác thực:
- Đăng ký tài khoản (Customer, Merchant, Shipper)
- Đăng nhập (Username/Password, Google OAuth)
- Xác thực OTP qua email
- Quên mật khẩu
- Quản lý thông tin cá nhân
- JWT Token (Access Token + Refresh Token)

### 4. Order Service (Port 8084)

Quản lý đơn hàng:
- Tạo đơn hàng (Checkout)
- Xem lịch sử đơn hàng
- Hủy đơn hàng
- Merchant xác nhận và xử lý đơn
- Đánh giá đơn hàng (Review)

Trạng thái đơn hàng:
```
CREATED -> PAID -> PREPARING -> READY -> PICKED_UP -> DELIVERING -> COMPLETED
                                                                   └-> CANCELLED
```

### 5. Merchant Service (Port 8082)

Quản lý nhà hàng và sản phẩm:
- CRUD nhà hàng (Restaurant)
- CRUD danh mục sản phẩm (Category)
- CRUD sản phẩm (Product)
- CRUD addon/topping
- Upload hình ảnh (MinIO)
- Quản lý ví tiền (Wallet)

### 6. Delivery Service (Port 8085)

Quản lý giao hàng:
- Theo dõi vị trí shipper (Redis GEO)
- Thuật toán tìm shipper gần nhất
- Shipper nhận đơn
- Cập nhật trạng thái giao hàng
- Quản lý lịch sử giao hàng

### 7. Payment Service (Port 8083)

Tích hợp thanh toán:
- Tạo link thanh toán ZaloPay
- Webhook callback từ ZaloPay
- Truy vấn trạng thái thanh toán

### 8. Notification Service (Port 8086)

Gửi thông báo:
- Email OTP (đăng ký, quên mật khẩu)
- Email thông báo đơn hàng
- Push notification FCM (đơn mới, shipper)

---

## API Endpoints

### User Service - Auth (`/api/v1/auth`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/register/customer` | Đăng ký tài khoản khách hàng |
| POST | `/register/merchant` | Đăng ký tài khoản chủ quán |
| POST | `/register/shipper` | Đăng ký tài khoản shipper |
| POST | `/verify-registration` | Xác thực OTP đăng ký |
| POST | `/resend-otp` | Gửi lại mã OTP |
| POST | `/login` | Đăng nhập |
| POST | `/login/google` | Đăng nhập bằng Google |
| POST | `/refresh` | Làm mới Access Token |
| POST | `/logout` | Đăng xuất |
| GET | `/forgot-password` | Yêu cầu đặt lại mật khẩu |
| POST | `/reset-password` | Đặt mật khẩu mới |

### User Service - User (`/api/v1/user`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/me` | Lấy thông tin bản thân |
| GET | `/{userId}` | Lấy thông tin user theo ID |
| GET | `/` | Lấy danh sách user (phân trang) |
| PUT | `/{userId}` | Cập nhật thông tin user |
| DELETE | `/{userId}` | Xóa user |

### Order Service - Customer (`/api/v1/order`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/` | Tạo đơn hàng mới |
| GET | `/` | Xem lịch sử đơn hàng |
| GET | `/{id}` | Xem chi tiết đơn hàng |
| PATCH | `/{id}/cancel` | Hủy đơn hàng |

### Order Service - Merchant (`/api/v1/management/order/merchant`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/{id}` | Lấy danh sách đơn của quán |
| PUT | `/{id}/confirm` | Xác nhận đơn (bắt đầu nấu) |
| PUT | `/{id}/ready` | Báo món đã làm xong |
| PUT | `/{id}/cancel` | Hủy đơn hàng |

### Delivery Service - Shipper (`/api/v1/delivery/shippers`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/location` | Cập nhật vị trí shipper |
| POST | `/offline` | Tắt trạng thái trực tuyến |
| POST | `/accept` | Nhận đơn hàng |
| POST | `/picked-up` | Xác nhận đã lấy hàng |
| POST | `/complete` | Xác nhận giao thành công |

### Payment Service (`/api/v1/payment`)

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/zalopay/create` | Tạo link thanh toán ZaloPay |
| POST | `/callback` | Webhook callback từ ZaloPay |
| POST | `/zalopay/query` | Truy vấn trạng thái thanh toán |

---

## Yêu cầu hệ thống

Để chạy dự án, máy tính cần cài đặt:

1. **Java JDK 21**: [Tải tại đây](https://www.oracle.com/java/technologies/downloads/#java21)
2. **Maven**: Đã cài đặt hoặc dùng `mvnw` có sẵn trong dự án
3. **Docker & Docker Desktop**: [Tải tại đây](https://www.docker.com/products/docker-desktop/)

---

## Hướng dẫn cài đặt và chạy

### 1. Build Source Code

Tại thư mục gốc của dự án:

```powershell
.\mvnw clean package -DskipTests -Dfile.encoding=UTF-8
```

### 2. Khởi động hệ thống

```powershell
docker-compose up -d --build
```

Lệnh này sẽ:
- Tạo và chạy các container (MySQL, Redis, Kafka, Zookeeper, MinIO)
- Build và chạy các Microservices
- Khởi động Ngrok tunnel

Các tùy chọn:
- `-d`: Chạy ngầm (detached mode)
- `--build`: Build lại image nếu có thay đổi

### 3. Dừng hệ thống

```powershell
docker-compose stop
```

### 4. Reset hoàn toàn hệ thống

Lệnh này sẽ dừng tất cả container và xóa toàn bộ dữ liệu (MySQL, Redis, MinIO):

```powershell
docker-compose down -v
```

### 5. Cập nhật một service cụ thể

Khi chỉ sửa code của một service (ví dụ: user-service):

```powershell
# Build lại file .jar của service
cd user-service
..\mvnw clean package -DskipTests -Dfile.encoding=UTF-8
cd ..

# Restart container của service
docker-compose up -d --build --force-recreate user-service
```

---

## Cấu hình môi trường

File `.env` chứa các biến môi trường cần thiết. Các biến quan trọng:

| Biến | Mô tả |
|------|-------|
| `MYSQL_ROOT_PASSWORD` | Mật khẩu root MySQL |
| `MYSQL_PORT` | Cổng MySQL (mặc định: 3306) |
| `REDIS_PORT` | Cổng Redis (mặc định: 6379) |
| `GATEWAY_PORT` | Cổng API Gateway (mặc định: 8222) |
| `JWT_SIGNER_KEY` | Secret key để ký JWT |
| `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD` | Cấu hình SMTP |
| `MINIO_ROOT_USER`, `MINIO_ROOT_PASSWORD` | Credentials MinIO |
| `ZALOPAY_APP_ID`, `ZALOPAY_KEY1`, `ZALOPAY_KEY2` | Cấu hình ZaloPay |
| `NGROK_AUTHTOKEN`, `NGROK_DOMAIN` | Cấu hình Ngrok tunnel |

---

## Các cổng mặc định

| Service | Port | Mô tả |
|---------|------|-------|
| API Gateway | 8222 | Cổng chính để gọi API |
| Discovery Server | 8761 | Dashboard Eureka |
| User Service | 8081 | Internal |
| Merchant Service | 8082 | Internal |
| Payment Service | 8083 | Internal |
| Order Service | 8084 | Internal |
| Delivery Service | 8085 | Internal |
| Notification Service | 8086 | Internal |
| MySQL | 3306 | Database |
| Redis | 6379 | Cache |
| Kafka | 9092 | Message Broker |
| MinIO API | 9000 | Object Storage API |
| MinIO Console | 9001 | MinIO Web UI |
| Ngrok | 4040 | Ngrok Dashboard |

---

## Luồng hoạt động chính

### 1. Đăng ký tài khoản

```
1. User gửi thông tin đăng ký
2. user-service validate và lưu tạm thông tin
3. user-service bắn event yêu cầu gửi OTP → Kafka
4. notification-service nhận event và gửi email OTP
5. User nhập OTP để xác thực
6. user-service tạo tài khoản chính thức
```

### 2. Đặt hàng

```
1. Customer chọn món và checkout
2. order-service tạo đơn với trạng thái CREATED
3. Customer thanh toán qua ZaloPay (tùy chọn)
4. order-service bắn event thông báo đơn mới → Kafka
5. notification-service gửi FCM notification cho Merchant
```

### 3. Xử lý đơn hàng

```
1. Merchant xác nhận đơn (CREATED → PREPARING)
2. order-service bắn event tìm shipper → Kafka
3. delivery-service tìm shipper gần nhất (Redis GEO)
4. delivery-service bắn event mời shipper → Kafka
5. notification-service gửi FCM cho Shipper
```

### 4. Giao hàng

```
1. Shipper nhận đơn
2. Shipper đến quán lấy hàng (READY → PICKED_UP)
3. Shipper giao hàng (PICKED_UP → DELIVERING)
4. order-service bắn event thông báo → Kafka
5. notification-service gửi email cho Customer
6. Shipper hoàn thành (DELIVERING → COMPLETED)
7. order-service bắn event hoàn thành → Kafka
8. merchant-service cộng tiền vào ví Merchant
```

---

## Kafka Event-Driven

Hệ thống sử dụng Apache Kafka để giao tiếp bất đồng bộ (asynchronous) giữa các microservices. Kiến trúc event-driven này giúp giảm coupling giữa các services, tăng khả năng mở rộng và đảm bảo độ tin cậy.

Chi tiết đầy đủ về event structure xem tại [KAFKA_EVENT_FLOWS.md](./KAFKA_EVENT_FLOWS.md)

### Sơ đồ Event Flow

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              KAFKA TOPICS                                    │
└─────────────────────────────────────────────────────────────────────────────┘

  ┌──────────────┐                                      ┌────────────────────┐
  │ user-service │ ──── otp-mail-sender-topic ────────► │notification-service│
  └──────────────┘                                      └────────────────────┘

  ┌───────────────┐                                     ┌────────────────────┐
  │ order-service │ ──── order-created-topic ─────────► │notification-service│
  │               │ ──── order-delivering-topic ──────► │                    │
  │               │                                     └────────────────────┘
  │               │ ──── order-confirmed-topic ───────► ┌────────────────────┐
  │               │                                     │  delivery-service  │
  │               │                                     └────────────────────┘
  │               │ ──── order-completed-topic ───────► ┌────────────────────┐
  └───────────────┘                                     │  merchant-service  │
                                                        └────────────────────┘

  ┌──────────────────┐                                  ┌────────────────────┐
  │ delivery-service │ ──── shipper-found-topic ──────► │notification-service│
  └──────────────────┘                                  └────────────────────┘
```

### Chi tiết các Topics

#### 1. otp-mail-sender-topic

- Producer: user-service
- Consumer: notification-service
- Mục đích: Gửi email OTP cho xác thực người dùng (đăng ký, quên mật khẩu)

Event Data:
```
{
  "recipientEmail": "user@example.com",
  "subject": "Xác thực tài khoản",
  "otpCode": "123456",
  "eventType": "REGISTER" | "FORGOT_PASSWORD"
}
```

#### 2. order-created-topic

- Producer: order-service
- Consumer: notification-service
- Mục đích: Thông báo cho merchant có đơn hàng mới

Event Data:
```
{
  "orderId": 1001,
  "merchantId": 5,
  "merchantName": "Quán ABC",
  "customerName": "Nguyễn Văn A",
  "grandTotal": 150000,
  "createdAt": "2026-01-14T10:30:00",
  "itemNames": ["Phở bò", "Nước mía"]
}
```

#### 3. order-confirmed-topic

- Producer: order-service
- Consumer: delivery-service
- Mục đích: Yêu cầu tìm shipper cho đơn hàng sau khi merchant xác nhận

Event Data:
```
{
  "orderId": 1001,
  "merchantId": 5,
  "customerName": "Nguyễn Văn A",
  "customerPhone": "0901234567",
  "deliveryAddress": "123 Đường ABC, Quận 1",
  "productPrice": 120000,
  "shippingFee": 30000,
  "merchantLatitude": 10.762622,
  "merchantLongitude": 106.660172
}
```

#### 4. order-delivering-topic

- Producer: order-service
- Consumer: notification-service
- Mục đích: Thông báo cho khách hàng đơn đang được giao

Event Data:
```
{
  "orderId": 1001,
  "customerName": "Nguyễn Văn A",
  "customerEmail": "user@example.com",
  "deliveryAddress": "123 Đường ABC, Quận 1",
  "merchantName": "Quán ABC",
  "descriptionOrder": "Ghi chú đơn hàng",
  "productName": ["Phở bò", "Nước mía"],
  "productPrice": 120000,
  "shippingFee": 30000
}
```

#### 5. order-completed-topic

- Producer: order-service
- Consumer: merchant-service
- Mục đích: Cộng tiền vào ví của merchant khi đơn hoàn thành

Event Data:
```
{
  "orderId": 1001,
  "merchantId": 5,
  "orderAmount": 120000
}
```

#### 6. order-paid-topic

- Producer: order-service
- Consumer: notification-service
- Mục đích: Thông báo thanh toán thành công cho merchant (sau khi khách thanh toán qua ZaloPay)

Event Data:
```
{
  "orderId": 1001,
  "merchantId": 5,
  "ownerId": 10,
  "amount": 150000,
  "paymentMethod": "ZALOPAY",
  "paidAt": "2026-01-14T10:35:00"
}
```

#### 7. shipper-found-topic

- Producer: delivery-service
- Consumer: notification-service
- Mục đích: Mời shipper nhận đơn hàng

Event Data:
```
{
  "shipperId": 10,
  "orderId": 1001,
  "pickupAddress": "456 Đường XYZ, Quận 3",
  "lat": 10.762622,
  "lon": 106.660172,
  "shippingFee": 30000
}
```

### Bảng tóm tắt

| Topic | Producer | Consumer | Mục đích |
|-------|----------|----------|----------|
| `otp-mail-sender-topic` | user-service | notification-service | Gửi email OTP |
| `order-created-topic` | order-service | notification-service | Thông báo đơn mới cho merchant |
| `order-confirmed-topic` | order-service | delivery-service | Yêu cầu tìm shipper |
| `order-delivering-topic` | order-service | notification-service | Thông báo đang giao cho khách |
| `order-completed-topic` | order-service | merchant-service | Cộng tiền vào wallet merchant |
| `order-paid-topic` | order-service | notification-service | Thông báo thanh toán cho merchant |
| `shipper-found-topic` | delivery-service | notification-service | Mời shipper nhận đơn |

### Debug Kafka

```powershell
# Xem logs của Kafka
docker logs fo-kafka -f

# Liệt kê tất cả topics
docker exec -it fo-kafka kafka-topics --bootstrap-server localhost:9092 --list

# Xem messages trong một topic
docker exec -it fo-kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic order-created-topic --from-beginning
```

---

## Các lỗi thường gặp

| Lỗi | Nguyên nhân | Cách xử lý |
|-----|-------------|------------|
| Gateway 503 Service Unavailable | Service chưa đăng ký với Eureka hoặc chưa start xong | Chờ 1-2 phút hoặc restart gateway: `docker restart fo-api-gateway` |
| Connection Refused (DB/Kafka) | Container DB/Kafka chưa sẵn sàng | Restart service bị lỗi: `docker restart fo-user-service` |
| Redis không lưu dữ liệu | Volume Redis bị lỗi hoặc đầy | Reset Redis: `docker-compose up -d --force-recreate redis-cache` |
| Lỗi build Maven | Thiếu thư viện hoặc lỗi mạng | Force update: `.\mvnw clean install -U -DskipTests` |
| Kafka Consumer không nhận message | Trusted packages chưa được cấu hình | Kiểm tra `spring.json.trusted.packages` trong application.yaml |
| MinIO upload lỗi | Bucket chưa được tạo | Truy cập MinIO Console (port 9001) và tạo bucket `merchant-images` |

### Debug Commands

```powershell
# Xem logs của một service
docker logs fo-user-service -f

# Xem logs của Kafka
docker logs fo-kafka -f

# Kiểm tra Eureka registered services
# Truy cập http://localhost:8761

# Kiểm tra container status
docker ps

# Restart tất cả services
docker-compose restart
```

---

## Tác giả

Dự án được phát triển bởi team Food Ordering.

---

Cập nhật lần cuối: Tháng 01/2026
