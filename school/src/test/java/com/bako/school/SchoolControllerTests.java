package com.bako.school;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(SchoolController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SchoolControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SchoolService schoolService;

    @Autowired
    private ObjectMapper objectMapper;

    School school;

    @BeforeEach
    public void setup() {
        school = School.builder()
                .name("FST MEKNES")
                .email("fstm@gmail.com")
                .build();
    }

    @Test
    @Order(1)
    @Rollback(false)
    public void saveSchoolTest() throws Exception {
        // Precontion
        when(schoolService.saveSchool(school)).thenReturn(school);

        // Action
        ResultActions response = mockMvc.perform(post("/api/v1/schools")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(school)));

        // verify

        response.andDo(print())
                .andExpect(status().isOk())
                /* .andExpect(jsonPath("$.name", is(school.getName())))
                .andExpect(jsonPath("$.email", is(school.getEmail()))) */;

    }

    @Test
    @Order(2)
    public void getAllSchoolsTest() throws Exception {
        // Precondition
        List<School> schoolsList = new ArrayList<>();
        schoolsList.add(school);
        when(schoolService.findAllSchools()).thenReturn(schoolsList);

        // Action
        ResultActions response = mockMvc.perform(get("/api/v1/schools"));

        // verify
        response.andExpect(status().isOk())
                .andDo(print())
                ;

    }
}
