package com.hostfully.interview.controller.property;

import com.hostfully.interview.model.dto.ErrorInfoDto;
import com.hostfully.interview.model.dto.PropertyCreateDto;
import com.hostfully.interview.model.entity.Property;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "properties", description = "the Property API")
public interface PropertyApi {
    @Operation(summary = "Find Property by id", description = "Property search by %id%", tags = {"properties"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Property.class))),
            @ApiResponse(responseCode = "400", description = "bad request", content = @Content(schema = @Schema(implementation = ErrorInfoDto.class)))})
    @GetMapping("/{id}")
    Property getProperty(@Parameter(description="Property id") @PathVariable("id") String id);

    @Operation(summary = "Find all Property", description = "Find All Property", tags = {"properties"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Property.class))))})
    @GetMapping
    List<Property> getAllProperties();

    @Operation(summary = "Create Property", description = "Create a new Property", tags = {"properties"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successful operation", content = @Content(schema = @Schema(implementation = Property.class))),
            @ApiResponse(responseCode = "400", description = "bad request", content = @Content(schema = @Schema(implementation = ErrorInfoDto.class)))})
    @PostMapping
    ResponseEntity<Property> createProperty(@RequestBody PropertyCreateDto propertyCreateDto);

    @Operation(summary = "Update Property", description = "Update a existing Property", tags = {"properties"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Property.class))),
            @ApiResponse(responseCode = "400", description = "bad request", content = @Content(schema = @Schema(implementation = ErrorInfoDto.class)))})
    @PutMapping("/{id}")
    ResponseEntity<Property> updateProperty(@Parameter(description="Property id") @PathVariable("id") String id, @RequestBody PropertyCreateDto propertyCreateDto);

    @Operation(summary = "Delete Property", description = "Delete a existing Property", tags = {"properties"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "bad request", content = @Content(schema = @Schema(implementation = ErrorInfoDto.class)))})
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteProperty(@Parameter(description="Property id")  @PathVariable("id") String id);
}
