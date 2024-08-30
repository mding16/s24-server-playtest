package edu.brown.cs.student.main.datasource;

/** This enum represents the 3 options that you can use to search the parsed CSV: */
public enum ColumnIdentifier {
  NO_IDENTIFIER, // User did not specify an identifier
  COLUMN_NAME, // User specified column name as identifier
  COLUMN_INDEX; // User specified column index as identifier
}
