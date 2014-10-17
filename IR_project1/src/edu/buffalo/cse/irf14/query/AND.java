package edu.buffalo.cse.irf14.query;

import java.util.Map;

import edu.buffalo.cse.irf14.analysis.util.TermMetadataForThisDoc;
import edu.buffalo.cse.irf14.index.IndexReader;

public class AND implements Expression {
	
	private Expression leftExpression;
	private Expression rightExpression;
	boolean notFlag=false;
	
	public AND()
	{
		
	}
	
	public AND(Expression leftExpression, Expression rightExpression) {
		this.leftExpression = rightExpression;
		this.rightExpression = leftExpression;
	}
	public Expression getLeftExpression() {
		return leftExpression;
	}
	public void setLeftExpression(Expression leftExpression) {
		this.leftExpression = leftExpression;
	}
	public Expression getRightExpression() {
		return rightExpression;
	}
	public void setRightExpression(Expression rightExpression) {
		this.rightExpression = rightExpression;
	}
	
	@Override
	public String toString()
	{
		return leftExpression.toString()+" "+"AND"+" "+rightExpression.toString();
		
	}

	@Override
	public Map<Character, Map<Long, Map<Long, TermMetadataForThisDoc>>> getPostings() {
		// TODO Auto-generated method stub
		return null;
	}
	public  Map<Character, Map<Long, Map<Long, TermMetadataForThisDoc>>> Intersec()
	{
		return null;
		
	}

}
