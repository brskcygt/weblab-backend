package com.weblab_backend.weblab_backend.exceptions;

import java.security.SignatureException;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import io.jsonwebtoken.ExpiredJwtException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleException(Exception exception) {
    ProblemDetail response = null;

    exception.printStackTrace();

    if (exception instanceof MissingTokenException) {
      response = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
      response.setProperty("description", "Bu işlemi yapmak için yetkiniz yok.");
      return response;
    }

    if (exception instanceof BadCredentialsException) {
      response = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
      response.setProperty("description", "Şifre veya email yanlış.");

      return response;
    }

    if (exception instanceof AccountStatusException) {
      response = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
      response.setProperty("description", "Bu hesaba erişim kapatılmıştır.");
    }

    if (exception instanceof AccessDeniedException) {
      response = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
      response.setProperty("description", "Bu işlemi yapmak için yetkiniz yok.");
    }

    if (exception instanceof SignatureException) {
      response = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
      response.setProperty("description", "JWT imzası geçersiz.");
    }

    if (exception instanceof ExpiredJwtException) {
      response = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
      response.setProperty("description", "JWT token süresi doldu.");
    }

    if (response == null) {
      response = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), exception.getMessage());
      response.setProperty("description", "Unknown internal server error.");
    }

    return response;
  }
}
