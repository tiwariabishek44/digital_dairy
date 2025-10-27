package com.digitaldairy.dto.response;

/**
 * PagedResponse: Generic wrapper for paginated list responses.
 * Includes pagination metadata (page, size, totalElements, totalPages).
 * Used with ApiResponse for consistent paginated endpoints.
 *
 * Usage:
 * ApiResponse<PagedResponse<FarmerDTO>> response = ApiResponse.success(
 *     new PagedResponse<>(farmerList, page, size, totalElements, totalPages),
 *     "Farmers retrieved successfully"
 * );
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {

    private List<T> content;          // Actual data items
    private int page;                 // Current page number (0-indexed)
    private int size;                 // Items per page
    private long totalElements;       // Total items across all pages
    private int totalPages;           // Total number of pages
    private boolean first;            // Is this the first page?
    private boolean last;             // Is this the last page?
    private boolean empty;            // Is content empty?

    /**
     * Constructor from Spring Page object.
     */
    public PagedResponse(List<T> content, org.springframework.data.domain.Page<?> page) {
        this.content = content;
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.empty = page.isEmpty();
    }

    /**
     * Convenience constructor with just the essentials.
     */
    public PagedResponse(List<T> content, int page, int size, long totalElements, int totalPages) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = page == 0;
        this.last = page == totalPages - 1;
        this.empty = content.isEmpty();
    }

    /**
     * Create empty paged response.
     */
    public static <T> PagedResponse<T> empty(int page, int size) {
        return new PagedResponse<>(
                List.of(),
                page,
                size,
                0L,
                0,
                true,
                true,
                true
        );
    }
}