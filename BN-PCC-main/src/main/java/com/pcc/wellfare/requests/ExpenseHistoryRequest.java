package com.pcc.wellfare.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseHistoryRequest {
	private String prefix;
	private String firstname;
    private String surname;
    private String dateOfAdmidtion;
    private String description;
    private String startDate;
    private String endDate;
    private Double withdraw;
    private String remark;
    private Integer dateCount;
}
