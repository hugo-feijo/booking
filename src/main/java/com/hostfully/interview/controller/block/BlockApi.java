package com.hostfully.interview.controller.block;

import com.hostfully.interview.model.dto.BlockCreateDto;
import com.hostfully.interview.model.dto.BookingCreateDto;
import com.hostfully.interview.model.dto.ErrorInfoDto;
import com.hostfully.interview.model.entity.Block;
import com.hostfully.interview.model.entity.Booking;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "block", description = "This is the Block API, that allows host to block dates for a property")
public interface BlockApi {
    @Operation(summary = "Create Block", description = "Create a new Block, only if the date is not already booked or blocked.", tags = {"block"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successful operation", content = @Content(schema = @Schema(implementation = Block.class))),
            @ApiResponse(responseCode = "400", description = "bad request", content = @Content(schema = @Schema(implementation = ErrorInfoDto.class)))})
    @PostMapping("/property/{property-id}/block")
    ResponseEntity<Block> createBlock(@RequestBody BlockCreateDto blockCreateDto, @Parameter(description="Property id")  @PathVariable("property-id") String propertyId);

    @Operation(summary = "Update Block", description = "Update a Block, increasing period only if the date is not already booked or blocked.", tags = {"block"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = Block.class))),
            @ApiResponse(responseCode = "400", description = "bad request", content = @Content(schema = @Schema(implementation = ErrorInfoDto.class)))})
    @PutMapping("/block/{block-id}")
    ResponseEntity<Block> updateBlock(@RequestBody BlockCreateDto blockCreateDto,  @Parameter(description="Block id")  @PathVariable("block-id") String blockId);

    @Operation(summary = "Delete Block", description = "Delete a existing Block", tags = {"block"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "successful operation"),
            @ApiResponse(responseCode = "400", description = "bad request", content = @Content(schema = @Schema(implementation = ErrorInfoDto.class)))})
    @DeleteMapping("/block/{block-id}")
    ResponseEntity<?> deleteBlock(@Parameter(description="Block id") @PathVariable("block-id") String blockId);
}
