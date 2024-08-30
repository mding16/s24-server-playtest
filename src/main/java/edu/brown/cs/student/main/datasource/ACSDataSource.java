package edu.brown.cs.student.main.datasource;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import edu.brown.cs.student.main.exceptions.InvalidRequestArgumentException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okio.Buffer;

/** This Class handles ACS datasource : */
public class ACSDataSource implements DataSource {
  private HashMap<String, String> stateCodes;

  /**
   * This is the constructor of ACS data source which loads all state code
   *
   * @throws DatasourceException
   * @throws IOException
   */
  public ACSDataSource() throws DatasourceException, IOException {
    // When the ACS datasource is first constructed, we will load the state codes
    this.loadStateCodes();
  }

  /**
   * Helper to load state codes by querying ACS (** THIS METHOD WORKS**)
   *
   * @throws DatasourceException if connection failure
   */
  private void loadStateCodes() throws DatasourceException {
    try {
      // Try to create a valid connection
      String censusAPIBaseURL = "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*";
      URL stateCodesURL = new URL(censusAPIBaseURL);
      HttpURLConnection stateCodesConnection = (HttpURLConnection) stateCodesURL.openConnection();
      stateCodesConnection.connect();

      if (stateCodesConnection.getResponseCode() == 200) {
        // If valid connection, deserialize response
        Moshi moshi = new Moshi.Builder().build();

        // HANDLING INPUT STREAM & POPULATING STATECODES HASHMAP
        JsonAdapter<List> adapter = moshi.adapter(List.class).nonNull();
        List<List<String>> jsonData =
            adapter.fromJson(new Buffer().readFrom(stateCodesConnection.getInputStream()));
        stateCodesConnection.disconnect();
        this.stateCodes = new HashMap<>();
        for (int i = 1; i < jsonData.size(); i++) {
          this.stateCodes.put(jsonData.get(i).get(0), jsonData.get(i).get(1));
        }
      } else {
        throw new DatasourceException(
            "unexpected: API connection not success status "
                + stateCodesConnection.getResponseMessage());
      }
    } catch (IOException | DatasourceException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get the county code of one county in a state
   *
   * @param stateCode represents state code
   * @param countyName represents county name
   * @return string of state code
   * @throws IOException if IO FAILURE
   * @throws InvalidRequestArgumentException if invalid county name
   */
  public String getCountyCode(String stateCode, String countyName)
      throws IOException, InvalidRequestArgumentException {
    String countyCodesQuery =
        "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:" + stateCode;
    URL countyCodesURL = new URL(countyCodesQuery);
    HttpURLConnection countyConnection = (HttpURLConnection) countyCodesURL.openConnection();
    countyConnection.connect();

    if (countyConnection.getResponseCode() == 200) {
      // If valid connection, deserialize response
      Moshi moshi = new Moshi.Builder().build();
      JsonAdapter<List> adapter = moshi.adapter(List.class).nonNull();
      List<List<String>> jsonData =
          adapter.fromJson(new Buffer().readFrom(countyConnection.getInputStream()));
      countyConnection.disconnect();

      // Create hashmap of county codes
      HashMap<String, String> countyStateCodes = new HashMap<>();
      for (int i = 1; i < jsonData.size(); i++) {
        String county = jsonData.get(i).get(0).split(",")[0];
        String countyCode = jsonData.get(i).get(2);
        countyStateCodes.put(county, countyCode);
      }
      return countyStateCodes.get(countyName);
    }
    throw new InvalidRequestArgumentException("county name: " + countyName + " is invalid");
  }

  /**
   * Search through the data
   *
   * @param state represents state code
   * @param county represents county name
   * @return Map<String, Object>
   * @throws IOException if IO FAILURE
   * @throws InvalidRequestArgumentException
   * @throws NullPointerException
   */
  public Map<String, Object> searchACS(String state, String county)
      throws InvalidRequestArgumentException, FactoryFailureException, NullPointerException {
    // state -> state code
    String stateCode = this.stateCodes.get(state);
    if (stateCode == null) {
      return null;
    }

    // county -> county code
    try {
      String countyCode = getCountyCode(stateCode, county);
      // Build the request URL for broadband data
      String query =
          "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
              + countyCode
              + "&in=state:"
              + stateCode;
      System.out.println(query);
      URL requestURL = new URL(query);
      HttpURLConnection broadbandConnection = (HttpURLConnection) requestURL.openConnection();
      broadbandConnection.connect();

      if (broadbandConnection.getResponseCode() == 200) {
        InputStream responseStream = broadbandConnection.getInputStream();
        InputStreamReader responseReader = new InputStreamReader(responseStream);

        // Create a Parser object to parse the CSV response
        Parser<List<String>> csvParser = new Parser<>(responseReader, values -> values, true);
        List<List<String>> responseData = csvParser.parse();

        if (!responseData.isEmpty()) {
          List<String> data = responseData.get(1);
          String countyName = data.get(0);
          String broadbandPercentage = data.get(1);

          // put in timestamp
          LocalDateTime currentDateTime = LocalDateTime.now();
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
          String formattedDateTime = currentDateTime.format(formatter);

          Map<String, Object> result = new HashMap<>();
          result.put("state", state);
          result.put("county", countyName);
          result.put("timestamp", formattedDateTime);
          result.put("broadbandPercentage", broadbandPercentage);
          return result;
        }
      }
    } catch (InvalidRequestArgumentException | IOException e) {
      throw new InvalidRequestArgumentException(e.getMessage());
    }
    return null;
  }

  /**
   * Search for percentage
   *
   * @param state represents state code
   * @param county represents county name
   * @return CensusData
   * @throws DatasourceException
   */
  @Override
  public CensusData search(String state, String county) throws DatasourceException {
    return new CensusData(this.search(state, county).percentage());
  }
}
