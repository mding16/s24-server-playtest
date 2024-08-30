package edu.brown.cs.student.main.datasource;

import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.exceptions.NoHeaderException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This Parser class creates a CSV Parser object that can turn parse any Reader object using a
 * CreatorFromRow<T> object into a list of rows (type T). It can be used for CSVs with and without
 * headers.
 *
 * @author michelleding
 * @version 15 Sept 2023
 * @param <T> represents the type of object that represents each row in the CSV.
 */
public class Parser<T> {
  /** ArrayList containing a list of T objects representing CSV content after parsing */
  private ArrayList<T> csvContent;

  /** Boolean representing whether the CSV has headers, true for headers, false otherwise */
  private final Boolean hasHeaders;

  /** int representing number of rows in the CSV including the header */
  private CreatorFromRow<T> creator;

  private final Reader reader;
  private int numRows;

  /**
   * Constructs the Parser object
   *
   * @param inputReader represents the Reader that contains the CSV
   * @param creator represents the CreatorFromRow object that converts each row into a T object
   * @param hasHeaders represents whether the CSV has headers (true for headers, false otherwise)
   * @throws IOException if file not found
   * @throws FactoryFailureException if CreatorFromRow fails to create from a certain row
   */
  public Parser(Reader inputReader, CreatorFromRow<T> creator, Boolean hasHeaders) {
    this.reader = inputReader;
    this.creator = creator;
    this.hasHeaders = hasHeaders;
  }

  /**
   * This method is the parse the csv and return back a list of object
   *
   * @return unmodified csv content
   * @throws FactoryFailureException
   * @throws IOException
   */
  public List<T> parse() throws IOException {
    this.csvContent = new ArrayList<>();
    List<String> values = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(this.reader)) {
      String line;
      while ((line = br.readLine()) != null) {
        Pattern regexSplitCSVRow =
            Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");
        values = Arrays.stream(regexSplitCSVRow.split(line)).toList();
        ;
        this.csvContent.add(this.creator.create(values));
      }
    } catch (IOException | FactoryFailureException e) {
      throw new IOException();
    }

    this.numRows = this.csvContent.size();
    return Collections.unmodifiableList(this.csvContent);
  }

  /** This method prints the CSV file after it is parsed */
  public void print() {
    for (T row : this.csvContent) {
      System.out.println(row.toString());
    }
  }

  /**
   * This method gets the number of rows (including the header) in the CSV
   *
   * @return Integer representing number of rows in CSV
   */
  public Integer getNumRows() {
    return this.numRows;
  }

  /**
   * This method returns whether the CSV has headers
   *
   * @return true if CSV has headers, false otherwise
   */
  public boolean hasHeaders() {
    return this.hasHeaders;
  }

  /**
   * This method returns the headers of the CSV
   *
   * @return object T representing headers (** assumes headers are the first row index 0)
   * @throws NoHeaderException if there are no headers in the CSV
   */
  public T getHeaders() throws NoHeaderException {
    if (this.hasHeaders) {
      return this.csvContent.get(0);
    } else {
      throw new NoHeaderException();
    }
  }

  /**
   * This method returns the row corresponding to a certain row index
   *
   * @param rowIndex represents index of row to return
   * @return object T representing the row
   * @throws IndexOutOfBoundsException if index is out of bounds (negative or >= than numRows)
   */
  public T getRow(int rowIndex) {
    if ((rowIndex >= this.numRows) | (rowIndex < 0)) {
      throw new IndexOutOfBoundsException();
    } else {
      return this.csvContent.get(rowIndex);
    }
  }
}
