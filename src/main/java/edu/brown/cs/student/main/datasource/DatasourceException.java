package edu.brown.cs.student.main.datasource;

/** This is the class to deal with all datasource related exceptions */
public class DatasourceException extends Exception {
  private final Throwable cause;

  /**
   * This is the constructor for datasource exception
   *
   * @param message
   */
  public DatasourceException(String message) {
    super(message);
    this.cause = null;
  }
}
