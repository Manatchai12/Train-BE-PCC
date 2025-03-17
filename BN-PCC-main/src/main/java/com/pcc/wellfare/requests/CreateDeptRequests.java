package com.pcc.wellfare.requests;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateDeptRequests {

    String code;
    String deptid;
    String company;
    String edivision;
    String tdivision;
    String divisionid;
    String edept;
    String tdept;
    String deptcode;
    String remark;
}
