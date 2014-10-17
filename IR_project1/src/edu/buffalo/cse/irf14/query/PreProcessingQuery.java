package edu.buffalo.cse.irf14.query;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreProcessingQuery {

	public String preProcessingQuery(String userQuery,String defaultOperator) throws QueryPreProcessingException
	{
		final String OPAND="AND";
		final String OPOR="OR";
		final String OPNOT="NOT";
		final String TERM="Term:";
		StringBuffer stringBuffer=new StringBuffer();
		StringBuffer stringBuffer1=new StringBuffer();
		String parsedQuery="";
		String token="";
		int start=0,end=0;
		int matchcount=0;
		boolean doubleQuotesFlag=false;
		boolean matchFlag=false;
		if(defaultOperator.equals("") || defaultOperator==null || defaultOperator.equals(" "))
			defaultOperator="OR";
		try
		{
			if(userQuery.contains("\""))
				doubleQuotesFlag=true;
			String [] tokens= userQuery.split(" ");
			if(tokens.length==1 && !doubleQuotesFlag)
			{
				StringBuffer stringBuffer2=new StringBuffer();
				token=userQuery;
				if(!token.equals(OPAND) && !token.equals(OPNOT) && !token.equals(OPOR) && !doubleQuotesFlag && !token.contains(":"))
				{
					parsedQuery=stringBuffer2.insert(0,TERM).append(userQuery).toString();
				}
				if(token.contains(":"))
				{
					parsedQuery=stringBuffer2.append(userQuery).toString();
				}
				return parsedQuery;
			}
			else
			{
				Pattern pattern = Pattern.compile("([Cc]ategory|[Pp]lace|[Aa]uthor):[(].*?[)]");
				Matcher matcher = pattern.matcher(userQuery);
				while(matcher.find())
				{
					matchcount++;
					matchFlag=true;
					stringBuffer1=new StringBuffer();
					String matchedString=userQuery.substring(matcher.start(), matcher.end());
					String reString=matchedString.substring(0,matchedString.lastIndexOf(':')+1);
					String two=matchedString.substring(matchedString.indexOf('(')+1, matchedString.lastIndexOf(')')+1);
					String[] h= two.split(" ");
					for(int j=0;j<h.length;j++)
					{
						if(!h[j].equals(OPAND) &&!h[j].equals(OPOR) &&!h[j].equals(OPNOT) && j==0)
						{
							stringBuffer1.append("(").append(reString).append(h[j]);	
						}
						else if(!h[j].equals(OPAND) &&!h[j].equals(OPOR) &&!h[j].equals(OPNOT))
						{
							stringBuffer1.append(" ").append(reString).append(h[j]);	
						}
						else
						{
							stringBuffer1.append(" ").append(h[j]);
						}
					}
					String finalString=stringBuffer1.toString();
					if(matchcount==1)
						parsedQuery=userQuery.replace(matchedString, finalString);
					else
						parsedQuery=parsedQuery.replace(matchedString, finalString);
				}
				if(!matchFlag)
					parsedQuery=userQuery;
				//Add code for handling brackets
				//code to be written for handling double quotes
				if(doubleQuotesFlag)
				{
					for(int i=0;i<tokens.length;i++)
					{

					}
				}
				//to be changed according to the above code
				tokens= parsedQuery.split(" ");
				for(int i=0;i<tokens.length;i++)
				{
					if(tokens[i].equals(OPAND) || tokens[i].equals(OPOR) || tokens[i].equals(OPOR) || tokens[i].contains(")"))
					{
						if(i!=tokens.length-1)
						{
							if(!tokens[i+1].equals(OPAND) && !tokens[i+1].equals(OPOR) && !tokens[i+1].equals(OPOR)&& !tokens[i+1].contains(":") && !tokens[i+1].contains(")"))
							{
								start=i+1;
								for(i=start;i<tokens.length;i++)
								{
									if(tokens[i].equals(OPAND) || tokens[i].equals(OPOR) || tokens[i].equals(OPOR) || tokens[i].contains(")"))
									{
										end=i-1;
										break;
									}
									if(i==(tokens.length-1))
									{
										end=tokens.length-1;
										break;
									}
								}
								break;
							}
						}
					}

				}
				boolean flag=false;
				int diff=end-start;
				if(diff>1)
				{
					flag=true;
				}
				/*System.out.println("Start--->"+start);
		System.out.println("END--->"+end);*/
				for(int i=0;i<tokens.length;i++)
				{
					if(flag)
					{
						if(i==start)
						{
							if(tokens[i].contains("("))
								tokens[i]=tokens[i].replace("(","[");
							else
								tokens[i]= "["+tokens[i];
						}
						if(i==end)
						{
							if(tokens[i].contains(")"))
								tokens[i]=tokens[i].replace(")","]");
							tokens[i]= tokens[i]+"]";
						}
						if(tokens[i].equals(OPAND) || tokens[i].equals(OPOR) || tokens[i].equals(OPOR) || tokens[i].contains(":"))
						{
							parsedQuery=stringBuffer.append(tokens[i]).append(" ").toString();
						}
						if(i!=tokens.length-1)
						{
							if(!tokens[i].equals(OPAND) && !tokens[i].equals(OPOR) && !tokens[i].equals(OPOR)&& !tokens[i].contains(":"))
							{
								if(!tokens[i+1].equals(OPAND) && !tokens[i+1].equals(OPOR) && !tokens[i+1].equals(OPOR)&& !tokens[i+1].contains(":") && !tokens[i+1].contains(")"))
								{
									parsedQuery=stringBuffer.append(tokens[i]).append(" ").append(defaultOperator).append(" ").toString();
								}
								else
								{
									parsedQuery=stringBuffer.append(tokens[i]).append(" ").toString();
								}
							}
						}
						if(i==tokens.length-1)
						{
							parsedQuery=stringBuffer.append(tokens[i]).toString();
						}
					}
					else
					{
						if(!tokens[i].equals(OPNOT) && !tokens[i].equals(OPAND) && !tokens[i].equals(OPOR) && !tokens[i].contains("\""))
						{
							if(i==tokens.length-1)
							{
								parsedQuery=stringBuffer.append(tokens[i]).toString();
							}
							else if(i!=tokens.length && !tokens[i+1].equals(OPAND) && !tokens[i+1].equals(OPOR) && !tokens[i+1].equals(OPNOT))
							{
								parsedQuery=stringBuffer.append(tokens[i]).append(" ").append(defaultOperator).append(" ").toString();
							}
							else
							{
								parsedQuery=stringBuffer.append(tokens[i]).append(" ").toString();
							}
						}
						else
						{
							parsedQuery=stringBuffer.append(tokens[i]).append(" ").toString();
						}
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new QueryPreProcessingException("Error Occured in PreProcesing Asshole.You didnt handle a case MR Harsh Harwani");		
		}
		return parsedQuery;

	}
}

