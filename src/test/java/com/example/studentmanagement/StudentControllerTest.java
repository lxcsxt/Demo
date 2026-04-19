package com.example.studentmanagement;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("应该返回初始化学生列表 - 当应用启动后查询学生时")
    void shouldReturnSeedStudents_whenApplicationStarts() throws Exception {
        mockMvc.perform(get("/api/students"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))))
            .andExpect(jsonPath("$[0].studentNo", is("S2024001")));
    }

    @Test
    @DisplayName("应该创建学生 - 当请求参数合法时")
    void shouldCreateStudent_whenRequestIsValid() throws Exception {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("studentNo", "S2024999");
        payload.put("name", "测试同学");
        payload.put("age", 19);
        payload.put("major", "数据科学");
        payload.put("email", "tester@example.com");

        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.studentNo", is("S2024999")))
            .andExpect(jsonPath("$.name", is("测试同学")));
    }

    @Test
    @DisplayName("应该返回错误 - 当创建重复学号学生时")
    void shouldReturnBadRequest_whenStudentNoIsDuplicated() throws Exception {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("studentNo", "S2024001");
        payload.put("name", "重复学号");
        payload.put("age", 20);
        payload.put("major", "人工智能");
        payload.put("email", "duplicate@example.com");

        mockMvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("学号已存在")));
    }
}
