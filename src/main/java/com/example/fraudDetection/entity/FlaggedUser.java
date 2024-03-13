package com.example.fraudDetection.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name ="flagged-user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlaggedUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private String flaggedReason;
}
