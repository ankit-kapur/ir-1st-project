package edu.buffalo.cse.irf14.analysis;

import java.util.HashMap;



public class SymbolFilter extends TokenFilter{

	TokenStream tStream=null;
	public SymbolFilter(TokenStream stream) {
		super(stream);
		this.tStream=stream;
		// TODO Auto-generated constructor stub
	}
	TokenStream tokenStream=new TokenStream();
	private static final HashMap<String,String> contractionsMap =  new HashMap<String,String>() ;
	static
	{
		contractionsMap.put("isn't","is not");
		contractionsMap.put("don't","do not");
		contractionsMap.put("won't","will not");
		contractionsMap.put("shan't","shall not");
		contractionsMap.put("I'm","I am");
		contractionsMap.put("we're","we are");
		contractionsMap.put("they're","they are");
		contractionsMap.put("I've","I have");
		contractionsMap.put("Should've","Should have");
		contractionsMap.put("They'd","They would");
		contractionsMap.put("She'll","She will");
		contractionsMap.put("'em","them");
	}
	public void symbolFilter(TokenStream tStream) throws FilterException
	{
		String filteredToken=null,hardString1=null,hardString2=null;
		int digitCount=0,alphacount=0;
		boolean apostropheFlag=false,digitFlag=false,alphaFlag=false,hardFlag=false;
		try{
			if(tStream.hasNext())
			{
				tStream.next();
				apostropheFlag=false;
				alphaFlag=false;
				digitFlag=false;
				Token tokens=tStream.getCurrent();
				if(tokens!=null)
				{
					String token=tokens.getTermText();
					if(token!=null)
					{
						if(token.contains("'s"))
						{
							filteredToken=token.replaceAll("'s","");
							apostropheFlag=true;
						}
						else if(contractionsMap.containsKey(token))
						{
							filteredToken=contractionsMap.get(token);
							if(token.contains("'em"))
							{
								String[] hardString=token.split(" ");
								hardString1=hardString[0];
								hardString2=contractionsMap.get("'em");
							}

						}
						else if(token.contains("'") && !apostropheFlag )
						{
							filteredToken=token.replaceAll("'","");
						}
						else if(token.endsWith("."))
						{
							filteredToken=token.substring(0,token.length()-1);
						}
						else if(token.endsWith("?") || token.endsWith("!"))
						{
							StringBuilder sb = new StringBuilder();
							for(char c:token.toCharArray()){
								if(Character.isDigit(c) || Character.isAlphabetic(c) || c=='.')
								{
									sb.append(c);
								} 
							}
							filteredToken=sb.toString();
						}
						else if(token.contains("-"))
						{
							for(char c : token.toCharArray()){
								if(Character.isDigit(c))
									digitCount++;
								if(Character.isAlphabetic(c))
									alphacount++;
							}
							if(digitCount==0)
							{
								filteredToken=token.replaceAll("[\\s--]","");
								digitFlag=true;
							}
							if(alphacount==token.length()-1)
							{
								filteredToken=token.replaceAll("[\\s--]"," ");
								alphaFlag=true;
							}
							if(!alphaFlag && !digitFlag)
							{
								filteredToken=token;
							}
						}

						else
						{
							filteredToken=token;
						}


						if(filteredToken!=null && !filteredToken.equals("") && !filteredToken.equals(" ") &&!hardFlag)
						{
							Token token2 = new Token();
							if(filteredToken!=null)
							{
								token2.setTermText(filteredToken);
								tokenStream.addTokenToStream(token2);
							}
						}
						if(hardFlag)
						{
							Token token2 = new Token();
							Token token3 = new Token();
							if(hardString1!=null && !hardString1.equals(""))
							{
								token2.setTermText(hardString1);
								tokenStream.addTokenToStream(token2);
							}
							if(hardString2!=null && !hardString2.equals(""))
							{
								token3.setTermText(hardString2);
								tokenStream.addTokenToStream(token3);
							}
						}

					}
				}
			}
		}
		catch(Exception e)
		{
			throw new FilterException("Exception in Symbol Filter");
		}		
	}


	@Override
	public boolean increment() throws TokenizerException {
		try{
			symbolFilter(tStream);
			if(tStream != null && tStream.hasNext())
				return true;
			else
				return false;
		}
		catch(FilterException e)
		{
			e.printStackTrace();
		}
		return false;
	}
	@Override
	public TokenStream getStream() {
		// TODO Auto-generated method stub
		return tokenStream;
	}


	@Override
	public void processThroughFilters() {
		// TODO Auto-generated method stub

	}

}
