package edu.buffalo.cse.irf14.query;

import java.util.Map;

import edu.buffalo.cse.irf14.analysis.util.TermMetadataForThisDoc;

public class Bracket implements Expression {

	
	private String bracketValue;

	public Bracket(String s) {
		this.bracketValue=s;
		// TODO Auto-generated constructor stub
	}

	public String getS() {
		return bracketValue;
	}

	public void setS(String s) {
		this.bracketValue = s;
	}

	@Override
	public Map<Character, Map<Long, Map<Long, TermMetadataForThisDoc>>> getPostings() {
		// TODO Auto-generated method stub
		return null;
	}
}
