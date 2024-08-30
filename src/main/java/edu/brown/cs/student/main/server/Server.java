package edu.brown.cs.student.main.server;

import edu.brown.cs.student.main.datasource.GeneralDataSource;
import edu.brown.cs.student.main.handlers.handler.BroadbandHandler;
import edu.brown.cs.student.main.handlers.handler.LoadCSVHandler;
import edu.brown.cs.student.main.handlers.handler.SearchCSVHandler;
import edu.brown.cs.student.main.handlers.handler.ViewCSVHandler;
import spark.Spark;

/** Server class for using all endpoint including loadcsv, viewcsv, searchcsv, and broadbandcsv. */
public class Server {
  private GeneralDataSource state;
  private final int port = 3232;

  /**
   * This is the constructor for the server class to get a port and use all api
   *
   * @param toUse
   */
  public Server(GeneralDataSource toUse) {
    this.state = toUse;
    Spark.port(this.port);
    Spark.init();
    Spark.get("/loadcsv", new LoadCSVHandler(this.state));
    Spark.get("/viewcsv", new ViewCSVHandler(this.state));
    Spark.get("/searchcsv", new SearchCSVHandler(this.state));
    // LOAD, VIEW, SEARCH DONE
    Spark.get("/broadband", new BroadbandHandler(toUse));
    System.out.println("Server started at http://localhost:" + this.port);
    Spark.awaitInitialization();
  }

  /**
   * This is main program to activate the server
   *
   * @param args
   */
  public static void main(String[] args) {
    GeneralDataSource myDataSource = new GeneralDataSource();
    Server server = new Server(myDataSource);
  }
}
