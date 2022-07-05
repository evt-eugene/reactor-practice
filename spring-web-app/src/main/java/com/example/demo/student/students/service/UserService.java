package com.example.demo.student.students.service;

import com.example.demo.student.students.entity.Student;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

  Flux<Student> findAllStudents();

  Mono<Student> getStudentById(Long studentId);

}
