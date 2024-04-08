package com.assignment.ledger.controller;

import com.assignment.ledger.dto.PostingLifecycleDTO;
import com.assignment.ledger.entity.MovementState;
import com.assignment.ledger.service.PostingLifecycleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@Slf4j
public class PostingLifecycleController {
    private final PostingLifecycleService postingLifecycleService;

    @Autowired
    public PostingLifecycleController(PostingLifecycleService postingLifecycleService) {
        this.postingLifecycleService = postingLifecycleService;
    }

    @PutMapping("/setState")
    public ResponseEntity<String> setPostingLifecycleState(@Valid  @RequestBody PostingLifecycleDTO postingLifecycleDTO) {
        try {
            postingLifecycleService.setPostingState(postingLifecycleDTO.getPostingId(), postingLifecycleDTO.getNewState());
            return ResponseEntity.ok("Posting lifecycle state updated successfully");
        } catch (RuntimeException e) {
            log.error("Error updating posting lifecycle state: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
