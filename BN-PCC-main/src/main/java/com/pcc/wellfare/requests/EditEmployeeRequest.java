package com.pcc.wellfare.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EditEmployeeRequest {

  String empId;
  String deptCode;
  String tPrefix;
  String tName;
  String tSurname;
  String tPosition;
  String level;
  String remark;
  String email;
}
