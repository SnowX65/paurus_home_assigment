package com.home.codingassignment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import com.home.codingassignment.task1.controller.CountryController;
import com.home.codingassignment.task1.controller.TraderController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CodingAssignmentApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CountryController countryController;

    @Autowired
    private TraderController traderController;

    /*
    *
    * Tests for traders api endpoints
    *
    * */
    @Test
    public void getTraders() throws Exception {

        ResultActions result = mockMvc.perform(get("/api/traders"));

        result  .andExpect(status().isOk())
                .andExpect(jsonPath("$.successful").value(true))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    public void getTraderById() throws Exception {

        ResultActions result = mockMvc.perform(get("/api/traders"));

        result  .andExpect(status().isOk())
                .andExpect(jsonPath("$.successful").value(true))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

    }



}
