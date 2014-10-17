/**
 * 
 */
package edu.buffalo.cse.irf14.query;

import edu.buffalo.cse.irf14.query.AND;
import edu.buffalo.cse.irf14.query.Bracket;
import edu.buffalo.cse.irf14.query.Expression;
import edu.buffalo.cse.irf14.query.NOT;
import edu.buffalo.cse.irf14.query.OR;
import edu.buffalo.cse.irf14.query.PreProcessingQuery;
import edu.buffalo.cse.irf14.query.QueryPreProcessingException;
import edu.buffalo.cse.irf14.query.Term;

import java.util.Stack;

/**
 * @author nikhillo
 * Static parser that converts raw text to Query objects
 */
public class QueryParser {
	/**
	 * MEthod to parse the given user query into a Query object
	 * @param userQuery : The query to parse
	 * @param defaultOperator : The default operator to use, one amongst (AND|OR)
	 * @return Query object if successfully parsed, null otherwise
	 */
	public static final String OPAND="AND";
	public static final String OPOR="OR";
	public static final String OPNOT="NOT";
	public static final String TERM="Term:";
	public static final String TERMNOT="<Term:";
	public static final String STARTBRACE="{";
	public static final String ENDBRACE="}";
	public static final String SQUAREBRACKETS="[";
	public static final String ENDSQUAREBRACKETS="]";
	public static final String ENDANGULAR=">";
	public static final String CATEGORY="Category:";
	public static final String AUTHOR="Author:";
	public static final String PLACE="Place:";
	public static Query parse(String userQuery, String defaultOperator) throws QueryParserException {

		Query query= new Query();
		final String OPAND="AND";
		final String OPOR="OR";
		final String OPNOT="NOT";
		System.out.println("The Entered Query Is-->"+userQuery);
		String proccessedQuery="";
		PreProcessingQuery pQuery=new PreProcessingQuery();
		try {
			proccessedQuery=pQuery.preProcessingQuery(userQuery,defaultOperator);
			System.out.println("The PreProcessed Query Is-->"+proccessedQuery);
			String[] queryTokens=proccessedQuery.split(" ");
			StringBuffer sbBuffer=new StringBuffer();
			Stack<Expression> operandStack = new Stack<Expression>();
			Stack<Expression> operatorStack= new Stack<Expression>();
			for(int i=0;i<queryTokens.length;i++)
			{
				if(queryTokens[i].equals(OPAND))
				{
					operatorStack.push(new AND());
				}
				if(queryTokens[i].equals(OPOR))
				{
					operatorStack.push(new OR());
				}
				if(queryTokens[i].equals(OPNOT))
				{
					operatorStack.push(new NOT());
				}
				if(!queryTokens[i].equals(OPAND) && !queryTokens[i].equals(OPNOT) && !queryTokens[i].equals(OPOR))
				{
					if(i!=0)
					{
						if(queryTokens[i-1].equals(OPNOT))
						{
							Term term=new Term(queryTokens[i]);
							term.notFlag=true;
							operandStack.push(term);
						}
						else
						{
							if(queryTokens[i].contains("\""))
							{
								String tokString="";
								for(;i<queryTokens.length;i++)
								{
									tokString=queryTokens[i];
									sbBuffer.append(tokString).append(" ").toString();
									if(tokString.endsWith("\""))
										break;
								}
								queryTokens[i]=sbBuffer.toString();
							}
							operandStack.push(new Term(queryTokens[i]));
						}
					}
					else
					{
						if(queryTokens[i].contains("\""))
						{
							String tokString="";
							for(;i<queryTokens.length;i++)
							{
								tokString=queryTokens[i];
								sbBuffer.append(tokString).append(" ").toString();
								if(tokString.endsWith("\""))
									break;
							}
							queryTokens[i]=sbBuffer.toString().trim();
						}
						operandStack.push(new Term(queryTokens[i]));
					}
				}
				if(queryTokens[i].contains("("))
					operatorStack.push(new Bracket("("));

				if(queryTokens[i].contains(")"))
				{
					Expression expression2;
					do
					{
						expression2=operatorStack.pop();

						if(expression2 instanceof AND)
						{
							operandStack.push(new AND(operandStack.pop(),operandStack.pop()));
						}
						if( expression2 instanceof OR)
						{
							operandStack.push(new OR(operandStack.pop(),operandStack.pop()));
						}
						if(expression2 instanceof NOT)
						{
							operandStack.push(new NOT(operandStack.pop(),operandStack.pop()));
						}
					}while(!(expression2 instanceof Bracket));
				}

			}
			while(!operatorStack.isEmpty())
			{
				Expression expression3=operatorStack.pop();
				if(expression3 instanceof AND)
				{
					operandStack.push(new AND(operandStack.pop(),operandStack.pop()));
				}
				if(expression3 instanceof OR)
				{
					operandStack.push(new OR(operandStack.pop(),operandStack.pop()));
				}
				if(expression3 instanceof NOT)
				{
					operandStack.push(new NOT(operandStack.pop(),operandStack.pop()));
				}
			}
			Expression expression=operandStack.pop();
			String FinalParsedQuery=expression.toString();
			System.out.println("The Final Processed query is-->"+"{"+FinalParsedQuery+"}");
			query.setParsedQuery(FinalParsedQuery);

		} catch (QueryPreProcessingException e) {
			e.printStackTrace();
		}
		catch(Exception e)
		{
			throw new QueryParserException("Error Occured in QueryParser");
		}
		return query;

	}
}


