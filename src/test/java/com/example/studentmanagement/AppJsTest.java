package com.example.studentmanagement;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("前端脚本回归测试")
class AppJsTest {

    private static final Path APP_JS_PATH = Path.of("src", "main", "resources", "static", "app.js");

    @Test
    @DisplayName("学生列表脚本 - 使用 textContent 构建文本单元格")
    void testAppJs_TextCellRendering_UseTextContent() throws IOException {
        // Given
        String script = Files.readString(APP_JS_PATH, StandardCharsets.UTF_8);

        // When & Then
        assertAll(
            () -> assertTrue(script.contains("function createTextCell(value)"), "应定义文本单元格创建函数"),
            () -> assertTrue(script.contains("cell.textContent = String(value);"), "应使用 textContent 渲染文本内容"),
            () -> assertFalse(script.contains("tableBody.innerHTML = students.map"), "不应回退到基于 innerHTML 的批量渲染")
        );
    }

    @Test
    @DisplayName("学生列表脚本 - 使用 DOM API 构建按钮和列表")
    void testAppJs_RenderRows_UseDomApi() throws IOException {
        // Given
        String script = Files.readString(APP_JS_PATH, StandardCharsets.UTF_8);

        // When & Then
        assertAll(
            () -> assertTrue(script.contains("function createActionButton(action, id, label, type)"), "应定义操作按钮创建函数"),
            () -> assertTrue(script.contains("button.dataset.action = action;"), "应设置按钮 action 数据属性"),
            () -> assertTrue(script.contains("button.dataset.id = String(id);"), "应设置按钮 id 数据属性"),
            () -> assertTrue(script.contains("tableBody.replaceChildren();"), "渲染前应先清空旧节点"),
            () -> assertTrue(script.contains("document.createDocumentFragment();"), "应使用 DocumentFragment 构建列表"),
            () -> assertTrue(script.contains("row.appendChild(createTextCell(student.name));"), "应通过安全文本节点渲染学生姓名")
        );
    }
}
