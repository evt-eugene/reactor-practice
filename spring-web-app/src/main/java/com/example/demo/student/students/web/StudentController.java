package com.example.demo.student.students.web;

import com.example.demo.student.students.entity.Student;
import com.example.demo.student.students.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/students")
public class StudentController {

  public final UserService service;

  @Autowired
  public StudentController(UserService service) {
    this.service = service;
  }

  @GetMapping
  @ResponseBody
  public Flux<Student> getStudents() {
    return service.findAllStudents();
  }

  @GetMapping("/{id}")
  @ResponseBody
  public Mono<Student> getStudent(@PathVariable("id") Long studentId) {
    return service.getStudentById(studentId);
  }

  @GetMapping("/view")
  public String viewStudents(final Model model) {
    var students = service.findAllStudents();

    model.addAttribute("students", new ReactiveDataDriverContextVariable(students, 10));

    return "students-view.html";
  }

}
