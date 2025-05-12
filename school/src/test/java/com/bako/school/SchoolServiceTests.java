package com.bako.school;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.Rollback;


@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SchoolServiceTests {

    @Mock
    private SchoolRepository schoolRepository;
    @InjectMocks
    private SchoolService schoolService;

    private School school;
    @BeforeEach
    public void setup(){
        school= School.builder()
            .name("fst settat")
            .email("fsts@gmail.com")
            .build();
    }

    @Test
    @Order(1)
    @DisplayName("Test1: save school")
    @Rollback(false)
    public void saveSchoolTest(){
        //precondition
       Mockito.when(schoolRepository.save(school))
       .thenReturn(school);
        //action
        School savedSchool = schoolService.saveSchool(school);
        //verify
        Mockito.verify(schoolRepository,Mockito.times(1)).save(school);
        System.out.println("School saved: " + savedSchool);
        Assertions.assertNotNull(savedSchool);
    }

    @Test
    @Order(2)
    @DisplayName("Test2: get all schools")
    public void getAllSchoolsTest(){
        //precondition
        Mockito.when(schoolRepository.findAll())
        .thenReturn(List.of(school));
        //action qui sera simuler pour le test 
        List<School> schools = schoolService.findAllSchools();
        //verify
        Mockito.verify(schoolRepository,Mockito.times(1)).findAll();
        System.out.println("All schools: " + schools);
        Assertions.assertTrue(schools.size() > 0, "Schools list should not be empty");
    }

    @Test
    @Order(3)
    @DisplayName("Test3: update school")
    @Rollback(false)
    public void updateSchoolTest(){
        
        //precondition
        Mockito.when(schoolRepository.save(school))
        .thenReturn(school);
        //action
        school.setId(1);
        school.setName("FST Settat");
        school.setEmail("fstsettat@gmail.com");
        School updatedSchool = schoolService.updateSchool(school);
        //verify
        Mockito.verify(schoolRepository, Mockito.times(1)).save(school);
        System.out.println("School updated: " + updatedSchool);
        assertEquals(updatedSchool.getEmail(), "fstsettat@gmail.com", "the email should be updated");
        assertEquals(updatedSchool.getName(), "FST Settat", "the name should be updated");
    }
}
