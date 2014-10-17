package edu.buffalo.cse.irf14.query;


public class Term implements Expression {
	
	private String queryTerm;
	boolean notFlag=false;
	boolean barFlag=false;
	StringBuffer sbBuffer=new StringBuffer();
	
	public Term(String string) {
		queryTerm=string;
		// TODO Auto-generated constructor stub
	}

	public String getQueryTerm() {
		return queryTerm;
	}

	public void setQueryTerm(String queryTerm) {
		this.queryTerm = queryTerm;
	}

	@Override
	public String toString()
	{
		if(queryTerm.contains(":") || queryTerm.equals("AND") || queryTerm.equals("OR") || queryTerm.equals("NOT") && !notFlag)
		{
			if(queryTerm.contains("(") || queryTerm.contains("["))
			{
				queryTerm=queryTerm.replace("(","[");
				barFlag=true;
			}
			if(queryTerm.contains(")"))
			{
				queryTerm=queryTerm.replace(")","]");
				barFlag=true;
			}
			 if(notFlag && !barFlag)
			{
				queryTerm="<Term:"+queryTerm+">";
				notFlag=false;
			}
			 if(notFlag && barFlag)
			{
				StringBuffer sbBuffer= new StringBuffer();
				queryTerm=sbBuffer.append("<").append(queryTerm).toString();
				queryTerm=sbBuffer.insert(queryTerm.indexOf("]"),">").toString();
			}
			
			return queryTerm;
		}
		else
		{
			if(queryTerm.contains("(") || queryTerm.contains("["))
			{
				queryTerm=queryTerm.replace("(","[");
				barFlag=true;
			}
			if(queryTerm.contains(")"))
			{
				queryTerm=queryTerm.replace(")","]");
				barFlag=true;
			}
			 if(notFlag && !barFlag)
			{
				queryTerm="<Term:"+queryTerm+">";
			}
			 if(notFlag && barFlag)
			{
				StringBuffer sbBuffer= new StringBuffer();
				queryTerm=sbBuffer.append("<Term:").append(queryTerm).toString();
				queryTerm=sbBuffer.insert(queryTerm.indexOf("]"),">").toString();
			}
			if(barFlag && !notFlag)
			{
				sbBuffer.append(queryTerm);
				queryTerm=sbBuffer.insert(sbBuffer.lastIndexOf("[")+1,"Term:").toString();
				
			}
			if(!notFlag && !barFlag)
			{
				queryTerm="Term:"+queryTerm;
			}
			
		}
		return queryTerm;
		
	}
	
}
