package edu.buffalo.cse.irf14.query;

public class NOT implements Expression
{
	private Expression leftExpression;
	private Expression rightExpression;
	
	public NOT()
	{
		
	}
	public NOT(Expression leftExpression, Expression rightExpression) {
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

}
