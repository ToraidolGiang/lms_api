package com.example.lms_api.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CreatePostRequest {

    private String title;

    private String content;

    private String category;

    private String type;

    private List<String> tags;

}