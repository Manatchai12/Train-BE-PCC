package com.pcc.wellfare.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String level;
    private Double opd;
    private Double ipd;

    @Column(name = "ค่าห้อง_ค่าอาหาร")
    private Double room;

}
