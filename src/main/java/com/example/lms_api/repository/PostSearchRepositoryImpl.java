package com.example.lms_api.repository;

import com.example.lms_api.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostSearchRepositoryImpl implements PostSearchRepository {

    private final MongoTemplate mongoTemplate;

    public PostSearchRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<Post> search(String q, String category, Pageable pageable) {
        Query query = new Query();

        if (category != null && !category.isBlank()) {
            query.addCriteria(Criteria.where("category").regex("^" + java.util.regex.Pattern.quote(category) + "$", "i"));
        }

        if (q != null && !q.isBlank()) {
            String pattern = java.util.regex.Pattern.quote(q.trim());
            Criteria c = new Criteria().orOperator(
                    Criteria.where("title").regex(pattern, "i"),
                    Criteria.where("content").regex(pattern, "i"),
                    Criteria.where("authorName").regex(pattern, "i"),
                    Criteria.where("tags").regex(pattern, "i")
            );
            query.addCriteria(c);
        }

        long total = mongoTemplate.count(query, Post.class);

        query.with(pageable);
        List<Post> items = mongoTemplate.find(query, Post.class);

        return new PageImpl<>(items, pageable, total);
    }
}
