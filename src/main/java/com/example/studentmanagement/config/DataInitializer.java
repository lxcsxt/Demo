package com.example.studentmanagement.config;

import com.example.studentmanagement.model.Student;
import com.example.studentmanagement.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initStudents(StudentRepository studentRepository) {
        return args -> {
            if (studentRepository.count() > 0) {
                return;
            }

            studentRepository.save(new Student("S2024001", "林晨", 20, "计算机科学与技术", "linchen@example.com"));
            studentRepository.save(new Student("S2024002", "周宁", 21, "软件工程", "zhouning@example.com"));
            studentRepository.save(new Student("S2024003", "陈雨晴", 22, "信息安全", "chenyuqing@example.com"));
        };
    }
}
