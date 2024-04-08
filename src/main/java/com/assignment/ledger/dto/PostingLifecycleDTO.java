package com.assignment.ledger.dto;

import com.assignment.ledger.entity.MovementState;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostingLifecycleDTO {
    @NotNull(message = "PostingId cannot be null")
    private Long postingId;
    @NotNull(message = "MovementState cannot be null")
    private MovementState newState;
}
