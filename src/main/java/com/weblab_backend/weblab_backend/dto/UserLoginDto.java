package com.weblab_backend.weblab_backend.dto;

import lombok.Data;

@Data
public class UserLoginDto {

  private String email;

  private String password;
}
