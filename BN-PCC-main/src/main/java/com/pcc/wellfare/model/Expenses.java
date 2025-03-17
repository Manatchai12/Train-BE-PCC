package com.pcc.wellfare.model;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Setter
public class Expenses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id") // ระบุชื่อคอลัมน์เพื่อให้ตรงกับฐานข้อมู]
    private Long id;

    //เชื่อมกับ employee
    @ManyToOne
    @JoinColumn(name = "userId")
    private Employee employee;

    @Column(name = "date_of_admission")
    private Date dateOfAdmission;


    private String description;
    private double opd;
    private double ipd;
    private String remark;
    private float roomService;
    int days;
    Date startDate;
    Date endDate;
    float canWithdraw;
}
