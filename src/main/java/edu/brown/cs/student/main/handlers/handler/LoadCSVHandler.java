package edu.brown.cs.student.main.handlers.handler;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.datasource.GeneralDataSource;
import edu.brown.cs.student.main.exceptions.InvalidRequestArgumentException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** LoadCSVHandler class for loading the input file. Use with loadcsv endpoint. */
public class LoadCSVHandler implements Route {

  GeneralDataSource datasource;
  List<String> requiredParams = new ArrayList<>();

  /**
   * This is the constructor for LoadCSVHandler class which initializes datasource,
   * requirementParams and has header
   *
   * @param toUse
   */
  public LoadCSVHandler(GeneralDataSource toUse) {
    this.datasource = toUse;
    this.requiredParams.add("filepath");
    this.requiredParams.add("hasheader");
  }

  /**
   * Loads the input file into the server and checks that the input file is valid if the file exists
   * and if the hasHeader parameter is valid.
   *
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {

    // Create RequestHelper object to help with getting hashmap of useful parameters from request
    RequestHelper loadHelper = new RequestHelper(request, this.requiredParams);

    // Initialize filePath to be within scope of all catch blocks
    String filePath = "";

    try {
      // Get the necessary fields are there using a RequestHelper method --
      // For loadCSVHandler, we need "filepath" and "hasHeader"
      // We need to check to make sure all the fields are there before proceeding to handle request
      Map<String, String> usefulParams = loadHelper.getUsefulRequestParams();
      filePath = usefulParams.get("filepath");
      String hasHeader = usefulParams.get("hasheader");

      // ======= LOAD THE CSV INTO DATASOURCE CLASS -> SUCCESS RESPONSE =========
      // the datasource.loadData() will throw IOException if FileNotFound

      this.datasource.loadData(filePath, this.createValidHeaderBool(hasHeader));
      return new SuccessResponse(filePath).serialize();

      // ======= CATCH EXCEPTIONS TO RETURN ERROR RESPONSES =========
      // If there is an IO exception, return error_datasource
    } catch (IOException e) {
      return new ErrorResponse("error_datasource", filePath).serialize();

      // If there is an invalid argument for any field, return
      // "error_bad_request_invalid_arg_for_" + field that has invalid argument
    } catch (InvalidRequestArgumentException e) {
      return new ErrorResponse("error_bad_request_invalid_arg_for_" + e.getInvalidField(), filePath)
          .serialize();

      // If there is a missing argument in the request, return
      // "error_bad_request_missing_field_for_" + field that is missing
    } catch (Exception e) {
      return new ErrorResponse("error_bad_request: " + e.getMessage(), filePath).serialize();
    }
    // "error_bad_json"
  }

  /**
   * Returns the boolean value of hasHeader input
   *
   * @param hasHeader represents the string input for hasHeader, must be "true" or "false" -
   *     case-insensitive
   * @return boolean equivalent of hasHeader string
   * @throws InvalidRequestArgumentException if hasHeader is not "true" or "false"
   */
  public boolean createValidHeaderBool(String hasHeader) throws InvalidRequestArgumentException {
    boolean validBool = hasHeader.equalsIgnoreCase("true") | hasHeader.equalsIgnoreCase("false");
    if (validBool) {
      return Boolean.parseBoolean(hasHeader);
    } else {
      throw new InvalidRequestArgumentException("hasHeader");
    }
  }

  /**
   * This is to create success response with all data loaded
   *
   * @param result
   * @param csvPath
   * @return SuccessResponse
   */
  public record SuccessResponse(String result, String csvPath) {
    public SuccessResponse(String csvPath) {
      this("success", csvPath);
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
  public record ErrorResponse(String result, String csvPath) {
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(ErrorResponse.class).toJson(this);
    }
  }
}
