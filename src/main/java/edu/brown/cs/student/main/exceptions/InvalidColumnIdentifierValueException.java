package edu.brown.cs.student.main.exceptions;

/** This is an exception that occurs when the column identifier value is invalid. */
public class InvalidColumnIdentifierValueException extends Exception {
  String message;

  /**
   * This is the constructor for InvalidColumnIdentifierValueException
   *
   * @param message
   */
  public InvalidColumnIdentifierValueException(String message) {
    this.message = message;
  }

  /**
   * This is the method to get the message of this exception
   *
   * @return String message
   */
  @Override
  public String getMessage() {
    return this.message;
  }
}
