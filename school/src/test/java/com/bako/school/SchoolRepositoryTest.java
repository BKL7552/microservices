package com.bako.school;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class SchoolRepositoryTest {

    @Autowired
    private SchoolRepository schoolRepository;

    @Test
    @DisplayName("Test1: save school")
    @Order(1)
    @Rollback(false)
    public void saveSchoolTest(){
        //action
        School school =School.builder()
                .name("FST Settat")
                .email("fstsettat@gmail.com")
                .build();
        
                schoolRepository.save(school);
        //verify
        System.out.println("School saved: " + school);
        Assertions.assertTrue(school.getId()>0, "School Id should be greater than 0");
        System.out.println("School ID: " + school.getId());
    }

    @Test
    @DisplayName("Test2: get all schools")
    @Order(2)
    @Rollback(false)
    public void getAllSchoolsTest(){
        //action
        List<School> schools = schoolRepository.findAll();
        //verify
        System.out.println("All schools: " + schools);
        Assertions.assertFalse(schools.isEmpty(), "Schools list should not be empty");
        Assertions.assertTrue(schools.size() > 0, "Schools list should have at least one school");
    }

    @Test
    @DisplayName("Test3: get school by id")
    @Order(3)
    @Rollback(false)
    public void getSchoolByIdTest(){
        //action
        School school = schoolRepository.findById(1).orElse(null);
        //verify
        System.out.println("School by id: " + school);
        Assertions.assertNotNull(school, "School should not be null");
    }

    @Test
    @DisplayName("Test4: update school")
    @Order(4)
    @Rollback(false)
    public void updateSchoolTest(){
        //action
        School school = schoolRepository.findById(1).orElse(null);
        school.setEmail("fsts@gmail.com");
        School savedSchool = schoolRepository.save(school);
        //verify
        System.out.println("School updated: " + savedSchool);
        Assertions.assertEquals(savedSchool.getEmail(), "fsts@gmail.com", "Email should be updated");
    }
}
