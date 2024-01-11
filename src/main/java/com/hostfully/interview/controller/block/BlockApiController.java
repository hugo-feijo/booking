package com.hostfully.interview.controller.block;

import com.hostfully.interview.model.dto.BlockCreateDto;
import com.hostfully.interview.model.entity.Block;
import com.hostfully.interview.service.BlockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/property")
public class BlockApiController implements BlockApi {

    private final BlockService blockService;

    public BlockApiController(BlockService blockService) {
        this.blockService = blockService;
    }

    @Override
    public ResponseEntity<Block> createBlock(BlockCreateDto blockCreateDto, String propertyId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(blockService.createBlock(blockCreateDto, propertyId));
    }
}
