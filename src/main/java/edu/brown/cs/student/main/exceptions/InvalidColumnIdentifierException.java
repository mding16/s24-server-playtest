package edu.brown.cs.student.main.exceptions;

/** This is an exception that is raised when a ColumnIdentifier object is invalid */
public class InvalidColumnIdentifierException extends Exception {

  String message;

  /**
   * This is the constructor for InvalidColumnIdentifierException
   *
   * @param message
   */
  public InvalidColumnIdentifierException(String message) {
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
