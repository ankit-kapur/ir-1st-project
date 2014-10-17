package edu.buffalo.cse.irf14.query;

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
}
