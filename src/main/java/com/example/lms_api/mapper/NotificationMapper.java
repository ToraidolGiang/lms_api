package com.example.lms_api.mapper;

import com.example.lms_api.dto.request.NotificationRequest;
import com.example.lms_api.dto.response.NotificationResponse;
import com.example.lms_api.entity.NotificationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isRead", constant = "false")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    // Map targetUserId từ Request thẳng vào trường userId của Document
    @Mapping(source = "targetUserId", target = "userId")
    NotificationEntity toEntity(NotificationRequest request);

    NotificationResponse toResponse(NotificationEntity entity);
}