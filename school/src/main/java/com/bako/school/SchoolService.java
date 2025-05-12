package com.bako.school;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.bako.school.client.StudentClient;

import java.util.List;
@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;
    private final StudentClient client;

    public School saveSchool(School school) {
        return schoolRepository.save(school);
    }

    public School updateSchool(School school){
        return schoolRepository.save(school);
    }

    public List<School> findAllSchools() {
        return schoolRepository.findAll();
    }

    public School findSchoolByID(Integer id){
        return schoolRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Ecole innexistante"));
    }
    public void deleteSchool(School school){
        schoolRepository.delete(school);
    }

    public FullSchoolResponse findSchoolsWithStudents(Integer schoolId) {
        var school = schoolRepository.findById(schoolId)
                .orElse(
                        School.builder()
                                .name("NOT_FOUND")
                                .email("NOT_FOUND")
                                .build()
                );
        var students = client.findAllStudentsBySchool(schoolId);
        return FullSchoolResponse.builder()
                .name(school.getName())
                .email(school.getEmail())
                .students(students)
                .build();
    }
}
