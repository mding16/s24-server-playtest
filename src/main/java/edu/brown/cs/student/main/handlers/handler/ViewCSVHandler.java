package edu.brown.cs.student.main.handlers.handler;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.datasource.GeneralDataSource;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

/** ViewCSVHandler class for viewing the loading the input file. Use with viewcsv endpoint. */
public class ViewCSVHandler implements Route {
  private GeneralDataSource dataSource;

  /**
   * This is the constructor for the ViewCSVHandler class which initializes datasource
   *
   * @param toUse
   */
  public ViewCSVHandler(GeneralDataSource toUse) {
    this.dataSource = toUse;
  }

  /**
   * This is the method to view the loadHandler's dataSource
   *
   * @param request
   * @param response
   * @return
   * @throws Exception
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    // ===== CALL DATASOURCE FOR VIEW DATA -> SUCCESS RESPONSE =====
    List<List<String>> csvData = this.dataSource.viewData();
    return new SuccessResponse(csvData).serialize();

    // ===== IF DATA WAS NOT LOADED -> MISSING DATA EXCEPTION -> ERROR RESPONSE =======
  }

  /**
   * This is to create success response with all data loaded
   *
   * @param result
   * @param data
   * @return SuccessResponse
   */
  public record SuccessResponse(String result, List<List<String>> data) {
    public SuccessResponse(List<List<String>> data) {
      this("success", data);
    }

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(SuccessResponse.class).toJson(this);
    }
  }

  /**
   * This is to create error response without data loaded
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
