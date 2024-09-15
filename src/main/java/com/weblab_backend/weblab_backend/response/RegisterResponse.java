package com.weblab_backend.weblab_backend.response;

import lombok.Data;

@Data
public class RegisterResponse {

  public String email;

  public String fullName;

  public String username;

  public String message;

}
