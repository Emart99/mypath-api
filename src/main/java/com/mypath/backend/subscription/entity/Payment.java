package com.mypath.backend.subscription.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter@Setter
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID id;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String provider; //Enum

}
