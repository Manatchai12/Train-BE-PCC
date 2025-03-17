package com.pcc.wellfare.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class JwtAuthenticationResponse {

  private String accessToken;
  private String tokenType;
  private String msg;
}