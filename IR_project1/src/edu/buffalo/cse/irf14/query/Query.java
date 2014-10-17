package edu.buffalo.cse.irf14.query;

import java.util.HashMap;

/**
 * Class that represents a parsed query
 * @author nikhillo
 *
 */
public class Query {
	/**
	 * Method to convert given parsed query into string
	 */
	private String parsedQuery;
	private HashMap<Integer,QueryResults> resultsMap;

	public String toString() {
		//TODO: YOU MUST IMPLEMENT THIS
		return parsedQuery;
	}
	public String getParsedQuery() {
		return parsedQuery;
	}
	public void setParsedQuery(String parsedQuery) {
		this.parsedQuery = parsedQuery;
	}
	public HashMap<Integer, QueryResults> getResultsMap() {
		return resultsMap;
	}
	public void setResultsMap(HashMap<Integer, QueryResults> resultsMap) {
		this.resultsMap = resultsMap;
	}

}