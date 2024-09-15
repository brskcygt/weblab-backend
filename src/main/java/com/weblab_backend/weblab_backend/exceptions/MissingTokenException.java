package com.weblab_backend.weblab_backend.exceptions;

public class MissingTokenException extends Exception {
  public MissingTokenException(String message) {
    super(message);
  }
}