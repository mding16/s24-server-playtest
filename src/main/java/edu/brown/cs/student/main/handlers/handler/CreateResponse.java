package edu.brown.cs.student.main.handlers.handler;

import java.util.LinkedHashMap;
import java.util.Map;

/** Class that creates the JSON response to be sent to the client. */
public class CreateResponse {

  private Map<String, Object> response;

  /**
   * Makes the success map, which indicates success and whatever else to be displayed on success.
   *
   * @param responseTitle
   * @param responseData
   * @return myResponse
   */
  public Map<String, Object> makeSuccess(String responseTitle, Object responseData) {
    Map<String, Object> myResponse = new LinkedHashMap<>();
    myResponse.put("result", "success");
    myResponse.put(responseTitle, responseData);
    this.response = myResponse;
    return myResponse;
  }
}
