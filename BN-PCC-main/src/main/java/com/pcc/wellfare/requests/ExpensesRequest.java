package com.pcc.wellfare.requests;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class ExpensesRequest {

    float ipd;
    float opd;
    float roomService;
    String types;
    int days;
    Date startDate;
    Date endDate;
    Date adMission;
    String description;
    String remark;

}
