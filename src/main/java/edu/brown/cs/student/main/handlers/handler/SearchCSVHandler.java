package edu.brown.cs.student.main.handlers.handler;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.datasource.ColumnIdentifier;
import edu.brown.cs.student.main.datasource.GeneralDataSource;
import edu.brown.cs.student.main.exceptions.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handler for the searchcsv. Searches a loaded csv file for a specified keyword. Can search through
 * all columns or, if a column id or header is provided, returns the relevant search result from the
 * Search class.
 */
public class SearchCSVHandler implements Route {
  GeneralDataSource dataSource;
  String toSearch;
  ColumnIdentifier columnIdentifier;
  String columnIdentifierValue;
  List<List<String>> searchResults;
  final List<String> requiredParams = new ArrayList<>();

  /**
   * This is the constructor for the SearchCSVHandler class which initializes dataSource and
   * requiredParams
   *
   * @param toUse
   */
  public SearchCSVHandler(GeneralDataSource toUse) {
    // For this handler, we require the fields "toSearch", "columnIdentifier [Name, Index, or None]
    // and columnIdentifierValue -- if columnIdentifier is None, columnIdentifierValue will be
    // disregarded (for future implementation, we can consider not requiring this field and
    // making it more flexible. For this search handler, we will just ask the user to say NA for
    // this field -- it will not be processed regardless of what value goes into it
    this.dataSource = toUse;
    this.requiredParams.add("toSearch");
    this.requiredParams.add("columnIdentifier");
    this.requiredParams.add("columnIdentifierValue");
  }

  /**
   * This is the method to search through the loaded csv data by toSearch columnIdentifier, and
   * columnIdentifierValue
   *
   * @param request
   * @param response
   * @return Object
   * @throws Exception
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    RequestHelper searchRequestHelper = new RequestHelper(request, this.requiredParams);
    try {
      Map<String, String> myParams = searchRequestHelper.getUsefulRequestParams();
      this.toSearch = myParams.get("toSearch");
      this.columnIdentifier = this.getIdentifierType(myParams.get("columnIdentifier"));
      this.columnIdentifierValue = myParams.get("columnIdentifierValue");
      this.searchResults =
          this.dataSource.search(this.toSearch, this.columnIdentifier, this.columnIdentifierValue);
      return new SuccessResponse(this.searchResults).serialize();

      // }
      // catch (NumberFormatException | IndexOutOfBoundsException e) {
      // return new ErrorResponse("error_bad_request: some number format error").serialize();

    } catch (Exception e) {
      return new ErrorResponse("error_bad_request:" + e.getMessage()).serialize();
    }
  }

  /**
   * This helper method converts user String identifier into enum ColumnIdentifier
   *
   * @param identifier represents string of user identifier (NAME, INDEX, NONE)
   * @return ColumnIdentifier enum of user input (COLUMN_NAME, COLUMN_INDEX, NO_IDENTIFIER)
   */
  public ColumnIdentifier getIdentifierType(String identifier)
      throws InvalidColumnIdentifierException {
    if (identifier.equalsIgnoreCase("name")) {
      return ColumnIdentifier.COLUMN_NAME;

    } else if (identifier.equalsIgnoreCase("index")) {
      return ColumnIdentifier.COLUMN_INDEX;

    } else if (identifier.equalsIgnoreCase("none")) {
      return ColumnIdentifier.NO_IDENTIFIER;
    } else {
      throw new InvalidColumnIdentifierException(
          "Invalid Column Identifier -- must be name, index or none");
    }
  }

  /**
   * This is to create success response with all data loaded
   *
   * @param result
   * @param searchResult
   * @return SuccessResponse
   */
  public record SuccessResponse(String result, List<List<String>> searchResult) {
    public SuccessResponse(List<List<String>> searchResult) {
      this("success", searchResult);
    }

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(SuccessResponse.class).toJson(this);
    }
  }

  /**
   * This is to create error response with all data loaded
   *
   * @param result
   * @return ErrorResponse
   */
  public record ErrorResponse(String result) {
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(ErrorResponse.class).toJson(this);
    }
  }
}
