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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Test
    public void getProperty_InvalidUUID_BadRequestIsThrows() throws Exception {
        propertyRepository.save(new Property(null, "Property name"));

        mockMvc.perform(get("/properties/" + "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Bad Request")));

        Assertions.assertEquals(1, propertyRepository.findAll().size());
    }

    @Test
    public void getProperty_IdNonexistent_Return400() throws Exception {
        propertyRepository.save(new Property(null, "Property name"));

        mockMvc.perform(get("/properties/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Bad Request")));

        Assertions.assertEquals(1, propertyRepository.findAll().size());
    }


    @Test
    @Sql("classpath:/sql/insert-property.sql")
    public void getAllProperties_ValidRequest_ReturnsAllEntity() throws Exception {
        mockMvc.perform(get("/properties")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        Assertions.assertEquals(3, propertyRepository.findAll().size());
    }

    @Test
    public void deleteProperty_ValidId_EntityIsDeleted() throws Exception {
        var property =  propertyRepository.save(new Property(null, "Property name"));

        Assertions.assertEquals(1, propertyRepository.findAll().size());

        mockMvc.perform(delete("/properties/" + property.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Assertions.assertEquals(0, propertyRepository.findAll().size());
    }

    @Test
    public void deleteProperty_InvalidUUID_BadRequestIsThrows() throws Exception {
        var property =  propertyRepository.save(new Property(null, "Property name"));

        Assertions.assertEquals(1, propertyRepository.findAll().size());

        mockMvc.perform(delete("/properties/" + "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Bad Request")));

        Assertions.assertEquals(1, propertyRepository.findAll().size());
    }

    @Test
    public void deleteProperty_IdNonexistent_Return204() throws Exception {
        propertyRepository.save(new Property(null, "Property name"));

        Assertions.assertEquals(1, propertyRepository.findAll().size());

        mockMvc.perform(delete("/properties/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        Assertions.assertEquals(1, propertyRepository.findAll().size());
    }

    @Test
    public void updateProperty_ValidId_EntityIsUpdated() throws Exception {
        var property =  propertyRepository.save(new Property(null, "Property name"));

        var propertyCreateDto = new PropertyCreateDto("Property name updated");

        mockMvc.perform(put("/properties/" + property.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(propertyCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("name", is(propertyCreateDto.getName())));

        Assertions.assertEquals(propertyCreateDto.getName(), propertyRepository.findById(property.getId()).get().getName());
    }

    @Test
    public void updateProperty_InvalidUUID_BadRequestIsThrows() throws Exception {
        var propertyCreateDto = new PropertyCreateDto("Property name updated");

        mockMvc.perform(put("/properties/" + "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(propertyCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Bad Request")));

    }

    @Test
    public void updateProperty_IdNonexistent_Return400() throws Exception {
        var propertyCreateDto = new PropertyCreateDto("Property name updated");

        mockMvc.perform(put("/properties/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(propertyCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Bad Request")));

    }

    @Test
    public void updateProperty_InvalidPropertyName_Return400() throws Exception {
        var propertyCreateDto = new PropertyCreateDto(null);

        mockMvc.perform(put("/properties/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(propertyCreateDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message", is("Property name is required")));

    }
}
