package com.pcc.wellfare.requests;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateBudgetRequest {

  String level;
  Double opd;
  Double ipd;
  Double room;
}
