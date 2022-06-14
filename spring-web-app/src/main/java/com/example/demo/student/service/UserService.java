package com.example.demo.student.service;

import com.example.demo.student.entity.Student;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

  Flux<Student> findAllStudents();

  Mono<Student> getStudentById(Long studentId);

}
