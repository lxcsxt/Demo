package com.example.studentmanagement;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.studentmanagement.exception.GlobalExceptionHandler;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@DisplayName("全局异常处理器单元测试")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Nested
    @DisplayName("数据约束异常处理测试")
    class HandleDataIntegrityViolationExceptionTest {

        @Test
        @DisplayName("处理数据约束异常 - 当消息包含 student_no 时返回学号已存在")
        void testHandleDataIntegrityViolationException_StudentNoConstraint_ReturnDuplicateMessage() {
            // Given
            DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "Constraint violation on column STUDENT_NO"
            );

            // When
            ResponseEntity<Map<String, Object>> response =
                globalExceptionHandler.handleDataIntegrityViolationException(exception);

            // Then
            assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "状态码应为400"),
                () -> assertNotNull(response.getBody(), "响应体不应为空"),
                () -> assertEquals("学号已存在", response.getBody().get("message"), "错误信息应提示学号重复")
            );
        }

        @Test
        @DisplayName("处理数据约束异常 - 当消息包含 unique 时返回学号已存在")
        void testHandleDataIntegrityViolationException_UniqueConstraint_ReturnDuplicateMessage() {
            // Given
            DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "Unique index or primary key violation"
            );

            // When
            ResponseEntity<Map<String, Object>> response =
                globalExceptionHandler.handleDataIntegrityViolationException(exception);

            // Then
            assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "状态码应为400"),
                () -> assertNotNull(response.getBody(), "响应体不应为空"),
                () -> assertEquals("学号已存在", response.getBody().get("message"), "错误信息应提示学号重复")
            );
        }

        @Test
        @DisplayName("处理数据约束异常 - 当消息不匹配唯一约束时返回通用提示")
        void testHandleDataIntegrityViolationException_GenericConstraint_ReturnDefaultMessage() {
            // Given
            DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "Foreign key constraint violation"
            );

            // When
            ResponseEntity<Map<String, Object>> response =
                globalExceptionHandler.handleDataIntegrityViolationException(exception);

            // Then
            assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "状态码应为400"),
                () -> assertNotNull(response.getBody(), "响应体不应为空"),
                () -> assertEquals("数据约束冲突", response.getBody().get("message"), "应返回通用约束冲突提示")
            );
        }

        @Test
        @DisplayName("处理数据约束异常 - 当最具体原因消息为空时返回通用提示")
        void testHandleDataIntegrityViolationException_NullMessage_ReturnDefaultMessage() {
            // Given
            DataIntegrityViolationException exception = new DataIntegrityViolationException(
                null,
                new RuntimeException()
            );

            // When
            ResponseEntity<Map<String, Object>> response =
                globalExceptionHandler.handleDataIntegrityViolationException(exception);

            // Then
            assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "状态码应为400"),
                () -> assertNotNull(response.getBody(), "响应体不应为空"),
                () -> assertEquals("数据约束冲突", response.getBody().get("message"), "空消息时应返回通用约束冲突提示")
            );
        }
    }
}
