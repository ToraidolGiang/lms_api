// Switch về DB của bạn (ví dụ: use lms_db)
// use lms_db;

// Dọn dẹp dữ liệu cũ
db.courses_content.deleteMany({});
db.course_reviews.deleteMany({});
db.student_progress.deleteMany({});
db.posts.deleteMany({});
db.discussions.deleteMany({});
db.notifications.deleteMany({});

// ═══════════════════════════════════════════════════════════════
// 1. COURSE CONTENT (10 khóa học, mỗi khóa: 1 video + 2 quiz + 1 assignment)
// ═══════════════════════════════════════════════════════════════
db.courses_content.insertMany([
  // ── Khóa 1: Fullstack Web Development ──
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

  // ── Khóa 2: UI/UX Design với Figma ──
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

  // ── Khóa 3: Java Spring Boot Nâng cao ──
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
            lessonId: "M002_Q001",
            title: "Quiz 2: JPA Repository",
            type: "quiz",
            orderIndex: 1,
            duration: 600,
            content: {
              questions: [
                { id: "q3", question: "JpaRepository kế thừa từ interface nào?", options: ["CrudRepository", "MongoRepository", "PagingAndSortingRepository", "Cả A và C"], correctAnswer: 3 },
                { id: "q4", question: "Annotation nào đánh dấu khóa chính?", options: ["@Column", "@Id", "@Table", "@Entity"], correctAnswer: 1 }
              ]
            },
            isPreview: false
          },
          {
            lessonId: "M002_A001",
            title: "Assignment: Thiết kế Entity",
            type: "assignment",
            orderIndex: 2,
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
  },

  // ── Khóa 4: Python cho Data Science ──
  {
    courseId: 4,
    nameCourse: "Python Data Science",
    courseTitle: "Python cho Data Science",
    description: "Học Python từ cơ bản đến nâng cao, ứng dụng trong phân tích dữ liệu với Pandas, NumPy và Matplotlib.",
    modules: [
      {
        moduleId: "M001",
        title: "Chương 1: Python cơ bản",
        orderIndex: 1,
        lessons: [
          {
            lessonId: "M001_L001",
            title: "Cài đặt môi trường Python và Jupyter Notebook",
            type: "video",
            orderIndex: 1,
            duration: 900,
            content: { videoId: "rfscVS0vtbw" },
            isPreview: true
          },
          {
            lessonId: "M001_Q001",
            title: "Quiz 1: Cú pháp Python cơ bản",
            type: "quiz",
            orderIndex: 2,
            duration: 600,
            content: {
              questions: [
                { id: "q1", question: "Python sử dụng gì để phân biệt khối lệnh?", options: ["Ngoặc nhọn {}", "Thụt lề (Indentation)", "Dấu chấm phẩy ;", "Từ khóa end"], correctAnswer: 1 },
                { id: "q2", question: "Kiểu dữ liệu nào KHÔNG có trong Python?", options: ["list", "tuple", "array", "dict"], correctAnswer: 2 },
                { id: "q3", question: "Hàm len() dùng để làm gì?", options: ["Tính tổng", "Đếm số phần tử", "Sắp xếp", "Tìm giá trị lớn nhất"], correctAnswer: 1 }
              ]
            },
            isPreview: false
          }
        ]
      },
      {
        moduleId: "M002",
        title: "Chương 2: Pandas và NumPy",
        orderIndex: 2,
        lessons: [
          {
            lessonId: "M002_L001",
            title: "Giới thiệu thư viện Pandas",
            type: "video",
            orderIndex: 1,
            duration: 1500,
            content: { videoId: "rfscVS0vtbw" },
            isPreview: false
          },
          {
            lessonId: "M002_Q001",
            title: "Quiz 2: Pandas DataFrame",
            type: "quiz",
            orderIndex: 2,
            duration: 600,
            content: {
              questions: [
                { id: "q4", question: "Hàm nào đọc file CSV trong Pandas?", options: ["pd.read_csv()", "pd.open_csv()", "pd.load_csv()", "pd.import_csv()"], correctAnswer: 0 },
                { id: "q5", question: "DataFrame.head() mặc định trả về bao nhiêu dòng?", options: ["3", "5", "10", "Tất cả"], correctAnswer: 1 }
              ]
            },
            isPreview: false
          },
          {
            lessonId: "M002_A001",
            title: "Assignment: Phân tích dữ liệu bán hàng",
            type: "assignment",
            orderIndex: 3,
            duration: 5400,
            content: { instructions: "Sử dụng Pandas để đọc file sales_data.csv, thực hiện phân tích: tìm sản phẩm bán chạy nhất, doanh thu theo tháng, vẽ biểu đồ với Matplotlib. Nộp file Jupyter Notebook (.ipynb) lên hệ thống." },
            isPreview: false
          }
        ]
      }
    ],
    metadata: {
      tags: ["Python", "Data Science", "Pandas", "NumPy", "Matplotlib"],
      difficulty: "Beginner",
      language: "Vietnamese",
      prerequisites: ["Không yêu cầu kiến thức trước"],
      estimatedCompletionTime: 30
    }
  },

  // ── Khóa 5: Flutter Mobile App ──
  {
    courseId: 5,
    nameCourse: "Flutter Mobile",
    courseTitle: "Lập trình Flutter Mobile App",
    description: "Xây dựng ứng dụng di động đa nền tảng (iOS & Android) với Flutter và ngôn ngữ Dart.",
    modules: [
      {
        moduleId: "M001",
        title: "Chương 1: Dart và Flutter cơ bản",
        orderIndex: 1,
        lessons: [
          {
            lessonId: "M001_L001",
            title: "Cài đặt Flutter SDK và tạo dự án đầu tiên",
            type: "video",
            orderIndex: 1,
            duration: 1500,
            content: { videoId: "x0AnCE9SE4A" },
            isPreview: true
          },
          {
            lessonId: "M001_Q001",
            title: "Quiz 1: Ngôn ngữ Dart",
            type: "quiz",
            orderIndex: 2,
            duration: 600,
            content: {
              questions: [
                { id: "q1", question: "Flutter sử dụng ngôn ngữ lập trình nào?", options: ["Java", "Kotlin", "Dart", "Swift"], correctAnswer: 2 },
                { id: "q2", question: "Widget nào là gốc của mọi ứng dụng Flutter?", options: ["Scaffold", "MaterialApp", "Container", "Column"], correctAnswer: 1 }
              ]
            },
            isPreview: false
          }
        ]
      },
      {
        moduleId: "M002",
        title: "Chương 2: Xây dựng UI nâng cao",
        orderIndex: 2,
        lessons: [
          {
            lessonId: "M002_L001",
            title: "ListView, GridView và Navigation",
            type: "video",
            orderIndex: 1,
            duration: 2000,
            content: { videoId: "x0AnCE9SE4A" },
            isPreview: false
          },
          {
            lessonId: "M002_Q001",
            title: "Quiz 2: Widget và Layout",
            type: "quiz",
            orderIndex: 2,
            duration: 600,
            content: {
              questions: [
                { id: "q3", question: "StatefulWidget khác StatelessWidget ở điểm nào?", options: ["Có thể thay đổi state", "Chạy nhanh hơn", "Không cần build()", "Không có sự khác biệt"], correctAnswer: 0 },
                { id: "q4", question: "Navigator.push() dùng để làm gì?", options: ["Xóa màn hình", "Điều hướng sang trang mới", "Tạo widget mới", "Gọi API"], correctAnswer: 1 }
              ]
            },
            isPreview: false
          },
          {
            lessonId: "M002_A001",
            title: "Assignment: Xây dựng ứng dụng Quản lý công việc",
            type: "assignment",
            orderIndex: 3,
            duration: 7200,
            content: { instructions: "Tạo ứng dụng quản lý công việc (Todo App) với Flutter. Yêu cầu: Thêm/Sửa/Xóa task, phân loại theo trạng thái (Đang làm, Hoàn thành). Nén project thành .zip và nộp lên hệ thống." },
            isPreview: false
          }
        ]
      }
    ],
    metadata: {
      tags: ["Flutter", "Dart", "Mobile", "iOS", "Android"],
      difficulty: "Intermediate",
      language: "Vietnamese",
      prerequisites: ["Lập trình OOP cơ bản"],
      estimatedCompletionTime: 50
    }
  },

  // ── Khóa 6: Digital Marketing ──
  {
    courseId: 6,
    nameCourse: "Digital Marketing",
    courseTitle: "Digital Marketing từ A đến Z",
    description: "Nắm vững SEO, Google Ads, Facebook Ads và Content Marketing để phát triển thương hiệu trực tuyến.",
    modules: [
      {
        moduleId: "M001",
        title: "Chương 1: Tổng quan Digital Marketing",
        orderIndex: 1,
        lessons: [
          {
            lessonId: "M001_L001",
            title: "Digital Marketing là gì? Các kênh phổ biến",
            type: "video",
            orderIndex: 1,
            duration: 1100,
            content: { videoId: "bMknfKXIFA8" },
            isPreview: true
          },
          {
            lessonId: "M001_Q001",
            title: "Quiz 1: Khái niệm cơ bản",
            type: "quiz",
            orderIndex: 2,
            duration: 500,
            content: {
              questions: [
                { id: "q1", question: "SEO là viết tắt của gì?", options: ["Search Engine Optimization", "Social Engagement Online", "Sales Enhancement Operation", "Site Evaluation Objective"], correctAnswer: 0 },
                { id: "q2", question: "CTR trong quảng cáo là gì?", options: ["Cost To Revenue", "Click Through Rate", "Customer Tracking Report", "Content Type Rating"], correctAnswer: 1 }
              ]
            },
            isPreview: false
          }
        ]
      },
      {
        moduleId: "M002",
        title: "Chương 2: Facebook Ads & Google Ads",
        orderIndex: 2,
        lessons: [
          {
            lessonId: "M002_L001",
            title: "Hướng dẫn chạy quảng cáo Facebook từ A-Z",
            type: "video",
            orderIndex: 1,
            duration: 2400,
            content: { videoId: "bMknfKXIFA8" },
            isPreview: false
          },
          {
            lessonId: "M002_Q001",
            title: "Quiz 2: Facebook Ads Manager",
            type: "quiz",
            orderIndex: 2,
            duration: 600,
            content: {
              questions: [
                { id: "q3", question: "Mục tiêu 'Conversions' trong Facebook Ads dùng để?", options: ["Tăng lượt thích trang", "Tối ưu hành động chuyển đổi", "Tăng lượt xem video", "Tăng tin nhắn"], correctAnswer: 1 },
                { id: "q4", question: "Pixel Facebook dùng để làm gì?", options: ["Chỉnh sửa ảnh", "Theo dõi hành vi người dùng trên website", "Tạo quảng cáo tự động", "Quản lý fanpage"], correctAnswer: 1 }
              ]
            },
            isPreview: false
          },
          {
            lessonId: "M002_A001",
            title: "Assignment: Lập kế hoạch chiến dịch Marketing",
            type: "assignment",
            orderIndex: 3,
            duration: 5400,
            content: { instructions: "Lập kế hoạch chiến dịch Digital Marketing cho một sản phẩm tự chọn. Bao gồm: phân tích đối tượng mục tiêu, lựa chọn kênh quảng cáo, ngân sách dự kiến, KPI đo lường. Trình bày bằng file PDF hoặc PowerPoint." },
            isPreview: false
          }
        ]
      }
    ],
    metadata: {
      tags: ["Marketing", "SEO", "Facebook Ads", "Google Ads"],
      difficulty: "Beginner",
      language: "Vietnamese",
      prerequisites: [],
      estimatedCompletionTime: 25
    }
  },

  // ── Khóa 7: Kỹ năng thuyết trình (FREE) ──
  {
    courseId: 7,
    nameCourse: "Thuyết trình chuyên nghiệp",
    courseTitle: "Kỹ năng thuyết trình chuyên nghiệp",
    description: "Rèn luyện kỹ năng thuyết trình, giao tiếp và xử lý tình huống trước đám đông.",
    modules: [
      {
        moduleId: "M001",
        title: "Chương 1: Nền tảng thuyết trình",
        orderIndex: 1,
        lessons: [
          {
            lessonId: "M001_L001",
            title: "Bí quyết trình bày ấn tượng trong 5 phút đầu",
            type: "video",
            orderIndex: 1,
            duration: 800,
            content: { videoId: "c9Wg6Cb_YlU" },
            isPreview: true
          },
          {
            lessonId: "M001_Q001",
            title: "Quiz 1: Nguyên tắc thuyết trình",
            type: "quiz",
            orderIndex: 2,
            duration: 400,
            content: {
              questions: [
                { id: "q1", question: "Quy tắc 10-20-30 của Guy Kawasaki là gì?", options: ["10 slide, 20 phút, font 30pt", "10 phút, 20 slide, 30 từ", "10 ý chính, 20 ví dụ, 30 hình ảnh", "Tất cả đều sai"], correctAnswer: 0 },
                { id: "q2", question: "Phần nào quan trọng nhất trong bài thuyết trình?", options: ["Phần giữa", "Mở bài và Kết bài", "Phần Q&A", "Phần giới thiệu bản thân"], correctAnswer: 1 }
              ]
            },
            isPreview: false
          }
        ]
      },
      {
        moduleId: "M002",
        title: "Chương 2: Xử lý tình huống và Q&A",
        orderIndex: 2,
        lessons: [
          {
            lessonId: "M002_L001",
            title: "Cách xử lý câu hỏi khó từ khán giả",
            type: "video",
            orderIndex: 1,
            duration: 1000,
            content: { videoId: "c9Wg6Cb_YlU" },
            isPreview: false
          },
          {
            lessonId: "M002_Q001",
            title: "Quiz 2: Xử lý tình huống",
            type: "quiz",
            orderIndex: 2,
            duration: 500,
            content: {
              questions: [
                { id: "q3", question: "Khi bị hỏi câu bạn không biết câu trả lời, nên làm gì?", options: ["Bịa đáp án", "Thừa nhận và hẹn trả lời sau", "Bỏ qua câu hỏi", "Đổi chủ đề"], correctAnswer: 1 },
                { id: "q4", question: "Ngôn ngữ cơ thể chiếm bao nhiêu % trong giao tiếp?", options: ["7%", "38%", "55%", "100%"], correctAnswer: 2 }
              ]
            },
            isPreview: false
          },
          {
            lessonId: "M002_A001",
            title: "Assignment: Quay video thuyết trình 5 phút",
            type: "assignment",
            orderIndex: 3,
            duration: 3600,
            content: { instructions: "Chọn 1 chủ đề tự do và thuyết trình trong 5 phút. Quay video và upload lên Google Drive (mở quyền xem công khai). Dán link vào ô nộp bài. Tiêu chí chấm: Nội dung rõ ràng, giọng nói tự tin, ngôn ngữ cơ thể tự nhiên." },
            isPreview: false
          }
        ]
      }
    ],
    metadata: {
      tags: ["Soft Skills", "Presentation", "Communication"],
      difficulty: "Beginner",
      language: "Vietnamese",
      prerequisites: [],
      estimatedCompletionTime: 10
    }
  },

  // ── Khóa 8: Machine Learning cơ bản ──
  {
    courseId: 8,
    nameCourse: "Machine Learning",
    courseTitle: "Machine Learning cơ bản với Python",
    description: "Tìm hiểu các thuật toán ML phổ biến: Linear Regression, Decision Tree, SVM, Neural Network.",
    modules: [
      {
        moduleId: "M001",
        title: "Chương 1: Giới thiệu Machine Learning",
        orderIndex: 1,
        lessons: [
          {
            lessonId: "M001_L001",
            title: "Machine Learning là gì? Supervised vs Unsupervised",
            type: "video",
            orderIndex: 1,
            duration: 2000,
            content: { videoId: "rfscVS0vtbw" },
            isPreview: true
          },
          {
            lessonId: "M001_Q001",
            title: "Quiz 1: Khái niệm ML cơ bản",
            type: "quiz",
            orderIndex: 2,
            duration: 600,
            content: {
              questions: [
                { id: "q1", question: "Supervised Learning là gì?", options: ["Học không giám sát", "Học có nhãn (label)", "Học tăng cường", "Học sâu"], correctAnswer: 1 },
                { id: "q2", question: "Thuật toán nào dùng cho bài toán phân loại?", options: ["Linear Regression", "K-Means", "Decision Tree", "PCA"], correctAnswer: 2 },
                { id: "q3", question: "Overfitting là gì?", options: ["Model quá đơn giản", "Model học quá tốt dữ liệu train nhưng kém trên dữ liệu mới", "Model không hội tụ", "Model chạy quá chậm"], correctAnswer: 1 }
              ]
            },
            isPreview: false
          }
        ]
      },
      {
        moduleId: "M002",
        title: "Chương 2: Thực hành với Scikit-learn",
        orderIndex: 2,
        lessons: [
          {
            lessonId: "M002_L001",
            title: "Xây dựng model dự đoán với Scikit-learn",
            type: "video",
            orderIndex: 1,
            duration: 2500,
            content: { videoId: "rfscVS0vtbw" },
            isPreview: false
          },
          {
            lessonId: "M002_Q001",
            title: "Quiz 2: Scikit-learn API",
            type: "quiz",
            orderIndex: 2,
            duration: 600,
            content: {
              questions: [
                { id: "q4", question: "Hàm nào dùng để chia dữ liệu train/test?", options: ["split_data()", "train_test_split()", "data_split()", "divide_data()"], correctAnswer: 1 },
                { id: "q5", question: "Metric nào đánh giá model phân loại?", options: ["MSE", "R²", "Accuracy / F1-Score", "MAE"], correctAnswer: 2 }
              ]
            },
            isPreview: false
          },
          {
            lessonId: "M002_A001",
            title: "Assignment: Dự đoán giá nhà với Linear Regression",
            type: "assignment",
            orderIndex: 3,
            duration: 7200,
            content: { instructions: "Sử dụng bộ dữ liệu Boston Housing, xây dựng model Linear Regression để dự đoán giá nhà. Yêu cầu: EDA (phân tích khám phá dữ liệu), train model, đánh giá bằng MSE và R². Nộp file Jupyter Notebook (.ipynb)." },
            isPreview: false
          }
        ]
      }
    ],
    metadata: {
      tags: ["Machine Learning", "Python", "Scikit-learn", "AI"],
      difficulty: "Advanced",
      language: "Vietnamese",
      prerequisites: ["Python cơ bản", "Toán thống kê"],
      estimatedCompletionTime: 70
    }
  },

  // ── Khóa 9: Thiết kế đồ họa Illustrator ──
  {
    courseId: 9,
    nameCourse: "Adobe Illustrator",
    courseTitle: "Thiết kế đồ họa với Adobe Illustrator",
    description: "Thành thạo công cụ Illustrator để tạo logo, banner, poster và thiết kế vector chuyên nghiệp.",
    modules: [
      {
        moduleId: "M001",
        title: "Chương 1: Giao diện và công cụ cơ bản",
        orderIndex: 1,
        lessons: [
          {
            lessonId: "M001_L001",
            title: "Làm quen giao diện Adobe Illustrator",
            type: "video",
            orderIndex: 1,
            duration: 1200,
            content: { videoId: "c9Wg6Cb_YlU" },
            isPreview: true
          },
          {
            lessonId: "M001_Q001",
            title: "Quiz 1: Công cụ trong Illustrator",
            type: "quiz",
            orderIndex: 2,
            duration: 500,
            content: {
              questions: [
                { id: "q1", question: "Pen Tool dùng để làm gì?", options: ["Tô màu", "Vẽ đường vector tự do", "Cắt hình", "Chèn text"], correctAnswer: 1 },
                { id: "q2", question: "File AI là định dạng của phần mềm nào?", options: ["Photoshop", "Illustrator", "InDesign", "Premiere"], correctAnswer: 1 }
              ]
            },
            isPreview: false
          }
        ]
      },
      {
        moduleId: "M002",
        title: "Chương 2: Thiết kế Logo và Branding",
        orderIndex: 2,
        lessons: [
          {
            lessonId: "M002_L001",
            title: "Quy trình thiết kế logo chuyên nghiệp",
            type: "video",
            orderIndex: 1,
            duration: 1800,
            content: { videoId: "c9Wg6Cb_YlU" },
            isPreview: false
          },
          {
            lessonId: "M002_Q001",
            title: "Quiz 2: Nguyên tắc thiết kế Logo",
            type: "quiz",
            orderIndex: 2,
            duration: 500,
            content: {
              questions: [
                { id: "q3", question: "Logo tốt cần đáp ứng tiêu chí nào?", options: ["Nhiều màu sắc", "Đơn giản, dễ nhớ, linh hoạt", "Càng chi tiết càng tốt", "Luôn dùng font chữ đặc biệt"], correctAnswer: 1 },
                { id: "q4", question: "Vector khác Raster ở điểm nào?", options: ["Vector nhẹ hơn", "Vector không bị vỡ khi phóng to", "Vector chỉ dùng cho web", "Không có sự khác biệt"], correctAnswer: 1 }
              ]
            },
            isPreview: false
          },
          {
            lessonId: "M002_A001",
            title: "Assignment: Thiết kế bộ nhận diện thương hiệu",
            type: "assignment",
            orderIndex: 3,
            duration: 7200,
            content: { instructions: "Thiết kế bộ nhận diện thương hiệu gồm: Logo, Name Card, Letterhead cho một thương hiệu tự chọn. Xuất file PDF và nộp lên hệ thống." },
            isPreview: false
          }
        ]
      }
    ],
    metadata: {
      tags: ["Illustrator", "Logo", "Branding", "Vector"],
      difficulty: "Intermediate",
      language: "Vietnamese",
      prerequisites: ["Không yêu cầu"],
      estimatedCompletionTime: 35
    }
  },

  // ── Khóa 10: English for IT ──
  {
    courseId: 10,
    nameCourse: "English for IT",
    courseTitle: "English for IT Professionals",
    description: "Nâng cao kỹ năng tiếng Anh chuyên ngành CNTT: đọc tài liệu, viết email, phỏng vấn bằng tiếng Anh.",
    modules: [
      {
        moduleId: "M001",
        title: "Chapter 1: IT Vocabulary & Reading",
        orderIndex: 1,
        lessons: [
          {
            lessonId: "M001_L001",
            title: "Essential IT Vocabulary for Developers",
            type: "video",
            orderIndex: 1,
            duration: 1000,
            content: { videoId: "bMknfKXIFA8" },
            isPreview: true
          },
          {
            lessonId: "M001_Q001",
            title: "Quiz 1: IT Terminology",
            type: "quiz",
            orderIndex: 2,
            duration: 600,
            content: {
              questions: [
                { id: "q1", question: "What does 'API' stand for?", options: ["Application Programming Interface", "Advanced Program Integration", "Automated Process Input", "Application Process Instruction"], correctAnswer: 0 },
                { id: "q2", question: "Which word means 'triển khai' in IT?", options: ["Debug", "Deploy", "Design", "Develop"], correctAnswer: 1 }
              ]
            },
            isPreview: false
          }
        ]
      },
      {
        moduleId: "M002",
        title: "Chapter 2: Writing & Interview Skills",
        orderIndex: 2,
        lessons: [
          {
            lessonId: "M002_L001",
            title: "How to write professional emails in English",
            type: "video",
            orderIndex: 1,
            duration: 1200,
            content: { videoId: "bMknfKXIFA8" },
            isPreview: false
          },
          {
            lessonId: "M002_Q001",
            title: "Quiz 2: Email Writing & Interview",
            type: "quiz",
            orderIndex: 2,
            duration: 600,
            content: {
              questions: [
                { id: "q3", question: "What is the best closing for a formal email?", options: ["Cheers", "Best regards", "See ya", "XOXO"], correctAnswer: 1 },
                { id: "q4", question: "'Tell me about yourself' in an interview should focus on?", options: ["Your hobbies", "Your relevant experience and skills", "Your family", "Your salary expectations"], correctAnswer: 1 }
              ]
            },
            isPreview: false
          },
          {
            lessonId: "M002_A001",
            title: "Assignment: Write a Cover Letter",
            type: "assignment",
            orderIndex: 3,
            duration: 3600,
            content: { instructions: "Write a cover letter in English for a Junior Developer position at a tech company. The letter should include: Introduction, relevant skills & experience, why you are a good fit. Submit as a PDF file." },
            isPreview: false
          }
        ]
      }
    ],
    metadata: {
      tags: ["English", "IT", "Communication", "Interview"],
      difficulty: "Intermediate",
      language: "Vietnamese",
      prerequisites: ["Tiếng Anh trình độ A2-B1"],
      estimatedCompletionTime: 20
    }
  }
]);

// ═══════════════════════════════════════════════════════════════
// 2. COURSE REVIEWS (12 bài đánh giá)
// ═══════════════════════════════════════════════════════════════
db.course_reviews.insertMany([
  // Khóa 1 – 3 review
  {
    courseId: 1, studentId: 1, enrollmentId: 1, rating: 5.0,
    review: {
      title: "Khóa học rất thực tế",
      content: "Giảng viên giảng bài cực kỳ chi tiết, dễ hiểu. Rất sát với thực tế công việc.",
      pros: ["Dễ hiểu", "Thực tiễn cao", "Support nhiệt tình"], cons: []
    },
    helpful: { upvotes: 12, downvotes: 0, votedBy: [], votedUpBy: [], votedDownBy: [] },
    isVerified: true, createdAt: new Date(), updatedAt: new Date()
  },
  {
    courseId: 1, studentId: 2, enrollmentId: 6, rating: 4.0,
    review: {
      title: "Tốt nhưng cần thêm bài tập",
      content: "Nội dung khóa học rất tốt, tuy nhiên mình muốn có thêm nhiều bài tập thực hành hơn.",
      pros: ["Nội dung chất lượng", "Video rõ ràng"], cons: ["Ít bài tập", "Cần cập nhật thêm"]
    },
    helpful: { upvotes: 8, downvotes: 1, votedBy: [], votedUpBy: [], votedDownBy: [] },
    isVerified: true, createdAt: new Date(), updatedAt: new Date()
  },
  {
    courseId: 1, studentId: 3, enrollmentId: 10, rating: 4.5,
    review: {
      title: "Rất phù hợp cho người mới",
      content: "Mình chưa biết gì về lập trình web nhưng sau khóa học này đã tự làm được 1 project nhỏ.",
      pros: ["Dành cho người mới", "Bài giảng từng bước"], cons: []
    },
    helpful: { upvotes: 15, downvotes: 0, votedBy: [], votedUpBy: [], votedDownBy: [] },
    isVerified: true, createdAt: new Date(), updatedAt: new Date()
  },
  // Khóa 2 – 2 review
  {
    courseId: 2, studentId: 1, enrollmentId: 2, rating: 4.5,
    review: {
      title: "Figma thật sự dễ học",
      content: "Trước đây mình toàn dùng Photoshop, giờ chuyển sang Figma thấy tiện hơn nhiều. Cảm ơn giảng viên!",
      pros: ["Dễ tiếp cận", "Nhiều ví dụ thực tế"], cons: ["Thiếu phần Auto Layout nâng cao"]
    },
    helpful: { upvotes: 6, downvotes: 0, votedBy: [], votedUpBy: [], votedDownBy: [] },
    isVerified: true, createdAt: new Date(), updatedAt: new Date()
  },
  {
    courseId: 2, studentId: 4, enrollmentId: 14, rating: 5.0,
    review: {
      title: "Khóa design hay nhất mình từng học",
      content: "Giảng viên chia sẻ rất nhiều kinh nghiệm thực chiến, không chỉ lý thuyết suông.",
      pros: ["Thực chiến", "Giảng viên chuyên nghiệp", "Cộng đồng hỗ trợ tốt"], cons: []
    },
    helpful: { upvotes: 10, downvotes: 0, votedBy: [], votedUpBy: [], votedDownBy: [] },
    isVerified: true, createdAt: new Date(), updatedAt: new Date()
  },
  // Khóa 3 – 2 review
  {
    courseId: 3, studentId: 2, enrollmentId: 7, rating: 4.5,
    review: {
      title: "Kiến thức nâng cao và thực tế",
      content: "Khóa học đi sâu vào Spring Boot và JPA, rất phù hợp cho những bạn muốn lên trình độ Senior.",
      pros: ["Giảng viên có tâm", "Kiến thức sâu"], cons: ["Hơi khó với người mới"]
    },
    helpful: { upvotes: 5, downvotes: 0, votedBy: [], votedUpBy: [], votedDownBy: [] },
    isVerified: true, createdAt: new Date(), updatedAt: new Date()
  },
  {
    courseId: 3, studentId: 1, enrollmentId: 3, rating: 4.0,
    review: {
      title: "Cần có kiến thức Java trước",
      content: "Khóa này nâng cao thật sự, ai chưa biết Java Core thì sẽ khó theo. Nhưng nếu đã có nền tảng thì rất bổ ích.",
      pros: ["Nội dung chuyên sâu"], cons: ["Yêu cầu kiến thức nền cao", "Cần thêm bài tập nhỏ"]
    },
    helpful: { upvotes: 3, downvotes: 1, votedBy: [], votedUpBy: [], votedDownBy: [] },
    isVerified: true, createdAt: new Date(), updatedAt: new Date()
  },
  // Khóa 4 – 2 review
  {
    courseId: 4, studentId: 1, enrollmentId: 4, rating: 5.0,
    review: {
      title: "Tuyệt vời cho người muốn học Data Science",
      content: "Thầy Phạm Văn C dạy rất hay, từ Python cơ bản đến Pandas nâng cao đều rất rõ ràng.",
      pros: ["Logic rõ ràng", "Nhiều bài tập thực hành", "Dataset thực tế"], cons: []
    },
    helpful: { upvotes: 20, downvotes: 0, votedBy: [], votedUpBy: [], votedDownBy: [] },
    isVerified: true, createdAt: new Date(), updatedAt: new Date()
  },
  {
    courseId: 4, studentId: 3, enrollmentId: 11, rating: 4.5,
    review: {
      title: "Khóa học thay đổi career path của mình",
      content: "Mình đang làm IT Support, nhờ khóa này mình đã chuyển sang được vị trí Data Analyst.",
      pros: ["Thực tiễn", "Hỗ trợ sau khóa học"], cons: ["Phần visualization cần sâu hơn"]
    },
    helpful: { upvotes: 18, downvotes: 0, votedBy: [], votedUpBy: [], votedDownBy: [] },
    isVerified: true, createdAt: new Date(), updatedAt: new Date()
  },
  // Khóa 5 – 1 review
  {
    courseId: 5, studentId: 2, enrollmentId: 8, rating: 4.0,
    review: {
      title: "Flutter rất mạnh mẽ",
      content: "Sau khóa học mình đã tự publish được app đầu tiên lên Google Play. Rất biết ơn giảng viên!",
      pros: ["Thực hành nhiều", "Cập nhật Flutter mới nhất"], cons: ["Phần state management cần chi tiết hơn"]
    },
    helpful: { upvotes: 7, downvotes: 0, votedBy: [], votedUpBy: [], votedDownBy: [] },
    isVerified: true, createdAt: new Date(), updatedAt: new Date()
  },
  // Khóa 7 (FREE) – 1 review
  {
    courseId: 7, studentId: 4, enrollmentId: 16, rating: 5.0,
    review: {
      title: "Khóa miễn phí mà chất lượng không thua kém",
      content: "Không ngờ khóa miễn phí mà hay đến vậy. Mình đã tự tin hơn rất nhiều khi thuyết trình trước lớp.",
      pros: ["Miễn phí", "Thực tế", "Nhiều mẹo hay"], cons: []
    },
    helpful: { upvotes: 25, downvotes: 0, votedBy: [], votedUpBy: [], votedDownBy: [] },
    isVerified: true, createdAt: new Date(), updatedAt: new Date()
  },
  // Khóa 8 – 1 review
  {
    courseId: 8, studentId: 5, enrollmentId: 19, rating: 4.5,
    review: {
      title: "Nền tảng ML vững chắc",
      content: "Khóa học giúp mình hiểu rõ từng thuật toán thay vì chỉ copy code. Rất đáng tiền!",
      pros: ["Giải thích toán rõ ràng", "Bài tập thực tế"], cons: ["Cần thêm phần Deep Learning"]
    },
    helpful: { upvotes: 9, downvotes: 0, votedBy: [], votedUpBy: [], votedDownBy: [] },
    isVerified: true, createdAt: new Date(), updatedAt: new Date()
  }
]);

// ═══════════════════════════════════════════════════════════════
// 3. STUDENT PROGRESS (8 bản ghi tiến độ)
// ═══════════════════════════════════════════════════════════════
db.student_progress.insertMany([
  // Student 1 – Khóa 1 (đã hoàn thành 75%)
  {
    studentId: 1, courseId: 1, enrollmentId: 1,
    progress: {
      completedLessons: ["M001_L001", "M001_Q001", "M001_Q002"],
      currentLesson: "M002_A001",
      overallProgress: 75.0,
      totalWatchTime: 2700,
      lastAccessedAt: new Date()
    },
    lessonProgress: [
      { lessonId: "M001_L001", status: "completed", watchedDuration: 1200, totalDuration: 1200, progressPercent: 100.0, attemptCount: 1, maxAttempts: 5, lastAccessedAt: new Date(), completedAt: new Date() },
      { lessonId: "M001_Q001", status: "completed", watchedDuration: 300, totalDuration: 600, progressPercent: 100.0, attemptCount: 1, maxAttempts: 5, score: 100, lastAccessedAt: new Date(), completedAt: new Date() },
      { lessonId: "M001_Q002", status: "completed", watchedDuration: 400, totalDuration: 900, progressPercent: 100.0, attemptCount: 2, maxAttempts: 5, score: 75, lastAccessedAt: new Date(), completedAt: new Date() }
    ],
    updatedAt: new Date()
  },
  // Student 1 – Khóa 2 (đã hoàn thành 50%)
  {
    studentId: 1, courseId: 2, enrollmentId: 2,
    progress: {
      completedLessons: ["M001_L001", "M001_Q001"],
      currentLesson: "M002_Q001",
      overallProgress: 50.0,
      totalWatchTime: 1000,
      lastAccessedAt: new Date()
    },
    lessonProgress: [
      { lessonId: "M001_L001", status: "completed", watchedDuration: 1000, totalDuration: 1000, progressPercent: 100.0, attemptCount: 1, maxAttempts: 5, lastAccessedAt: new Date(), completedAt: new Date() },
      { lessonId: "M001_Q001", status: "completed", watchedDuration: 250, totalDuration: 500, progressPercent: 100.0, attemptCount: 1, maxAttempts: 5, score: 100, lastAccessedAt: new Date(), completedAt: new Date() }
    ],
    updatedAt: new Date()
  },
  // Student 2 – Khóa 1 (mới bắt đầu 25%)
  {
    studentId: 2, courseId: 1, enrollmentId: 6,
    progress: {
      completedLessons: ["M001_L001"],
      currentLesson: "M001_Q001",
      overallProgress: 25.0,
      totalWatchTime: 1200,
      lastAccessedAt: new Date()
    },
    lessonProgress: [
      { lessonId: "M001_L001", status: "completed", watchedDuration: 1200, totalDuration: 1200, progressPercent: 100.0, attemptCount: 1, maxAttempts: 5, lastAccessedAt: new Date(), completedAt: new Date() }
    ],
    updatedAt: new Date()
  },
  // Student 2 – Khóa 5 Flutter (đang học 40%)
  {
    studentId: 2, courseId: 5, enrollmentId: 8,
    progress: {
      completedLessons: ["M001_L001", "M001_Q001"],
      currentLesson: "M002_L001",
      overallProgress: 40.0,
      totalWatchTime: 1500,
      lastAccessedAt: new Date()
    },
    lessonProgress: [
      { lessonId: "M001_L001", status: "completed", watchedDuration: 1500, totalDuration: 1500, progressPercent: 100.0, attemptCount: 1, maxAttempts: 5, lastAccessedAt: new Date(), completedAt: new Date() },
      { lessonId: "M001_Q001", status: "completed", watchedDuration: 300, totalDuration: 600, progressPercent: 100.0, attemptCount: 1, maxAttempts: 5, score: 100, lastAccessedAt: new Date(), completedAt: new Date() }
    ],
    updatedAt: new Date()
  },
  // Student 3 – Khóa 4 Python (đã hoàn thành 100%)
  {
    studentId: 3, courseId: 4, enrollmentId: 11,
    progress: {
      completedLessons: ["M001_L001", "M001_Q001", "M002_L001", "M002_Q001", "M002_A001"],
      currentLesson: null,
      overallProgress: 100.0,
      totalWatchTime: 2400,
      lastAccessedAt: new Date()
    },
    lessonProgress: [
      { lessonId: "M001_L001", status: "completed", watchedDuration: 900, totalDuration: 900, progressPercent: 100.0, attemptCount: 1, maxAttempts: 5, lastAccessedAt: new Date(), completedAt: new Date() },
      { lessonId: "M001_Q001", status: "completed", watchedDuration: 400, totalDuration: 600, progressPercent: 100.0, attemptCount: 1, maxAttempts: 5, score: 100, lastAccessedAt: new Date(), completedAt: new Date() },
      { lessonId: "M002_L001", status: "completed", watchedDuration: 1500, totalDuration: 1500, progressPercent: 100.0, attemptCount: 1, maxAttempts: 5, lastAccessedAt: new Date(), completedAt: new Date() },
      { lessonId: "M002_Q001", status: "completed", watchedDuration: 350, totalDuration: 600, progressPercent: 100.0, attemptCount: 2, maxAttempts: 5, score: 80, lastAccessedAt: new Date(), completedAt: new Date() },
      { lessonId: "M002_A001", status: "completed", watchedDuration: 0, totalDuration: 5400, progressPercent: 100.0, attemptCount: 1, maxAttempts: 5, score: 90, lastAccessedAt: new Date(), completedAt: new Date() }
    ],
    updatedAt: new Date()
  },
  // Student 4 – Khóa 5 Flutter (mới bắt đầu)
  {
    studentId: 4, courseId: 5, enrollmentId: 15,
    progress: {
      completedLessons: ["M001_L001"],
      currentLesson: "M001_Q001",
      overallProgress: 20.0,
      totalWatchTime: 1500,
      lastAccessedAt: new Date()
    },
    lessonProgress: [
      { lessonId: "M001_L001", status: "completed", watchedDuration: 1500, totalDuration: 1500, progressPercent: 100.0, attemptCount: 1, maxAttempts: 5, lastAccessedAt: new Date(), completedAt: new Date() }
    ],
    updatedAt: new Date()
  },
  // Student 5 – Khóa 4 Python (đang học 60%)
  {
    studentId: 5, courseId: 4, enrollmentId: 18,
    progress: {
      completedLessons: ["M001_L001", "M001_Q001", "M002_L001"],
      currentLesson: "M002_Q001",
      overallProgress: 60.0,
      totalWatchTime: 2400,
      lastAccessedAt: new Date()
    },
    lessonProgress: [
      { lessonId: "M001_L001", status: "completed", watchedDuration: 900, totalDuration: 900, progressPercent: 100.0, attemptCount: 1, maxAttempts: 5, lastAccessedAt: new Date(), completedAt: new Date() },
      { lessonId: "M001_Q001", status: "completed", watchedDuration: 500, totalDuration: 600, progressPercent: 100.0, attemptCount: 3, maxAttempts: 5, score: 67, lastAccessedAt: new Date(), completedAt: new Date() },
      { lessonId: "M002_L001", status: "completed", watchedDuration: 1500, totalDuration: 1500, progressPercent: 100.0, attemptCount: 1, maxAttempts: 5, lastAccessedAt: new Date(), completedAt: new Date() }
    ],
    updatedAt: new Date()
  },
  // Student 5 – Khóa 8 ML (vừa mới bắt đầu)
  {
    studentId: 5, courseId: 8, enrollmentId: 19,
    progress: {
      completedLessons: [],
      currentLesson: "M001_L001",
      overallProgress: 0.0,
      totalWatchTime: 0,
      lastAccessedAt: new Date()
    },
    lessonProgress: [],
    updatedAt: new Date()
  }
]);

// ═══════════════════════════════════════════════════════════════
// 4. DISCUSSIONS (5 thảo luận)
// ═══════════════════════════════════════════════════════════════
db.discussions.insertMany([
  {
    discussionId: 1, courseId: 1, lessonId: "M001_L001",
    authorId: 1, authorRole: "STUDENT",
    title: "Lỗi thư viện khi khởi tạo dự án React",
    content: "Mình chạy lệnh npx create-react-app báo lỗi thư viện, ai giúp mình với ạ?",
    codeSnippet: "npm ERR! code ENOTFOUND",
    tags: ["react", "error", "npm"],
    replies: [
      { replyId: 1, authorId: 1, authorRole: "TEACHER", content: "Em thử xóa bộ nhớ đệm (cache) của npm bằng lệnh: `npm cache clean --force` rồi thử lại nhé.", isAccepted: true, upvotes: 5, createdAt: new Date() }
    ],
    views: 120, upvotes: 2, isPinned: true, isSolved: true,
    createdAt: new Date(), updatedAt: new Date()
  },
  {
    discussionId: 2, courseId: 1, lessonId: "M001_Q001",
    authorId: 2, authorRole: "STUDENT",
    title: "Câu hỏi về Virtual DOM",
    content: "Cho mình hỏi Virtual DOM hoạt động cụ thể như nào? Tại sao nó lại nhanh hơn Real DOM?",
    codeSnippet: null,
    tags: ["react", "virtual-dom", "performance"],
    replies: [
      { replyId: 1, authorId: 1, authorRole: "TEACHER", content: "Virtual DOM là bản copy nhẹ của Real DOM. Khi state thay đổi, React so sánh Virtual DOM mới với cũ (diffing), rồi chỉ cập nhật phần thay đổi lên Real DOM (reconciliation). Nhờ vậy tránh re-render toàn bộ trang.", isAccepted: true, upvotes: 12, createdAt: new Date() },
      { replyId: 2, authorId: 3, authorRole: "STUDENT", content: "Cảm ơn thầy, giờ mình hiểu rồi ạ!", isAccepted: false, upvotes: 1, createdAt: new Date() }
    ],
    views: 250, upvotes: 8, isPinned: false, isSolved: true,
    createdAt: new Date(), updatedAt: new Date()
  },
  {
    discussionId: 3, courseId: 4, lessonId: "M002_L001",
    authorId: 3, authorRole: "STUDENT",
    title: "Lỗi khi import Pandas trên Mac M1",
    content: "Mình dùng Macbook M1, pip install pandas thành công nhưng import pandas báo lỗi. Có ai gặp tình huống tương tự không?",
    codeSnippet: "ImportError: dlopen(/opt/homebrew/lib/python3.11/site-packages/pandas/_libs/lib.cpython-311-darwin.so, 0x0002)",
    tags: ["python", "pandas", "mac-m1"],
    replies: [
      { replyId: 1, authorId: 3, authorRole: "TEACHER", content: "Em thử cài lại bằng conda thay vì pip nhé: `conda install pandas`. Conda xử lý dependency tốt hơn trên ARM chip.", isAccepted: true, upvotes: 8, createdAt: new Date() }
    ],
    views: 180, upvotes: 5, isPinned: false, isSolved: true,
    createdAt: new Date(), updatedAt: new Date()
  },
  {
    discussionId: 4, courseId: 5, lessonId: "M002_L001",
    authorId: 2, authorRole: "STUDENT",
    title: "StatefulWidget vs StatelessWidget khi nào dùng?",
    content: "Mình vẫn chưa rõ khi nào nên dùng StatefulWidget và khi nào dùng StatelessWidget. Có quy tắc nào không ạ?",
    codeSnippet: null,
    tags: ["flutter", "widget", "state"],
    replies: [
      { replyId: 1, authorId: 4, authorRole: "TEACHER", content: "Quy tắc đơn giản: Nếu widget CẦN thay đổi giao diện khi có tương tác (ví dụ: bấm nút đổi màu, toggle switch) → StatefulWidget. Nếu widget CHỈ hiển thị dữ liệu tĩnh → StatelessWidget. Tuy nhiên với state management (Provider, Bloc), bạn có thể dùng StatelessWidget gần như mọi lúc.", isAccepted: true, upvotes: 15, createdAt: new Date() },
      { replyId: 2, authorId: 4, authorRole: "STUDENT", content: "Vậy nếu dùng Provider thì hầu như không cần StatefulWidget luôn hả thầy?", isAccepted: false, upvotes: 3, createdAt: new Date() }
    ],
    views: 320, upvotes: 12, isPinned: true, isSolved: true,
    createdAt: new Date(), updatedAt: new Date()
  },
  {
    discussionId: 5, courseId: 3, lessonId: "M001_L001",
    authorId: 4, authorRole: "STUDENT",
    title: "Spring Boot 3 có gì khác Spring Boot 2?",
    content: "Thầy ơi, khóa học dùng Spring Boot 3 hay 2 ạ? Em nghe nói SB3 thay đổi nhiều, đặc biệt là về namespace javax → jakarta.",
    codeSnippet: null,
    tags: ["spring-boot", "java", "migration"],
    replies: [
      { replyId: 1, authorId: 1, authorRole: "TEACHER", content: "Khóa học dùng Spring Boot 3.x nhé em. Đúng rồi, thay đổi lớn nhất là chuyển từ javax.* sang jakarta.*. Ngoài ra còn yêu cầu Java 17+ và có nhiều cải tiến về performance. Em cứ theo khóa học là sẽ nắm được hết.", isAccepted: true, upvotes: 6, createdAt: new Date() }
    ],
    views: 95, upvotes: 3, isPinned: false, isSolved: true,
    createdAt: new Date(), updatedAt: new Date()
  }
]);

// ═══════════════════════════════════════════════════════════════
// 5. POSTS (5 bài viết blog)
// ═══════════════════════════════════════════════════════════════
db.posts.insertMany([
  {
    title: "Lộ trình trở thành Fullstack Developer 2024",
    content: "Trong bài viết này, chúng ta sẽ tìm hiểu những kỹ năng cần thiết để trở thành Fullstack Developer. Bắt đầu từ HTML/CSS/JS, sau đó học React cho Frontend và Spring Boot cho Backend. Đừng quên database (PostgreSQL, MongoDB) và DevOps (Docker, CI/CD).",
    category: "Career",
    type: "blog",
    views: 850,
    likes: ["user2", "user4", "user8", "user9"],
    comments: [
      { commentId: "C1", userId: "4", authorName: "Lê Thị C", content: "Bài viết rất hữu ích, cảm ơn ad!", createdAt: new Date() },
      { commentId: "C2", userId: "5", authorName: "Hoàng Văn D", content: "Cho mình hỏi nên học React hay Vue trước ạ?", createdAt: new Date() }
    ],
    tags: ["Fullstack", "Career", "2024"],
    userId: "1", authorName: "Admin", authorRole: "ADMIN",
    createdAt: new Date(), updatedAt: new Date(), pinned: true
  },
  {
    title: "5 mẹo viết code sạch mà lập trình viên nào cũng nên biết",
    content: "Clean code không chỉ là convention mà còn là mindset. 1) Đặt tên biến có ý nghĩa. 2) Hàm chỉ làm 1 việc. 3) Tránh magic number. 4) Viết comment giải thích 'tại sao' chứ không phải 'làm gì'. 5) Refactor thường xuyên.",
    category: "Tips",
    type: "blog",
    views: 1200,
    likes: ["user4", "user5", "user8", "user9", "user10"],
    comments: [
      { commentId: "C1", userId: "8", authorName: "Nguyễn Thị E", content: "Mẹo số 4 rất hay, mình hay mắc lỗi comment thừa!", createdAt: new Date() },
      { commentId: "C2", userId: "9", authorName: "Trần Văn F", content: "Bổ sung thêm: nên viết unit test nữa ạ.", createdAt: new Date() },
      { commentId: "C3", userId: "4", authorName: "Lê Thị C", content: "Đồng ý! Clean code giúp teamwork hiệu quả hơn nhiều.", createdAt: new Date() }
    ],
    tags: ["Clean Code", "Best Practice", "Tips"],
    userId: "2", authorName: "Nguyễn Văn A", authorRole: "TEACHER",
    createdAt: new Date(), updatedAt: new Date(), pinned: false
  },
  {
    title: "So sánh Flutter vs React Native trong năm 2024",
    content: "Flutter và React Native đều là framework phổ biến để phát triển ứng dụng mobile đa nền tảng. Flutter dùng Dart, compile sang native code nên performance tốt hơn. React Native dùng JavaScript, cộng đồng lớn hơn. Tùy vào project mà chọn framework phù hợp.",
    category: "Technology",
    type: "blog",
    views: 650,
    likes: ["user5", "user9"],
    comments: [
      { commentId: "C1", userId: "5", authorName: "Hoàng Văn D", content: "Mình thích Flutter hơn vì UI đẹp và nhất quán trên cả 2 nền tảng.", createdAt: new Date() }
    ],
    tags: ["Flutter", "React Native", "Mobile"],
    userId: "7", authorName: "Lê Văn D", authorRole: "TEACHER",
    createdAt: new Date(), updatedAt: new Date(), pinned: false
  },
  {
    title: "Kinh nghiệm phỏng vấn vị trí Junior Developer",
    content: "Chia sẻ kinh nghiệm sau 5 buổi phỏng vấn: 1) Nắm vững OOP và Data Structure. 2) Chuẩn bị 2-3 project cá nhân để demo. 3) Luyện giải thuật trên LeetCode. 4) Soft skills cũng rất quan trọng. 5) Đừng sợ nói 'Em chưa biết nhưng sẽ tìm hiểu'.",
    category: "Career",
    type: "blog",
    views: 2100,
    likes: ["user4", "user5", "user8", "user9", "user10"],
    comments: [
      { commentId: "C1", userId: "4", authorName: "Lê Thị C", content: "Cảm ơn bạn chia sẻ, rất bổ ích cho sinh viên như mình!", createdAt: new Date() },
      { commentId: "C2", userId: "10", authorName: "Đặng Văn G", content: "Mình cũng đang chuẩn bị phỏng vấn, bài viết đúng lúc quá!", createdAt: new Date() },
      { commentId: "C3", userId: "8", authorName: "Nguyễn Thị E", content: "LeetCode thật sự rất cần thiết, nên luyện ít nhất 2 bài/ngày.", createdAt: new Date() }
    ],
    tags: ["Interview", "Career", "Junior Developer"],
    userId: "4", authorName: "Lê Thị C", authorRole: "STUDENT",
    createdAt: new Date(), updatedAt: new Date(), pinned: true
  },
  {
    title: "Tại sao Data Science là nghề hot nhất hiện nay?",
    content: "Với lượng dữ liệu ngày càng lớn, nhu cầu về Data Scientist tăng vọt. Theo LinkedIn, Data Science nằm trong top 3 nghề được tuyển dụng nhiều nhất. Mức lương trung bình cho fresher đã từ 15-25 triệu VNĐ. Hãy bắt đầu học Python và SQL ngay hôm nay!",
    category: "Career",
    type: "blog",
    views: 980,
    likes: ["user8", "user10"],
    comments: [
      { commentId: "C1", userId: "10", authorName: "Đặng Văn G", content: "Mình đang chuyển từ IT Support sang Data, bài viết rất motivating!", createdAt: new Date() }
    ],
    tags: ["Data Science", "Career", "Python"],
    userId: "6", authorName: "Phạm Văn C", authorRole: "TEACHER",
    createdAt: new Date(), updatedAt: new Date(), pinned: false
  }
]);

// ═══════════════════════════════════════════════════════════════
// 6. NOTIFICATIONS (10 thông báo)
// ═══════════════════════════════════════════════════════════════
db.notifications.insertMany([
  // Thông báo hệ thống cho tất cả sinh viên
  { userId: 4, title: "Chào mừng bạn mới", message: "Chào mừng Lê Thị C đến với hệ thống LMS. Chúc bạn học tập tốt!", type: "system", link: "/", read: true, createdAt: new Date(Date.now() - 30*24*60*60*1000), _class: "com.example.lms_api.entity.Notification" },
  { userId: 5, title: "Chào mừng bạn mới", message: "Chào mừng Hoàng Văn D đến với hệ thống LMS. Chúc bạn học tập tốt!", type: "system", link: "/", read: true, createdAt: new Date(Date.now() - 28*24*60*60*1000), _class: "com.example.lms_api.entity.Notification" },
  { userId: 8, title: "Chào mừng bạn mới", message: "Chào mừng Nguyễn Thị E đến với hệ thống LMS. Chúc bạn học tập tốt!", type: "system", link: "/", read: false, createdAt: new Date(Date.now() - 22*24*60*60*1000), _class: "com.example.lms_api.entity.Notification" },
  { userId: 9, title: "Chào mừng bạn mới", message: "Chào mừng Trần Văn F đến với hệ thống LMS. Chúc bạn học tập tốt!", type: "system", link: "/", read: false, createdAt: new Date(Date.now() - 20*24*60*60*1000), _class: "com.example.lms_api.entity.Notification" },
  { userId: 10, title: "Chào mừng bạn mới", message: "Chào mừng Đặng Văn G đến với hệ thống LMS. Chúc bạn học tập tốt!", type: "system", link: "/", read: false, createdAt: new Date(Date.now() - 16*24*60*60*1000), _class: "com.example.lms_api.entity.Notification" },

  // Thông báo thanh toán
  { userId: 4, title: "Thanh toán thành công", message: "Bạn đã thanh toán thành công khóa học: Khóa học Fullstack Web Development", type: "PAYMENT", link: "course://1", read: true, createdAt: new Date(Date.now() - 30*24*60*60*1000), _class: "com.example.lms_api.entity.Notification" },
  { userId: 4, title: "Thanh toán thành công", message: "Bạn đã thanh toán thành công khóa học: Python cho Data Science", type: "PAYMENT", link: "course://4", read: true, createdAt: new Date(Date.now() - 10*24*60*60*1000), _class: "com.example.lms_api.entity.Notification" },

  // Thông báo khóa học mới
  { userId: 4, title: "Khóa học mới!", message: "Khóa học 'Machine Learning cơ bản với Python' vừa được mở. Đăng ký ngay để nhận ưu đãi!", type: "COURSE", link: "course://8", read: false, createdAt: new Date(Date.now() - 5*24*60*60*1000), _class: "com.example.lms_api.entity.Notification" },
  { userId: 5, title: "Khóa học mới!", message: "Khóa học 'English for IT Professionals' vừa được mở. Nâng cao tiếng Anh chuyên ngành IT!", type: "COURSE", link: "course://10", read: false, createdAt: new Date(Date.now() - 3*24*60*60*1000), _class: "com.example.lms_api.entity.Notification" },

  // Thông báo nhắc nhở học tập
  { userId: 4, title: "Nhắc nhở học tập", message: "Bạn chưa hoàn thành bài Assignment trong khóa 'Fullstack Web'. Hãy cố gắng nhé!", type: "REMINDER", link: "course://1", read: false, createdAt: new Date(Date.now() - 1*24*60*60*1000), _class: "com.example.lms_api.entity.Notification" }
]);
