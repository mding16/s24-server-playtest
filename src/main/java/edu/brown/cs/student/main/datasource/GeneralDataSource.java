package edu.brown.cs.student.main.datasource;

import edu.brown.cs.student.main.exceptions.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/** This is the class with datasource related helper methods */
public class GeneralDataSource implements DataSource {
  Parser<List<String>> dataParser;
  private boolean dataLoaded;
  private List<List<String>> csvParser;
  private CensusData censusData;

  /** This is the constructor for general datasource */
  public GeneralDataSource() {
    this.dataLoaded = false;
    this.censusData = new CensusData(89.9);
  }

  /**
   * This method load the csv from parser
   *
   * @param filePath
   * @param hasHeader
   */
  public void loadData(String filePath, boolean hasHeader)
      throws IOException, InvalidRequestArgumentException {
    try (FileReader myReader = new FileReader(filePath)) {
      this.dataParser = new Parser<>(myReader, new ListOfStringsCreator(), hasHeader);
      this.csvParser = dataParser.parse();
      this.dataLoaded = true;
    } catch (IOException e) {
      throw new IOException();
    }
  }

  /**
   * This is method show all data from the loaded csv
   *
   * @throws MissingDataException
   * @return List<List<String>> as csv parser
   */
  public List<List<String>> viewData() throws MissingDataException {
    if (!this.dataLoaded) {
      throw new MissingDataException();
    } else {
      return this.csvParser;
    }
  }

  /**
   * This method is to search in the loaded csv by toSearch, columnIdentifier, and
   * columnIdentifierValue
   *
   * @param toSearch
   * @param columnIdentifier
   * @param columnIdentifierValue
   */
  public List<List<String>> search(
      String toSearch, ColumnIdentifier columnIdentifier, String columnIdentifierValue)
      throws MissingDataException, InvalidColumnIdentifierException, NoHeaderException,
          InvalidColumnIdentifierValueException, IOException, FactoryFailureException {
    if (!this.dataLoaded) {
      throw new MissingDataException();
    } else {
      Searcher dataSearcher = new Searcher(this.dataParser);
      switch (columnIdentifier) {
        case NO_IDENTIFIER -> {
          return dataSearcher.searchAll(toSearch);
        }
        case COLUMN_NAME -> {
          return dataSearcher.searchByHeaderName(columnIdentifierValue, toSearch);
        }
        case COLUMN_INDEX -> {
          Integer index = Integer.parseInt(columnIdentifierValue);
          return dataSearcher.searchByIndex(index, toSearch);
        }
      }
    }
    throw new MissingDataException();
  }

  /**
   * This method is to search in the braodBand
   *
   * @param county
   * @param state
   * @throws DatasourceException
   */
  @Override
  public CensusData search(String state, String county) throws DatasourceException {
    return this.censusData;
  }
}
