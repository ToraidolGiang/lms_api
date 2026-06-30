-- Xóa dữ liệu an toàn và reset toàn bộ tự động tăng (Identity) về 1
TRUNCATE TABLE gradebook, submission, payment, enrollment, courses, category, teacher, student, users, refresh_tokens, course_grade RESTART IDENTITY CASCADE;

-- 1. Thêm Users
-- Mật khẩu mặc định là "123456"
INSERT INTO users (email, username, password, image_url, role, created_at, is_active) VALUES
('admin@example.com', 'admin', '$2a$10$wY1txQsroTWqE6.c7qQ6rOGk8YdCq1Z3gR7r.yXqQ4L/k6Q4x9yS2', 'https://ui-avatars.com/api/?name=Admin', 'ADMIN', CURRENT_TIMESTAMP, true),
('teacher1@example.com', 'nguyenvana', '$2a$10$wY1txQsroTWqE6.c7qQ6rOGk8YdCq1Z3gR7r.yXqQ4L/k6Q4x9yS2', 'https://ui-avatars.com/api/?name=Nguyen+Van+A', 'TEACHER', CURRENT_TIMESTAMP, true),
('teacher2@example.com', 'tranvanb', '$2a$10$wY1txQsroTWqE6.c7qQ6rOGk8YdCq1Z3gR7r.yXqQ4L/k6Q4x9yS2', 'https://ui-avatars.com/api/?name=Tran+Van+B', 'TEACHER', CURRENT_TIMESTAMP, true),
('student1@example.com', 'lethic', '$2a$10$wY1txQsroTWqE6.c7qQ6rOGk8YdCq1Z3gR7r.yXqQ4L/k6Q4x9yS2', 'https://ui-avatars.com/api/?name=Le+Thi+C', 'STUDENT', CURRENT_TIMESTAMP, true),
('student2@example.com', 'hoangvand', '$2a$10$wY1txQsroTWqE6.c7qQ6rOGk8YdCq1Z3gR7r.yXqQ4L/k6Q4x9yS2', 'https://ui-avatars.com/api/?name=Hoang+Van+D', 'STUDENT', CURRENT_TIMESTAMP, true);

-- 2. Thêm Teachers (Liên kết với users id 2 và 3)
INSERT INTO teacher (id, first_name, last_name, birth_date, location, phone, bio, degree) VALUES
(2, 'Nguyễn', 'Văn A', '1985-05-12', 'Hà Nội', '0901234567', 'Chuyên gia IT', 'Thạc sĩ Khoa học Máy tính'),
(3, 'Trần', 'Văn B', '1990-08-22', 'TP HCM', '0912345678', 'Designer UI/UX', 'Cử nhân Thiết kế Đồ họa');

-- 3. Thêm Students (Liên kết với users id 4 và 5)
INSERT INTO student (id, first_name, last_name, birth_date, gender, location, phone, bio, school) VALUES
(4, 'Lê', 'Thị C', '2000-01-15', 'Female', 'Đà Nẵng', '0923456789', 'Sinh viên IT', 'Đại học Bách Khoa'),
(5, 'Hoàng', 'Văn D', '2002-11-03', 'Male', 'Hà Nội', '0934567890', 'Yêu thích học hỏi', 'Đại học Quốc gia');

-- 4. Thêm Categories
INSERT INTO category (category_name) VALUES
('Lập trình Web'),
('Thiết kế đồ họa'),
('Khoa học dữ liệu'),
('Kỹ năng mềm');

-- 5. Thêm Courses
-- Ánh xạ chuẩn với schema (categoryid, teacherid, image_url, created_at, is_deleted, is_active)
INSERT INTO courses (title, description, image_url, price, created_at, is_deleted, is_active, categoryid, teacherid) VALUES
('Khóa học Fullstack Web Development', 'Khóa học lập trình web toàn diện.', 'https://img.youtube.com/vi/bMknfKXIFA8/maxresdefault.jpg', 99.99, CURRENT_TIMESTAMP, false, true, 1, 1),
('Nhập môn UI/UX Design với Figma', 'Học cách thiết kế giao diện ứng dụng từ số 0.', 'https://img.youtube.com/vi/c9Wg6Cb_YlU/maxresdefault.jpg', 49.99, CURRENT_TIMESTAMP, false, true, 2, 2),
('Lập trình Java Spring Boot Nâng cao', 'Master Java Core, Spring Data JPA, Security và Microservices.', 'https://img.youtube.com/vi/bMknfKXIFA8/maxresdefault.jpg', 79.99, CURRENT_TIMESTAMP, false, true, 1, 1);

-- 6. Thêm Enrollment
INSERT INTO enrollment (course_id, student_id, enroll_date, access_status, can_access_after_deletion) VALUES
(1, 1, CURRENT_TIMESTAMP, 'Active', true),
(2, 1, CURRENT_TIMESTAMP, 'Active', true),
(1, 2, CURRENT_TIMESTAMP, 'Active', true);

-- 7. Thêm Payment
INSERT INTO payment (student_id, course_id, amount, payment_date, payment_status) VALUES
(1, 1, 99.99, CURRENT_TIMESTAMP, 'Paid'),
(1, 2, 49.99, CURRENT_TIMESTAMP, 'Paid'),
(2, 1, 99.99, CURRENT_TIMESTAMP, 'Paid');
