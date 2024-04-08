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

public class PostingLifecycleControllerTest {

    @Mock
    private PostingLifecycleService postingLifecycleService;

    @InjectMocks
    private PostingLifecycleController postingLifecycleController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSetPostingLifecycleState_Success() {
        // Arrange
        PostingLifecycleDTO postingLifecycleDTO = new PostingLifecycleDTO();
        postingLifecycleDTO.setPostingId(1L);
        postingLifecycleDTO.setNewState(MovementState.CLEARED);

        // Act
        ResponseEntity<String> responseEntity = postingLifecycleController.setPostingLifecycleState(postingLifecycleDTO);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Posting lifecycle state updated successfully", responseEntity.getBody());
        verify(postingLifecycleService, times(1)).setPostingState(1L, MovementState.CLEARED);
    }

    @Test
    public void testSetPostingLifecycleState_Failure() {
        // Arrange
        PostingLifecycleDTO postingLifecycleDTO = new PostingLifecycleDTO();
        postingLifecycleDTO.setPostingId(1L);
        postingLifecycleDTO.setNewState(MovementState.CLEARED);
        doThrow(new RuntimeException("Error")).when(postingLifecycleService).setPostingState(1L, MovementState.CLEARED);

        // Act
        ResponseEntity<String> responseEntity = postingLifecycleController.setPostingLifecycleState(postingLifecycleDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Error", responseEntity.getBody());
    }
}
