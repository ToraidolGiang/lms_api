package com.example.lms_api.service.impl;


import com.example.lms_api.dto.request.course_content_request.CourseContentRequest;
import com.example.lms_api.dto.request.course_content_request.LessonRequest;
import com.example.lms_api.dto.request.course_content_request.ModuleRequest;
import com.example.lms_api.dto.response.course_content_response.CourseContentResponse;
import com.example.lms_api.entity.CourseContent;
import com.example.lms_api.entity.CourseMetadata;
import com.example.lms_api.entity.Lesson;
import com.example.lms_api.entity.Module;
import com.example.lms_api.mapper.CourseContentMapper;
import com.example.lms_api.repository.CourseContentRepository;
import com.example.lms_api.service.CourseContentService;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseContentServiceImpl implements CourseContentService {

    private final CourseContentRepository repository;
    private final CourseContentMapper mapper;

    // ──────────────────────────────────────────────────────────
    // CRUD COURSE CONTENT
    // ──────────────────────────────────────────────────────────

    @Override
    public CourseContentResponse createCourseContent(Integer courseId, CourseContentRequest request) {
        if (repository.existsByCourseId(courseId)) {
            throw new RuntimeException("Nội dung cho khóa học ID " + courseId + " đã tồn tại");
        }

        CourseContent entity = mapper.toEntity(request);
        entity.setCourseId(courseId);

        // Sinh ID cho modules và lessons
        if (request.getModules() != null) {
            List<Module> modules = buildModulesFromRequest(request.getModules());
            entity.setModules(modules);
        }

        entity.setMetadata(buildMetadata(entity.getModules()));
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public CourseContentResponse getCourseContent(Integer courseId) {
        CourseContent entity = findByCourseId(courseId);
        return mapper.toResponse(entity);
    }

    @Override
    public CourseContentResponse updateCourseContent(Integer courseId, CourseContentRequest request) {
        CourseContent entity = findByCourseId(courseId);

        // Cập nhật thông tin tổng quan
        mapper.updateEntityFromRequest(request, entity);

        // Cập nhật modules nếu có truyền lên
        if (request.getModules() != null) {
            List<Module> modules = buildModulesFromRequest(request.getModules());
            entity.setModules(modules);
        }

        entity.setMetadata(buildMetadata(entity.getModules()));
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public void deleteCourseContent(Integer courseId) {
        if (!repository.existsByCourseId(courseId)) {
            throw new RuntimeException("Không tìm thấy nội dung khóa học ID: " + courseId);
        }
        repository.deleteByCourseId(courseId);
    }

    // ──────────────────────────────────────────────────────────
    // CRUD MODULE
    // ──────────────────────────────────────────────────────────

    @Override
    public CourseContentResponse addModule(Integer courseId, ModuleRequest request) {
        CourseContent entity = findByCourseId(courseId);

        List<Module> modules = entity.getModules();
        if (modules == null) modules = new ArrayList<>();

        // Sinh moduleId tự động: M001, M002...
        String moduleId = generateModuleId(modules);

        Module newModule = Module.builder()
                .moduleId(moduleId)
                .title(request.getTitle())
                .orderIndex(request.getOrderIndex())
                .lessons(new ArrayList<>())
                .build();

        if (request.getLessons() != null) {
            List<Lesson> lessons = buildLessonsFromRequest(moduleId, request.getLessons());
            newModule.setLessons(lessons);
        }

        modules.add(newModule);
        entity.setModules(modules);
        entity.setMetadata(buildMetadata(modules));

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public CourseContentResponse updateModule(Integer courseId, String moduleId, ModuleRequest request) {
        CourseContent entity = findByCourseId(courseId);

        Module module = findModule(entity, moduleId);
        module.setTitle(request.getTitle());
        module.setOrderIndex(request.getOrderIndex());

        // Cập nhật lessons nếu có
        if (request.getLessons() != null) {
            List<Lesson> lessons = buildLessonsFromRequest(moduleId, request.getLessons());
            module.setLessons(lessons);
        }

        entity.setMetadata(buildMetadata(entity.getModules()));
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public CourseContentResponse deleteModule(Integer courseId, String moduleId) {
        CourseContent entity = findByCourseId(courseId);

        List<Module> modules = entity.getModules();
        modules.removeIf(m -> m.getModuleId().equals(moduleId));
        entity.setModules(modules);
        entity.setMetadata(buildMetadata(modules));

        return mapper.toResponse(repository.save(entity));
    }

    // ──────────────────────────────────────────────────────────
    // CRUD LESSON
    // ──────────────────────────────────────────────────────────

    @Override
    public CourseContentResponse addLesson(Integer courseId, String moduleId, LessonRequest request) {
        CourseContent entity = findByCourseId(courseId);
        Module module = findModule(entity, moduleId);

        List<Lesson> lessons = module.getLessons();
        if (lessons == null) lessons = new ArrayList<>();

        // Sinh lessonId tự động: M001_L001, M001_L002...
        String lessonId = generateLessonId(moduleId, lessons);

        Lesson lesson = Lesson.builder()
                .lessonId(lessonId)
                .title(request.getTitle())
                .type(request.getType())
                .orderIndex(request.getOrderIndex())
                .duration(request.getDuration() != null ? request.getDuration() : 0)
                .content(request.getContent())
                .isPreview(request.getIsPreview())
                .build();

        lessons.add(lesson);
        module.setLessons(lessons);
        entity.setMetadata(buildMetadata(entity.getModules()));

        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public CourseContentResponse updateLesson(Integer courseId, String moduleId,
                                              String lessonId, LessonRequest request) {
        CourseContent entity = findByCourseId(courseId);
        Module module = findModule(entity, moduleId);
        Lesson lesson = findLesson(module, lessonId);

        lesson.setTitle(request.getTitle());
        lesson.setType(request.getType());
        lesson.setOrderIndex(request.getOrderIndex());
        if (request.getDuration() != null) lesson.setDuration(request.getDuration());
        if (request.getContent() != null) lesson.setContent(request.getContent());
        if (request.getIsPreview() != null) lesson.setIsPreview(request.getIsPreview());

        entity.setMetadata(buildMetadata(entity.getModules()));
        return mapper.toResponse(repository.save(entity));
    }

    @Override
    public CourseContentResponse deleteLesson(Integer courseId, String moduleId, String lessonId) {
        CourseContent entity = findByCourseId(courseId);
        Module module = findModule(entity, moduleId);

        module.getLessons().removeIf(l -> l.getLessonId().equals(lessonId));
        entity.setMetadata(buildMetadata(entity.getModules()));

        return mapper.toResponse(repository.save(entity));
    }

    // ──────────────────────────────────────────────────────────
    // HELPER METHODS
    // ──────────────────────────────────────────────────────────

    private CourseContent findByCourseId(Integer courseId) {
        return repository.findByCourseId(courseId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy nội dung khóa học ID: " + courseId));
    }

    private Module findModule(CourseContent entity, String moduleId) {
        return entity.getModules().stream()
                .filter(m -> m.getModuleId().equals(moduleId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy module ID: " + moduleId));
    }

    private Lesson findLesson(Module module, String lessonId) {
        return module.getLessons().stream()
                .filter(l -> l.getLessonId().equals(lessonId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy lesson ID: " + lessonId));
    }

    // Sinh moduleId theo format M001, M002...
    private String generateModuleId(List<Module> modules) {
        int next = modules.size() + 1;
        return String.format("M%03d", next);
    }

    // Sinh lessonId theo format M001_L001, M001_L002...
    private String generateLessonId(String moduleId, List<Lesson> lessons) {
        int next = lessons.size() + 1;
        return String.format("%s_L%03d", moduleId, next);
    }

    // Build danh sách Module từ request — tự sinh ID
    private List<Module> buildModulesFromRequest(List<ModuleRequest> moduleRequests) {
        List<Module> modules = new ArrayList<>();
        for (int i = 0; i < moduleRequests.size(); i++) {
            ModuleRequest req = moduleRequests.get(i);
            String moduleId = req.getModuleId() != null && !req.getModuleId().isEmpty()
                    ? req.getModuleId()
                    : String.format("M%03d", i + 1);

            List<Lesson> lessons = new ArrayList<>();
            if (req.getLessons() != null) {
                lessons = buildLessonsFromRequest(moduleId, req.getLessons());
            }

            modules.add(Module.builder()
                    .moduleId(moduleId)
                    .title(req.getTitle())
                    .orderIndex(req.getOrderIndex())
                    .lessons(lessons)
                    .build());
        }
        return modules;
    }

    // Build danh sách Lesson từ request — tự sinh ID
    private List<Lesson> buildLessonsFromRequest(String moduleId, List<LessonRequest> lessonRequests) {
        List<Lesson> lessons = new ArrayList<>();
        for (int i = 0; i < lessonRequests.size(); i++) {
            LessonRequest req = lessonRequests.get(i);
            String lessonId = req.getLessonId() != null && !req.getLessonId().isEmpty()
                    ? req.getLessonId()
                    : String.format("%s_L%03d", moduleId, i + 1);

            lessons.add(Lesson.builder()
                    .lessonId(lessonId)
                    .title(req.getTitle())
                    .type(req.getType())
                    .orderIndex(req.getOrderIndex())
                    .duration(req.getDuration() != null ? req.getDuration() : 0)
                    .content(req.getContent())
                    .isPreview(req.getIsPreview())
                    .build());
        }
        return lessons;
    }

    // Tính toán lại metadata sau mỗi thay đổi
    private CourseMetadata buildMetadata(List<Module> modules) {
        if (modules == null) {
            return CourseMetadata.builder()
                    .totalLessons(0)
                    .totalDuration(0)
                    .lastUpdated(LocalDateTime.now())
                    .build();
        }

        int totalLessons = modules.stream()
                .mapToInt(m -> m.getLessons() != null ? m.getLessons().size() : 0)
                .sum();

        int totalDuration = modules.stream()
                .flatMap(m -> m.getLessons() != null ? m.getLessons().stream() : java.util.stream.Stream.empty())
                .mapToInt(Lesson::getDuration)
                .sum();

        return CourseMetadata.builder()
                .totalLessons(totalLessons)
                .totalDuration(totalDuration)
                .lastUpdated(LocalDateTime.now())
                .build();
    }
    @Override
    public Integer getTotalLessons(Integer courseId) {
        CourseContent content = repository.findByCourseId(courseId).orElse(null);
        if (content == null) {
            System.out.println(">>> KẾT QUẢ: KHÔNG TÌM THẤY DOCUMENT NÀO! (Có thể sai type Int/String hoặc DB trống)");
            System.out.println("=============================================");
            return 0;
        }
        if (content.getMetadata() == null) {
            System.out.println(">>> LỖI MAPPING: TÌM THẤY DỮ LIỆU NHƯNG TRƯỜNG 'METADATA' BỊ NULL!");
            System.out.println("=============================================");
            return 0;
        }

        Integer lessons = content.getMetadata().getTotalLessons();
        System.out.println(">>> THÀNH CÔNG: TỔNG SỐ BÀI HỌC LÀ: " + lessons);
        System.out.println("=============================================");

        return lessons != null ? lessons : 0;
    }
}