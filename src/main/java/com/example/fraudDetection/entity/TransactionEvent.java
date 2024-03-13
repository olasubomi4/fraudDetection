package com.example.fraudDetection.entity;


import jakarta.persistence.*;
import lombok.*;

import javax.validation.constraints.NotBlank;


@Entity
@Getter
@Setter
@Table(name ="transaction_event")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class  TransactionEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message =  "timestamp cannot be blank")
    @NonNull
    @Column(nullable = false)
    private Long timestamp;

    @NotBlank(message =  "amount cannot be blank")
    @NonNull
    @Column(nullable = false)
    private Double amount;

    @NotBlank(message =  "userID cannot be blank")
    @NonNull
    @Column(nullable = false)
    private String userID;

    @NotBlank(message =  "serviceId cannot be blank")
    @NonNull
    @Column(nullable = false)
    private String serviceID;

}
