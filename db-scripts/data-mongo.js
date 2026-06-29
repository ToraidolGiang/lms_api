// Switch về DB của bạn (ví dụ: use lms_db)
// use lms_db;

// Dọn dẹp dữ liệu cũ
db.courses_content.deleteMany({});
db.course_reviews.deleteMany({});
db.student_progress.deleteMany({});
db.posts.deleteMany({});
db.discussions.deleteMany({});
db.notifications.deleteMany({});

// 1. Thêm Course Content (Lưu trữ modules và bài giảng của Courses)
// Yêu cầu: Mỗi khoá học có 2 bài quiz và 1 bài assignment, cấu trúc quiz phải chuẩn.
db.courses_content.insertMany([
  {
    courseId: 1,
    nameCourse: "Fullstack Web",
    courseTitle: "Khóa học Fullstack Web Development",
    description: "Khóa học lập trình web toàn diện với React, Spring Boot và cơ sở dữ liệu.",
    modules: [
      {
        moduleId: "M001",
        title: "Chương 1: Frontend với React JS",
        orderIndex: 1,
        lessons: [
          {
            lessonId: "M001_L001",
            title: "Giới thiệu về React JS",
            type: "video",
            orderIndex: 1,
            duration: 1200,
            content: { videoId: "bMknfKXIFA8" },
            isPreview: true
          },
          {
            lessonId: "M001_Q001",
            title: "Quiz 1: Kiến thức cơ bản React",
            type: "quiz",
            orderIndex: 2,
            duration: 600,
            content: {
              questions: [
                { id: "q1", question: "React JS là gì?", options: ["Thư viện Javascript", "Framework CSS", "Cơ sở dữ liệu", "Hệ điều hành"], correctAnswer: 0 },
                { id: "q2", question: "Virtual DOM là gì?", options: ["DOM thật của trình duyệt", "Bản sao thu nhỏ của DOM thật", "Một loại database", "Một ngôn ngữ lập trình"], correctAnswer: 1 }
              ]
            },
            isPreview: false
          },
          {
            lessonId: "M001_Q002",
            title: "Quiz 2: Quản lý State",
            type: "quiz",
            orderIndex: 3,
            duration: 900,
            content: {
              questions: [
                { id: "q3", question: "Hook nào dùng để quản lý state?", options: ["useEffect", "useMemo", "useState", "useContext"], correctAnswer: 2 },
                { id: "q4", question: "useEffect được gọi khi nào?", options: ["Sau khi component render xong", "Trước khi component render", "Khi component bị huỷ", "Không bao giờ"], correctAnswer: 0 }
              ]
            },
            isPreview: false
          }
        ]
      },
      {
        moduleId: "M002",
        title: "Chương 2: Bài tập thực hành",
        orderIndex: 2,
        lessons: [
          {
            lessonId: "M002_A001",
            title: "Bài tập lớn: Hoàn thiện giao diện React",
            type: "assignment",
            orderIndex: 1,
            duration: 7200,
            content: { instructions: "Viết giao diện Todo App với React, sử dụng useState và useEffect. Hãy nén source code thành file .zip và đính kèm link Google Drive của bạn (Nhớ mở quyền truy cập public). Ghi chú thêm nếu bạn gặp khó khăn phần nào đó để giảng viên hỗ trợ." },
            isPreview: false
          }
        ]
      }
    ],
    metadata: {
      tags: ["React", "Spring Boot", "Fullstack", "Web"],
      difficulty: "Beginner",
      language: "Vietnamese",
      prerequisites: ["HTML cơ bản", "CSS", "Javascript"],
      estimatedCompletionTime: 40
    }
  },
  {
    courseId: 2,
    nameCourse: "UI/UX Design",
    courseTitle: "Nhập môn UI/UX Design với Figma",
    description: "Học cách thiết kế giao diện ứng dụng từ số 0.",
    modules: [
      {
        moduleId: "M001",
        title: "Chương 1: Cơ bản về công cụ Figma",
        orderIndex: 1,
        lessons: [
          {
            lessonId: "M001_L001",
            title: "Giới thiệu các công cụ trong Figma",
            type: "video",
            orderIndex: 1,
            duration: 1000,
            content: { videoId: "c9Wg6Cb_YlU" },
            isPreview: true
          },
          {
            lessonId: "M001_Q001",
            title: "Quiz 1: Làm quen Figma",
            type: "quiz",
            orderIndex: 2,
            duration: 500,
            content: {
              questions: [
                { id: "q1", question: "Figma là công cụ chuyên dùng để làm gì?", options: ["Lập trình ứng dụng", "Thiết kế UI/UX", "Quản lý database", "Edit video"], correctAnswer: 1 },
                { id: "q2", question: "Phím tắt để tạo Frame trong Figma là gì?", options: ["F", "A", "Cả F và A", "Tất cả đều sai"], correctAnswer: 2 }
              ]
            },
            isPreview: false
          }
        ]
      },
      {
        moduleId: "M002",
        title: "Chương 2: Màu sắc và Typography",
        orderIndex: 2,
        lessons: [
          {
            lessonId: "M002_Q001",
            title: "Quiz 2: Lý thuyết màu sắc",
            type: "quiz",
            orderIndex: 1,
            duration: 600,
            content: {
              questions: [
                { id: "q3", question: "Màu nào là màu tương phản của Đỏ?", options: ["Xanh dương", "Xanh lá cây", "Vàng", "Cam"], correctAnswer: 1 },
                { id: "q4", question: "Primary color là gì?", options: ["Màu nền", "Màu chữ chính", "Màu chủ đạo của thương hiệu", "Màu lỗi"], correctAnswer: 2 }
              ]
            },
            isPreview: false
          },
          {
            lessonId: "M002_A001",
            title: "Assignment: Thiết kế giao diện Login",
            type: "assignment",
            orderIndex: 2,
            duration: 3600,
            content: { instructions: "Hãy sử dụng Figma để thiết kế 1 trang Login cho ứng dụng LMS. Trang cần có đầy đủ Input, Button, Text và Hình ảnh minh họa. Sau khi hoàn thành, copy link Figma (Nhớ để quyền Anyone with the link can view) và dán vào ô nộp bài." },
            isPreview: false
          }
        ]
      }
    ],
    metadata: {
      tags: ["Figma", "Design", "UI/UX"],
      difficulty: "Beginner",
      language: "Vietnamese",
      prerequisites: [],
      estimatedCompletionTime: 15
    }
  },
  {
    courseId: 3,
    nameCourse: "Java Spring Boot Nâng cao",
    courseTitle: "Lập trình Java Spring Boot Nâng cao",
    description: "Master Java Core, Spring Data JPA, Security và Microservices.",
    modules: [
      {
        moduleId: "M001",
        title: "Chương 1: Spring Boot Cơ bản",
        orderIndex: 1,
        lessons: [
          {
            lessonId: "M001_L001",
            title: "Khởi tạo dự án Spring Boot",
            type: "video",
            orderIndex: 1,
            duration: 1800,
            content: { videoId: "bMknfKXIFA8" },
            isPreview: true
          },
          {
            lessonId: "M001_Q001",
            title: "Quiz 1: Dependency Injection",
            type: "quiz",
            orderIndex: 2,
            duration: 600,
            content: {
              questions: [
                { id: "q1", question: "Annotation nào dùng để khai báo một Bean trong Spring?", options: ["@Component", "@Entity", "@Table", "@Autowired"], correctAnswer: 0 },
                { id: "q2", question: "Cách tốt nhất để inject dependency là gì?", options: ["Field Injection", "Setter Injection", "Constructor Injection", "Không cần inject"], correctAnswer: 2 }
              ]
            },
            isPreview: false
          }
        ]
      },
      {
        moduleId: "M002",
        title: "Chương 2: Spring Data JPA",
        orderIndex: 2,
        lessons: [
          {
            lessonId: "M002_A001",
            title: "Assignment: Thiết kế Entity",
            type: "assignment",
            orderIndex: 1,
            duration: 7200,
            content: { instructions: "Hãy tạo 2 Entity là User và Role có quan hệ Many-To-Many. Nén file code thành .zip và nộp lên hệ thống." },
            isPreview: false
          }
        ]
      }
    ],
    metadata: {
      tags: ["Java", "Spring Boot", "JPA", "Backend"],
      difficulty: "Advanced",
      language: "Vietnamese",
      prerequisites: ["Java Core", "SQL cơ bản"],
      estimatedCompletionTime: 60
    }
  }
]);

// 2. Thêm Course Reviews
db.course_reviews.insertMany([
  {
    courseId: 1,
    studentId: 1,
    enrollmentId: 1,
    rating: 5.0,
    review: {
      title: "Khóa học rất thực tế",
      content: "Giảng viên giảng bài cực kỳ chi tiết, dễ hiểu. Rất sát với thực tế công việc.",
      pros: ["Dễ hiểu", "Thực tiễn cao", "Support nhiệt tình"],
      cons: []
    },
    helpful: {
      upvotes: 12,
      downvotes: 0,
      votedBy: [],
      votedUpBy: [],
      votedDownBy: []
    },
    isVerified: true,
    createdAt: new Date(),
    updatedAt: new Date()
  }
]);

// 3. Thêm Student Progress
db.student_progress.insertMany([
  {
    studentId: 1,
    courseId: 1,
    enrollmentId: 1,
    progress: {
      completedLessons: ["M001_L001"],
      currentLesson: "M001_Q001",
      overallProgress: 25.0,
      totalWatchTime: 1200,
      lastAccessedAt: new Date()
    },
    lessonProgress: [
      {
        lessonId: "M001_L001",
        status: "completed",
        watchedDuration: 1200,
        totalDuration: 1200,
        progressPercent: 100.0,
        attemptCount: 1,
        maxAttempts: 5,
        lastAccessedAt: new Date(),
        completedAt: new Date()
      }
    ],
    updatedAt: new Date()
  }
]);

// 4. Thêm Discussions (Thảo luận / Q&A)
db.discussions.insertMany([
  {
    discussionId: 1,
    courseId: 1,
    lessonId: "M001_L001",
    authorId: 1,
    authorRole: "STUDENT",
    title: "Lỗi thư viện khi khởi tạo dự án React",
    content: "Mình chạy lệnh npx create-react-app báo lỗi thư viện, ai giúp mình với ạ?",
    codeSnippet: "npm ERR! code ENOTFOUND",
    tags: ["react", "error", "npm"],
    replies: [
      {
        replyId: 1,
        authorId: 1,
        authorRole: "TEACHER",
        content: "Em thử xóa bộ nhớ đệm (cache) của npm bằng lệnh: `npm cache clean --force` rồi thử lại nhé.",
        isAccepted: true,
        upvotes: 5,
        createdAt: new Date()
      }
    ],
    views: 120,
    upvotes: 2,
    isPinned: true,
    isSolved: true,
    createdAt: new Date(),
    updatedAt: new Date()
  }
]);

// 5. Thêm Posts (Blog bài viết)
db.posts.insertMany([
  {
    title: "Lộ trình trở thành Fullstack Developer 2024",
    content: "Trong bài viết này, chúng ta sẽ tìm hiểu những kỹ năng cần thiết để trở thành Fullstack Developer...",
    category: "Career",
    type: "blog",
    views: 850,
    likes: ["user2", "user4"],
    comments: [
      {
        commentId: "C1",
        userId: "4",
        authorName: "An Nguyen",
        content: "Bài viết rất hữu ích, cảm ơn ad!",
        createdAt: new Date()
      }
    ],
    tags: ["Fullstack", "Career", "2024"],
    userId: "1",
    authorName: "Admin",
    authorRole: "ADMIN",
    createdAt: new Date(),
    updatedAt: new Date(),
    pinned: true
  }
]);

// 6. Thêm Notifications (Thông báo)
db.notifications.insertMany([
  {
    userId: 1,
    title: "Chào mừng bạn mới",
    message: "Chào mừng bạn đến với hệ thống LMS. Chúc bạn học tập tốt!",
    type: "system",
    link: "/",
    read: false,
    createdAt: new Date(),
    _class: "com.example.lms_api.entity.Notification"
  }
]);
