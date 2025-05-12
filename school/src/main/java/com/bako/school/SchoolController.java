package com.bako.school;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/schools")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolService schoolService;

    @PostMapping
    //@ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<School> save(
            @RequestBody School school
    ) {
        School savedSchool = schoolService.saveSchool(school);
        return ResponseEntity.ok(savedSchool);
    }

    @GetMapping
    public ResponseEntity<List<School>> findAllSchools() {
        return ResponseEntity.ok(schoolService.findAllSchools());
    }

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<School> updateSchool(@RequestBody School school){
        School sc= schoolService.findSchoolByID(school.getId());
        sc.setName(school.getName());
        sc.setEmail(school.getEmail());
        return ResponseEntity.ok(sc);
    }

    @GetMapping("/{id}")
    public ResponseEntity<School> getSchoolById(@PathVariable int id) {
       School school = schoolService.findSchoolByID(id);
       return ResponseEntity.ok(school);
    }
    @PostMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<School, Boolean>> delete(
        @RequestBody School ecole
    ){
        School school = schoolService.findSchoolByID(ecole.getId());
        schoolService.deleteSchool(school);
        Map<School, Boolean> response = new HashMap<>();
        response.put(school, Boolean.TRUE );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/with-students/{school-id}")
    public ResponseEntity<FullSchoolResponse> findAllSchools(
            @PathVariable("school-id") Integer schoolId
    ) {
        return ResponseEntity.ok(schoolService.findSchoolsWithStudents(schoolId));
    }
}
