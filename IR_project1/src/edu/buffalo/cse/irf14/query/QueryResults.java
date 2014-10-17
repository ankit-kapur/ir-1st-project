package edu.buffalo.cse.irf14.query;

public class QueryResults {
	
private int rank;
private String title;
private String snippet;
private float relevancyScore;

public int getRank() {
	return rank;
}
public void setRank(int rank) {
	this.rank = rank;
}
public String getTitle() {
	return title;
}
public void setTitle(String title) {
	this.title = title;
}
public String getSnippet() {
	return snippet;
}
public void setSnippet(String snippet) {
	this.snippet = snippet;
}
public float getRelevancyScore() {
	return relevancyScore;
}
public void setRelevancyScore(float relevancyScore) {
	this.relevancyScore = relevancyScore;
}
}
