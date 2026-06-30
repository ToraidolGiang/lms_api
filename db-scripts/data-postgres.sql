-- Xóa dữ liệu an toàn và reset toàn bộ tự động tăng (Identity) về 1
TRUNCATE TABLE gradebook, submission, payment, enrollment, courses, category, teacher, student, users, refresh_tokens, course_grade RESTART IDENTITY CASCADE;

-- ═══════════════════════════════════════════════════════════════
-- 1. Thêm Users (10 users: 1 admin, 4 teacher, 5 student)
-- Mật khẩu mặc định là "123456"
-- ═══════════════════════════════════════════════════════════════
INSERT INTO users (email, username, password, image_url, role, created_at, is_active) VALUES
('admin@example.com',    'admin',      '$2a$10$wY1txQsroTWqE6.c7qQ6rOGk8YdCq1Z3gR7r.yXqQ4L/k6Q4x9yS2', 'https://ui-avatars.com/api/?name=Admin',           'ADMIN',   CURRENT_TIMESTAMP, true),
('teacher1@example.com', 'nguyenvana', '$2a$10$wY1txQsroTWqE6.c7qQ6rOGk8YdCq1Z3gR7r.yXqQ4L/k6Q4x9yS2', 'https://ui-avatars.com/api/?name=Nguyen+Van+A',    'TEACHER', CURRENT_TIMESTAMP, true),
('teacher2@example.com', 'tranvanb',   '$2a$10$wY1txQsroTWqE6.c7qQ6rOGk8YdCq1Z3gR7r.yXqQ4L/k6Q4x9yS2', 'https://ui-avatars.com/api/?name=Tran+Van+B',      'TEACHER', CURRENT_TIMESTAMP, true),
('student1@example.com', 'lethic',     '$2a$10$wY1txQsroTWqE6.c7qQ6rOGk8YdCq1Z3gR7r.yXqQ4L/k6Q4x9yS2', 'https://ui-avatars.com/api/?name=Le+Thi+C',        'STUDENT', CURRENT_TIMESTAMP, true),
('student2@example.com', 'hoangvand',  '$2a$10$wY1txQsroTWqE6.c7qQ6rOGk8YdCq1Z3gR7r.yXqQ4L/k6Q4x9yS2', 'https://ui-avatars.com/api/?name=Hoang+Van+D',     'STUDENT', CURRENT_TIMESTAMP, true),
('teacher3@example.com', 'phamvanc',   '$2a$10$wY1txQsroTWqE6.c7qQ6rOGk8YdCq1Z3gR7r.yXqQ4L/k6Q4x9yS2', 'https://ui-avatars.com/api/?name=Pham+Van+C',      'TEACHER', CURRENT_TIMESTAMP, true),
('teacher4@example.com', 'levand',     '$2a$10$wY1txQsroTWqE6.c7qQ6rOGk8YdCq1Z3gR7r.yXqQ4L/k6Q4x9yS2', 'https://ui-avatars.com/api/?name=Le+Van+D',        'TEACHER', CURRENT_TIMESTAMP, true),
('student3@example.com', 'nguyenthie', '$2a$10$wY1txQsroTWqE6.c7qQ6rOGk8YdCq1Z3gR7r.yXqQ4L/k6Q4x9yS2', 'https://ui-avatars.com/api/?name=Nguyen+Thi+E',    'STUDENT', CURRENT_TIMESTAMP, true),
('student4@example.com', 'tranvanf',   '$2a$10$wY1txQsroTWqE6.c7qQ6rOGk8YdCq1Z3gR7r.yXqQ4L/k6Q4x9yS2', 'https://ui-avatars.com/api/?name=Tran+Van+F',      'STUDENT', CURRENT_TIMESTAMP, true),
('student5@example.com', 'dangvang',   '$2a$10$wY1txQsroTWqE6.c7qQ6rOGk8YdCq1Z3gR7r.yXqQ4L/k6Q4x9yS2', 'https://ui-avatars.com/api/?name=Dang+Van+G',      'STUDENT', CURRENT_TIMESTAMP, true);

-- ═══════════════════════════════════════════════════════════════
-- 2. Thêm Teachers (4 giảng viên, liên kết user id 2,3,6,7)
--    teacher_id auto: 1→user2, 2→user3, 3→user6, 4→user7
-- ═══════════════════════════════════════════════════════════════
INSERT INTO teacher (id, first_name, last_name, birth_date, location, phone, bio, degree) VALUES
(2, 'Nguyễn', 'Văn A',  '1985-05-12', 'Hà Nội',  '0901234567', 'Chuyên gia lập trình với 10+ năm kinh nghiệm trong phát triển web và ứng dụng doanh nghiệp.',           'Thạc sĩ Khoa học Máy tính'),
(3, 'Trần',   'Văn B',  '1990-08-22', 'TP HCM',  '0912345678', 'Designer UI/UX với 7 năm kinh nghiệm tại các công ty công nghệ hàng đầu Việt Nam.',                    'Cử nhân Thiết kế Đồ họa'),
(6, 'Phạm',   'Văn C',  '1988-03-15', 'Đà Nẵng', '0945678901', 'Kỹ sư Machine Learning, từng làm việc tại Google và hiện là researcher tại Đại học Bách Khoa.',         'Tiến sĩ Trí tuệ Nhân tạo'),
(7, 'Lê',     'Văn D',  '1992-12-01', 'Hải Phòng','0956789012', 'Chuyên gia Mobile Development với hơn 50 ứng dụng đã phát hành trên App Store và Google Play.',         'Thạc sĩ Công nghệ Phần mềm');

-- ═══════════════════════════════════════════════════════════════
-- 3. Thêm Students (5 sinh viên, liên kết user id 4,5,8,9,10)
--    student_id auto: 1→user4, 2→user5, 3→user8, 4→user9, 5→user10
-- ═══════════════════════════════════════════════════════════════
INSERT INTO student (id, first_name, last_name, birth_date, gender, location, phone, bio, school) VALUES
(4,  'Lê',     'Thị C',  '2000-01-15', 'Female', 'Đà Nẵng',  '0923456789', 'Sinh viên IT đam mê lập trình web và mobile.',              'Đại học Bách Khoa Đà Nẵng'),
(5,  'Hoàng',  'Văn D',  '2002-11-03', 'Male',   'Hà Nội',   '0934567890', 'Yêu thích học hỏi công nghệ mới, đặc biệt là AI.',          'Đại học Quốc gia Hà Nội'),
(8,  'Nguyễn', 'Thị E',  '2001-06-20', 'Female', 'TP HCM',   '0967890123', 'Sinh viên năm cuối chuyên ngành Khoa học dữ liệu.',          'Đại học Công nghệ TP HCM'),
(9,  'Trần',   'Văn F',  '2003-04-10', 'Male',   'Huế',      '0978901234', 'Fresher Developer đang tìm kiếm cơ hội thực tập.',           'Đại học Khoa học Huế'),
(10, 'Đặng',   'Văn G',  '1999-09-28', 'Male',   'Cần Thơ',  '0989012345', 'Nhân viên IT đang chuyển ngành sang Data Science.',           'Đại học Cần Thơ');

-- ═══════════════════════════════════════════════════════════════
-- 4. Thêm Categories (7 danh mục)
-- ═══════════════════════════════════════════════════════════════
INSERT INTO category (category_name) VALUES
('Lập trình Web'),
('Thiết kế đồ họa'),
('Khoa học dữ liệu'),
('Kỹ năng mềm'),
('Lập trình Mobile'),
('Marketing'),
('Ngoại ngữ');

-- ═══════════════════════════════════════════════════════════════
-- 5. Thêm Courses (10 khóa học)
-- ═══════════════════════════════════════════════════════════════
INSERT INTO courses (title, description, image_url, price, created_at, is_deleted, is_active, categoryid, teacherid) VALUES
-- Khóa 1-3: Giữ nguyên dữ liệu gốc
('Khóa học Fullstack Web Development',        'Khóa học lập trình web toàn diện từ Frontend đến Backend với React, Node.js và cơ sở dữ liệu.',                     'https://img.youtube.com/vi/bMknfKXIFA8/maxresdefault.jpg', 999000,  CURRENT_TIMESTAMP, false, true, 1, 1),
('Nhập môn UI/UX Design với Figma',           'Học cách thiết kế giao diện ứng dụng chuyên nghiệp từ số 0 với công cụ Figma.',                                     'https://img.youtube.com/vi/c9Wg6Cb_YlU/maxresdefault.jpg', 499000,  CURRENT_TIMESTAMP, false, true, 2, 2),
('Lập trình Java Spring Boot Nâng cao',       'Master Java Core, Spring Data JPA, Spring Security và kiến trúc Microservices.',                                     'https://img.youtube.com/vi/bMknfKXIFA8/maxresdefault.jpg', 799000,  CURRENT_TIMESTAMP, false, true, 1, 1),
-- Khóa 4-10: Dữ liệu mới
('Python cho Data Science',                    'Học Python từ cơ bản đến nâng cao, ứng dụng trong phân tích dữ liệu với Pandas, NumPy và Matplotlib.',              'https://img.youtube.com/vi/rfscVS0vtbw/maxresdefault.jpg', 699000,  CURRENT_TIMESTAMP, false, true, 3, 3),
('Lập trình Flutter Mobile App',              'Xây dựng ứng dụng di động đa nền tảng (iOS & Android) với Flutter và ngôn ngữ Dart.',                                'https://img.youtube.com/vi/x0AnCE9SE4A/maxresdefault.jpg', 899000,  CURRENT_TIMESTAMP, false, true, 5, 4),
('Digital Marketing từ A đến Z',              'Nắm vững SEO, Google Ads, Facebook Ads và Content Marketing để phát triển thương hiệu trực tuyến.',                  'https://img.youtube.com/vi/bMknfKXIFA8/maxresdefault.jpg', 599000,  CURRENT_TIMESTAMP, false, true, 6, 2),
('Kỹ năng thuyết trình chuyên nghiệp',       'Rèn luyện kỹ năng thuyết trình, giao tiếp và xử lý tình huống trước đám đông.',                                     'https://img.youtube.com/vi/c9Wg6Cb_YlU/maxresdefault.jpg', 0,      CURRENT_TIMESTAMP, false, true, 4, 2),
('Machine Learning cơ bản với Python',        'Tìm hiểu các thuật toán ML phổ biến: Linear Regression, Decision Tree, SVM, Neural Network.',                        'https://img.youtube.com/vi/rfscVS0vtbw/maxresdefault.jpg', 1099000, CURRENT_TIMESTAMP, false, true, 3, 3),
('Thiết kế đồ họa với Adobe Illustrator',     'Thành thạo công cụ Illustrator để tạo logo, banner, poster và thiết kế vector chuyên nghiệp.',                       'https://img.youtube.com/vi/c9Wg6Cb_YlU/maxresdefault.jpg', 399000,  CURRENT_TIMESTAMP, false, true, 2, 2),
('English for IT Professionals',              'Nâng cao kỹ năng tiếng Anh chuyên ngành CNTT: đọc tài liệu, viết email, phỏng vấn bằng tiếng Anh.',                 'https://img.youtube.com/vi/bMknfKXIFA8/maxresdefault.jpg', 449000,  CURRENT_TIMESTAMP, false, true, 7, 4);

-- ═══════════════════════════════════════════════════════════════
-- 6. Thêm Enrollment (20 lượt đăng ký)
-- student_id: 1(LeThiC), 2(HoangVanD), 3(NguyenThiE), 4(TranVanF), 5(DangVanG)
-- ═══════════════════════════════════════════════════════════════
INSERT INTO enrollment (course_id, student_id, enroll_date, access_status, can_access_after_deletion) VALUES
-- Student 1 (Lê Thị C): 5 khóa
(1, 1, CURRENT_TIMESTAMP - INTERVAL '30 days', 'Active', true),
(2, 1, CURRENT_TIMESTAMP - INTERVAL '25 days', 'Active', true),
(3, 1, CURRENT_TIMESTAMP - INTERVAL '20 days', 'Active', true),
(4, 1, CURRENT_TIMESTAMP - INTERVAL '10 days', 'Active', true),
(7, 1, CURRENT_TIMESTAMP - INTERVAL '5 days',  'Active', true),
-- Student 2 (Hoàng Văn D): 4 khóa
(1, 2, CURRENT_TIMESTAMP - INTERVAL '28 days', 'Active', true),
(3, 2, CURRENT_TIMESTAMP - INTERVAL '18 days', 'Active', true),
(5, 2, CURRENT_TIMESTAMP - INTERVAL '12 days', 'Active', true),
(8, 2, CURRENT_TIMESTAMP - INTERVAL '3 days',  'Active', true),
-- Student 3 (Nguyễn Thị E): 4 khóa
(1, 3, CURRENT_TIMESTAMP - INTERVAL '22 days', 'Active', true),
(4, 3, CURRENT_TIMESTAMP - INTERVAL '15 days', 'Active', true),
(6, 3, CURRENT_TIMESTAMP - INTERVAL '8 days',  'Active', true),
(10,3, CURRENT_TIMESTAMP - INTERVAL '2 days',  'Active', true),
-- Student 4 (Trần Văn F): 4 khóa
(2, 4, CURRENT_TIMESTAMP - INTERVAL '20 days', 'Active', true),
(5, 4, CURRENT_TIMESTAMP - INTERVAL '14 days', 'Active', true),
(7, 4, CURRENT_TIMESTAMP - INTERVAL '7 days',  'Active', true),
(9, 4, CURRENT_TIMESTAMP - INTERVAL '1 day',   'Active', true),
-- Student 5 (Đặng Văn G): 3 khóa
(4, 5, CURRENT_TIMESTAMP - INTERVAL '16 days', 'Active', true),
(8, 5, CURRENT_TIMESTAMP - INTERVAL '9 days',  'Active', true),
(10,5, CURRENT_TIMESTAMP - INTERVAL '4 days',  'Active', true);

-- ═══════════════════════════════════════════════════════════════
-- 7. Thêm Payment (20 thanh toán tương ứng)
-- ═══════════════════════════════════════════════════════════════
INSERT INTO payment (student_id, course_id, amount, payment_date, payment_status) VALUES
-- Student 1
(1, 1, 999000,  CURRENT_TIMESTAMP - INTERVAL '30 days', 'Paid'),
(1, 2, 499000,  CURRENT_TIMESTAMP - INTERVAL '25 days', 'Paid'),
(1, 3, 799000,  CURRENT_TIMESTAMP - INTERVAL '20 days', 'Paid'),
(1, 4, 699000,  CURRENT_TIMESTAMP - INTERVAL '10 days', 'Paid'),
(1, 7, 0,      CURRENT_TIMESTAMP - INTERVAL '5 days',  'Paid'),
-- Student 2
(2, 1, 999000,  CURRENT_TIMESTAMP - INTERVAL '28 days', 'Paid'),
(2, 3, 799000,  CURRENT_TIMESTAMP - INTERVAL '18 days', 'Paid'),
(2, 5, 899000,  CURRENT_TIMESTAMP - INTERVAL '12 days', 'Paid'),
(2, 8, 1099000, CURRENT_TIMESTAMP - INTERVAL '3 days',  'Paid'),
-- Student 3
(3, 1, 999000,  CURRENT_TIMESTAMP - INTERVAL '22 days', 'Paid'),
(3, 4, 699000,  CURRENT_TIMESTAMP - INTERVAL '15 days', 'Paid'),
(3, 6, 599000,  CURRENT_TIMESTAMP - INTERVAL '8 days',  'Paid'),
(3,10, 449000,  CURRENT_TIMESTAMP - INTERVAL '2 days',  'Paid'),
-- Student 4
(4, 2, 499000,  CURRENT_TIMESTAMP - INTERVAL '20 days', 'Paid'),
(4, 5, 899000,  CURRENT_TIMESTAMP - INTERVAL '14 days', 'Paid'),
(4, 7, 0,      CURRENT_TIMESTAMP - INTERVAL '7 days',  'Paid'),
(4, 9, 399000,  CURRENT_TIMESTAMP - INTERVAL '1 day',   'Paid'),
-- Student 5
(5, 4, 699000,  CURRENT_TIMESTAMP - INTERVAL '16 days', 'Paid'),
(5, 8, 1099000, CURRENT_TIMESTAMP - INTERVAL '9 days',  'Paid'),
(5,10, 449000,  CURRENT_TIMESTAMP - INTERVAL '4 days',  'Paid');
