package edu.brown.cs.student.main.exceptions;

/** This is an exception that occurs when missing arguments. */
public class MissingArgumentException extends Exception {
  String missingField;

  /**
   * This is the constructor for MissingArgumentException
   *
   * @param missingField
   */
  public MissingArgumentException(String missingField) {
    this.missingField = missingField;
  }

  /**
   * This is the method to get the message of this exception
   *
   * @return String message
   */
  public String getMissingField() {
    return this.missingField;
  }
}
