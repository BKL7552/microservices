package com.bako.student;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StudentRepositoryTests {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    @DisplayName("Test1: save student")
    @Order(1)
    @Rollback(false)
    public void saveSstudentTest(){

        //action
        Student student =Student.builder()
                .firstname("BKL")
                .lastname("KYLE")
                .email("kyle@gmail.com")
                .schoolId(1)
                .build();
        studentRepository.save(student);

        //verify
        System.out.println("Student saved: " + student);
        Assertions.assertThat(student.getId()).isGreaterThan(0);
        System.out.println("Student ID: " + student.getId());
    }
}
