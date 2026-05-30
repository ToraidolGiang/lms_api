package com.example.lms_api.repository;

import com.example.lms_api.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class PostSearchRepositoryImpl implements PostSearchRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<Post> search(String query, String category, String sortBy, Pageable pageable) {
        List<AggregationOperation> operations = new ArrayList<>();

        if (category != null && !category.trim().isEmpty()) {
            operations.add(Aggregation.match(Criteria.where("category").is(category)));
        }

        if (query != null && !query.trim().isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("title").regex(query, "i"),
                    Criteria.where("content").regex(query, "i")
            );
            operations.add(Aggregation.match(searchCriteria));
        }

        // 🌟 BỔ SUNG LOGIC SẮP XẾP: Ghép thêm cờ "isPinned" vào tham số đầu tiên của Sort.
        // Do xếp DESC (giảm dần) nên True (1) sẽ luôn đứng trên False (0).
        if ("trending".equalsIgnoreCase(sortBy)) {
            operations.add(Aggregation.project(Post.class)
                    .and(ArrayOperators.Size.lengthOfArray(ConditionalOperators.ifNull("likes").then(Collections.emptyList())))
                    .as("likesCount"));
            operations.add(Aggregation.sort(Sort.Direction.DESC, "isPinned", "likesCount", "createdAt"));

        } else if ("replies".equalsIgnoreCase(sortBy)) {
            operations.add(Aggregation.project(Post.class)
                    .and(ArrayOperators.Size.lengthOfArray(ConditionalOperators.ifNull("comments").then(Collections.emptyList())))
                    .as("commentsCount"));
            operations.add(Aggregation.sort(Sort.Direction.DESC, "isPinned", "commentsCount", "createdAt"));

        } else {
            operations.add(Aggregation.sort(Sort.Direction.DESC, "isPinned", "createdAt"));
        }

        operations.add(Aggregation.skip((long) pageable.getPageNumber() * pageable.getPageSize()));
        operations.add(Aggregation.limit(pageable.getPageSize()));

        Aggregation aggregation = Aggregation.newAggregation(operations);
        List<Post> pagedResults = mongoTemplate.aggregate(aggregation, "posts", Post.class).getMappedResults();

        List<AggregationOperation> countOps = new ArrayList<>();
        if (category != null && !category.trim().isEmpty()) {
            countOps.add(Aggregation.match(Criteria.where("category").is(category)));
        }
        if (query != null && !query.trim().isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("title").regex(query, "i"),
                    Criteria.where("content").regex(query, "i")
            );
            countOps.add(Aggregation.match(searchCriteria));
        }
        countOps.add(Aggregation.count().as("total"));

        Aggregation countAggregation = Aggregation.newAggregation(countOps);
        Long totalCount = 0L;
        Map countResult = mongoTemplate.aggregate(countAggregation, "posts", Map.class).getUniqueMappedResult();
        if (countResult != null && countResult.get("total") != null) {
            totalCount = Long.valueOf(countResult.get("total").toString());
        }

        return new PageImpl<>(pagedResults, pageable, totalCount);
    }
}