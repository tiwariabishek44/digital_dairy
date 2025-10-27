package com.digitaldairy.controller;

/**
 * BaseController: Abstract base controller with generic response helper methods.
 * All controllers extend this to get consistent response patterns.
 *
 * Provides methods for:
 * - Success responses (with data)
 * - Empty list responses (200 OK with empty array)
 * - Paginated responses
 * - Created responses (201)
 * - No content responses (204)
 *
 * Usage in child controllers:
 * public class FarmerController extends BaseController {
 *     public ResponseEntity<?> getFarmers() {
 *         List<Farmer> farmers = service.getAll();
 *         if (farmers.isEmpty()) {
 *             return okEmptyList("No farmers found");
 *         }
 *         return ok(farmers, "Farmers retrieved successfully");
 *     }
 * }
 */

import com.digitaldairy.dto.response.ApiResponse;
import com.digitaldairy.dto.response.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

public abstract class BaseController {

    /**
     * Standard 200 OK response with data.
     */
    protected <T> ResponseEntity<ApiResponse<T>> ok(T data, String message) {
        return ResponseEntity.ok(ApiResponse.success(data, message));
    }

    /**
     * 200 OK with empty list (for queries that return no results).
     */
    protected <T> ResponseEntity<ApiResponse<List<T>>> okEmptyList(String message) {
        return ResponseEntity.ok(ApiResponse.emptyList(message));
    }

    /**
     * 200 OK with null data (for operations that succeed but return nothing).
     */
    protected <T> ResponseEntity<ApiResponse<T>> okEmpty(String message) {
        return ResponseEntity.ok(ApiResponse.empty(message));
    }

    /**
     * 201 CREATED response (for POST operations).
     */
    protected <T> ResponseEntity<ApiResponse<T>> created(T data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(data, message));
    }

    /**
     * 204 NO CONTENT response (for DELETE operations).
     */
    protected ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    /**
     * Paginated response (200 OK with pagination metadata).
     * Automatically handles empty pages.
     */
    protected <T> ResponseEntity<ApiResponse<PagedResponse<T>>> okPaged(
            Page<T> page,
            String message) {

        if (page.isEmpty()) {
            PagedResponse<T> emptyPage = PagedResponse.empty(page.getNumber(), page.getSize());
            return ResponseEntity.ok(
                    ApiResponse.success(emptyPage, message + " (No results found)")
            );
        }

        PagedResponse<T> pagedResponse = new PagedResponse<>(page.getContent(), page);
        return ResponseEntity.ok(
                ApiResponse.success(pagedResponse,
                        String.format("%s (%d of %d items)", message, page.getNumberOfElements(), page.getTotalElements()))
        );
    }

    /**
     * Paginated response with custom DTO mapping.
     * Use when you need to convert entities to DTOs.
     */
    protected <T, R> ResponseEntity<ApiResponse<PagedResponse<R>>> okPagedMapped(
            Page<T> page,
            List<R> mappedContent,
            String message) {

        if (page.isEmpty()) {
            PagedResponse<R> emptyPage = PagedResponse.empty(page.getNumber(), page.getSize());
            return ResponseEntity.ok(
                    ApiResponse.success(emptyPage, message + " (No results found)")
            );
        }

        PagedResponse<R> pagedResponse = new PagedResponse<>(mappedContent, page);
        return ResponseEntity.ok(
                ApiResponse.success(pagedResponse,
                        String.format("%s (%d of %d items)", message, page.getNumberOfElements(), page.getTotalElements()))
        );
    }

    /**
     * Simple list response (non-paginated).
     * Automatically handles empty lists.
     */
    protected <T> ResponseEntity<ApiResponse<List<T>>> okList(List<T> items, String message) {
        if (items == null || items.isEmpty()) {
            return okEmptyList(message + " (No results found)");
        }
        return ResponseEntity.ok(
                ApiResponse.success(items, String.format("%s (%d items)", message, items.size()))
        );
    }
}