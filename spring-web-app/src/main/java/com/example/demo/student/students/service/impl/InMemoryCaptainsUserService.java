package com.example.demo.student.students.service.impl;

import com.example.demo.student.students.entity.Student;
import com.example.demo.student.students.service.UserService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class InMemoryCaptainsUserService implements UserService {

  @Override
  public Flux<Student> findAllStudents() {
    return Flux.just(
            new Student(1L, "Captain Jack Sparrow"),
            new Student(2L, "Captain America")
        )
        .delayElements(Duration.ofSeconds(2));
  }

  @Override
  public Mono<Student> getStudentById(Long studentId) {
    return Mono.just(new Student(studentId, "Captain Morgan"))
        .delayElement(Duration.ofSeconds(2));
  }
}
