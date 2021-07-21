package controller.service;

import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * A class to handle exceptions thrown in the controllers by sending the appropriate response
 * statuses to the client.
 */
public class ControllerExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(value = HttpStatus.BAD_REQUEST)
  public @ResponseBody
  ErrorResponse handleRuntimeException(RuntimeException e) {
    return new ErrorResponse(e.getMessage());
  }

  @ExceptionHandler(IOException.class)
  @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
  public @ResponseBody
  ErrorResponse handleIOException(IOException e) {
    return new ErrorResponse(e.getMessage());
  }

  /**
   * Represents a response sent when an error occurs with a message describing the error.
   */
  public static class ErrorResponse {

    private final String message;

    public ErrorResponse(String message) {
      this.message = message;
    }

    public String getMessage() {
      return this.message;
    }
  }
}
