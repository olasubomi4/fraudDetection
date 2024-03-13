package com.example.fraudDetection.entity;


import jakarta.persistence.*;
import lombok.*;

//import ja.persistence.*;

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

    private Long timestamp;
    private Double amount;
    private String userID;
    private String serviceID;

}
