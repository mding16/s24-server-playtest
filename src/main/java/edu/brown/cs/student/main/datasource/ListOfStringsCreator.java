package edu.brown.cs.student.main.datasource;

import java.util.List;

/** This ListOfStringsCreator takes a list of strings and returns the same list of strings */
public class ListOfStringsCreator implements CreatorFromRow<List<String>> {

  /** This is the constructor of ListOfStringsCreator */
  public ListOfStringsCreator() {}

  /**
   * This is the method to create the rows from list of strings
   *
   * @param row
   * @return List<String> row
   */
  @Override
  public List<String> create(List<String> row) {
    return row;
  }
}
