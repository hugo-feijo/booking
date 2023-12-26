package com.hostfully.interview.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hostfully.interview.model.dto.PropertyCreateDto;
import com.hostfully.interview.model.entity.Property;
import com.hostfully.interview.repository.PropertyRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class PropertyControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createProperty_ValidProperty_EntityIsReturnedAndInserted() throws Exception {
        var propertyCreateDto = new PropertyCreateDto("Property name");

        var insertResponse = mockMvc.perform(post("/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(propertyCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("name", is(propertyCreateDto.getName())))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var property = objectMapper.readValue(insertResponse, Property.class);

        mockMvc.perform(get("/properties/" + property.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("name", is(propertyCreateDto.getName())));
    }

    @Test
    public void createProperty_InvalidPropertyName_EntityIsNotInserted() throws Exception {
        var propertyCreateDto = new PropertyCreateDto(null);

        mockMvc.perform(post("/properties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(propertyCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Property name is required")));

        Assertions.assertEquals(0, propertyRepository.findAll().size());
    }


}
