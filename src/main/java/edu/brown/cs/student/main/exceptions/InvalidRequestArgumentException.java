package edu.brown.cs.student.main.exceptions;

/** This is an exception that occurs when request argument is invalid. */
public class InvalidRequestArgumentException extends Exception {
  String invalidArgumentField;

  /**
   * This is the constructor for InvalidRequestArgumentException
   *
   * @param invalidArgumentField
   */
  public InvalidRequestArgumentException(String invalidArgumentField) {
    this.invalidArgumentField = invalidArgumentField;
  }

  /**
   * This is the method to get the message of this exception
   *
   * @return String message
   */
  public String getInvalidField() {
    return this.invalidArgumentField;
  }
}
