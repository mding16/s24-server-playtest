package edu.brown.cs.student.main.handlers.handler;

import edu.brown.cs.student.main.exceptions.MissingArgumentException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import spark.Request;

/** Class that helps for request process */
public class RequestHelper {
  Request request;
  List<String> requiredParams;

  /**
   * This is the constructor for RequestHelper class which initializes request and requiredParams
   *
   * @param request
   * @param requiredParams
   */
  public RequestHelper(Request request, List<String> requiredParams) {
    this.request = request;
    this.requiredParams = requiredParams;
  }

  /**
   * Method to check if the Request contains all the parameters required for a valid request
   *
   * @return map of useful query params if all needed parameters are included in the query Params
   * @throws MissingArgumentException if query params are invalid
   */
  public Map<String, String> getUsefulRequestParams() throws MissingArgumentException {
    Set<String> requestParamSet = request.queryParams();
    Map<String, String> usefulParams = new HashMap<>();

    for (String s : this.requiredParams) {
      if (requestParamSet.contains(s)) {
        usefulParams.put(s, this.request.queryParams(s));
      } else {
        throw new MissingArgumentException(s);
      }
    }
    return usefulParams;
  }
}
