package edu.brown.cs.student.main.handlers.handler;

import com.squareup.moshi.Moshi;
import com.sun.jdi.request.InvalidRequestStateException;
import edu.brown.cs.student.main.datasource.ACSDataSource;
import edu.brown.cs.student.main.datasource.GeneralDataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/** BroadBandHandler class for searching through the acsdata for specific state and county. */
public class BroadbandHandler implements Route {
  GeneralDataSource datasource;
  ACSDataSource acsDataSource;
  final List<String> requiredParams = new ArrayList<>();

  /** This is the construction of Broadbandhandler */
  public BroadbandHandler(GeneralDataSource dataSource) {
    // Required parameters = state, county
    this.datasource = dataSource;
    this.requiredParams.add("state");
    this.requiredParams.add("county");
  }

  /**
   * This is the method to handle broadband search for specific county and state
   *
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    try {
      this.acsDataSource = new ACSDataSource();
      String state = request.queryParams("state");
      String county = request.queryParams("county");
      if (state == null || state.isEmpty()) {
        return new ErrorResponse("error_bad_request").serialize();
      }
      if (county == null || county.isEmpty()) {
        return new ErrorResponse("error_bad_request").serialize();
      }
      // Get useful parameters from broadband request
      // If all parameters are successfully retrieved, use the
      // ACS datasource to process request + pull search results
      Map<String, Object> broadbandData = this.acsDataSource.searchACS(state, county);
      if (broadbandData != null) {
        // If there are results, send...
        return new SuccessResponse(broadbandData).serialize();
      } else {
        return new ErrorResponse("No data found").serialize();
      }

      // Catch possibility that parameters are invalid (does not include state/county)
    } catch (InvalidRequestStateException e) {
      return new ErrorResponse("error_bad_request: " + e.getMessage()).serialize();
    }
  }

  /**
   * This is to create success response with all data loaded
   *
   * @param result
   * @param data
   * @return SuccessResponse
   */
  public record SuccessResponse(String result, Map<String, Object> data) {

    public SuccessResponse(Map<String, Object> data) {
      this("success", data);
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
