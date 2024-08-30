package edu.brown.cs.student.main.datasource;

import edu.brown.cs.student.main.exceptions.InvalidColumnIdentifierValueException;
import edu.brown.cs.student.main.exceptions.NoHeaderException;
import java.util.ArrayList;
import java.util.List;

/**
 * This Searcher class search a parsed csv by columnvalue and columnIdentifier.
 *
 * @author michelleding
 * @version 15 Sept 2023
 */
public class Searcher {
  Parser<List<String>> toParse;
  String toSearch;
  ColumnIdentifier columnIdentifierType;
  String columnIdentifier;
  List<String> headers;
  Integer startSearchIndex = 0;

  /**
   * This is the constructor of the searcher class
   *
   * @param toParse represents the type of object that represents each row in the CSV.
   */
  public Searcher(Parser<List<String>> toParse) throws NoHeaderException {
    this.toParse = toParse;
    if (this.toParse.hasHeaders()) {
      this.populateHeaders();
    }
  }

  /**
   * This method populates the header list if headers exist and updates startSearchIndex to 1
   * (meaning search skips the header row)
   *
   * @throws NoHeaderException if the CSV does not have headers
   */
  private void populateHeaders() throws NoHeaderException {
    try {
      this.headers = this.toParse.getHeaders();
      this.startSearchIndex = 1;
    } catch (NoHeaderException e) {
      throw new NoHeaderException();
    }
  }

  /**
   * This helper method searches the CSV by header name
   *
   * @param headerName represents name of the header to search from
   * @param toSearch represents string to search
   * @return an ArrayList representing results of the search, empty if no results found
   */
  public ArrayList<List<String>> searchByHeaderName(String headerName, String toSearch)
      throws NoHeaderException, InvalidColumnIdentifierValueException {
    if (!this.toParse.hasHeaders()) {
      throw new NoHeaderException();
    }

    // get index of the header then call helper method searchByIndex()
    Integer indexToSearch = this.headers.indexOf(headerName);
    if (indexToSearch == -1) {
      throw new InvalidColumnIdentifierValueException("invalid header name");
    } else {
      return this.searchByIndex(indexToSearch, toSearch);
    }
  }

  /**
   * This helper method searches the CSV by column index
   *
   * @param index represents column index to search from
   * @param toSearch represents string to search
   * @return an ArrayList representing results of the search, empty if no results found
   */
  public ArrayList<List<String>> searchByIndex(Integer index, String toSearch)
      throws InvalidColumnIdentifierValueException {
    if ((index < 0) | (index >= this.toParse.getRow(0).size())) {
      throw new InvalidColumnIdentifierValueException("Index out of bounds");
    }
    // Create empty searchContent array list
    ArrayList<List<String>> searchContent = new ArrayList<>();

    // For each row in the CSV in the Parser object...
    for (int j = this.startSearchIndex; j < this.toParse.getNumRows(); j++) {

      // For each element in the row...
      List<String> currentRow = this.toParse.getRow(j);

      // Check if the element equals to the toSearch element
      // helper method wordMatch is called to accommodate case-sensitivity
      if (toSearch.equals(currentRow.get(index))) {

        // If terms equal, add row to searchContent
        searchContent.add(currentRow);
      }
    }
    return searchContent;
  }

  /**
   * This helper method searches the entire CSV without identifier
   *
   * @param toSearch represents string to search
   * @return an ArrayList representing results of the search, empty if no results found
   */
  public ArrayList<List<String>> searchAll(String toSearch) {
    ArrayList<List<String>> searchContent = new ArrayList<>();

    // Store whether each row has been added yet
    boolean rowAdded = false;

    // For each row in the CSV...
    for (int j = this.startSearchIndex; j < this.toParse.getNumRows(); j++) {
      List<String> currentRow = this.toParse.getRow(j);
      int i = 0;

      // Run through each term in the row and add the row if terms match and row has not been added
      while ((!rowAdded) && i < currentRow.size()) {
        if (toSearch.equals(currentRow.get(i))) {
          searchContent.add(currentRow);
          rowAdded = true;
        }
        i++;
      }
      rowAdded = false;
    }
    return searchContent;
  }
}
