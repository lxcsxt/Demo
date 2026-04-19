package com.example.studentmanagement.service;

import com.example.studentmanagement.dto.StudentRequest;
import com.example.studentmanagement.model.Student;
import com.example.studentmanagement.repository.StudentRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return studentRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Transactional
    public Student create(StudentRequest request) {
        if (studentRepository.existsByStudentNo(request.getStudentNo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "学号已存在");
        }

        Student student = new Student();
        copyProperties(request, student);
        return studentRepository.save(student);
    }

    @Transactional
    public Student update(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学生不存在"));

        if (studentRepository.existsByStudentNoAndIdNot(request.getStudentNo(), id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "学号已存在");
        }

        copyProperties(request, student);
        return studentRepository.save(student);
    }

    @Transactional
    public void delete(Long id) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学生不存在"));
        studentRepository.delete(student);
    }

    private void copyProperties(StudentRequest request, Student student) {
        student.setStudentNo(request.getStudentNo().trim());
        student.setName(request.getName().trim());
        student.setAge(request.getAge());
        student.setMajor(request.getMajor().trim());
        student.setEmail(request.getEmail().trim());
    }
}
