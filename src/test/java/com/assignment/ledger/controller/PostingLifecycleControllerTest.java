package com.assignment.ledger.controller;


import com.assignment.ledger.dto.PostingLifecycleDTO;
import com.assignment.ledger.entity.MovementState;
import com.assignment.ledger.service.PostingLifecycleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PostingLifecycleControllerTest {

    @Mock
    private PostingLifecycleService postingLifecycleService;

    @InjectMocks
    private PostingLifecycleController postingLifecycleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void setPostingLifecycleState_ValidInput_ReturnsOk() {
        // Arrange
        PostingLifecycleDTO postingLifecycleDTO = new PostingLifecycleDTO();
        postingLifecycleDTO.setPostingId(1L);
        postingLifecycleDTO.setNewState(MovementState.CLEARED);
        // Act
        ResponseEntity<String> response = postingLifecycleController.setPostingLifecycleState(postingLifecycleDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Posting lifecycle state updated successfully", response.getBody());
        verify(postingLifecycleService, times(1)).setPostingState(postingLifecycleDTO.getPostingId(), postingLifecycleDTO.getNewState());
    }

}
