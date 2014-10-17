package edu.buffalo.cse.irf14.query;

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

}