-- CHỈ CHẠY LẦN ĐẦU KHI CONTAINER ĐƯỢC TẠO
-- 1. Tạo các Database
CREATE DATABASE IF NOT EXISTS `fo_user_db`;
CREATE DATABASE IF NOT EXISTS `fo_merchant_db`;
CREATE DATABASE IF NOT EXISTS `fo_order_db`;
CREATE DATABASE IF NOT EXISTS `fo_payment_db`;
CREATE DATABASE IF NOT EXISTS `fo_delivery_order_db`;

-- 2. Tạo User riêng cho mỗi Service (Bảo mật)
-- Thay 'MatKhauServiceChung123' bằng một mật khẩu BÍ MẬT
-- Đây là mật khẩu các service sẽ dùng để kết nối, KHÔNG PHẢI mật khẩu 'root'

CREATE USER 'user_user'@'%' IDENTIFIED BY 'user1012';
GRANT ALL ON `fo_user_db`.* TO 'user_user'@'%';

CREATE USER 'merchant_user'@'%' IDENTIFIED BY 'merchant1012';
GRANT ALL ON `fo_merchant_db`.* TO 'merchant_user'@'%';

CREATE USER 'order_user'@'%' IDENTIFIED BY 'order1012';
GRANT ALL ON `fo_order_db`.* TO 'order_user'@'%';

CREATE USER 'payment_user'@'%' IDENTIFIED BY 'pay1012';
GRANT ALL ON `fo_payment_db`.* TO 'payment_user'@'%';

CREATE USER 'delivery_order_user'@'%' IDENTIFIED BY 'ship1012';
GRANT ALL ON `fo_delivery_order_db`.* TO 'delivery_order_user'@'%';

-- 3. Áp dụng các thay đổi
FLUSH PRIVILEGES;