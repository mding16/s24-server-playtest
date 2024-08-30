package edu.brown.cs.student.main.datasource;

/** This is the interface to deal with datasource */
public interface DataSource {
  CensusData search(String state, String county) throws DatasourceException;
}
