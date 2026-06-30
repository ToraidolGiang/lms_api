package com.example.lms_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic wrapper cho phân trang.
 * Trả về cùng cấu trúc cho mọi endpoint có pagination.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {

    /** Danh sách bản ghi của trang hiện tại */
    private List<T> content;

    /** Trang hiện tại (0-indexed) */
    private int page;

    /** Số bản ghi mỗi trang */
    private int size;

    /** Tổng số bản ghi */
    private long totalElements;

    /** Tổng số trang */
    private int totalPages;

    /** Có trang tiếp theo không */
    private boolean hasNext;

    /** Có trang trước không */
    private boolean hasPrevious;

    /** Factory method tiện lợi */
    public static <T> PagedResponse<T> of(List<T> allItems, int page, int size) {
        int total = allItems.size();
        int totalPages = (int) Math.ceil((double) total / size);
        int safePage = Math.max(0, Math.min(page, totalPages - 1));

        int fromIndex = safePage * size;
        int toIndex   = Math.min(fromIndex + size, total);

        List<T> content = (fromIndex >= total)
                ? java.util.Collections.emptyList()
                : allItems.subList(fromIndex, toIndex);

        return PagedResponse.<T>builder()
                .content(content)
                .page(safePage)
                .size(size)
                .totalElements(total)
                .totalPages(totalPages == 0 ? 1 : totalPages)
                .hasNext(safePage < totalPages - 1)
                .hasPrevious(safePage > 0)
                .build();
    }
}
