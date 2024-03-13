package com.example.fraudDetection.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {
    @NotBlank(message = "Timestamp id must not be blank")
    private Long timestamp;
    @NotBlank(message ="Amount must not be blank")
    private Double amount;
    @NotBlank(message ="User id must not be blank")
    private String userID;
    @NotBlank(message ="Service id must not be blank")
    private String serviceID;
}
