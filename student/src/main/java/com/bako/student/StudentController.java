package com.bako.student;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/students")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void save(
            @RequestBody Student student
    ) {
        service.saveStudent(student);
    }

    @GetMapping
    public ResponseEntity<List<Student>> findAllStudents() {
        return ResponseEntity.ok(service.findAllStudents());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable int id) {
        Student student = service.findStudentById(id);
        return ResponseEntity.ok(student);
    }

    @PostMapping("/modify")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateStudent(@RequestBody Student student) {
        Student etudient = service.findStudentById(student.getId());
        etudient.setFirstname(student.getFirstname());
        etudient.setLastname(student.getLastname());
        etudient.setEmail(student.getEmail());
        etudient.setSchoolId(student.getSchoolId());
        service.saveStudent(etudient);
    }
    
    @GetMapping("/school/{school-id}")
    public ResponseEntity<List<Student>> findAllStudents(
            @PathVariable("school-id") Integer schoolId
    ) {
        return ResponseEntity.ok(service.findAllStudentsBySchool(schoolId));
    }

    @PostMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<Student, Boolean>> delete(
        @RequestBody Student student
    ){
        Student etudient = service.findStudentById(student.getId());
        service.deleteStudent(etudient);
        Map<Student, Boolean> response = new HashMap<>();
        response.put(etudient, Boolean.TRUE );
        return ResponseEntity.ok(response);
    }
    
}