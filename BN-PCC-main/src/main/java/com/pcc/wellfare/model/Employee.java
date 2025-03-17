package com.pcc.wellfare.model;

import java.util.Date;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Long userId;
    private String empid;
    private String tprefix;
    private String tname;
    private String tsurname;
    private String tposition;
    private Date startDate;
    @ManyToOne
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @ManyToOne
    @JoinColumn(name = "dept_code")
    private Dept dept;

    private String remark;
    private String status;

    @Column(name = "e-mail")
    private String email;
}
